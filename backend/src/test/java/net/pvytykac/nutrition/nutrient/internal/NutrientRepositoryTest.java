package net.pvytykac.nutrition.nutrient.internal;

import net.pvytykac.nutrition.common.RepositoryTestBase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class NutrientRepositoryTest extends RepositoryTestBase {

    @Autowired
    private NutrientRepository nutrientRepository;

    @Autowired
    private NutrientVoteRepository nutrientVoteRepository;

    private Nutrient createNutrient(String name) {
        return Nutrient.builder()
                .name(name)
                .kcalPerGram(new BigDecimal("4.0000"))
                .defaultUnit(NutrientUnit.GRAM)
                .status(NutrientStatus.ACTIVE)
                .source(NutrientSource.SEED)
                .createdAt(Instant.now())
                .build();
    }

    @Test
    void shouldSaveAndFindNutrient() {
        var nutrient = createNutrient("TestNutrient");
        var saved = nutrientRepository.save(nutrient);

        var found = nutrientRepository.findById(saved.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("TestNutrient");
    }

    @Test
    void shouldFindByName() {
        var nutrient = createNutrient("UniqueName");
        nutrientRepository.save(nutrient);

        assertThat(nutrientRepository.existsByName("UniqueName")).isTrue();
        assertThat(nutrientRepository.existsByName("NonExistent")).isFalse();
    }

    @Test
    void shouldEnforceUniqueName() {
        var nutrient1 = createNutrient("DuplicateName");
        nutrientRepository.saveAndFlush(nutrient1);

        var nutrient2 = createNutrient("DuplicateName");

        assertThatThrownBy(() -> nutrientRepository.saveAndFlush(nutrient2))
                .isInstanceOf(Exception.class);
    }

    @Test
    void shouldSaveAndCountVotes() {
        var nutrient = createNutrient("VotableNutrient");
        nutrient = nutrientRepository.saveAndFlush(nutrient);

        var vote1 = NutrientVote.builder()
                .nutrient(nutrient)
                .voterId("user1")
                .createdAt(Instant.now())
                .build();
        nutrientVoteRepository.saveAndFlush(vote1);

        var vote2 = NutrientVote.builder()
                .nutrient(nutrient)
                .voterId("user2")
                .createdAt(Instant.now())
                .build();
        nutrientVoteRepository.saveAndFlush(vote2);

        assertThat(nutrientVoteRepository.countByNutrientId(nutrient.getId())).isEqualTo(2);
        assertThat(nutrientVoteRepository.existsByNutrientIdAndVoterId(nutrient.getId(), "user1")).isTrue();
        assertThat(nutrientVoteRepository.existsByNutrientIdAndVoterId(nutrient.getId(), "user3")).isFalse();
    }

    @Test
    void shouldEnforceUniqueVoterPerNutrient() {
        var nutrient = createNutrient("UniqueVoteNutrient");
        nutrient = nutrientRepository.saveAndFlush(nutrient);

        var vote1 = NutrientVote.builder()
                .nutrient(nutrient)
                .voterId("user1")
                .createdAt(Instant.now())
                .build();
        nutrientVoteRepository.saveAndFlush(vote1);

        var vote2 = NutrientVote.builder()
                .nutrient(nutrient)
                .voterId("user1")
                .createdAt(Instant.now())
                .build();

        assertThatThrownBy(() -> nutrientVoteRepository.saveAndFlush(vote2))
                .isInstanceOf(Exception.class);
    }

    @Test
    void shouldDeleteNutrient() {
        var nutrient = createNutrient("DeletableNutrient");
        nutrient = nutrientRepository.saveAndFlush(nutrient);
        var nutrientId = nutrient.getId();

        nutrientRepository.delete(nutrient);
        nutrientRepository.flush();

        assertThat(nutrientRepository.findById(nutrientId)).isEmpty();
    }
}
