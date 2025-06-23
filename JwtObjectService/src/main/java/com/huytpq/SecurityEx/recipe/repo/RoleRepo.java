package com.huytpq.SecurityEx.recipe.repo;

import com.huytpq.SecurityEx.recipe.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepo extends JpaRepository<Role, Long> {

    Optional<Role> findByName(String name);
    @Query("SELECT r FROM Role r JOIN UserRole ur ON r.id = ur.role.id WHERE ur.user.id = :userId")
    List<Role> findRolesByUserId(@Param("userId") Long userId);

}