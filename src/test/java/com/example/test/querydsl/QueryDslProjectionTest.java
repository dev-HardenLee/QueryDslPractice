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
import com.example.test.projection.ClubMemberDTO;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

@SpringBootTest
@Transactional
class QueryDslProjectionTest {
	
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
	void simpleProjection() {
		QClubMember clubMember = QClubMember.clubMember;
		
		List<String> userNameList = queryFactory
				.select(clubMember.username)
				.from(clubMember)
				.fetch();
		
		for (String userName : userNameList) {
			System.out.println("userName : " + userName);
		}// for
	}// simpleProjection
	
	@Test
	@Disabled
	void tupleProjection() {
		QClubMember clubMember = QClubMember.clubMember;
		
		List<Tuple> userNameList = queryFactory
				.select(clubMember.username, clubMember.age)
				.from(clubMember)
				.fetch();
		
		for (Tuple tuple : userNameList) {
			String  username = tuple.get(clubMember.username);
			Integer age      = tuple.get(clubMember.age     );
			
			System.out.println("username : " + username + ", age : " + age);
		}// for
	}// tupleProjection
	
	@Test
	@Disabled
	void findDTOBySetter() {
		QClubMember clubMember = QClubMember.clubMember;
		
		List<ClubMemberDTO> dtoList = queryFactory
				.select(Projections.bean(
						  ClubMemberDTO.class
						, clubMember.username
						, clubMember.age
				))
				.from(clubMember)
				.fetch();
		
		for (ClubMemberDTO clubMemberDTO : dtoList) {
			System.out.println(clubMemberDTO);
		}// for
	}// findDTOByJPQL
	
	@Test
	@Disabled
	void findDTOByField() {
		QClubMember clubMember = QClubMember.clubMember;
		
		// Getter,Setter is not neccessary
		
		List<ClubMemberDTO> dtoList = queryFactory
				.select(Projections.fields(
						  ClubMemberDTO.class
						, clubMember.username
						, clubMember.age
				))
				.from(clubMember)
				.fetch();
		
		for (ClubMemberDTO clubMemberDTO : dtoList) {
			System.out.println(clubMemberDTO);
		}// for
	}// findDTOByJPQL
	
	@Test
	@Disabled
	void findDTOByConstructor() {
		QClubMember clubMember = QClubMember.clubMember;

		List<ClubMemberDTO> dtoList = queryFactory
				.select(Projections.constructor(
						  ClubMemberDTO.class
						, clubMember.username
						, clubMember.age
				))
				.from(clubMember)
				.fetch();
		
		for (ClubMemberDTO clubMemberDTO : dtoList) {
			System.out.println(clubMemberDTO);
		}// for
	}// findDTOByConstructor

}// QueryDslProjectionTest



























