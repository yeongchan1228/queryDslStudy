package jpa.queryDsl.controller;

import jpa.queryDsl.dto.MemberTeamDto;
import jpa.queryDsl.dto.SearchCond;
import jpa.queryDsl.entity.Member;
import jpa.queryDsl.repository.MemberJpaRepository;
import jpa.queryDsl.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberJpaRepository memberJpaRepository;
    private final MemberRepository memberRepository;

    // @RequestParam을 안해도 url 파라미터로 넘오는 값이 SearchCond의 필드명에 맞게 들어간다.
    @GetMapping("/v1/members")
    public List<MemberTeamDto> searchMemberV1(SearchCond searchCond){
        return memberJpaRepository.searchByWhere(searchCond);
    }

    @GetMapping("/v2/members")
    public Page<MemberTeamDto> searchMemberV2(SearchCond searchCond, Pageable pageable){
        return memberRepository.searchPageComplex(searchCond, pageable);
    }

}