package javax.persistence.upsert.entity;

import java.util.List;

/**
 * Responsible to find entity instance by reference.
 */
public interface Finder {
	/**
	 * Finds entity instance.
	 *
	 * @param entityType the entity type
	 * @param references the references
	 * @param value the values
	 * @return the found instance
	 */
	Object findBy(Class<?> entityType, List<String> references, String value);
}
