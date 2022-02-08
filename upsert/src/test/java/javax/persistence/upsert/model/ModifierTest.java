package javax.persistence.upsert.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class ModifierTest {

	@Test
	void uniqueModifier() {
		assertEquals(true, Modifier.UNIQUE.getDefaultValue());
	}

	@Test
	void defaultModifier() {
		assertNull(Modifier.DEFAULT.getDefaultValue());
	}
}
