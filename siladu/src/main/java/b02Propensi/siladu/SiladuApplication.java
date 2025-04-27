package b02Propensi.siladu;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import b02Propensi.siladu.user.authentication.PasswordConfig;
import b02Propensi.siladu.user.model.Role;
import b02Propensi.siladu.user.model.UserModel;
import b02Propensi.siladu.user.repository.UserDb;

import jakarta.transaction.Transactional;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class SiladuApplication {

	public static void main(String[] args) {
		SpringApplication.run(SiladuApplication.class, args);
		

	}
}
