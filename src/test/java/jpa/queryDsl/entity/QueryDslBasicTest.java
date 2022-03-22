package jpa.queryDsl.entity;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;

import java.util.List;

import static jpa.queryDsl.entity.QMember.*;
import static jpa.queryDsl.entity.QTeam.*;
import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback(value = false)
public class QueryDslBasicTest {

    @Autowired EntityManager em;
    JPAQueryFactory query;

    @BeforeEach
    public void before(){
        query = new JPAQueryFactory(em);

        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        em.persist(teamA);
        em.persist(teamB);

        Member memberA = new Member("memberA", 10, teamA);
        Member memberB = new Member("memberB", 20, teamA);
        Member memberC = new Member("memberC", 30, teamB);
        Member memberD = new Member("memberD", 40, teamB);
        em.persist(memberA);
        em.persist(memberB);
        em.persist(memberC);
        em.persist(memberD);
    }

    @Test
    public void startJPQL() throws Exception {
        // MemberA 찾기
        Member findMemberA = em.createQuery("select m from Member m where m.username = :username", Member.class)
                .setParameter("username", "memberA")
                .getSingleResult();

        assertThat(findMemberA.getUsername()).isEqualTo("memberA");
    }
    
    @Test
    public void startQueryDsl() throws Exception {
        Member findMemberA = query
                .selectFrom(member)
                .where(member.username.eq("memberA"))
                .fetchOne();

        assertThat(findMemberA.getUsername()).isEqualTo("memberA");

    }

    @Test
    public void search() throws Exception {
        Member findMember = query
                .selectFrom(member)
                .where(member.username.eq("memberA").and(member.age.eq(10)))
                .fetchOne();

        assertThat(findMember.getUsername()).isEqualTo("memberA");

        List<Member> result = query
                .selectFrom(member)
                .where((member.age.between(10, 30)))
                .fetch();

        assertThat(result.size()).isEqualTo(3);
        /**
         * eq : =
         * ne : !=
         * eq(~).not() : !=
         * isNotNull() : is not null
         * in(10, 20) : 10 or 20
         * notIn() : !(10 or 20)
         * between(10, 30) : 10 <= x <=30
         * goe(30) : great or equal  x >= 30
         * gt(30) : great then x > 30
         * loe(30) : low or equal x <= 30
         * lt(30) : low then x < 30
         * like("member%") : like 검색
         * contains("member") : %member% like 검색
         * startWith("member") member% like 검색
         */
    }
    
    @Test
    public void searchAndParam() throws Exception {
        Member findMember = query
                .selectFrom(member)
                .where(member.username.eq("memberA"), member.age.eq(10)) // ,로 and 지원 -> null 무시 가능
                .fetchOne();

        assertThat(findMember.getUsername()).isEqualTo("memberA");
    }
    
    @Test
    public void resultFetchTest() throws Exception {
        List<Member> result = query
                .selectFrom(member)
                .fetch(); // 결과를 리스트로 반환

        Member result2 = query
                .selectFrom(member)
                .where(member.username.eq("memberA"))
                .fetchOne(); // 단건 조회

        Member result3 = query
                .selectFrom(QMember.member)
                .fetchFirst(); // .limit(1).fetchOne()과 동일, 여러 개가 있어도 한 개만 반환 -> limit 1

        Long totalCount = query
                .select(member.count()) // member_id 기반 카운트 탐색
                .from(member)
                .fetchOne();
        System.out.println("totalCount = " + totalCount);

        Long totalCount2 = query
                .select(Wildcard.count) // count(*) 탐색
                .from(member)
                .fetchOne();
        System.out.println("totalCount2 = " + totalCount2);

    }

    /**
     * 회원 나이 내림차순(desc)
     * 회원 이름 올림차순(asc)
     * 단 회원 이름이 없으면 마지막(nulls last)
     */
    @Test
    public void sort() throws Exception {
        // 회원 보충
        em.persist(new Member(null, 100));
        em.persist(new Member("memberE", 100));
        em.persist(new Member("memberF", 100));

        List<Member> result = query
                .selectFrom(member)
                .orderBy(member.age.desc(), member.username.asc().nullsLast())
                .fetch();

        for (Member member1 : result) {
            System.out.println(member1);
        }
    }

    @Test
    public void paging() throws Exception {
        List<Member> result = query
                .selectFrom(member)
                .orderBy(member.username.desc())
                .offset(1) // 0부터 시작이라 1개 스킵 의미
                .limit(2)
                .fetch();

        Long totalCount = query
                .select(member.count())
                .from(member)
                .fetchOne();

        assertThat(result.size()).isEqualTo(2);
        assertThat(totalCount).isEqualTo(4);
        assertThat(totalCount/result.size()).isEqualTo(2); // 페이징 개수
    }

