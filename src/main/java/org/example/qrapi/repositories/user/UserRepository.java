package org.example.qrapi.repositories.user;

import org.example.qrapi.model.user.Role;
import org.example.qrapi.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);

    List<User> findAllByRoles(List<Role> roles);
}
