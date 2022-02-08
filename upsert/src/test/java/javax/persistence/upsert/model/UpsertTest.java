package javax.persistence.upsert.model;

import static org.junit.jupiter.api.Assertions.*;

import javax.persistence.upsert.JpaUpsertException;
import javax.persistence.upsert.operation.Operation;
import org.junit.jupiter.api.Test;

class UpsertTest {

	@Test
	void operation() {
		final Upsert upsert = new Upsert();
		upsert.setOperation(Operation.INSERT);

		assertEquals(Operation.INSERT, upsert.getOperation());
	}

	@Test
	void entity() {
		final Upsert upsert = new Upsert();
		upsert.setEntity("Upsert");

		assertEquals(Upsert.class, upsert.getEntityType("javax.persistence.upsert.model"));
	}

	@Test
	void entityNotFound() {
		final Upsert upsert = new Upsert();
		upsert.setEntity("Foobar");

		assertThrows(JpaUpsertException.class, () -> upsert.getEntityType(""));
	}

	@Test
	void noHeaders() {
		final Upsert upsert = new Upsert();
		upsert.setHeader("");

		assertEquals(0, upsert.getHeaders().size());
	}

	@Test
	void singleHeader() {
		final Upsert upsert = new Upsert();
		upsert.setHeader("name");

		assertEquals(1, upsert.getHeaders().size());
		assertEquals("name", upsert.getHeaders().get(0).getName());
		assertEquals(0, upsert.getHeaders().get(0).getModifiers().size());
	}

	@Test
	void singleHeaderWithModifiers() {
		final Upsert upsert = new Upsert();
		upsert.setHeader("name [ unique ]");

		assertEquals(1, upsert.getHeaders().size());
		assertEquals("name", upsert.getHeaders().get(0).getName());
		assertEquals(1, upsert.getHeaders().get(0).getModifiers().size());
		assertEquals(true, upsert.getHeaders().get(0).getModifiers().get(Modifier.UNIQUE));
	}

	@Test
	void multipleHeaders() {
		final Upsert upsert = new Upsert();
		upsert.setHeader("name;    age");

		assertEquals(2, upsert.getHeaders().size());
		assertEquals("name", upsert.getHeaders().get(0).getName());
		assertEquals("age", upsert.getHeaders().get(1).getName());
		assertEquals(0, upsert.getHeaders().get(0).getModifiers().size());
		assertEquals(0, upsert.getHeaders().get(1).getModifiers().size());
	}

	@Test
	void multipleHeadersWithModifiers() {
		final Upsert upsert = new Upsert();
		upsert.setHeader("name;    age  [  default    =19]");

		assertEquals(2, upsert.getHeaders().size());
		assertEquals("name", upsert.getHeaders().get(0).getName());
		assertEquals("age", upsert.getHeaders().get(1).getName());
		assertEquals(0, upsert.getHeaders().get(0).getModifiers().size());
		assertEquals(1, upsert.getHeaders().get(1).getModifiers().size());
		assertEquals("19", upsert.getHeaders().get(1).getModifiers().get(Modifier.DEFAULT));
	}

	@Test
	void referenceWithModifier() {
		final Upsert upsert = new Upsert();
		upsert.setHeader("owner(name)[unique]");

		assertEquals(1, upsert.getHeaders().size());
		assertEquals("owner", upsert.getHeaders().get(0).getName());
		assertEquals(1, upsert.getHeaders().get(0).getModifiers().size());
		assertEquals(true, upsert.getHeaders().get(0).getModifiers().get(Modifier.UNIQUE));
		assertEquals(1, upsert.getHeaders().get(0).getReferences().size());
		assertEquals("name", upsert.getHeaders().get(0).getReferences().get(0));
	}
}
