package net.pvytykac.nutrition;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.modulith.Modulithic;

@Modulithic(sharedModules = {"common"})
@SpringBootApplication
@EnableSpringDataWebSupport(pageSerializationMode = EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO)
public class NutritionDemoApplication {

    static void main(String[] args) {
        SpringApplication.run(NutritionDemoApplication.class, args);
    }
}
