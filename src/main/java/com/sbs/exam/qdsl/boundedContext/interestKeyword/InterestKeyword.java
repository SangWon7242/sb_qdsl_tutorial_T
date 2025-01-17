package com.sbs.exam.qdsl.boundedContext.interestKeyword;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InterestKeyword {
  @Id
  private String content;
}
