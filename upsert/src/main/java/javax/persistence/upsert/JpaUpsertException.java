package javax.persistence.upsert;

public class JpaUpsertException extends RuntimeException {

	public JpaUpsertException(final String message) {
		super(message);
	}

	public JpaUpsertException(final String message, final Throwable cause) {
		super(message, cause);
	}
}
