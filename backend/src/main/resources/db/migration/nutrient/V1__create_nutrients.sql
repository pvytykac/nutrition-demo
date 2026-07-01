CREATE TABLE nutrients (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    kcal_per_gram DECIMAL(10, 4),
    default_unit VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL,
    source VARCHAR(20) NOT NULL,
    author_id VARCHAR(255),
    created_at TIMESTAMP NOT NULL
);

CREATE UNIQUE INDEX idx_nutrients_name ON nutrients(name);

CREATE TABLE nutrient_votes (
    id UUID PRIMARY KEY,
    nutrient_id UUID NOT NULL REFERENCES nutrients(id) ON DELETE CASCADE,
    voter_id VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    CONSTRAINT uq_nutrient_voter UNIQUE (nutrient_id, voter_id)
);

CREATE INDEX idx_nutrient_votes_nutrient_id ON nutrient_votes(nutrient_id);

INSERT INTO nutrients (id, name, kcal_per_gram, default_unit, status, source, author_id, created_at) VALUES
    (gen_random_uuid(), 'Carbohydrates', 4.0000, 'GRAM', 'ACTIVE', 'SEED', NULL, NOW()),
    (gen_random_uuid(), 'Protein', 4.0000, 'GRAM', 'ACTIVE', 'SEED', NULL, NOW()),
    (gen_random_uuid(), 'Fat', 9.0000, 'GRAM', 'ACTIVE', 'SEED', NULL, NOW()),
    (gen_random_uuid(), 'Phenylalanine', NULL, 'MILLIGRAM', 'ACTIVE', 'SEED', NULL, NOW());
