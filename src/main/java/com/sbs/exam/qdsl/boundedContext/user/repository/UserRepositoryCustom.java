package com.sbs.exam.qdsl.boundedContext.user.repository;

import com.sbs.exam.qdsl.boundedContext.user.entity.SiteUser;

public interface UserRepositoryCustom {
  SiteUser getQslUser(Long id);
}
