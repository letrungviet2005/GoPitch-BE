package com.gopitch.GoPitch.repository;

import org.springframework.stereotype.Repository;
import com.gopitch.GoPitch.domain.ImageClub;
import org.springframework.data.jpa.repository.JpaRepository;
import com.gopitch.GoPitch.domain.Comment;
import com.gopitch.GoPitch.domain.ImageClub;

@Repository
public interface ImageClubRepository extends JpaRepository<ImageClub, Long> {
    // Additional query methods can be defined here if needed
    boolean existsById(long id);

    boolean existsByUrl(String url);

    boolean existsByUrlAndIdNot(String url, Long id);

}
