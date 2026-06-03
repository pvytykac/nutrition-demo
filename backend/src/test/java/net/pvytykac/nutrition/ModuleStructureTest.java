package net.pvytykac.nutrition;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.docs.Documenter;

public class ModuleStructureTest {

    @Test
    void verifyModuleStructure() {
        ApplicationModules.of(NutritionDemoApplication.class)
                .detectViolations()
                .throwIfPresent();
    }

    @Test
    void documentModuleStructure() {
        new Documenter(ApplicationModules.of(NutritionDemoApplication.class))
                .writeDocumentation()
                .writeAggregatingDocument();
    }

}
