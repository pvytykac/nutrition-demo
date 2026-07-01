package net.pvytykac.nutrition.nutrient.internal;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.pvytykac.nutrition.common.security.HasAdminRole;
import net.pvytykac.nutrition.common.security.HasUserOrAdminRole;
import net.pvytykac.nutrition.common.security.HasUserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/nutrient-suggestions")
@RequiredArgsConstructor
class NutrientSuggestionsController {

    private final NutrientService nutrientService;
    private final NutrientLinkBuilder linkBuilder;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @HasUserRole
    public SuggestionResponseDTO suggestNutrient(
            @Valid @RequestBody SuggestionRequestDTO request,
            Authentication auth) {

        var response = nutrientService.suggestNutrient(request, auth.getName());
        response.add(linkBuilder.buildSuggestionResourceLinks(response.getId(), auth, false));
        return response;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @HasUserOrAdminRole
    public PagedModel<SuggestionResponseDTO> getSuggestions(
            Pageable pageable,
            Authentication auth) {

        var page = nutrientService.findAllSuggestions(pageable);
        page.forEach(item -> {
            var alreadyVoted = auth != null && auth.isAuthenticated()
                    && nutrientService.hasVoted(item.getId(), auth.getName());
            item.add(linkBuilder.buildSuggestionResourceLinks(item.getId(), auth, alreadyVoted));
        });
        var links = linkBuilder.buildSuggestionCollectionLinks(auth);
        return toPagedModel(page, links);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @HasUserOrAdminRole
    public SuggestionResponseDTO getSuggestion(
            @PathVariable UUID id,
            Authentication auth) {

        var response = nutrientService.findSuggestionById(id);
        var alreadyVoted = auth != null && auth.isAuthenticated()
                && nutrientService.hasVoted(id, auth.getName());
        response.add(linkBuilder.buildSuggestionResourceLinks(id, auth, alreadyVoted));
        return response;
    }

    @PostMapping(value = "/{id}/votes", produces = MediaType.APPLICATION_JSON_VALUE)
    @HasUserRole
    public SuggestionResponseDTO voteOnSuggestion(
            @PathVariable UUID id,
            Authentication auth) {

        var response = nutrientService.voteOnSuggestion(id, auth.getName());
        response.add(linkBuilder.buildSuggestionResourceLinks(id, auth, true));
        return response;
    }

    @PostMapping(value = "/{id}/approve", produces = MediaType.APPLICATION_JSON_VALUE)
    @HasAdminRole
    public SuggestionResponseDTO approveSuggestion(
            @PathVariable UUID id,
            Authentication auth) {

        var response = nutrientService.approveSuggestion(id);
        response.add(linkBuilder.buildSuggestionResourceLinks(id, auth, false));
        return response;
    }

    private static <T> PagedModel<T> toPagedModel(Page<T> page, List<Link> links) {
        var metadata = new PagedModel.PageMetadata(
                page.getSize(),
                page.getNumber(),
                page.getTotalElements(),
                page.getTotalPages());
        return PagedModel.of(page.getContent(), metadata, links);
    }
}
