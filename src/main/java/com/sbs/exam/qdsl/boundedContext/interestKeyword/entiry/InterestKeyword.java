package com.sbs.exam.qdsl.boundedContext.interestKeyword.entiry;

import com.sbs.exam.qdsl.boundedContext.user.entity.SiteUser;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.ManyToOne;
import lombok.*;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@IdClass(InterestKeywordId.class)
public class InterestKeyword {

  @Id
  @ManyToOne
  @EqualsAndHashCode.Include
  private SiteUser user;

  @Id
  @EqualsAndHashCode.Include
  private String content;
}
