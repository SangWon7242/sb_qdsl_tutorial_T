package com.sbs.exam.qdsl;

import com.sbs.exam.qdsl.boundedContext.user.entity.SiteUser;
import com.sbs.exam.qdsl.boundedContext.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
// 이렇게 클래스 @Transactional를 붙이면, 클래스의 각 테스트케이스에 전부 @Transactional 붙은 것과 동일
// @Test + @Transactional 조합은 자동으로 롤백을 유발시킨다.
@Transactional
@ActiveProfiles("test") // test profile로 실행
class QdslApplicationTests {
	@Autowired
	private UserRepository userRepository;

	@Test
	@DisplayName("회원 생성")
	void t1() {
		SiteUser u3 = SiteUser.builder()
				.username("user9")
				.password("{noop}1234")
				.email("user9@test.com")
				.build();

		SiteUser u4 = SiteUser.builder()
				.username("user10")
				.password("{noop}1234")
				.email("user10@test.com")
				.build();

		userRepository.saveAll(Arrays.asList(u3, u4));
	}

	@Test
	@DisplayName("1번 회원을 Qsl로 가져오기")
	void t2() {
		SiteUser u1 = userRepository.getQslUser(1L);

		assertThat(u1.getId()).isEqualTo(1L);
		assertThat(u1.getUsername()).isEqualTo("user1");
		assertThat(u1.getEmail()).isEqualTo("user1@test.com");
		assertThat(u1.getPassword()).isEqualTo("{noop}1234");
	}

	@Test
	@DisplayName("2번 회원을 Qsl로 가져오기")
	void t3() {
		SiteUser u2 = userRepository.getQslUser(2L);

		assertThat(u2.getId()).isEqualTo(2L);
		assertThat(u2.getUsername()).isEqualTo("user2");
		assertThat(u2.getEmail()).isEqualTo("user2@test.com");
		assertThat(u2.getPassword()).isEqualTo("{noop}1234");
	}

	@Test
	@DisplayName("모든 회원 수")
	void t4() {
		long count = userRepository.getQslCount();

		assertThat(count).isGreaterThan(0); // 회원 수가 0보다 큰지 확인
	}

	@Test
	@DisplayName("가장 오래된 회원 1명")
	void t5() {
		SiteUser u1 = userRepository.getQslUserOrderByIdAscOne();

		assertThat(u1.getId()).isEqualTo(1L);
		assertThat(u1.getUsername()).isEqualTo("user1");
		assertThat(u1.getEmail()).isEqualTo("user1@test.com");
		assertThat(u1.getPassword()).isEqualTo("{noop}1234");
	}

	@Test
	@DisplayName("전체회원, 오래된 순")
	void t6() {
		List<SiteUser> users = userRepository.getQslUsersOrderByIdAsc();

		SiteUser u1 = users.get(0);

		assertThat(u1.getId()).isEqualTo(1L);
		assertThat(u1.getUsername()).isEqualTo("user1");
		assertThat(u1.getEmail()).isEqualTo("user1@test.com");
		assertThat(u1.getPassword()).isEqualTo("{noop}1234");

		SiteUser u2 = users.get(1);

		assertThat(u2.getId()).isEqualTo(2L);
		assertThat(u2.getUsername()).isEqualTo("user2");
		assertThat(u2.getEmail()).isEqualTo("user2@test.com");
		assertThat(u2.getPassword()).isEqualTo("{noop}1234");
	}

	@Test
	@DisplayName("검색, List 리턴")
	void t7() {
		// 검색대상 : username, email
		// user1 로 검색
		List<SiteUser> users = userRepository.searchQsl("user1");
		assertThat(users.size()).isEqualTo(1);
		SiteUser u = users.get(0);
		assertThat(u.getId()).isEqualTo(1L);
		assertThat(u.getUsername()).isEqualTo("user1");
		assertThat(u.getEmail()).isEqualTo("user1@test.com");
		assertThat(u.getPassword()).isEqualTo("{noop}1234");

		// user2 로 검색
		users = userRepository.searchQsl("user2");
		assertThat(users.size()).isEqualTo(1);
		u = users.get(0);
		assertThat(u.getId()).isEqualTo(2L);
		assertThat(u.getUsername()).isEqualTo("user2");
		assertThat(u.getEmail()).isEqualTo("user2@test.com");
		assertThat(u.getPassword()).isEqualTo("{noop}1234");
	}

	@Test
	@DisplayName("검색, Page 리턴, id ASC, pageSize=1, page=0")
	void t8() {
		long totalCount = userRepository.count();
		int pageSize = 1; // 한 페이지에 보여줄 아이템 개수
		int totalPages = (int)Math.ceil(totalCount / (double)pageSize);
		int page = 1;
		String kw = "user";

		List<Sort.Order> sorts = new ArrayList<>();
		sorts.add(Sort.Order.asc("id"));
		Pageable pageable = PageRequest.of(page, pageSize, Sort.by(sorts)); // 한 페이지에 10까지 가능
		Page<SiteUser> usersPage = userRepository.searchQsl(kw, pageable);

		assertThat(usersPage.getTotalPages()).isEqualTo(totalPages);
		assertThat(usersPage.getNumber()).isEqualTo(page);
		assertThat(usersPage.getSize()).isEqualTo(pageSize);

		List<SiteUser> users = usersPage.get().toList();
		assertThat(users.size()).isEqualTo(pageSize);
		SiteUser u = users.get(0);
		assertThat(u.getId()).isEqualTo(2L);
		assertThat(u.getUsername()).isEqualTo("user2");
		assertThat(u.getEmail()).isEqualTo("user2@test.com");
		assertThat(u.getPassword()).isEqualTo("{noop}1234");

		// 검색어 : user1
		// 한 페이지에 나올 수 있는 아이템 수 : 1개
		// 현재 페이지 : 1
		// 정렬 : id 역순
		// 내용 가져오는 SQL
        /*
        SELECT site_user.*
        FROM site_user
        WHERE site_user.username LIKE '%user%'
        OR site_user.email LIKE '%user%'
        ORDER BY site_user.id ASC
        LIMIT 1, 1
         */
		
				// 쿼리가 두번 실행됨
				// 전체 개수 계산하는 SQL
        /*
        SELECT COUNT(*)
        FROM site_user
        WHERE site_user.username LIKE '%user%'
        OR site_user.email LIKE '%user%'
         */
	}

