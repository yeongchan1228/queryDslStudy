package jpa.queryDsl.controller;

import jpa.queryDsl.entity.Member;
import jpa.queryDsl.entity.Team;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PrePersist;

@Profile("local") // 로컬 yml 일 때 동작
@Component
@RequiredArgsConstructor
public class InitMember {

    private final InitMemberService initMemberService;

    // 스프링 라이프 사이클 때문에 @PostContruct와 @Transactional은 동시에 사용 불가
    @PostConstruct
    public void init(){
        initMemberService.init();
    }

    @Component
    static class InitMemberService{
        @PersistenceContext private EntityManager em;

        @Transactional
        public void init(){
            Team teamA = new Team("teamA");
            Team teamB = new Team("teamB");

            em.persist(teamA);
            em.persist(teamB);

            for (int i = 0; i < 100; i++) {
                Team selectedTeam = i % 2 == 0 ? teamA : teamB;
                em.persist(new Member("member" + i, i, selectedTeam));
            }
        }
    }

}
