package com.megait.soir;

import com.megait.soir.domain.Category;
import com.megait.soir.repository.CategoryRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;

@SpringBootTest
public class CategoryTest {

    @Autowired
    CategoryRepository categoryRepository;

    @PostConstruct
    void init(){
        Category c1 = new Category();
        c1.setName("Test Album");

        Category c2 = new Category();
        c2.setName("Test Book");

        Category c3 = new Category();
        c3.setName("Test Game");

        categoryRepository.save(c1);
        categoryRepository.save(c2);
        categoryRepository.save(c3);
    }


    @Test
    void find_by_name_with_existing_entity(){
        // given
        String name = "wrong_name";

        // when
        Category category = categoryRepository.findByName(name);

        // then
        Assertions.assertThat(category).isNull();
    }

    @Test
    void find_by_name_with_none_existing_entity(){
        // given
        String name = "Test Game";

        // when
        Category category = categoryRepository.findByName(name);

        // then
        Assertions.assertThat(category).isNotNull();
    }

}
