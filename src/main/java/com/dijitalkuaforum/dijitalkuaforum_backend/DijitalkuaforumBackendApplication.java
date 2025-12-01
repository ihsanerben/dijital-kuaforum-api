package com.dijitalkuaforum.dijitalkuaforum_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
public class DijitalkuaforumBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(DijitalkuaforumBackendApplication.class, args);
	}


	// YENİ CORS KONFİGÜRASYON BEAN'İ BURAYA EKLENİYOR
	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/**") // Tüm endpoint'ler için geçerli
						.allowedOrigins("http://localhost:5173") // React uygulamasının adresi
						.allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // İzin verilen HTTP metodları
						// Sizin özel başlıklarınızın (Username, Password) tarayıcıya iletilmesine izin verir
						.allowedHeaders("*")
						.allowCredentials(true); // Cookie'ler veya yetkilendirme bilgileri için (sizin durumda gerekli olabilir)
			}
		};
	}
}
