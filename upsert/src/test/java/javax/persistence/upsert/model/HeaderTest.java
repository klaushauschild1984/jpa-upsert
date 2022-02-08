package javax.persistence.upsert.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Map;
import org.junit.jupiter.api.Test;

class HeaderTest {

	@Test
	void name() {
		final Header header = Header //
			.builder()
			.name("name")
			.build();

		assertEquals("name", header.getName());
	}

	@Test
	void modifier() {
		final Header header = Header //
			.builder()
			.name("name")
			.modifiers("unique = true")
			.build();

		assertThat(header.getModifiers().entrySet())
			.hasSize(1)
			.flatExtracting( //
				Map.Entry::getKey,
				Map.Entry::getValue
			)
			.contains( //
				Modifier.UNIQUE,
				true
			);
	}

	@Test
	void modifierDefault() {
		final Header header = Header //
			.builder()
			.name("name")
			.modifiers("unique")
			.build();

		assertThat(header.getModifiers().entrySet())
			.hasSize(1)
			.flatExtracting( //
				Map.Entry::getKey,
				Map.Entry::getValue
			)
			.contains( //
				Modifier.UNIQUE,
				true
			);
	}

	@Test
	void multipleModifiers() {
		final Header header = Header //
			.builder()
			.name("name")
			.modifiers("unique, default = foobar")
			.build();

		assertThat(header.getModifiers().entrySet())
			.hasSize(2)
			.flatExtracting( //
				Map.Entry::getKey,
				Map.Entry::getValue
			)
			.contains( //
				Modifier.DEFAULT,
				"foobar",
				Modifier.UNIQUE,
				true
			);
	}
}
