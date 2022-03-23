package jpa.queryDsl.repository;

import jpa.queryDsl.dto.MemberTeamDto;
import jpa.queryDsl.dto.SearchCond;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface MemberRepositoryCustom {

    List<MemberTeamDto> search(SearchCond searchCond);
    Page<MemberTeamDto> searchPageComplex(SearchCond searchCond, Pageable pageable);

}
