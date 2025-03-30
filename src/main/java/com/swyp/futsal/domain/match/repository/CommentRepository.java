package com.swyp.futsal.domain.match.repository;

import com.swyp.futsal.domain.match.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, String> {
} 