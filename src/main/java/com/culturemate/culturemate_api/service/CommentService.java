package com.culturemate.culturemate_api.service;

import com.culturemate.culturemate_api.domain.community.Comment;
import com.culturemate.culturemate_api.domain.community.CommentLike;
import com.culturemate.culturemate_api.domain.member.Member;
import com.culturemate.culturemate_api.dto.CommentDto;
import com.culturemate.culturemate_api.repository.BoardRepository;
import com.culturemate.culturemate_api.repository.CommentLikeRepository;
import com.culturemate.culturemate_api.repository.CommentRepository;
import com.culturemate.culturemate_api.repository.MemberRepository;
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
  private final BoardRepository boardRepository;
  private final MemberRepository memberRepository;

  // 댓글 생성
  public CommentDto createComment(Long boardId, Long parentId, String content) {
    var board = boardRepository.findById(boardId)
      .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));
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

    return CommentDto.fromEntity(commentRepository.save(comment));
  }

  @Transactional
  public CommentDto updateComment(Long commentId, String content) {
    Comment comment = commentRepository.findById(commentId)
      .orElseThrow(() -> new IllegalArgumentException("댓글이 존재하지 않습니다."));

    comment.setContent(content);
    return CommentDto.fromEntity(comment);
  }


  // 특정 게시글 댓글 조회
  public List<CommentDto> getCommentsByBoard(Long boardId) {
    return commentRepository.findByBoardIdOrderByCreatedAtDesc(boardId)
      .stream()
      .map(CommentDto::fromEntity)
      .toList();
  }

  // 특정 댓글의 대댓글 조회
  public List<CommentDto> getReplies(Long parentId) {
    return commentRepository.findByParentId(parentId)
      .stream()
      .map(CommentDto::fromEntity)
      .toList();
  }

  // 삭제
  public void deleteComment(Long commentId) {
    commentRepository.deleteById(commentId);
  }

  @Transactional
  public boolean toggleCommentLike(Long commentId, Long memberId) {
    Comment comment = commentRepository.findById(commentId)
      .orElseThrow(() -> new IllegalArgumentException("댓글이 존재하지 않습니다."));
    Member member = memberRepository.findById(memberId)
      .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));

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
