package com.culturemate.culturemate_api.service;

import com.culturemate.culturemate_api.domain.community.Board;
import com.culturemate.culturemate_api.domain.community.Comment;
import com.culturemate.culturemate_api.domain.community.CommentLike;
import com.culturemate.culturemate_api.domain.member.Member;
import com.culturemate.culturemate_api.dto.CommentDto;
import com.culturemate.culturemate_api.repository.CommentLikeRepository;
import com.culturemate.culturemate_api.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {
  private final CommentRepository commentRepository;
  private final CommentLikeRepository commentLikeRepository;
  private final BoardService boardService;
  private final MemberService memberService;

  // 댓글 생성
  @Transactional
  public Comment create(Long boardId, Long parentId, String content) {
    Board board = boardService.findById(boardId);
    Comment parent = null;
    if (parentId != null) {
      parent = commentRepository.findById(parentId)
        .orElseThrow(() -> new IllegalArgumentException("부모 댓글이 존재하지 않습니다."));
    }

    Comment comment = Comment.builder()
      .board(board)
      .parent(parent)
      .content(content)
      .likeCount(0)
//      .dislikeCount(0)
      .build();

    return commentRepository.save(comment);
  }

  @Transactional
  public Comment update(Long commentId, String content) {
    Comment comment = commentRepository.findById(commentId)
      .orElseThrow(() -> new IllegalArgumentException("댓글이 존재하지 않습니다."));

    comment.setContent(content);
    return comment;
  }


  // 특정 게시글 댓글 조회
  public List<Comment> findByBoard(Long boardId) {
    return commentRepository.findByBoardIdOrderByCreatedAtDesc(boardId);
  }

  // 특정 댓글의 대댓글 조회
  public List<Comment> findReplies(Long parentId) {
    return commentRepository.findByParentId(parentId);
  }

  // 삭제
  @Transactional
  public void delete(Long commentId) {
    commentRepository.deleteById(commentId);
  }

  @Transactional
  public boolean toggleCommentLike(Long commentId, Long memberId) {
    Comment comment = commentRepository.findById(commentId)
      .orElseThrow(() -> new IllegalArgumentException("댓글이 존재하지 않습니다."));
    Member member = memberService.findById(memberId);  // MemberService에서 조회

    Optional<CommentLike> existing = commentLikeRepository.findByCommentAndMember(comment, member);

    if (existing.isPresent()) {
      // 이미 좋아요 눌렀으면 취소
      commentLikeRepository.delete(existing.get());
      comment.setLikeCount(comment.getLikeCount() - 1);
      return false; // 취소됨
    } else {
      // 좋아요 추가
      CommentLike commentLike = CommentLike.builder()
        .comment(comment)
        .member(member)
        .build();
      commentLikeRepository.save(commentLike);
      comment.setLikeCount(comment.getLikeCount() + 1);
      return true; // 좋아요 성공
    }
  }

  // 싫어요
//  public CommentDto dislikeComment(Long commentId) {
//    Comment comment = commentRepository.findById(commentId)
//      .orElseThrow(() -> new IllegalArgumentException("댓글이 존재하지 않습니다."));
//    comment.getDislikeCount();
//    return CommentDto.fromEntity(comment);
//  }
}
