package jpa.queryDsl.repository;

import jpa.queryDsl.entity.Member;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
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
}