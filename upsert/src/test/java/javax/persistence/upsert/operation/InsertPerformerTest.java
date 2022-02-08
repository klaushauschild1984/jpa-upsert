package javax.persistence.upsert.operation;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.upsert.entity.Creator;
import javax.persistence.upsert.model.Header;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class InsertPerformerTest {

	@InjectMocks
	private InsertPerformer insertPerformer;

	@Mock
	private EntityManager entityManager;

	@Mock
	private Creator creator;

	@Test
	void perform() {
		final List<Header> headers = List.of();
		final List<String> line = List.of();
		final List<List<String>> data = List.of(line);
		final Object entity = new Object();
		doReturn(entity).when(creator).create(Object.class, headers, line);

		insertPerformer.perform(Object.class, headers, data);

		verify(creator).create(Object.class, headers, line);
		verify(entityManager).persist(entity);
		verify(entityManager).flush();
	}

	@Test
	void noPerform() {
		final List<Header> headers = List.of();
		final List<List<String>> data = List.of();
		final Object entity = new Object();

		insertPerformer.perform(Object.class, headers, data);

		verifyNoInteractions(creator, entityManager);
	}
}
