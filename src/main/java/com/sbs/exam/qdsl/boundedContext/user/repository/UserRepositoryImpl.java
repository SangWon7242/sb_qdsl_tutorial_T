package com.sbs.exam.qdsl.boundedContext.user.repository;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sbs.exam.qdsl.boundedContext.user.entity.SiteUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

import static com.sbs.exam.qdsl.boundedContext.user.entity.QSiteUser.siteUser;

@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepositoryCustom {
  private final JPAQueryFactory jpaQueryFactory;

  @Override
  public SiteUser getQslUser(Long id) {
     /*
    SELECT *
    FROM site_user
    WHERE id = 1
    */

    return jpaQueryFactory
        .selectFrom(siteUser) // SELECT * FROM site_user
        .where(siteUser.id.eq(id)) // WHERE id = 1
        .fetchOne(); // 단일 결과 반환
  }

  @Override
  public long getQslCount() {
    return jpaQueryFactory
        .select(siteUser.count())
        .from(siteUser)
        .fetchOne();
  }

  @Override
  public SiteUser getQslUserOrderByIdAscOne() {
    return jpaQueryFactory
        .selectFrom(siteUser) // SELECT * FROM site_user
        .orderBy(siteUser.id.asc()) // ORDER BY id ASC
        .limit(1) // LIMIT 1
        .fetchOne();
  }

  @Override
  public List<SiteUser> getQslUsersOrderByIdAsc() {
    return jpaQueryFactory
        .selectFrom(siteUser) // SELECT * FROM site_user
        .orderBy(siteUser.id.asc()) // ORDER BY id ASC
        .fetch();
  }

  @Override
  public List<SiteUser> searchQsl(String kw) {
    return jpaQueryFactory
        .selectFrom(siteUser)
        .where(
            siteUser.username.contains(kw)
                .or(siteUser.email.contains(kw))
        ).fetch();
  }

  @Override
  public Page<SiteUser> searchQsl(String kw, Pageable pageable) {
    // 검색 조건
    // BooleanExpression
    // - 검색 조건을 표현하는 객체
    // - username 또는 email에 keyword가 포함되어 있는 데이터를 검색
    // containsIgnoreCase : 대소문자 구분 없이 포함 여부를 확인하는 메서드
    BooleanExpression predicate = siteUser.username.containsIgnoreCase(kw)
        .or(siteUser.email.containsIgnoreCase(kw));

    // QueryDSL로 데이터 조회
    // QueryResults : 쿼리 실행 결과와 함께 페이징을 위한 추가 정보 포함
    JPAQuery<SiteUser> usersQuery = jpaQueryFactory
        .selectFrom(siteUser) // SELECT * FROM site_user
        .where(predicate) // WHERE username LIKE '%kw%' OR email LIKE '%kw%'
        .offset(pageable.getOffset()) // 몇개를 건너 띄어야 하는지 LIMIT {1}, ? // 페이지 시작 위치
        .limit(pageable.getPageSize()); // 한페이지에 몇개가 보여야 하는지 LIMIT ?, {1} // 페이지 크기

    for (Sort.Order o : pageable.getSort()) {
      PathBuilder pathBuilder = new PathBuilder(siteUser.getType(), siteUser.getMetadata());
      usersQuery.orderBy(new OrderSpecifier(o.isAscending() ? Order.ASC : Order.DESC, pathBuilder.get(o.getProperty())));
    }

    // 결과를 Page 객체로 변환
    List<SiteUser> content = usersQuery.fetch();

    // 전체 개수 계산 쿼리
    // 페이지네이션을 구현하려면 필수로 있어야 한다.
    JPAQuery<Long> usersCountQuery = jpaQueryFactory
        .select(siteUser.count())
        .from(siteUser)
        .where(predicate);

    // PageImpl : Page 인터페이스를 구현한 클래스
    return new PageImpl<>(content, pageable, usersCountQuery.fetchOne());
  }
}
