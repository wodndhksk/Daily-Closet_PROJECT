package com.megait.soir;

import com.megait.soir.domain.Category;
import com.megait.soir.repository.CategoryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class SoirRunner implements ApplicationRunner {

    // Category를 활용한 JPA repository test
    @Autowired
    CategoryRepository categoryRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        List<Category> list = categoryRepository.findAll();
        StringBuilder sb = new StringBuilder();

        for (Category c : list){
            sb.append(c.getName()).append(" ");
        }

        log.info("Category : " + sb.toString());

    }

}
