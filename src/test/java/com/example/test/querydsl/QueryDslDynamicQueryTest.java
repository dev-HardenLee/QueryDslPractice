package com.example.test.querydsl;

import java.util.List;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.example.test.entity.ClubMember;
import com.example.test.entity.QClubMember;
import com.example.test.entity.Team;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

@SpringBootTest
@Transactional
class QueryDslDynamicQueryTest {

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
	@Disabled
	void dynamicQuery_BooleanBuilder() {
		String  username = "Bruno";
		Integer age      = 28;
		
		QClubMember clubMember = QClubMember.clubMember;
		
		List<ClubMember> memberList = queryFactory
				.select(clubMember)
				.from(clubMember)
				.where(searchClubMember(clubMember, username, age))
				.fetch();
		
		for (ClubMember cm : memberList) {
			System.out.println(cm);
		}// for
	}// dynamicQuery_BooleanBuilder

	private BooleanBuilder searchClubMember(QClubMember clubMember, String username, Integer age) {
		BooleanBuilder builder = new BooleanBuilder();
		
		if(username != null) builder.and(clubMember.username.eq(username));		
		if(age      != null) builder.and(clubMember.age.eq(age));
		
		return builder;
	}// searchClubMember
	
	@Test
	void dynamicQuery_WhereMultiParameter() {
		String  username = "Bruno";
		Integer age      = 28;
		
		QClubMember clubMember = QClubMember.clubMember;
		
		List<ClubMember> memberList = queryFactory
				.select(clubMember)
				.from(clubMember)
				.where(usernameEq(username), ageEq(age))
				.fetch();
		
		for (ClubMember cm : memberList) {
			System.out.println(cm);
		}// for
	}// dynamicQuery_WhereMultiParameter
	
	private BooleanExpression usernameEq(String username) {
		if(username == null) return null;
		
		return QClubMember.clubMember.username.eq(username);
	}// usernameEq
	 
	private BooleanExpression ageEq(Integer age) {
		if(age == null) return null;
		
		return QClubMember.clubMember.age.eq(age);
	}// ageEq
	
}// QueryDslBooleanBuilderTest














