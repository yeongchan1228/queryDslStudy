package jpa.queryDsl.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MemberDto {

    private String username;
    private int age;
}
