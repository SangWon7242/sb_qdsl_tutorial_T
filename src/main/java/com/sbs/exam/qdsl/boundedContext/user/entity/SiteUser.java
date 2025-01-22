package com.sbs.exam.qdsl.boundedContext.user.entity;

import com.sbs.exam.qdsl.boundedContext.interestKeyword.InterestKeyword;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SiteUser {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(unique = true)
  private String username;

  private String password;

  @Column(unique = true)
  private String email;

  @Builder.Default
  @ManyToMany(cascade = CascadeType.ALL)
  private Set<InterestKeyword> interestKeywords = new HashSet<>();

  @Builder.Default
  @ManyToMany(cascade = CascadeType.ALL)
  private Set<SiteUser> followers = new HashSet<>();

  public void addInterestKeywordContent(String keywordContent) {
    interestKeywords.add(new InterestKeyword(keywordContent));
  }

  public void follow(SiteUser following) {
    if (this == following) return;
    if (following == null) return;
    if (this.getId() == following.getId()) return;

    following.getFollowers().add(this);
  }

  public Set<Object> getFollowings() {
    return new HashSet<>();
  }
}
