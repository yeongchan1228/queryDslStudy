package jpa.queryDsl.repository;

import jpa.queryDsl.entity.Member;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class MemberRepositoryTest {

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
}