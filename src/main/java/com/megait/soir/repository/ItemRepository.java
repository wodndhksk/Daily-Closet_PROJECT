package com.megait.soir.repository;

import com.megait.soir.domain.ChildCategory;
import com.megait.soir.domain.Item;
import com.megait.soir.domain.ParentCategory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    /**
     * 수환
     * @param pageable
     * @return
     */
    @Query("SELECT i FROM Item i")
    List<Item> findItem(Pageable pageable);

    @Query("SELECT i FROM Item i WHERE i.parentCategory IN (SELECT pc.id FROM ParentCategory pc WHERE pc.name = ?1)")
    List<Item> findItemByParentCategory(String category, Pageable pageable);

    // 신발과 스니커즈 & 가방과 여성가방
    @Query("SELECT i FROM Item i WHERE i.parentCategory IN (SELECT pc.id FROM ParentCategory pc WHERE pc.name = ?1 or pc.name = ?2)")
    List<Item> findItemByParentCategory(String category_1, String category_2, Pageable pageable);

    @Query(value = "SELECT count(i) FROM Item i WHERE i.parentCategory IN (SELECT pc.id FROM ParentCategory pc WHERE pc.name = ?1)")
    Long countItemByParentCategory(String category);

    // 신발과 스니커즈 & 가방과 여성가방
    @Query(value = "SELECT count(i) FROM Item i WHERE i.parentCategory IN (SELECT pc.id FROM ParentCategory pc WHERE pc.name = ?1 or pc.name = ?2)")
    Long countItemByParentCategory(String category_1, String category_2);


    // 재우
    //상품이름, 브랜드 이름으로 전체 검색
    @Query(value = "select * from  Item e where e.Name like %:keyword% or e.Brand like %:keyword%", nativeQuery = true)
    List<Item> findByAllKeyword(@Param("keyword") String keyword);

    @Query(value = "select * from  Item e where e.Name like %:keyword%", nativeQuery = true)
    List<Item> findByNameKeyword(@Param("keyword") String keyword);

    @Query(value = "select * from  Item e where e.Brand like %:keyword%", nativeQuery = true)
    List<Item> findByBrandKeyword(@Param("keyword") String keyword);

//    @Query(value = "select * from  ITEM e where e.PARENT_CATEGORY_ID  like %:parent_category_id%  and " +
//            "e.CHILD_CATEGORY_ID like %:child_category_id%", nativeQuery = true)
//    List <Item> findRecommendCategory(@Param("parent_category_id") Long parent,
//                                      @Param("child_category_id") Long child
//    );

    List<Item> findAllByParentCategoryAndChildCategory(ParentCategory parent, ChildCategory child);
}









