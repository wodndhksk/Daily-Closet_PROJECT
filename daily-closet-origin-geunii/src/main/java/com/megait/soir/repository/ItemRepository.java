package com.megait.soir.repository;

import com.megait.soir.domain.Item;
import com.megait.soir.domain.Member;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    @Query("SELECT i FROM Item i WHERE i.parentCategory IN (SELECT pc.id FROM ParentCategory pc WHERE pc.name = ?1)")
//    List<Item> findByParentCategory(String category, Pageable pageable);
    List<Item> findItemByParentCategory(String category, Pageable pageable);

    // 신발과 스니커즈 & 가방과 여성가방
    @Query("SELECT i FROM Item i WHERE i.parentCategory IN (SELECT pc.id FROM ParentCategory pc WHERE pc.name = ?1 or pc.name = ?2)")
//    List<Item> findByParentCategory(String category, Pageable pageable);
    List<Item> findItemByParentCategory(String category_1, String category_2, Pageable pageable);


    // 재우
    //상품이름, 브랜드 이름으로 전체 검색
    @Query(value = "select * from  Item e where e.Name like %:keyword% or e.Brand like %:keyword%", nativeQuery = true)
    List<Item> findByAllKeyword(@Param("keyword") String keyword);

    @Query(value = "select * from  Item e where e.Name like %:keyword%", nativeQuery = true)
    List<Item> findByNameKeyword(@Param("keyword") String keyword);

    @Query(value = "select * from  Item e where e.Brand like %:keyword%", nativeQuery = true)
    List<Item> findByBrandKeyword(@Param("keyword") String keyword);
}









