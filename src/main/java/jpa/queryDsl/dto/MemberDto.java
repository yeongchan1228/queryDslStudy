package jpa.queryDsl.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MemberDto {

    private String username;
    private int age;

    @QueryProjection // 작성 후 Gradle -> other -> compileQuerydsl : Dto를 QDto로
    public MemberDto(String username, int age) {
        this.username = username;
        this.age = age;
    }
}
