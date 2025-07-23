package com.gopitch.GoPitch.repository;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.gopitch.GoPitch.domain.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    public static final String USER_NOT_FOUND = "User not found";
    public static final String USER_ALREADY_EXISTS = "User already exists with this email";
    public static final String USER_DELETED = "User deleted successfully";
    // Define methods for user-related database operations here
    // For example:
    // Optional<User> findByEmail(String email);
    // List<User> findAll();
    // User save(User user);
    // void deleteById(Long id);

}
