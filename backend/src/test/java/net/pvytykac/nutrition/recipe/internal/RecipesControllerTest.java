package net.pvytykac.nutrition.recipe.internal;

import net.pvytykac.nutrition.common.ControllerTestBase;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;

@WebMvcTest(controllers = RecipesController.class)
public class RecipesControllerTest extends ControllerTestBase {

    @Test
    void getShouldRespondWithOkWhenUserRole() {
        getRestHelper()
                .withUserAuth()
                .get()
                .uri("/recipes")
                .apiVersion("v1")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.content[0].id").isEqualTo("1")
                .jsonPath("$.content[0].name").isEqualTo("mashed potatoes")
                .jsonPath("$.page.number").isEqualTo(0)
                .jsonPath("$.page.totalElements").isEqualTo(1);
    }

    @Test
    void getShouldRespondWithOkWhenAdminRole() {
        getRestHelper()
                .withAdminAuth()
                .get()
                .uri("/recipes")
                .apiVersion("v1")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.content[0].id").isEqualTo("1")
                .jsonPath("$.content[0].name").isEqualTo("mashed potatoes")
                .jsonPath("$.page.number").isEqualTo(0)
                .jsonPath("$.page.totalElements").isEqualTo(1);
    }

    @Test
    void getShouldRespondWithUnauthorizedWhenUserUnauthenticated() {
        getRestHelper()
                .unauthenticated()
                .get()
                .uri("/recipes")
                .apiVersion("v1")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isUnauthorized();
    }

}
