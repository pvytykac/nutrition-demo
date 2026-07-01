package net.pvytykac.nutrition.nutrient.internal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "nutrients")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Nutrient {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(name = "kcal_per_gram", precision = 10, scale = 4)
    private BigDecimal kcalPerGram;

    @Enumerated(EnumType.STRING)
    @Column(name = "default_unit", nullable = false, length = 20)
    private NutrientUnit defaultUnit;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private NutrientStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private NutrientSource source;

    @Column(name = "author_id")
    private String authorId;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

}
