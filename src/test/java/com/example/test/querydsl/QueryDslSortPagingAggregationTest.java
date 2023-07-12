package com.example.test.querydsl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.transaction.annotation.Transactional;

import com.example.test.entity.ClubMember;
import com.example.test.entity.QClubMember;
import com.example.test.entity.QTeam;
import com.example.test.entity.Team;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;

@SpringBootTest
@Transactional
class QueryDslSortPagingAggregationTest {

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
	@DisplayName("나이가 24살 이상인 멤버들을 추출. 나이순으로 내림차순, 이름순으로 오름차순, 이름이 없으면 뒤에 출력")
	@Disabled
	void sort() {
		em.persist(ClubMember.builder().username(null).age(24).team(null).build());
		em.persist(ClubMember.builder().username("Lee-seoungWoo").age(24).team(null).build());
		em.persist(ClubMember.builder().username("Lee-KangIn").age(21).team(null).build());
		em.persist(ClubMember.builder().username("Son-heoungMin").age(30).team(null).build());
		
		QClubMember clubMember = QClubMember.clubMember; 
		
		List<ClubMember> memberList = queryFactory
				.select(clubMember)
				.from(clubMember)
				.where(clubMember.age.goe(24))
				.orderBy(clubMember.age.desc(), clubMember.username.asc().nullsLast())
				.fetch();
		
		for (ClubMember cm : memberList) System.out.println(cm);
		
		assertEquals("Vandijk"      , memberList.get(0).getUsername());
		assertEquals("Son-heoungMin", memberList.get(1).getUsername());
		assertEquals("Salah"        , memberList.get(2).getUsername());
	}// sort
	
	@Test
	@Disabled
	void pagingTest() {
		for(int i=1; i<=500; i++) em.persist(ClubMember.builder().username("PagingTestMember_" + i).age(50).team(null).build());
		
		PageRequest pageRequest = PageRequest.of(1, 10, Sort.by(Direction.DESC, "id"));
		
		QClubMember clubMember = QClubMember.clubMember;
		
		int totalCount = queryFactory
				.select(clubMember)
				.from(clubMember)
				.where(
						clubMember.username.startsWith("PagingTestMember_")
				)
				.fetch().size();
		
		List<ClubMember> pagingList = queryFactory
				.select(clubMember)
				.from(clubMember)
				.where(
						clubMember.username.startsWith("PagingTestMember_")
				)
				.offset(pageRequest.getOffset())
				.limit(pageRequest.getPageSize())
				.orderBy(clubMember.id.desc())
				.fetch();
		
		System.out.println("totalCount : " + totalCount);
		
		for (ClubMember cm : pagingList) {
			System.out.println(cm);
		}// for
	}// pagingTest
	
	@Test
	@DisplayName("각 팀별로 선수 수를 구해라.")
	void groupByTest() {
		
		QClubMember clubMember = QClubMember.clubMember;
		QTeam       team       = QTeam.team;
		
		List<Tuple> groupByResult = queryFactory
				.select(team.name, clubMember.count())
				.from(clubMember)
				.join(clubMember.team, team)
				.groupBy(team.name)
				.fetch();
		
		for (Tuple tuple : groupByResult) {
			String  teamName = tuple.get(team.name);
			Long    count    = tuple.get(clubMember.count());
			
			System.out.println("teamName : " + teamName + ", count : " + count);
		}// for
	}// groupByTest
	
}// QueryDslSortPagingAggregationTest





























