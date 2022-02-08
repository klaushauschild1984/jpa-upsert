package javax.persistence.upsert.example;

import static org.assertj.core.api.Assertions.assertThat;

import javax.persistence.EntityManager;
import javax.persistence.upsert.JpaUpsert;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@Transactional
@Slf4j
class InsertTest {

	@Autowired
	private EntityManager entityManager;

	@Autowired
	private PersonRepository personRepository;

	@Autowired
	private AddressRepository addressRepository;

	@Test
	void insertPerson() {
		JpaUpsert
			.builder()
			.entityManager(entityManager)
			.entityPackage("javax.persistence.upsert.example")
			.build()
			.apply(new ClassPathResource("insert_person.json"));

		personRepository.findAll().forEach(person -> log.info("{}", person));

		assertThat(personRepository.findAll())
			.hasSize(3)
			.flatExtracting( //
				Person::getName,
				Person::getAge,
				Person::getGender
			)
			.contains( //
				"Ada",
				19,
				Gender.FEMALE,
				"John",
				38,
				Gender.MALE,
				"Alan",
				26,
				Gender.DIVERSE
			);
	}

	@Test
	void insertAddressWithReferenceToPerson() {
		JpaUpsert
			.builder()
			.entityManager(entityManager)
			.entityPackage("javax.persistence.upsert.example")
			.build()
			.apply(new ClassPathResource("insert_person.json"))
			.apply(new ClassPathResource("insert_address.json"));

		assertThat(addressRepository.findAll())
			.hasSize(1)
			.flatExtracting(
				Address::getStreet,
				Address::getNumber,
				Address::getZip,
				Address::getCity,
				Address::getCountry,
				address -> address.getOwner().getName()
			)
			.contains("Street", "Number", "Zip", "City", "Country", "Ada");
	}
}
