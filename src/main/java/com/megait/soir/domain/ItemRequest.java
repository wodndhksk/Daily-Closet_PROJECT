package com.megait.soir.domain;

import lombok.*;

@Getter @Setter
@AllArgsConstructor
/**
 * 베스트 아이템 조회하기 위한 Request 객체
 */
public class ItemRequest {
  @Builder.Default
  private String categoryName = "best";
  @Builder.Default
  private int limit = 20;
  @Builder.Default
  private String sort = "name";
  @Builder.Default
  private int page = 0;

}
