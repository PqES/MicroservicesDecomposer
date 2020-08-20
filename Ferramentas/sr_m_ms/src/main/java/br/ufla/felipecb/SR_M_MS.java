package br.ufla.felipecb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories(basePackages = "br.ufla.felipecb.dao")
@SpringBootApplication
public class SR_M_MS {

	public static void main(String[] args) {
		SpringApplication.run(SR_M_MS.class, args);
	}

//	@Bean(name = "multipartResolver")
//	public CommonsMultipartResolver multipartResolver() {
//		CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver();
//		multipartResolver.setMaxUploadSize(100000);
//		return multipartResolver;
//	}
}
