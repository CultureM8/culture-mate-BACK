package com.culturemate.culturemate_api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentRequestDto {

  Long boardId;
  Long authorId;
  Long parentId;
  String comment;

}