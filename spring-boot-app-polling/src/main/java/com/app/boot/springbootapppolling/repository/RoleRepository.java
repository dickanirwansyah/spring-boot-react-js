package com.app.boot.springbootapppolling.repository;

import com.app.boot.springbootapppolling.entity.Role;
import com.app.boot.springbootapppolling.entity.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long>{

    Optional<Role> findByRoleName(RoleName roleName);
}
