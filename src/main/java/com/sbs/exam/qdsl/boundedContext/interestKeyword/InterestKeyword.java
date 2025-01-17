package com.sbs.exam.qdsl.boundedContext.interestKeyword;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

import java.util.Objects;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InterestKeyword {
  @Id
  private String content;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    InterestKeyword that = (InterestKeyword) o;
    return Objects.equals(content, that.content);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(content);
  }
}
