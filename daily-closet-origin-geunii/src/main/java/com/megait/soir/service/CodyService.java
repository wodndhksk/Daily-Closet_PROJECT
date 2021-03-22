package com.megait.soir.service;

import com.megait.soir.form.CodyForm;
import com.megait.soir.domain.Cody;
import com.megait.soir.domain.Member;
import com.megait.soir.repository.CodyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CodyService {

    private final CodyRepository codyRepository;

    public Cody createNewCody(Member member,CodyForm codyForm) {

        if (codyForm.getTopId()==null){
            codyForm.setTopId(0L);
        }

        Cody cody = Cody.builder()
                .member(member)
                .topId(codyForm.getTopId())
                .bottomId(codyForm.getBottomId())
                .shoesId(codyForm.getShoesId())
                .build();

        if(codyForm.getOuterId()!=null){
            cody.setOuterId((codyForm.getOuterId()));
        }

        if(codyForm.getAccId()!=null){
            cody.setAccId(((codyForm.getAccId())));
        }

        codyRepository.save(cody);

        return cody;

    }
}
