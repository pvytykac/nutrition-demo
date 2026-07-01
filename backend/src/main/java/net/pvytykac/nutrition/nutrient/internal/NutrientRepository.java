package net.pvytykac.nutrition.nutrient.internal;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

interface NutrientRepository extends JpaRepository<Nutrient, UUID>, JpaSpecificationExecutor<Nutrient> {

    boolean existsByName(String name);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT n FROM Nutrient n WHERE n.id = :id")
    Optional<Nutrient> findByIdWithLock(@Param("id") UUID id);

}
