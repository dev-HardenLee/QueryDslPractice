package com.example.test.querydsl;

import java.util.List;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.example.test.entity.ClubMember;
import com.example.test.entity.QClubMember;
import com.example.test.entity.QTeam;
import com.example.test.entity.Team;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;

@SpringBootTest
@Transactional
class QueryDslJoinTest {
	
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
		
		em.flush();
		em.clear();
	}// before
	
	@Test
	@DisplayName("모든 선수들과 선수들이 소속된 팀 정보를 출력하라.")
	void joinTest1() {
		
		QClubMember clubMember = QClubMember.clubMember;
		QTeam       team       = QTeam.team;
		
		List<Tuple> memberList = queryFactory
				.select(clubMember, team)
				.from(clubMember)
				.join(clubMember.team, team)
				.fetch();
		
		for (Tuple tuple : memberList) {
			System.out.println(tuple.get(clubMember) + ", " + tuple.get(team));
		}// for
		
	}// joinTest1
	
	@Test
	@DisplayName("모든 선수들을 출력하되, 소속팀이 맨유인 팀만 팀 정보를 표시하라.")
	@Disabled
	void joinTest2() {
		QClubMember clubMember = QClubMember.clubMember;
		QTeam       team       = QTeam.team;
		
		List<Tuple> memberList = queryFactory
				.select(clubMember, team)
				.from(clubMember)
				.leftJoin(clubMember.team, team).on(team.name.eq("Manchester United"))
				.fetch();
		
		for (Tuple tuple : memberList) {
			System.out.println(tuple.get(clubMember) + ", " + tuple.get(team));
		}// for
	}// joinTest2
	
	@Test
	@DisplayName("fetch join test")
	void fetchJoinTest() {
		QClubMember clubMember = QClubMember.clubMember;
		QTeam       team       = QTeam.team;
		
		List<ClubMember> memberList = queryFactory
				.selectFrom(clubMember)
				.join(clubMember.team, team).fetchJoin()
				.fetch();
		
		for (ClubMember cm : memberList) {
			boolean isloaded = em.getEntityManagerFactory().getPersistenceUnitUtil().isLoaded(cm.getTeam());
			
			System.out.println(cm + ", isloaded : " + isloaded);
		}// foreach
		
	}// fetchJoinTest
	
}// QueryDslJoinTest


