	@Test
	@DisplayName("검색, Page 리턴, id DESC, pageSize=1, page=0")
	void t9() {
		long totalCount = userRepository.count();
		int pageSize = 1; // 한 페이지에 보여줄 아이템 개수
		int totalPages = (int)Math.ceil(totalCount / (double)pageSize);
		int page = 1;
		String kw = "user";

		List<Sort.Order> sorts = new ArrayList<>();
		sorts.add(Sort.Order.desc("id"));
		Pageable pageable = PageRequest.of(page, pageSize, Sort.by(sorts)); // 한 페이지에 10까지 가능
		Page<SiteUser> usersPage = userRepository.searchQsl(kw, pageable);

		assertThat(usersPage.getTotalPages()).isEqualTo(totalPages);
		assertThat(usersPage.getNumber()).isEqualTo(page);
		assertThat(usersPage.getSize()).isEqualTo(pageSize);

		List<SiteUser> users = usersPage.get().toList();
		assertThat(users.size()).isEqualTo(pageSize);

		SiteUser u = users.get(0);
		assertThat(u.getId()).isEqualTo(7L);
		assertThat(u.getUsername()).isEqualTo("user7");
		assertThat(u.getEmail()).isEqualTo("user7@test.com");
		assertThat(u.getPassword()).isEqualTo("{noop}1234");
	}

	@Test
	@DisplayName("회원에게 관심사를 등록할 수 있다.")
	@Rollback(false)
	void t10() {
		SiteUser u2 = userRepository.getQslUser(2L);
		u2.addInterestKeywordContent("테니스");
		u2.addInterestKeywordContent("오버워치");
		u2.addInterestKeywordContent("축구");
		u2.addInterestKeywordContent("헬스"); // 중복등록은 무시
		u2.addInterestKeywordContent("헬스");

		userRepository.save(u2);
		// 엔티티클래스 : InterestKeyword(interest_keyword 테이블)
		// 중간테이블도 생성되어야 함, 힌트 : @ManyToMany
		// interest_keyword 테이블에 축구, 롤, 헬스에 해당하는 row 3개 생성
	}

	@Test
	@DisplayName("야구에 관심이 있는 회원들 검색")
	void t11() {
		List<SiteUser> users = userRepository.getQslUsersByInterestKeyword("야구");

		assertThat(users.size()).isEqualTo(1);
		SiteUser u = users.get(0);
		assertThat(u.getId()).isEqualTo(1L);
		assertThat(u.getUsername()).isEqualTo("user1");
		assertThat(u.getEmail()).isEqualTo("user1@test.com");
		assertThat(u.getPassword()).isEqualTo("{noop}1234");
	}

	@Test
	@DisplayName("no qsl, 클라이밍에 관심이 있는 회원들 검색")
	void t12() {
		List<SiteUser> users = userRepository.findByInterestKeywords_content("클라이밍");

		assertThat(users.size()).isEqualTo(1);
		SiteUser u = users.get(0);
		assertThat(u.getId()).isEqualTo(2L);
		assertThat(u.getUsername()).isEqualTo("user2");
		assertThat(u.getEmail()).isEqualTo("user2@test.com");
		assertThat(u.getPassword()).isEqualTo("{noop}1234");
	}

	@Test
	@DisplayName("u2=아이돌, u1=팬 u1은 u2의 팔로워 이다.")
	void t13() {
		SiteUser u1 = userRepository.getQslUser(1L);
		SiteUser u2 = userRepository.getQslUser(2L);

		u1.follow(u2); // u1은 u2를 팔로우(구독) 한다.

		userRepository.save(u2);
	}

	@Test
	@DisplayName("본인이 본인을 follow 할 수 없다.")
	@Rollback(false)
	void t14() {
		SiteUser u1 = userRepository.getQslUser(1L);
		u1.follow(u1);
		assertThat(u1.getFollowers().size()).isEqualTo(0);
	}

	@Test
	@DisplayName("특정회원의 follower들과 following들을 모두 알 수 있어야 한다.")
	void t15() {
		SiteUser u1 = userRepository.getQslUser(1L);
		SiteUser u2 = userRepository.getQslUser(2L);

		u1.follow(u2);
		// follower
		// u1의 구독자 : 0
		assertThat(u1.getFollowers().size()).isEqualTo(0);

		// follower
		// u2의 구독자 : 1
		assertThat(u2.getFollowers().size()).isEqualTo(1);

		// following
		// u1이 구독중인 회원 : 1
		assertThat(u1.getFollowings().size()).isEqualTo(1);

		// following
		// u2가 구독중인 회원 : 0
		assertThat(u2.getFollowings().size()).isEqualTo(0);
	}

	@Test
	@DisplayName("u1은 더 이상 야구에 관심이 없습니다.")
	@Rollback(false)
	void t16() {
		SiteUser u1 = userRepository.getQslUser(1L);
		
		u1.removeInterestKeywordContent("야구");
	}
}
