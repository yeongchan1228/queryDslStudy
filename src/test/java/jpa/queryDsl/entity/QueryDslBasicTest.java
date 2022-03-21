package jpa.queryDsl.entity;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static jpa.queryDsl.entity.QMember.*;
import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback(value = false)
public class QueryDslBasicTest {

    EntityManager em;
    JPAQueryFactory query;

    @Autowired
    public QueryDslBasicTest(EntityManager em) {
        this.em = em;
        query = new JPAQueryFactory(em);
    }

    @BeforeEach
    public void before(){
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
}
