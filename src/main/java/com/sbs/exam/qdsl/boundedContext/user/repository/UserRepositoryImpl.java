package com.sbs.exam.qdsl.boundedContext.user.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sbs.exam.qdsl.boundedContext.user.entity.QSiteUser;
import com.sbs.exam.qdsl.boundedContext.user.entity.SiteUser;
import lombok.RequiredArgsConstructor;

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

    QSiteUser siteUser = QSiteUser.siteUser;

    return jpaQueryFactory
        .selectFrom(siteUser) // SELECT * FROM site_user
        .where(siteUser.id.eq(id)) // WHERE id = 1
        .fetchOne(); // 단일 결과 반환
  }
}
