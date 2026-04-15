// backend/src/main/java/com/hub/repository/ContactRepository.java
package com.hub.repository;

import com.hub.entity.Contact;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ContactRepository extends JpaRepository<Contact, UUID> {

    Page<Contact> findByUserId(UUID userId, Pageable pageable);

    Optional<Contact> findByIdAndUserId(UUID id, UUID userId);

    @Query("SELECT c FROM Contact c WHERE c.user.id = :userId " +
           "AND (LOWER(c.firstName) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(c.lastName) LIKE LOWER(CONCAT('%', :query, '%')))")
    Page<Contact> searchByUserIdAndName(@Param("userId") UUID userId,
                                        @Param("query") String query,
                                        Pageable pageable);

    @Query("SELECT c FROM Contact c WHERE c.user.id = :userId AND c.groupName = :groupName")
    Page<Contact> findByUserIdAndGroupName(@Param("userId") UUID userId,
                                           @Param("groupName") String groupName,
                                           Pageable pageable);

    @Query("SELECT c FROM Contact c WHERE c.user.id = :userId AND c.isFavorite = true")
    Page<Contact> findByUserIdAndFavorite(@Param("userId") UUID userId,
                                          Pageable pageable);

    @Query("SELECT c FROM Contact c WHERE c.user.id = :userId " +
           "AND c.groupName = :groupName " +
           "AND (LOWER(c.firstName) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(c.lastName) LIKE LOWER(CONCAT('%', :query, '%')))")
    Page<Contact> searchByUserIdGroupNameAndName(@Param("userId") UUID userId,
                                                 @Param("groupName") String groupName,
                                                 @Param("query") String query,
                                                 Pageable pageable);

    @Query("SELECT c FROM Contact c WHERE c.user.id = :userId " +
           "AND c.isFavorite = true " +
           "AND (LOWER(c.firstName) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(c.lastName) LIKE LOWER(CONCAT('%', :query, '%')))")
    Page<Contact> searchByUserIdFavoriteAndName(@Param("userId") UUID userId,
                                                @Param("query") String query,
                                                Pageable pageable);
}
