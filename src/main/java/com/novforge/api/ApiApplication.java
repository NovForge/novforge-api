package com.novforge.api;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ApiApplication {

	public static void main(String[] args) {
		Dotenv dotenv = Dotenv.configure()
				.ignoreIfMissing()
				.load();

		setPropertyIfPresent(dotenv, "DB_URL");
		setPropertyIfPresent(dotenv, "DB_USERNAME");
		setPropertyIfPresent(dotenv, "DB_PASSWORD");
		setPropertyIfPresent(dotenv, "GOOGLE_CLIENT_ID");
		setPropertyIfPresent(dotenv, "JWT_SECRET");
		setPropertyIfPresent(dotenv, "ADMIN_EMAILS");

		SpringApplication.run(ApiApplication.class, args);
	}

	private static void setPropertyIfPresent(Dotenv dotenv, String key) {
		String value = dotenv.get(key);

		if (value != null && System.getProperty(key) == null) {
			System.setProperty(key, value);
		}
	}
}
