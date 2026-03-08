package net.pvytykac.nutrition.recipe;

import net.pvytykac.nutrition.common.filtering.StringFilter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("RecipeFilter")
class RecipeFilterTest {

    @Nested
    @DisplayName("isActive")
    class IsActive {

        @Test
        @DisplayName("should return true when name filter is active")
        void shouldReturnTrueWhenNameFilterActive() {
            // given
            StringFilter nameFilter = new StringFilter();
            nameFilter.setValue(List.of("test"));
            nameFilter.setOperator(StringFilter.Operator.CONTAINS);

            RecipeFilter filter = RecipeFilter.builder()
                    .name(nameFilter)
                    .build();

            // when/then
            assertThat(filter.isActive()).isTrue();
        }

        @Test
        @DisplayName("should return false when filter has no name")
        void shouldReturnFalseWhenNoNameFilter() {
            // given
            RecipeFilter filter = RecipeFilter.builder().build();

            // when/then
            assertThat(filter.isActive()).isFalse();
        }

        @Test
        @DisplayName("should return false when name filter is null")
        void shouldReturnFalseWhenNameFilterIsNull() {
            // given
            RecipeFilter filter = RecipeFilter.builder()
                    .name(null)
                    .build();

            // when/then
            assertThat(filter.isActive()).isFalse();
        }

        @Test
        @DisplayName("should return false when name filter is not active")
        void shouldReturnFalseWhenNameFilterNotActive() {
            // given
            StringFilter nameFilter = new StringFilter();
            nameFilter.setValue(null);

            RecipeFilter filter = RecipeFilter.builder()
                    .name(nameFilter)
                    .build();

            // when/then
            assertThat(filter.isActive()).isFalse();
        }

        @Test
        @DisplayName("should return false when name filter has empty values")
        void shouldReturnFalseWhenNameFilterHasEmptyValues() {
            // given
            StringFilter nameFilter = new StringFilter();
            nameFilter.setValue(List.of());

            RecipeFilter filter = RecipeFilter.builder()
                    .name(nameFilter)
                    .build();

            // when/then
            assertThat(filter.isActive()).isFalse();
        }
    }

    @Nested
    @DisplayName("toSpecification")
    class ToSpecification {

        @Test
        @DisplayName("should return null when filter is not active")
        void shouldReturnNullWhenFilterNotActive() {
            // given
            RecipeFilter filter = RecipeFilter.builder().build();

            // when
            Specification<Recipe> result = filter.toSpecification();

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("should return specification when name filter is active")
        void shouldReturnSpecificationWhenNameFilterActive() {
            // given
            StringFilter nameFilter = new StringFilter();
            nameFilter.setValue(List.of("Potato"));
            nameFilter.setOperator(StringFilter.Operator.CONTAINS);

            RecipeFilter filter = RecipeFilter.builder()
                    .name(nameFilter)
                    .build();

            // when
            Specification<Recipe> result = filter.toSpecification();

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("should create specification with EXACT_MATCH operator")
        void shouldCreateSpecificationWithExactMatch() {
            // given
            StringFilter nameFilter = new StringFilter();
            nameFilter.setValue(List.of("Baked Potatoes"));
            nameFilter.setOperator(StringFilter.Operator.EXACT_MATCH);

            RecipeFilter filter = RecipeFilter.builder()
                    .name(nameFilter)
                    .build();

            // when
            Specification<Recipe> result = filter.toSpecification();

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("should create specification with STARTS_WITH operator")
        void shouldCreateSpecificationWithStartsWith() {
            // given
            StringFilter nameFilter = new StringFilter();
            nameFilter.setValue(List.of("Baked"));
            nameFilter.setOperator(StringFilter.Operator.STARTS_WITH);

            RecipeFilter filter = RecipeFilter.builder()
                    .name(nameFilter)
                    .build();

            // when
            Specification<Recipe> result = filter.toSpecification();

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("should create specification with ENDS_WITH operator")
        void shouldCreateSpecificationWithEndsWith() {
            // given
            StringFilter nameFilter = new StringFilter();
            nameFilter.setValue(List.of("Potatoes"));
            nameFilter.setOperator(StringFilter.Operator.ENDS_WITH);

            RecipeFilter filter = RecipeFilter.builder()
                    .name(nameFilter)
                    .build();

            // when
            Specification<Recipe> result = filter.toSpecification();

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("should create specification with IN operator for multiple values")
        void shouldCreateSpecificationWithInOperator() {
            // given
            StringFilter nameFilter = new StringFilter();
            nameFilter.setValue(List.of("Potato", "Chicken"));
            nameFilter.setOperator(StringFilter.Operator.IN);

            RecipeFilter filter = RecipeFilter.builder()
                    .name(nameFilter)
                    .build();

            // when
            Specification<Recipe> result = filter.toSpecification();

            // then
            assertThat(result).isNotNull();
        }
    }

    @Nested
    @DisplayName("builder")
    class Builder {

        @Test
        @DisplayName("should create filter with name")
        void shouldCreateFilterWithName() {
            // given
            StringFilter nameFilter = new StringFilter();
            nameFilter.setValue(List.of("Test"));

            // when
            RecipeFilter filter = RecipeFilter.builder()
                    .name(nameFilter)
                    .build();

            // then
            assertThat(filter.getName()).isEqualTo(nameFilter);
        }

        @Test
        @DisplayName("should create empty filter when nothing is set")
        void shouldCreateEmptyFilter() {
            // when
            RecipeFilter filter = RecipeFilter.builder().build();

            // then
            assertThat(filter.getName()).isNull();
            assertThat(filter.isActive()).isFalse();
        }
    }
}
