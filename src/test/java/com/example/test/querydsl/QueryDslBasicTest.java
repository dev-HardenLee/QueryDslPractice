package com.example.test.querydsl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.example.test.entity.ClubMember;
import com.example.test.entity.QClubMember;
import com.example.test.entity.Team;
import com.querydsl.jpa.impl.JPAQueryFactory;

@SpringBootTest
@Transactional
class QueryDslBasicTest {
	
	@Autowired
	private EntityManager em;
	
	private JPAQueryFactory queryFactory;
	
	@BeforeEach
	void before() {
		queryFactory = new JPAQueryFactory(em);
		
		Team teamA = Team.builder().name("Manchester United").build();
		Team teamB = Team.builder().name("Liverpool").build();
		
		em.persist(teamA);
		em.persist(teamB);
		
		ClubMember member1 = ClubMember.builder().username("Antony").age(25).team(teamA).build(); 
		ClubMember member2 = ClubMember.builder().username("Bruno" ).age(28).team(teamA).build();
		
		ClubMember member3 = ClubMember.builder().username("Salah"  ).age(29).team(teamB).build(); 
		ClubMember member4 = ClubMember.builder().username("Vandijk").age(31).team(teamB).build();
		
		em.persist(member1);
		em.persist(member2);
		em.persist(member3);
		em.persist(member4);
	}// before
	
	@Test
	@DisplayName("JPQL을 사용하여 안토니를 찾아라!")
	void findAntony1() {
		ClubMember findByJPQL = em.createQuery("SELECT m FROM ClubMember m WHERE 1=1 AND m.username = :username", ClubMember.class)
				.setParameter("username", "Antony")
				.getSingleResult();
		
		// JPQL은 직접 String 형식으로 SQL문을 작성해야한다.
		// JPQL은 기본적으로 런타임 시에 오류가 발생했는지 알 수 있다.
		
		assertEquals("Antony", findByJPQL.getUsername());
	}// test
	
	@Test
	@DisplayName("QueryDsl을 사용하여 안토니를 찾아라!")
	void findAntony2() {
		QClubMember clubMember = QClubMember.clubMember;
		
		ClubMember findByQueryDsl = queryFactory
				.select(clubMember)
				.from(clubMember)
				.where(clubMember.username.eq("Antony"))
				.fetchOne();
		
		assertEquals("Antony", findByQueryDsl.getUsername());
	}// findAntony2
	
	@Test
	@DisplayName("QueryDsl을 사용하여 안토니를 찾아라!")
	void findAntony3() {
		QClubMember clubMember = QClubMember.clubMember;
		
		ClubMember findByQueryDsl = queryFactory
				.select(clubMember)
				.from(clubMember)
				.where(
						clubMember.username.eq("Antony"),
						clubMember.age.eq(25)
				)
				.fetchOne();
		
		assertEquals("Antony", findByQueryDsl.getUsername());
	}// findAntony2
	
}// QueryDslBasicTest






















