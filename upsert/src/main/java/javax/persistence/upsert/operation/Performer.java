package javax.persistence.upsert.operation;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.upsert.entity.Creator;
import javax.persistence.upsert.model.Header;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class Performer {

	protected final EntityManager entityManager;
	protected final Creator creator;

	public abstract void perform(final Class<?> entityType, final List<Header> headers, final List<List<String>> data);
}
