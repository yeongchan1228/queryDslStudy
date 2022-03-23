package jpa.queryDsl.repository;

import jpa.queryDsl.dto.MemberTeamDto;
import jpa.queryDsl.dto.SearchCond;
import jpa.queryDsl.entity.Member;
import jpa.queryDsl.entity.Team;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback(value = false)
class MemberJpaRepositoryTest {

    @Autowired EntityManager em;

    @Autowired MemberJpaRepository memberJpaRepository;

    @Test
    public void basicTest() throws Exception {
        // given
        Member member1 = new Member("member1", 10);
        memberJpaRepository.save(member1);

        // when
        Optional<Member> findMember = memberJpaRepository.findById(member1.getId());
        List<Member> all = memberJpaRepository.findAll();
        List<Member> all2 = memberJpaRepository.findByUsername(member1.getUsername());

        // then
         assertThat(findMember.get().getUsername()).isEqualTo("member1");
         assertThat(all).containsExactly(member1);
         assertThat(all2).containsExactly(member1);
    }

    @Test
    public void queryDslTest() throws Exception {
        // given
        Member member1 = new Member("member1", 10);
        memberJpaRepository.save(member1);

        // when
        List<Member> all = memberJpaRepository.findAllQueryDsl();
        List<Member> all2 = memberJpaRepository.findByUsernameQueryDsl(member1.getUsername());

        // then
        assertThat(all).containsExactly(member1);
        assertThat(all2).containsExactly(member1);
    }

    @Test
    public void builderTest() throws Exception {
        // given
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

        // when
        SearchCond searchCond = new SearchCond("memberA", "teamA", null, null);
        List<MemberTeamDto> result = memberJpaRepository.searchByBuilder(searchCond);

        // then
        for (MemberTeamDto memberTeamDto : result) {
            System.out.println("memberTeamDto = " + memberTeamDto);
        }
    }

    @Test
    public void whereTest() throws Exception {
        // given
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

        // when
        SearchCond cond = new SearchCond("memberA", "teamB", 10, null);
        List<MemberTeamDto> result = memberJpaRepository.searchByWhere(cond);

        // then
        for (MemberTeamDto memberTeamDto : result) {
            System.out.println("memberTeamDto = " + memberTeamDto);
        }
    }
}