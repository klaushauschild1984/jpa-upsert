package javax.persistence.upsert.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verifyNoInteractions;

import java.util.List;
import javax.persistence.upsert.model.Header;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LombokBuilderCreatorTest {

	@InjectMocks
	private LombokBuilderCreator creator;

	@Mock
	private Finder finder;

	@Test
	void create() {
		final Class<Entity> entityType = Entity.class;
		final List<Header> headers = List.of(
			Header.builder().name("string").build(),
			Header.builder().name("integer").build(),
			Header.builder().name("type").build()
		);
		final List<String> line = List.of("string", "42", "A");

		final Entity entity = (Entity) creator.create(entityType, headers, line);

		assertEquals("string", entity.getString());
		assertEquals(42, entity.getInteger());
		assertEquals(Type.A, entity.getType());
		verifyNoInteractions(finder);
	}
}
