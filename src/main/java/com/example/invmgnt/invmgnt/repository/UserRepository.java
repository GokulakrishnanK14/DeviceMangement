package com.example.invmgnt.invmgnt.repository;

import com.example.invmgnt.invmgnt.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findByMail(String mail);
    Optional<User> findById(Long id);
    Page<User> findAllByOrderByNameAsc(Pageable pageable);
}
