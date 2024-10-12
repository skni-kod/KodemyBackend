package pl.sknikod.kodemyauth.infrastructure.database;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PermissionRepository extends JpaRepository<Permission, Long> {
  }