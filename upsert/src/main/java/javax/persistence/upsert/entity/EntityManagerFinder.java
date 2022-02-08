package javax.persistence.upsert.entity;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.TypedQuery;
import javax.persistence.upsert.JpaUpsertException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of {@link Finder} utilizing {@link EntityManager} and {@link TypedQuery}.
 */
@RequiredArgsConstructor
@Slf4j
public class EntityManagerFinder implements Finder {

	private final EntityManager entityManager;

	@Override
	public Object findBy(final Class<?> entityType, final List<String> references, final String value) {
		final List<String> values = Arrays.asList(value.split(":"));

		final String query = String.format(
			"SELECT e FROM %s e WHERE %s",
			entityType.getSimpleName(),
			IntStream
				.range(0, values.size())
				.mapToObj(index -> String.format("e.%s = ?%s", references.get(index), index + 1))
				.collect(Collectors.joining(" AND "))
		);

		final TypedQuery<?> typedQuery = entityManager.createQuery(query, entityType);
		IntStream.range(0, values.size()).forEach(index -> typedQuery.setParameter(index + 1, values.get(index)));

		try {
			return typedQuery.getSingleResult();
		} catch (final NoResultException | NonUniqueResultException exception) {
			throw new JpaUpsertException(
				String.format(
					"Unable to find %s(%s) referenced by %s",
					entityType.getSimpleName(),
					String.join(", ", references),
					value
				),
				exception
			);
		}
	}
}
