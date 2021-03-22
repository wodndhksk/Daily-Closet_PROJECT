package com.megait.soir.service;


import com.megait.soir.domain.*;
import com.megait.soir.repository.AlbumRepository;
import com.megait.soir.repository.BookRepository;
import com.megait.soir.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@Service
@Transactional // 객체가 사용하는 모든 method에 transaction이 적용된다.
@RequiredArgsConstructor
@Slf4j
public class ItemService {

    private final ItemRepository itemRepository;
    private final AlbumRepository albumRepository;
    private final BookRepository bookRepository;

    @PostConstruct
    public void initAlbumItems() throws IOException, ParseException {


        Resource resource = new ClassPathResource("items.json");
        JSONParser parser = new JSONParser();

        String uri = resource.getFile().toPath().toString();

        JSONArray obj = (JSONArray) parser.parse(new FileReader(uri));



        for (int i = 0; i < obj.size(); i++) {
            JSONObject object = (JSONObject) obj.get(i);
            Item item = new Item();
            item.setId((long) object.get("id"));
            item.setBrand((String) object.get("brand"));
            item.setName((String) object.get("name"));
            item.setPrice((long) object.get("price"));
            item.setCode((String) object.get("item_code"));
            item.setImg_name((String) object.get("img_name"));

            //category
            ParentCategory parent = new ParentCategory();
            parent.setName((String) object.get("big_category"));
            item.setParentCategory(parent);

            ChildCategory child = new ChildCategory();
            child.setName((String) object.get("small_category"));
            child.setParentCategory(parent);
            item.setChildCategory(child);

            System.out.println("부모-------->"+parent.getName());
            System.out.println("자식-------->"+child.getName());

            // image urls
            JSONArray urlArr = (JSONArray) object.get("detail_img");


            Iterator<String> iterator1 = urlArr.iterator();
            while (iterator1.hasNext()) {
                item.getUrls().add(iterator1.next());
            }

            // sizes
            JSONArray sizeArr = (JSONArray) object.get("size");

            Iterator<String> iterator2 = sizeArr.iterator();
            while (iterator2.hasNext()) {
                item.getSizes().add(iterator2.next());
            }

            itemRepository.save(item);
        }
    }

    @PostConstruct
    public void initBookItems() throws IOException {
//        Resource resource = new ClassPathResource("album.CSV");
//        List<String> list = Files.readAllLines(resource.getFile().toPath(), StandardCharsets.UTF_8);
//
//        Stream<String> stream = list.stream();
//
//        Stream<Item> stream2 = stream.map(
//                line->{
//                    String[] split = line.split("\\|");
//                    Book book = new Book();
//                    book.setName(split[0]);
//                    book.setImageUrl(split[1]);
//                    book.setPrice(Integer.parseInt(split[2]));
//                    return book;
//                });
//
//        List<Item> items = stream2.collect(Collectors.toList());
//
//        // 위에서 만든 List<에 저장
//        itemRepository.saveAll(items);

    }

    public List<Album> getAlbumList() {
        return albumRepository.findAll();
    }

    public List<Book> getBookList() {
        return bookRepository.findAll();
    }

    public List<Item> getItemList() {
        return itemRepository.findAll();
    }
    // 재우
    //Get Item by All keyword (name, brand 전체검색)
    public List<Item> findByAllKeyword(String keyword){
        return itemRepository.findByAllKeyword(keyword);
    }

        //Get Item by Name keyword (name 만 검색)
        public List<Item> findByNameKeyword(String keyword){
            return itemRepository.findByNameKeyword(keyword);
        }

        //Get Item by Brand keyword (brand 만 검색)
        public List<Item> findByBrandKeyword(String keyword){
            return itemRepository.findByBrandKeyword(keyword);
        }
        //

    /**
     * 베스트 아이템 조회
     * @return
     */
    public List<Item> getBestItemList() {
        return itemRepository.findAll(Sort.by(Sort.Direction.DESC, "liked"));
    }

    /**
     * 카테고리 아이템 조회
     * @param category
     * @param pageable
     * @return
     */
    public List<Item> getParentCategoryItemList(String category, Pageable pageable) {
        if (category.indexOf("_") > -1) {
            return itemRepository.findItemByParentCategory(category.split("_")[0], category.split("_")[1], pageable);
        } else {
            return itemRepository.findItemByParentCategory(category, pageable);
        }

    }

    /**
     * 베스트 아이템 페이징처리 및 정렬 가져오기
     * @param itemRequest
     * @return
     */
    public Pageable getPageable(ItemRequest itemRequest) {
        String sort = itemRequest.getSort() != null ? itemRequest.getSort() : "name";
        int page = itemRequest.getPage();
        int limit = itemRequest.getLimit();

        if (limit == 0) {
            limit = 20;
        }

        Sort sortBy = null;
        if (sort == "price_high") {
            sortBy = Sort.by(Sort.Direction.DESC, "price");
        } else if (sort == "price_low") {
            sortBy = Sort.by(Sort.Direction.ASC, "price");
        } else {
            sortBy = Sort.by(Sort.Direction.ASC, sort);
        }

        return PageRequest.of(page, limit, sortBy);
    }

    public Item findItem(Long id){

//       < 방법 1 > getOne(id) 사용
//       return itemRepository.getOne(id);

//       < 방법 2 > findById(id) 사용
//       Optional은 null 방지 클래스
        Optional<Item> optional = itemRepository.findById(id);
        return optional.orElseGet(() -> itemRepository.findById(id).get());
    }


}
