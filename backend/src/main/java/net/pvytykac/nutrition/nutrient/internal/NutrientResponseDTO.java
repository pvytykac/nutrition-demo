package net.pvytykac.nutrition.nutrient.internal;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Relation(collectionRelation = "nutrients")
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NutrientResponseDTO extends RepresentationModel<NutrientResponseDTO> {

    private UUID id;
    private String name;
    private BigDecimal kcalPerGram;
    private NutrientUnit defaultUnit;
    private NutrientStatus status;
    private NutrientSource source;
    private String authorId;
    private Instant createdAt;

}
