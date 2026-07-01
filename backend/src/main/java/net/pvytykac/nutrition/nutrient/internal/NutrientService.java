package net.pvytykac.nutrition.nutrient.internal;

import lombok.RequiredArgsConstructor;
import net.pvytykac.nutrition.common.exceptions.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
class NutrientService {

    private static final int AUTO_APPROVAL_THRESHOLD = 10;

    private final NutrientRepository nutrientRepository;
    private final NutrientVoteRepository nutrientVoteRepository;

    public NutrientResponseDTO createNutrient(NutrientRequestDTO request, String authorId) {
        if (nutrientRepository.existsByName(request.getName())) {
            throw new DuplicateNutrientNameException(request.getName());
        }

        var nutrient = Nutrient.builder()
                .name(request.getName())
                .kcalPerGram(request.getKcalPerGram())
                .defaultUnit(request.getDefaultUnit())
                .status(NutrientStatus.ACTIVE)
                .source(NutrientSource.ADMIN)
                .authorId(authorId)
                .createdAt(Instant.now())
                .build();

        nutrient = nutrientRepository.save(nutrient);
        return toResponseDTO(nutrient);
    }

    @Transactional(readOnly = true)
    public Page<NutrientResponseDTO> findAllNutrients(String name, Pageable pageable) {
        Specification<Nutrient> spec = Specification.where(
                (root, query, cb) -> cb.equal(root.get("status"), NutrientStatus.ACTIVE));

        if (name != null && !name.isBlank()) {
            spec = spec.and((root, query, cb) ->
                    cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
        }

        var sort = pageable.getSortOr(Sort.by(Sort.Direction.ASC, "name"));
        var pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

        return nutrientRepository.findAll(spec, pageRequest)
                .map(NutrientService::toResponseDTO);
    }

    @Transactional(readOnly = true)
    public NutrientResponseDTO findNutrientById(UUID id) {
        var nutrient = nutrientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Nutrient", id));

        if (nutrient.getStatus() != NutrientStatus.ACTIVE) {
            throw new ResourceNotFoundException("Nutrient", id);
        }

        return toResponseDTO(nutrient);
    }

    public NutrientResponseDTO updateNutrient(UUID id, NutrientRequestDTO request) {
        var nutrient = nutrientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Nutrient", id));

        if (!nutrient.getName().equals(request.getName())
                && nutrientRepository.existsByName(request.getName())) {
            throw new DuplicateNutrientNameException(request.getName());
        }

        nutrient.setName(request.getName());
        nutrient.setKcalPerGram(request.getKcalPerGram());
        nutrient.setDefaultUnit(request.getDefaultUnit());

        nutrient = nutrientRepository.save(nutrient);
        return toResponseDTO(nutrient);
    }

    public void deleteNutrient(UUID id) {
        if (!nutrientRepository.existsById(id)) {
            throw new ResourceNotFoundException("Nutrient", id);
        }
        nutrientRepository.deleteById(id);
    }

    public SuggestionResponseDTO suggestNutrient(SuggestionRequestDTO request, String authorId) {
        if (nutrientRepository.existsByName(request.getName())) {
            throw new DuplicateNutrientNameException(request.getName());
        }

        var nutrient = Nutrient.builder()
                .name(request.getName())
                .kcalPerGram(request.getKcalPerGram())
                .defaultUnit(request.getDefaultUnit())
                .status(NutrientStatus.SUGGESTED)
                .source(NutrientSource.SUGGESTION)
                .authorId(authorId)
                .createdAt(Instant.now())
                .build();

        nutrient = nutrientRepository.save(nutrient);

        var vote = NutrientVote.builder()
                .nutrient(nutrient)
                .voterId(authorId)
                .createdAt(Instant.now())
                .build();
        nutrientVoteRepository.save(vote);

        var voteCount = nutrientVoteRepository.countByNutrientId(nutrient.getId());
        return toSuggestionResponseDTO(nutrient, voteCount);
    }

    @Transactional(readOnly = true)
    public Page<SuggestionResponseDTO> findAllSuggestions(Pageable pageable) {
        Specification<Nutrient> spec = (root, query, cb) ->
                cb.equal(root.get("status"), NutrientStatus.SUGGESTED);

        var sort = pageable.getSortOr(Sort.by(Sort.Direction.ASC, "name"));
        var pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

        return nutrientRepository.findAll(spec, pageRequest)
                .map(nutrient -> {
                    var voteCount = nutrientVoteRepository.countByNutrientId(nutrient.getId());
                    return toSuggestionResponseDTO(nutrient, voteCount);
                });
    }

    @Transactional(readOnly = true)
    public SuggestionResponseDTO findSuggestionById(UUID id) {
        var nutrient = nutrientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Suggestion", id));

        if (nutrient.getStatus() != NutrientStatus.SUGGESTED) {
            throw new ResourceNotFoundException("Suggestion", id);
        }

        var voteCount = nutrientVoteRepository.countByNutrientId(id);
        return toSuggestionResponseDTO(nutrient, voteCount);
    }

    @Transactional(readOnly = true)
    public boolean hasVoted(UUID suggestionId, String voterId) {
        return nutrientVoteRepository.existsByNutrientIdAndVoterId(suggestionId, voterId);
    }

    public SuggestionResponseDTO voteOnSuggestion(UUID suggestionId, String voterId) {
        var nutrient = nutrientRepository.findByIdWithLock(suggestionId)
                .orElseThrow(() -> new ResourceNotFoundException("Suggestion", suggestionId));

        if (nutrient.getStatus() != NutrientStatus.SUGGESTED) {
            throw new IllegalStateException("Cannot vote on a suggestion that is no longer open");
        }

        if (!nutrientVoteRepository.existsByNutrientIdAndVoterId(suggestionId, voterId)) {
            var vote = NutrientVote.builder()
                    .nutrient(nutrient)
                    .voterId(voterId)
                    .createdAt(Instant.now())
                    .build();

            nutrientVoteRepository.save(vote);

            var voteCount = nutrientVoteRepository.countByNutrientId(suggestionId);
            if (voteCount >= AUTO_APPROVAL_THRESHOLD) {
                nutrient.setStatus(NutrientStatus.ACTIVE);
                nutrientRepository.save(nutrient);
            }
        }

        var voteCount = nutrientVoteRepository.countByNutrientId(suggestionId);
        return toSuggestionResponseDTO(nutrient, voteCount);
    }

    public SuggestionResponseDTO approveSuggestion(UUID suggestionId) {
        var nutrient = nutrientRepository.findById(suggestionId)
                .orElseThrow(() -> new ResourceNotFoundException("Suggestion", suggestionId));

        if (nutrient.getStatus() != NutrientStatus.SUGGESTED) {
            throw new IllegalStateException("Suggestion is already approved or not in SUGGESTED status");
        }

        nutrient.setStatus(NutrientStatus.ACTIVE);
        nutrient = nutrientRepository.save(nutrient);

        var voteCount = nutrientVoteRepository.countByNutrientId(suggestionId);
        return toSuggestionResponseDTO(nutrient, voteCount);
    }

    static NutrientResponseDTO toResponseDTO(Nutrient nutrient) {
        return NutrientResponseDTO.builder()
                .id(nutrient.getId())
                .name(nutrient.getName())
                .kcalPerGram(nutrient.getKcalPerGram())
                .defaultUnit(nutrient.getDefaultUnit())
                .status(nutrient.getStatus())
                .source(nutrient.getSource())
                .authorId(nutrient.getAuthorId())
                .createdAt(nutrient.getCreatedAt())
                .build();
    }

    static SuggestionResponseDTO toSuggestionResponseDTO(Nutrient nutrient, long voteCount) {
        return SuggestionResponseDTO.builder()
                .id(nutrient.getId())
                .name(nutrient.getName())
                .kcalPerGram(nutrient.getKcalPerGram())
                .defaultUnit(nutrient.getDefaultUnit())
                .status(nutrient.getStatus())
                .source(nutrient.getSource())
                .authorId(nutrient.getAuthorId())
                .voteCount(voteCount)
                .createdAt(nutrient.getCreatedAt())
                .build();
    }
}
