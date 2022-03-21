package jpa.queryDsl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jpa.queryDsl.hello.entity.Hello;
import jpa.queryDsl.hello.entity.QHello;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class QueryDslApplicationTests {

	@Autowired  EntityManager em;

	@Test
	void contextLoads() {
		Hello hello = new Hello();
		em.persist(hello);

		JPAQueryFactory query = new JPAQueryFactory(em);
		QHello qHello = QHello.hello;

		Hello result = query.selectFrom(qHello).fetchOne();
		assertThat(result).isEqualTo(hello);
		assertThat(result.getId()).isEqualTo(1);


	}

}
