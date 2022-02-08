package javax.persistence.upsert.operation;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.upsert.entity.Creator;
import javax.persistence.upsert.model.Header;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class InsertPerformer extends Performer {

	public InsertPerformer(final EntityManager entityManager, final Creator creator) {
		super(entityManager, creator);
	}

	@Override
	public void perform(final Class<?> entityType, final List<Header> headers, final List<List<String>> data) {
		if (data.isEmpty()) {
			log.warn("Not data provided.");
			return;
		}

		data.forEach(line -> {
			final Object entity = creator.create(entityType, headers, line);
			entityManager.persist(entity);
		});

		log.info("{} entities inserted. Flushing data.", data.size());
		entityManager.flush();
	}
}
