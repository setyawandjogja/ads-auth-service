package ads.user_management_service.repository;
import ads.user_management_service.entity.User;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    @Query("""
           SELECT u FROM User u
           JOIN FETCH u.role
           WHERE u.id = :id
           """)
    Optional<User> findByIdWithRole(@Param("id") UUID id);

    @Query("""
       SELECT u FROM User u
       JOIN FETCH u.role
       WHERE u.userName = :userName
       """)
    Optional<User> findByUserNameWithRole(@Param("userName") String userName);

    boolean existsByUserName(String userName);

    boolean existsByEmail(String email);
}