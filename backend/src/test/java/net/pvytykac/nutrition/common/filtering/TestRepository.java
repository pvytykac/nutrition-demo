package net.pvytykac.nutrition.common.filtering;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface TestRepository extends JpaRepository<TestEntity, String>, JpaSpecificationExecutor<TestEntity> {
}
