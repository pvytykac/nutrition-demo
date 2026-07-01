package net.pvytykac.nutrition.nutrient.internal;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.LinkRelation;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
class NutrientLinkBuilder {

    private static final String ADMIN_ROLE = "admin";
    private static final String USER_ROLE = "user";

    List<Link> buildNutrientCollectionLinks(Authentication auth) {
        var links = new ArrayList<Link>();
        links.add(Link.of("/v1/nutrients", "self"));

        if (hasRole(auth, ADMIN_ROLE)) {
            links.add(Link.of("/v1/nutrients", LinkRelation.of("create-nutrient")));
        }

        return links;
    }

    List<Link> buildNutrientResourceLinks(UUID id, Authentication auth) {
        var links = new ArrayList<Link>();
        links.add(Link.of("/v1/nutrients/" + id, "self"));

        if (hasRole(auth, ADMIN_ROLE)) {
            links.add(Link.of("/v1/nutrients/" + id, LinkRelation.of("edit")));
            links.add(Link.of("/v1/nutrients/" + id, LinkRelation.of("delete")));
        }

        return links;
    }

    List<Link> buildSuggestionCollectionLinks(Authentication auth) {
        var links = new ArrayList<Link>();
        links.add(Link.of("/v1/nutrient-suggestions", "self"));

        if (hasRole(auth, USER_ROLE)) {
            links.add(Link.of("/v1/nutrient-suggestions", LinkRelation.of("suggest-nutrient")));
        }

        return links;
    }

    List<Link> buildSuggestionResourceLinks(UUID id, Authentication auth, boolean alreadyVoted) {
        var links = new ArrayList<Link>();
        links.add(Link.of("/v1/nutrient-suggestions/" + id, "self"));

        if (hasRole(auth, USER_ROLE) && !alreadyVoted) {
            links.add(Link.of("/v1/nutrient-suggestions/" + id + "/votes", LinkRelation.of("vote")));
        }

        if (hasRole(auth, ADMIN_ROLE)) {
            links.add(Link.of("/v1/nutrient-suggestions/" + id + "/approve", LinkRelation.of("approve")));
        }

        return links;
    }

    private boolean hasRole(Authentication auth, String role) {
        if (auth == null || !auth.isAuthenticated()) {
            return false;
        }
        return auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(a -> a.equals(role));
    }
}
