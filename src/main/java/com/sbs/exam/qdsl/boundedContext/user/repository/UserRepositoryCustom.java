package com.sbs.exam.qdsl.boundedContext.user.repository;

import com.sbs.exam.qdsl.boundedContext.user.entity.SiteUser;

import java.util.List;

public interface UserRepositoryCustom {
  SiteUser getQslUser(Long id);

  long getQslCount();

  SiteUser getQslUserOrderByIdAscOne();

  List<SiteUser> getQslUsersOrderByIdAsc();
}
