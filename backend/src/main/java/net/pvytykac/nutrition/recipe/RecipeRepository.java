package net.pvytykac.nutrition.recipe;

import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
interface RecipeRepository extends JpaRepository<Recipe, UUID>, JpaSpecificationExecutor<Recipe> {

    Page<Recipe> findAllByUserId(String userId, Pageable pageable);

    Page<Recipe> findAllByUserId(String userId, Specification<Recipe> spec, Pageable pageable);

    Optional<Recipe> findByIdAndUserId(UUID id, String userId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT r FROM Recipe r WHERE r.id = :id AND r.userId = :userId")
    Optional<Recipe> findByIdAndUserIdForUpdate(@Param("id") UUID id, @Param("userId") String userId);

    boolean existsByIdAndUserId(UUID id, String userId);

}
