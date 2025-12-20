package com.coursivo.coursivo_backend.repository;

import com.coursivo.coursivo_backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
