package net.pvytykac.nutrition.nutrient.internal;

import net.pvytykac.nutrition.common.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NutrientServiceTest {

    @Mock
    private NutrientRepository nutrientRepository;

    @Mock
    private NutrientVoteRepository nutrientVoteRepository;

    @InjectMocks
    private NutrientService nutrientService;

    @Captor
    private ArgumentCaptor<Nutrient> nutrientCaptor;

    private Nutrient createActiveNutrient(UUID id, String name) {
        return Nutrient.builder()
                .id(id)
                .name(name)
                .kcalPerGram(new BigDecimal("4.0000"))
                .defaultUnit(NutrientUnit.GRAM)
                .status(NutrientStatus.ACTIVE)
                .source(NutrientSource.ADMIN)
                .authorId("admin")
                .createdAt(Instant.now())
                .build();
    }

    private Nutrient createSuggestedNutrient(UUID id, String name) {
        return Nutrient.builder()
                .id(id)
                .name(name)
                .kcalPerGram(new BigDecimal("2.0000"))
                .defaultUnit(NutrientUnit.GRAM)
                .status(NutrientStatus.SUGGESTED)
                .source(NutrientSource.SUGGESTION)
                .authorId("user1")
                .createdAt(Instant.now())
                .build();
    }

    @Nested
    class CreateNutrient {

        @Test
        void shouldCreateNutrientSuccessfully() {
            var request = new NutrientRequestDTO("Vitamin C", new BigDecimal("0.5000"), NutrientUnit.MILLIGRAM);

            when(nutrientRepository.existsByName("Vitamin C")).thenReturn(false);
            when(nutrientRepository.save(any())).thenAnswer(invocation -> {
                var n = invocation.<Nutrient>getArgument(0);
                n.setId(UUID.randomUUID());
                return n;
            });

            var response = nutrientService.createNutrient(request, "admin");

            assertThat(response.getName()).isEqualTo("Vitamin C");
            assertThat(response.getStatus()).isEqualTo(NutrientStatus.ACTIVE);
            assertThat(response.getSource()).isEqualTo(NutrientSource.ADMIN);
            assertThat(response.getAuthorId()).isEqualTo("admin");
            assertThat(response.getDefaultUnit()).isEqualTo(NutrientUnit.MILLIGRAM);
        }

        @Test
        void shouldThrowWhenDuplicateName() {
            var request = new NutrientRequestDTO("Protein", new BigDecimal("4.0000"), NutrientUnit.GRAM);

            when(nutrientRepository.existsByName("Protein")).thenReturn(true);

            assertThatThrownBy(() -> nutrientService.createNutrient(request, "admin"))
                    .isInstanceOf(DuplicateNutrientNameException.class);
        }
    }

    @Nested
    class FindAllNutrients {

        @Test
        void shouldReturnAllActiveNutrients() {
            var nutrient = createActiveNutrient(UUID.randomUUID(), "Protein");
            var page = new PageImpl<>(List.of(nutrient));

            when(nutrientRepository.findAll(any(Specification.class), any(PageRequest.class)))
                    .thenReturn(page);

            var result = nutrientService.findAllNutrients(null, PageRequest.of(0, 20));

            assertThat(result).hasSize(1);
            assertThat(result.getContent().getFirst().getName()).isEqualTo("Protein");
        }

        @Test
        void shouldFilterByName() {
            var nutrient = createActiveNutrient(UUID.randomUUID(), "Protein");
            var page = new PageImpl<>(List.of(nutrient));

            when(nutrientRepository.findAll(any(Specification.class), any(PageRequest.class)))
                    .thenReturn(page);

            var result = nutrientService.findAllNutrients("pro", PageRequest.of(0, 20));

            assertThat(result).hasSize(1);
        }

        @Test
        void shouldSortByNameDesc() {
            var protein = createActiveNutrient(UUID.randomUUID(), "Protein");
            var carbs = createActiveNutrient(UUID.randomUUID(), "Carbohydrates");
            var page = new PageImpl<>(List.of(protein, carbs));

            var sort = Sort.by(Sort.Direction.DESC, "name");
            when(nutrientRepository.findAll(any(Specification.class), any(PageRequest.class)))
                    .thenReturn(page);

            var result = nutrientService.findAllNutrients(null, PageRequest.of(0, 20, sort));

            assertThat(result).hasSize(2);
        }
    }

    @Nested
    class FindNutrientById {

        @Test
        void shouldReturnActiveNutrient() {
            var id = UUID.randomUUID();
            var nutrient = createActiveNutrient(id, "Protein");

            when(nutrientRepository.findById(id)).thenReturn(Optional.of(nutrient));

            var response = nutrientService.findNutrientById(id);

            assertThat(response.getName()).isEqualTo("Protein");
        }

        @Test
        void shouldThrowWhenNotFound() {
            var id = UUID.randomUUID();

            when(nutrientRepository.findById(id)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> nutrientService.findNutrientById(id))
                    .isInstanceOf(ResourceNotFoundException.class);
        }

        @Test
        void shouldThrowWhenNotActive() {
            var id = UUID.randomUUID();
            var nutrient = createSuggestedNutrient(id, "Vitamin C");

            when(nutrientRepository.findById(id)).thenReturn(Optional.of(nutrient));

            assertThatThrownBy(() -> nutrientService.findNutrientById(id))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    @Nested
    class UpdateNutrient {

        @Test
        void shouldUpdateNutrientSuccessfully() {
            var id = UUID.randomUUID();
            var existing = createActiveNutrient(id, "Protein");
            var request = new NutrientRequestDTO("Updated Protein", new BigDecimal("5.0000"), NutrientUnit.GRAM);

            when(nutrientRepository.findById(id)).thenReturn(Optional.of(existing));
            when(nutrientRepository.existsByName("Updated Protein")).thenReturn(false);
            when(nutrientRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

            var response = nutrientService.updateNutrient(id, request);

            assertThat(response.getName()).isEqualTo("Updated Protein");
            assertThat(response.getKcalPerGram()).isEqualByComparingTo("5.0000");
        }

        @Test
        void shouldThrowWhenNotFound() {
            var id = UUID.randomUUID();
            var request = new NutrientRequestDTO("Protein", new BigDecimal("4.0000"), NutrientUnit.GRAM);

            when(nutrientRepository.findById(id)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> nutrientService.updateNutrient(id, request))
                    .isInstanceOf(ResourceNotFoundException.class);
        }

        @Test
        void shouldThrowWhenDuplicateName() {
            var id = UUID.randomUUID();
            var existing = createActiveNutrient(id, "Protein");
            var request = new NutrientRequestDTO("Carbs", new BigDecimal("4.0000"), NutrientUnit.GRAM);

            when(nutrientRepository.findById(id)).thenReturn(Optional.of(existing));
            when(nutrientRepository.existsByName("Carbs")).thenReturn(true);

            assertThatThrownBy(() -> nutrientService.updateNutrient(id, request))
                    .isInstanceOf(DuplicateNutrientNameException.class);
        }
    }

    @Nested
    class DeleteNutrient {

        @Test
        void shouldDeleteExistingNutrient() {
            var id = UUID.randomUUID();

            when(nutrientRepository.existsById(id)).thenReturn(true);

            nutrientService.deleteNutrient(id);

            verify(nutrientRepository).deleteById(id);
        }

        @Test
        void shouldThrowWhenNotFound() {
            var id = UUID.randomUUID();

            when(nutrientRepository.existsById(id)).thenReturn(false);

            assertThatThrownBy(() -> nutrientService.deleteNutrient(id))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    @Nested
    class SuggestNutrient {

        @Test
        void shouldSuggestNutrientSuccessfully() {
            var request = new SuggestionRequestDTO("Vitamin C", new BigDecimal("0.5000"), NutrientUnit.MILLIGRAM);

            when(nutrientRepository.existsByName("Vitamin C")).thenReturn(false);
            when(nutrientRepository.save(any())).thenAnswer(invocation -> {
                var n = invocation.<Nutrient>getArgument(0);
                n.setId(UUID.randomUUID());
                return n;
            });
            when(nutrientVoteRepository.countByNutrientId(any())).thenReturn(1L);

            var response = nutrientService.suggestNutrient(request, "user1");

            assertThat(response.getName()).isEqualTo("Vitamin C");
            assertThat(response.getStatus()).isEqualTo(NutrientStatus.SUGGESTED);
            assertThat(response.getSource()).isEqualTo(NutrientSource.SUGGESTION);
            assertThat(response.getAuthorId()).isEqualTo("user1");
            assertThat(response.getVoteCount()).isOne();
        }

        @Test
        void shouldThrowWhenDuplicateName() {
            var request = new SuggestionRequestDTO("Protein", new BigDecimal("4.0000"), NutrientUnit.GRAM);

            when(nutrientRepository.existsByName("Protein")).thenReturn(true);

            assertThatThrownBy(() -> nutrientService.suggestNutrient(request, "user1"))
                    .isInstanceOf(DuplicateNutrientNameException.class);
        }
    }

    @Nested
    class FindAllSuggestions {

        @Test
        void shouldReturnOnlySuggestedNutrients() {
            var nutrient = createSuggestedNutrient(UUID.randomUUID(), "Vitamin C");
            var page = new PageImpl<>(List.of(nutrient));

            when(nutrientRepository.findAll(any(Specification.class), any(PageRequest.class)))
                    .thenReturn(page);
            when(nutrientVoteRepository.countByNutrientId(nutrient.getId())).thenReturn(3L);

            var result = nutrientService.findAllSuggestions(PageRequest.of(0, 20));

            assertThat(result).hasSize(1);
            assertThat(result.getContent().getFirst().getVoteCount()).isEqualTo(3);
        }
    }

    @Nested
    class FindSuggestionById {

        @Test
        void shouldReturnSuggestedNutrient() {
            var id = UUID.randomUUID();
            var nutrient = createSuggestedNutrient(id, "Vitamin C");

            when(nutrientRepository.findById(id)).thenReturn(Optional.of(nutrient));
            when(nutrientVoteRepository.countByNutrientId(id)).thenReturn(5L);

            var response = nutrientService.findSuggestionById(id);

            assertThat(response.getName()).isEqualTo("Vitamin C");
            assertThat(response.getVoteCount()).isEqualTo(5);
        }

        @Test
        void shouldThrowWhenNotFound() {
            var id = UUID.randomUUID();

            when(nutrientRepository.findById(id)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> nutrientService.findSuggestionById(id))
                    .isInstanceOf(ResourceNotFoundException.class);
        }

        @Test
        void shouldThrowWhenNotSuggested() {
            var id = UUID.randomUUID();
            var nutrient = createActiveNutrient(id, "Protein");

            when(nutrientRepository.findById(id)).thenReturn(Optional.of(nutrient));

            assertThatThrownBy(() -> nutrientService.findSuggestionById(id))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    @Nested
    class VoteOnSuggestion {

        @Test
        void shouldVoteSuccessfully() {
            var id = UUID.randomUUID();
            var nutrient = createSuggestedNutrient(id, "Vitamin C");

            when(nutrientRepository.findByIdWithLock(id)).thenReturn(Optional.of(nutrient));
            when(nutrientVoteRepository.existsByNutrientIdAndVoterId(id, "user2")).thenReturn(false);
            when(nutrientVoteRepository.countByNutrientId(id)).thenReturn(1L);

            var response = nutrientService.voteOnSuggestion(id, "user2");

            assertThat(response.getVoteCount()).isEqualTo(1);
            verify(nutrientVoteRepository).save(any(NutrientVote.class));
        }

        @Test
        void shouldBeIdempotentOnDuplicateVote() {
            var id = UUID.randomUUID();
            var nutrient = createSuggestedNutrient(id, "Vitamin C");

            when(nutrientRepository.findByIdWithLock(id)).thenReturn(Optional.of(nutrient));
            when(nutrientVoteRepository.existsByNutrientIdAndVoterId(id, "user2")).thenReturn(true);
            when(nutrientVoteRepository.countByNutrientId(id)).thenReturn(1L);

            var response = nutrientService.voteOnSuggestion(id, "user2");

            assertThat(response.getVoteCount()).isEqualTo(1);
        }

        @Test
        void shouldAutoApproveAtThreshold() {
            var id = UUID.randomUUID();
            var nutrient = createSuggestedNutrient(id, "Vitamin C");

            when(nutrientRepository.findByIdWithLock(id)).thenReturn(Optional.of(nutrient));
            when(nutrientVoteRepository.existsByNutrientIdAndVoterId(id, "user2")).thenReturn(false);
            when(nutrientVoteRepository.countByNutrientId(id)).thenReturn(10L);

            var response = nutrientService.voteOnSuggestion(id, "user2");

            assertThat(response.getStatus()).isEqualTo(NutrientStatus.ACTIVE);
            assertThat(response.getVoteCount()).isEqualTo(10);
        }

        @Test
        void shouldThrowWhenSuggestionNotOpen() {
            var id = UUID.randomUUID();
            var nutrient = createActiveNutrient(id, "Protein");

            when(nutrientRepository.findByIdWithLock(id)).thenReturn(Optional.of(nutrient));

            assertThatThrownBy(() -> nutrientService.voteOnSuggestion(id, "user2"))
                    .isInstanceOf(IllegalStateException.class);
        }

        @Test
        void shouldThrowWhenNotFound() {
            var id = UUID.randomUUID();

            when(nutrientRepository.findByIdWithLock(id)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> nutrientService.voteOnSuggestion(id, "user2"))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    @Nested
    class ApproveSuggestion {

        @Test
        void shouldApproveSuccessfully() {
            var id = UUID.randomUUID();
            var nutrient = createSuggestedNutrient(id, "Vitamin C");

            when(nutrientRepository.findById(id)).thenReturn(Optional.of(nutrient));
            when(nutrientVoteRepository.countByNutrientId(id)).thenReturn(0L);
            when(nutrientRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

            var response = nutrientService.approveSuggestion(id);

            assertThat(response.getStatus()).isEqualTo(NutrientStatus.ACTIVE);
        }

        @Test
        void shouldThrowWhenNotFound() {
            var id = UUID.randomUUID();

            when(nutrientRepository.findById(id)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> nutrientService.approveSuggestion(id))
                    .isInstanceOf(ResourceNotFoundException.class);
        }

        @Test
        void shouldThrowWhenAlreadyActive() {
            var id = UUID.randomUUID();
            var nutrient = createActiveNutrient(id, "Protein");

            when(nutrientRepository.findById(id)).thenReturn(Optional.of(nutrient));

            assertThatThrownBy(() -> nutrientService.approveSuggestion(id))
                    .isInstanceOf(IllegalStateException.class);
        }
    }

    @Nested
    class HasVoted {

        @Test
        void shouldReturnTrueWhenUserVoted() {
            var id = UUID.randomUUID();

            when(nutrientVoteRepository.existsByNutrientIdAndVoterId(id, "user1")).thenReturn(true);

            assertThat(nutrientService.hasVoted(id, "user1")).isTrue();
        }

        @Test
        void shouldReturnFalseWhenUserNotVoted() {
            var id = UUID.randomUUID();

            when(nutrientVoteRepository.existsByNutrientIdAndVoterId(id, "user1")).thenReturn(false);

            assertThat(nutrientService.hasVoted(id, "user1")).isFalse();
        }
    }
}
