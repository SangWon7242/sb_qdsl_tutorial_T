package com.sbs.exam.qdsl.boundedContext.interestKeyword.entiry;

import com.sbs.exam.qdsl.boundedContext.user.entity.SiteUser;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class InterestKeywordId implements Serializable {
  private SiteUser user;
  private String content;
}
