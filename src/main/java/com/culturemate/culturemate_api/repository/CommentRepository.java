package com.culturemate.culturemate_api.repository;

import com.culturemate.culturemate_api.domain.community.Comment;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

  // 특정 게시글(Board)에 달린 댓글 조회 (N+1 해결: ManyToOne 관계만 포함)
  @EntityGraph(attributePaths = {"board", "parent"})
  List<Comment> findByBoardId(Long boardId);

  // 부모 댓글에 달린 대댓글 조회 (N+1 해결)
  @EntityGraph(attributePaths = {"board", "parent"})
  List<Comment> findByParentId(Long parentId);

  // 최신순 정렬 (게시글 기준) (N+1 해결)
  @EntityGraph(attributePaths = {"board", "parent"})
  List<Comment> findByBoardIdOrderByCreatedAtDesc(Long boardId);

}
