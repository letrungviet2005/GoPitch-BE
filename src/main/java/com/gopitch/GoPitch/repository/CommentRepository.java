package com.gopitch.GoPitch.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.gopitch.GoPitch.domain.Comment;

import com.gopitch.GoPitch.domain.Permission;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    // Additional query methods can be defined here if needed
    boolean existsById(long id);

    boolean existsByContent(String content);

    boolean existsByContentAndIdNot(String content, Long id);

}
