package ru.sf.personalfinancemanagementsystem.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.sf.personalfinancemanagementsystem.domains.User;
import ru.sf.personalfinancemanagementsystem.entities.UserEntity;

import java.util.Optional;
import java.util.UUID;


@Repository
public interface UserRepository extends JpaRepository<UserEntity, UUID> {

    @Query(value = """
            select id
                 , login
                 , password_hash
            from users
            where login = :login
            """, nativeQuery = true)
    Optional<UserEntity> findByLogin(String login);

}
