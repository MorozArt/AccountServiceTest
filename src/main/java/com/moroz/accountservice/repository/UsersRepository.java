package com.moroz.accountservice.repository;

import com.moroz.accountservice.entity.User;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UsersRepository extends JpaRepository<User, Integer> {

    @Cacheable(cacheNames="users")
    Optional<User> findById(Integer id);

    @Modifying
    @CacheEvict(cacheNames="users", key="#p1")
    @Query(value = "update users set amount = ? where id = ?", nativeQuery = true)
    void addAmount(Long amount, Integer id);
}
