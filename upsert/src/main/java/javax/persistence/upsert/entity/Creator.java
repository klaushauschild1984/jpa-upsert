package javax.persistence.upsert.entity;

import java.util.List;
import javax.persistence.upsert.model.Header;

/**
 * Responsible for creating instances of a given entity.
 */
public interface Creator {
	/**
	 * Creates an instance of a given entity.
	 *
	 * @param entityType the entity type
	 * @param headers the headers
	 * @param line the data line
	 * @return the created instance
	 */
	Object create(Class<?> entityType, List<Header> headers, List<String> line);
}
