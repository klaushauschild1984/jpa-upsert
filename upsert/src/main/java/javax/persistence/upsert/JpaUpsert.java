package javax.persistence.upsert;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.persistence.upsert.entity.Creator;
import javax.persistence.upsert.entity.EntityManagerFinder;
import javax.persistence.upsert.entity.Finder;
import javax.persistence.upsert.entity.LombokBuilderCreator;
import javax.persistence.upsert.model.Header;
import javax.persistence.upsert.model.Upsert;
import javax.persistence.upsert.operation.Operation;
import javax.persistence.upsert.operation.Performer;
import javax.persistence.upsert.reader.JsonReader;
import javax.persistence.upsert.reader.Reader;
import lombok.AccessLevel;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.core.io.Resource;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;

/**
 * Provides functionality to simply INSERT, UPDATE or REMOVE data defined in text based form via the JPA layer.
 */
@Slf4j
@SuperBuilder
public class JpaUpsert {
	static {
		JpaUpsertBanner.banner();
	}

	private final EntityManager entityManager;
	private final String entityPackage;

	@Default
	private final Reader reader = new JsonReader();

	@Getter(lazy = true, value = AccessLevel.PRIVATE)
	private final Creator creator = initializeCreator();

	@Getter(lazy = true, value = AccessLevel.PRIVATE)
	private final Finder finder = initializeFinder();

	/**
	 * Applies the given data instructions to your JPA setup.
	 *
	 * @param resource contains data in textual form, has to be aligned with {@link JpaUpsertBuilder#reader(Reader)},
	 *                 default is {@link JsonReader}
	 * @return self, for chained calls
	 */
	@Transactional
	public JpaUpsert apply(final Resource resource) {
		final List<Upsert> upserts = reader.read(resource);

		upserts.forEach(upsert -> {
			final Operation operation = upsert.getOperation();
			final Performer performer = (Performer) BeanUtils.instantiateClass(
				operation.getPerformerType().getConstructors()[0],
				entityManager,
				getCreator()
			);

			final Class<?> entityType = upsert.getEntityType(entityPackage);
			final List<Header> headers = upsert.getHeaders();
			final List<List<String>> data = upsert.getData();

			log.info(
				"Perform {} on {} with { {} }",
				operation,
				entityType.getName(),
				headers.stream().map(Objects::toString).collect(Collectors.joining(", "))
			);

			final StopWatch stopWatch = new StopWatch();
			stopWatch.start();
			performer.perform(entityType, headers, data);
			stopWatch.stop();
			log.info("{} on {} took {}s", operation, entityType.getName(), stopWatch.getTotalTimeSeconds());
		});

		return this;
	}

	private Creator initializeCreator() {
		return new LombokBuilderCreator(getFinder());
	}

	private Finder initializeFinder() {
		return new EntityManagerFinder(entityManager);
	}
}
