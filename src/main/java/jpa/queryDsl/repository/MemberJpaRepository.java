package jpa.queryDsl.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.JPQLQueryFactory;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jpa.queryDsl.dto.MemberTeamDto;
import jpa.queryDsl.dto.QMemberTeamDto;
import jpa.queryDsl.dto.SearchCond;
import jpa.queryDsl.entity.Member;
import jpa.queryDsl.entity.QMember;
import jpa.queryDsl.entity.QTeam;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

import static jpa.queryDsl.entity.QMember.*;
import static jpa.queryDsl.entity.QTeam.*;

@Repository
@RequiredArgsConstructor
public class MemberJpaRepository {

    private final EntityManager em;
    private final JPAQueryFactory queryFactory;
//    private final JPQLQueryFactory queryFactory; // bean으로 등록 가능

//    public MemberJpaRepository(EntityManager em) {
//        this.em = em;
//        this.queryFactory = new JPAQueryFactory(em);
//    }

    public void save(Member member){
        em.persist(member);
    }

    public Optional<Member> findById(Long id){
        return Optional.ofNullable(em.find(Member.class, id));
    }

    public List<Member> findAll(){
        return em.createQuery("select m from Member m", Member.class).getResultList();
    }

    public List<Member> findAllQueryDsl(){
        return queryFactory.select(member).from(member).fetch();
    }

    public List<Member> findByUsername(String username){
        return em.createQuery("select m from Member m where m.username = :username" )
                .setParameter("username", username)
                .getResultList();
    }

    public List<Member> findByUsernameQueryDsl(String username){
        return queryFactory.selectFrom(member).where(member.username.eq(username)).fetch();
    }

    public List<MemberTeamDto> searchByBuilder(SearchCond searchCond){
        // 동적 쿼리에서 모든 조건이 null이면 모든 검색 결과를 다 가져오기 때문에 limit이나 반드시 한 조건은 성립하는게 좋다.
        BooleanBuilder builder = new BooleanBuilder();

        if(StringUtils.hasText(searchCond.getUsername() )){
            builder.and(member.username.eq(searchCond.getUsername()));
        }
        if(StringUtils.hasText(searchCond.getTeamName() )){
            builder.and(team.name.eq(searchCond.getTeamName()));
        }
        if (searchCond.getAgeGoe() != null) {
            builder.and(member.age.goe(searchCond.getAgeGoe()));
        }
        if (searchCond.getAgeLoe() != null) {
            builder.and(member.age.loe(searchCond.getAgeLoe()));
        }

        return queryFactory
                .select(new QMemberTeamDto(member.id.as("memberId"), member.username, member.age,
                        team.id.as("teamId"), team.name.as("teamName")))
                .from(member)
                .leftJoin(member.team, team)
                .where(builder)
                .fetch();

    }
}
