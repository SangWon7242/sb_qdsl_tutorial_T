package com.sbs.exam.qdsl.boundedContext.user.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sbs.exam.qdsl.boundedContext.user.entity.SiteUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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
    return null;
  }
}
