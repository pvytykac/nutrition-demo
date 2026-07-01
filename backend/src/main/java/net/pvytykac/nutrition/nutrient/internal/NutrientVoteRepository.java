package net.pvytykac.nutrition.nutrient.internal;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

interface NutrientVoteRepository extends JpaRepository<NutrientVote, UUID> {

    boolean existsByNutrientIdAndVoterId(UUID nutrientId, String voterId);

    long countByNutrientId(UUID nutrientId);

    Optional<NutrientVote> findByNutrientIdAndVoterId(UUID nutrientId, String voterId);

}
