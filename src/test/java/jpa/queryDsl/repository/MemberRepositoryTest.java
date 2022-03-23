package jpa.queryDsl.repository;

import jpa.queryDsl.dto.MemberTeamDto;
import jpa.queryDsl.dto.SearchCond;
import jpa.queryDsl.entity.Member;
import jpa.queryDsl.entity.QMember;
import jpa.queryDsl.entity.Team;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class MemberRepositoryTest {

    @Autowired EntityManager em;
    @Autowired MemberRepository memberRepository;

    @Test
    public void basicTest() throws Exception {
        // given
        Member member1 = new Member("member1", 10);
        memberRepository.save(member1);

        // when
        Optional<Member> findMember = memberRepository.findById(member1.getId());
        List<Member> all = memberRepository.findAll();
        List<Member> all2 = memberRepository.findByUsername(member1.getUsername());

        // then
        assertThat(findMember.get().getUsername()).isEqualTo("member1");
        assertThat(all).containsExactly(member1);
        assertThat(all2).containsExactly(member1);
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
        SearchCond cond = new SearchCond("memberA", "teamA", 10, null);
        List<MemberTeamDto> result = memberRepository.search(cond);

        // then
        for (MemberTeamDto memberTeamDto : result) {
            System.out.println("memberTeamDto = " + memberTeamDto);
        }
    }

    @Test
    public void simplePageTest() throws Exception {
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
        SearchCond cond = new SearchCond(null, null , 10, null);
        PageRequest pageRequest = PageRequest.of(0, 3);// 0페이지의 사이즈 3개
        Page<MemberTeamDto> result = memberRepository.searchPageComplex(cond, pageRequest);

        // then
        for (MemberTeamDto memberTeamDto : result) {
            System.out.println("memberTeamDto = " + memberTeamDto);
        }
    }
    
    @Test
    public void queryDslPredicateExecutorTest() throws Exception {
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
        QMember member = QMember.member;
        Iterable<Member> findMember = memberRepository.findAll(member.age.between(10, 30).and(member.username.eq("memberA")));


        // then
        System.out.println("findMember = " + findMember);
    }
}