package org.example.qrapi.repositories.user;

import org.example.qrapi.model.user.Role;
import org.example.qrapi.model.user.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByRoleName(RoleName roleName);
}
