package javax.persistence.upsert.example;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class Example implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(Example.class, args);
	}

	@Override
	public void run(final String... args) throws Exception {}
}
