package com.megait.soir.service;

import com.megait.soir.domain.Album;
import com.megait.soir.domain.Book;
import com.megait.soir.domain.Item;
import com.megait.soir.repository.AlbumRepository;
import com.megait.soir.repository.BookRepository;
import com.megait.soir.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Transactional // 객체가 사용하는 모든 method에 transaction이 적용된다.
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final AlbumRepository albumRepository;
    private final BookRepository bookRepository;

    @PostConstruct
    public void initAlbumItems() throws IOException {
        // CSV 내용을 가져와 entity 객체에 담고, Entity를 DB에 저장(save())
        
        // 파일 로드
        Resource resource = new ClassPathResource("album.CSV");

        /*
        Queue 구조 : 하나씩 읽어들임.
        이 경우 크기가 커지면 시간/비용 문제 발생 -> 이를 병렬 처리하여 해결한다(by stream()).
        * stream() : Collection Framework(from java) ->
         */

        // line별로(enter) List 생성
        List<String> list = Files.readAllLines(resource.getFile().toPath(), StandardCharsets.UTF_8);

        // 생성된 List를 Stream 형으로 생성(map() 메소드의 사용을 위해)
        Stream<String> stream = list.stream();

        // map() method를 사용하여 1:1 대응되는 Stream을 생성한다.
        Stream<Item> stream2 = stream.map(
            line->{
                String[] split = line.split("\\|");
                Album album = new Album();
                album.setName(split[0]);
                album.setImageUrl(split[1]);
                album.setPrice(Integer.parseInt(split[2]));
                return album;
            });

        List<Item> items = stream2.collect(Collectors.toList());

        // 위에서 만든 List<에 저장
        itemRepository.saveAll(items);

        // 위를 요약하여 작성
//        List<Item> albumList = Files.readAllLines
//                (resource.getFile().toPath(), StandardCharsets.UTF_8)
//                .stream()
//                .map(line->{
//                    String[] split = line.split("\\|");
//                    Album album = new Album();
//                    album.setName(split[0]);
//                    album.setImageUrl(split[1]);
//                    album.setPrice(Integer.parseInt(split[2]));
//                    return album;
//                })
//                .collect(Collectors.toList());

    }

    @PostConstruct
    public void initBookItems() throws IOException{
        Resource resource = new ClassPathResource("album.CSV");
        List<String> list = Files.readAllLines(resource.getFile().toPath(), StandardCharsets.UTF_8);

        Stream<String> stream = list.stream();

        Stream<Item> stream2 = stream.map(
                line->{
                    String[] split = line.split("\\|");
                    Book book = new Book();
                    book.setName(split[0]);
                    book.setImageUrl(split[1]);
                    book.setPrice(Integer.parseInt(split[2]));
                    return book;
                });

        List<Item> items = stream2.collect(Collectors.toList());

        // 위에서 만든 List<에 저장
        itemRepository.saveAll(items);

    }


    public List<Album> getAlbumList(){
        return albumRepository.findAll();
    }

    public List<Book> getBookList(){
        return bookRepository.findAll();
    }

    public Item findItem(Long id) {

//        방법 1 : itemRepository.getOne(id);
// getOne()의 return type : Item -> 확실히 존재하는 것이 보장될 때 사용

        // 방법 2 : findById() 
        // return type : optional -> nullPointerException 방지를 위한 포장 객체

        Optional<Item> optional = itemRepository.findById(id);
//        if(optional.isPresent()){
//            return optional.get(); // findById()로 받으면 casting error -> get() 추가
//        }
        // 위 if문을 아래와 같이 바꿔도 된다
        return optional.orElse(null);

        
    }
}
