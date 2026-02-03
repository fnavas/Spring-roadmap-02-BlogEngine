package com.fnavas.BlogEngine.repository;

import com.fnavas.BlogEngine.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
