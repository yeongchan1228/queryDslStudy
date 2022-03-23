package jpa.queryDsl.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jpa.queryDsl.dto.MemberTeamDto;
import jpa.queryDsl.dto.QMemberTeamDto;
import jpa.queryDsl.dto.SearchCond;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.util.StringUtils;

import java.util.List;

import static jpa.queryDsl.entity.QMember.*;
import static jpa.queryDsl.entity.QTeam.*;

@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public List<MemberTeamDto> search(SearchCond searchCond) {
        return queryFactory
                .select(new QMemberTeamDto(member.id, member.username, member.age, team.id, team.name))
                .from(member )
                .leftJoin(member.team, team)
                .where(
                        usernameEq(searchCond.getUsername()),
                        teamNameEq(searchCond.getTeamName()),
                        ageGoe(searchCond.getAgeGoe()),
                        ageLoe(searchCond.getAgeLoe())
                )
                .fetch();
    }

    @Override
    public Page<MemberTeamDto> searchPageComplex(SearchCond searchCond, Pageable pageable) {
        List<MemberTeamDto> content = queryFactory
                .select(new QMemberTeamDto(member.id, member.username, member.age, team.id, team.name))
                .from(member)
                .leftJoin(member.team, team)
                .where(
                        usernameEq(searchCond.getUsername()),
                        teamNameEq(searchCond.getTeamName()),
                        ageGoe(searchCond.getAgeGoe()),
                        ageLoe(searchCond.getAgeLoe())
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(member.count())
                .from(member)
                .where(
                        usernameEq(searchCond.getUsername()),
                        teamNameEq(searchCond.getTeamName()),
                        ageGoe(searchCond.getAgeGoe()),
                        ageLoe(searchCond.getAgeLoe())
                );

        // 페이지 보다 검색 결과가 적을 때, 페이지 마지막일 땐 카운트 쿼리를 날릴 필요가 없기 때문에 그런 경우
        // countQuery::fetchOne은 알아서 실행되지 않는다. -> 필요할 때만 사용한다. 알아서.
        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);

    }

    private BooleanExpression usernameEq(String usernameCond) {
        return StringUtils.hasText(usernameCond) ? member.username.eq(usernameCond) : null;
    }

    private BooleanExpression teamNameEq(String teamNameCond) {
        return StringUtils.hasText(teamNameCond) ? team.name.eq(teamNameCond) : null;
    }

    private BooleanExpression ageGoe(Integer ageGoeCond) {
        return ageGoeCond == null ? null : member.age.goe(ageGoeCond);
    }

    private BooleanExpression ageLoe(Integer ageLoeCond) {
        return ageLoeCond == null ? null : member.age.loe(ageLoeCond);
    }
}
