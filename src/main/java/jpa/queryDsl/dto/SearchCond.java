package jpa.queryDsl.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SearchCond {
    // 사이트로부터 검색 조건으로 넘어옴
    private String username;
    private String teamName;
    private Integer ageGoe;
    private Integer ageLoe;
}
