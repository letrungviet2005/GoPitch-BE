package com.gopitch.GoPitch.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.gopitch.GoPitch.domain.Comment;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    boolean existsById(long id);

    boolean existsByContent(String content);

    boolean existsByContentAndIdNot(String content, Long id);

}
