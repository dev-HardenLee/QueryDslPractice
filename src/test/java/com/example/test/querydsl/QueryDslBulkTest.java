package com.example.test.querydsl;

import java.util.List;

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
class QueryDslBulkTest {

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
	@DisplayName("모든 회원의 나이를 +1해라")
	void bulkUpdate_age() {
		em.flush();
		em.clear();
		
		QClubMember clubMember = QClubMember.clubMember;
		
		long count = queryFactory
				.update(clubMember)
				.set(clubMember.age, clubMember.age.add(1))
				.execute();
		
		System.out.println("count : " + count);
		
		List<ClubMember> memberList = queryFactory
				.select(clubMember)
				.from(clubMember)
				.fetch();
		
		for (ClubMember cm : memberList) {
			System.out.println(cm);
		}// for
	}// bulkUpdate_age
	
}// QueryDslBulkTest


























