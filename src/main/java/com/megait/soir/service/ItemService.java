package com.megait.soir.service;


import com.megait.soir.domain.*;
import com.megait.soir.repository.*;
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
    private final ParentCategoryRepository parentCategoryRepository;
    private final ChildCategoryRepository childCategoryRepository;

//    @PostConstruct
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

            //parentcategory
            ParentCategory parent = parentCategoryRepository.findByName((String)object.get("big_category"));
            if(parent == null){
                ParentCategory newParent = new ParentCategory();
                newParent.setName((String) object.get("big_category"));
                item.setParentCategory(newParent);
            }
            else{
                item.setParentCategory(parent);
            }

            //childcategory
            ChildCategory child = childCategoryRepository.findByname((String) object.get("small_category"));
            if(child == null){
                ChildCategory newChild = new ChildCategory();
                newChild.setName((String) object.get("small_category"));
                item.setChildCategory(newChild);
            }
            else{
                item.setChildCategory(child);
            }


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
     * 카테고리 아이템 조회
     * @param category
     * @param pageable
     * @return
     */
    public List <Item> getItemListByCategory(String category, Pageable pageable) {
        if (category.equals("best")) {
            return itemRepository.findItem(pageable);
        } else if (category.indexOf("_") > -1) {
            return itemRepository.findItemByParentCategory(category.split("_")[0], category.split("_")[1], pageable);
        } else {
            return itemRepository.findItemByParentCategory(category, pageable);
        }
    }

    public Long getCountItemListByCategory(String category) {
        if (category.equals("best")) {
            return itemRepository.count();
        } else if (category.indexOf("_") > -1) {
            return itemRepository.countItemByParentCategory(category.split("_")[0], category.split("_")[1]);
        } else {
            return itemRepository.countItemByParentCategory(category);
        }
    }

    /**
     * 베스트 아이템 페이징처리 및 정렬 가져오기
     * @param itemRequest
     * @return
     */
    public Pageable getPageable(ItemRequest itemRequest) {
        String sort = itemRequest.getSort() != null ? itemRequest.getSort() : "liked";
        int page = itemRequest.getPage() - 1;
        int limit = itemRequest.getLimit();

        if (limit == 0) {
            limit = 20;
        }
        Sort sortBy = null;
        if (sort.equals("price_high")) {
            sortBy = Sort.by(Sort.Direction.DESC, "price");
        } else if (sort.equals("price_low")) {
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

    //추천 옷 (아우터, 상의 ,하의 )
    public Item findRecommendCategory(Long parent, Long child){
        ParentCategory parentCategory = parentCategoryRepository.getOne(parent);
        ChildCategory childCategory = childCategoryRepository.getOne(child);

        // 카테고리에 해당하는 아이템들을 받는다.
        List<Item> itemList = itemRepository.findAllByParentCategoryAndChildCategory(parentCategory, childCategory);

        // 이 중 랜덤하게 1개를 선택한다.
        int index = (int)(Math.random() * itemList.size());
        Item item = itemList.get(index);

        // 아이템의 url들 중, 0번 url을 mainUrl로
        item.setMainUrl(item.getUrls().get(0));
        return item;
    }

}