    @Test
    public void aggregation() throws Exception {
        List<Tuple> result = query
                .select(member.count(), member.age.sum(), member.age.avg(), member.age.max(), member.age.min())
                .from(member)
                .fetch();

        Tuple tuple = result.get(0);
        assertThat(tuple.size()).isEqualTo(5); // 위 member.count() ... 총 5개
        assertThat(tuple.get(member.count())).isEqualTo(4);
        assertThat(tuple.get(member.age.max())).isEqualTo(40);
    }

    /**
     * 각 팀의 이름과 평균 연령
     */
    @Test
    public void group() throws Exception {
        List<Tuple> result = query
                .select(team.name, member.age.avg())
                .from(member)
                .join(member.team, team)
                .groupBy(team.name)
                .fetch();

        Tuple teamA = result.get(0);
        Tuple teamB = result.get(1);

        assertThat(teamA.get(team.name)).isEqualTo("teamA");
        assertThat(teamA.get(member.age.avg())).isEqualTo(15);
        assertThat(teamB.get(team.name)).isEqualTo("teamB");
        assertThat(teamB.get(member.age.avg())).isEqualTo(35);
    }

    @Test
    public void join() throws Exception {
        /**
         * inner : null X
         * outer(left, right) : null O
         */
        List<Member> result = query
                .selectFrom(member)
                .join(member.team, team)
                .where(team.name.eq("teamA"))
                .fetch();

        assertThat(result).extracting("username").containsExactly("memberA", "memberB");


        em.persist(new Member("member1", 20));
        em.persist(new Member("member2", 20));
        List<Member> result2 = query
                .selectFrom(member)
                .join(member.team, team)
                .fetch();
        assertThat(result2.size()).isEqualTo(4);

        List<Member> result3 = query
                .selectFrom(member)
                .leftJoin(member.team, team)
                .fetch();
        assertThat(result3.size()).isEqualTo(6);

        List<Member> result4 = query
                .selectFrom(member)
                .rightJoin(member.team, team)
                .fetch();
        assertThat(result4.size()).isEqualTo(4);
    }

    /**
     * 회원 이름과 팀 이름이 같은 회원 조회
     * 막 조인
     * 외부 조인 불가
     */
    @Test
    public void theta_join() throws Exception {
         em.persist(new Member("teamA"));
         em.persist(new Member("teamB"));
         em.persist(new Member("teamC"));

        List<Member> result = query
                .select(member)
                .from(member, team)
                .where(team.name.eq(member.username))
                .fetch();
        assertThat(result.size()).isEqualTo(2);
    }

    /**
     * 회원과 팀을 조인하면서, 팀 이름이 teamA인 팀만, 회원은 모두 조회
     * JPQL : select t, m from Member m left join m.team t on t.name = 'teamA'
     */
    @Test
    public void join_on_filter() throws Exception {

        List<Tuple> result = query
                .select(member, team)
                .from(member)
                .leftJoin(member.team, team)
                .on(team.name.eq("teamA"))
                .fetch();

        for (Tuple tuple : result) {
            System.out.println("tuple = " + tuple);
        }
    }

    @Test
    public void join_on_no_relation() throws Exception {
        em.persist(new Member("teamA"));
        em.persist(new Member("teamB"));
        em.persist(new Member("teamC"));

        /**
         * join(대상, 조인할 값) -> join(대상).on(조인할 값)
         */
        List<Tuple> result = query
                .select(member, team)
                .from(member)
                .join(team).on(team.name.eq(member.username))
                .fetch();

        for (Tuple tuple : result) {
            System.out.println("tuple = " + tuple);
        }
    }

    @PersistenceUnit
    EntityManagerFactory emf;

    @Test
    public void fetchJoinNo() throws Exception {
        em.flush();
        em.clear();

        Member findOne = query
                .selectFrom(member)
                .where(member.username.eq("memberA"))
                .fetchOne();

        boolean loaded = emf.getPersistenceUnitUtil().isLoaded(findOne.getTeam()); // 영속성 컨텍스트에 존재하는지 알려줌
        assertThat(loaded).as("패치 조인 미적용").isFalse();
    }

    @Test
    public void fetchJoinUse() throws Exception {
        em.flush();
        em.clear();

        Member findOne = query
                .selectFrom(member)
                .join(member.team, team).fetchJoin()
                .where(member.username.eq("memberA"))
                .fetchOne();

        boolean loaded = emf.getPersistenceUnitUtil().isLoaded(findOne.getTeam()); // 영속성 컨텍스트에 존재하는지 알려줌
        assertThat(loaded).as("패치 조인 적용").isTrue();
    }
}
