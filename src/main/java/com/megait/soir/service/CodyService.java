package com.megait.soir.service;

import com.megait.soir.domain.Item;
import com.megait.soir.form.CodyForm;
import com.megait.soir.domain.Cody;
import com.megait.soir.domain.Member;
import com.megait.soir.repository.CodyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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
                .backgroundId(codyForm.getBackgroundId())
                .topSize(codyForm.getTopSize())
                .bottomSize(codyForm.getBottomSize())
                .shoesSize(codyForm.getShoesSize())
                .build();

        if(codyForm.getOuterId()!=null){
            cody.setOuterId(codyForm.getOuterId());
            cody.setOuterSize(codyForm.getOuterSize());
        }

        if(codyForm.getAccId()!=null){
            cody.setAccId(codyForm.getAccId());
            cody.setAccSize(codyForm.getAccSize());
        }

        codyRepository.save(cody);

        return cody;

    }

    public List<Cody> getCodyList(Member member) {
        List<Cody> codyList = codyRepository.findAllByMember(member);
        return codyList;
    }

    public List<Cody> getAllList(){
        List<Cody> codyList = codyRepository.findAll();
        return codyList;
    }
}
