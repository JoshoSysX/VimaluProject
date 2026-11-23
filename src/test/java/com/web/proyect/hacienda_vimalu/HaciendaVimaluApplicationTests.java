package com.web.proyect.hacienda_vimalu;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties={"spring.datasource.url=jdbc:h2:mem:context;MODE=Oracle", "spring.datasource.driver-class-name=org.h2.Driver", "spring.datasource.username=sa", "spring.datasource.password=", "spring.jpa.hibernate.ddl-auto=create-drop", "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect", "spring.jpa.properties.hibernate.default_schema=PUBLIC", "spring.jpa.show-sql=false"})
class HaciendaVimaluApplicationTests {
 @org.junit.jupiter.api.io.TempDir static java.nio.file.Path images;
 @org.springframework.test.context.DynamicPropertySource static void storage(org.springframework.test.context.DynamicPropertyRegistry registry){ registry.add("storage.location", images::toString); }

	@Test
	void contextLoads() {
	}

}
