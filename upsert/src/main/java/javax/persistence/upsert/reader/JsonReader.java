package javax.persistence.upsert.reader;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import javax.persistence.upsert.JpaUpsertException;
import javax.persistence.upsert.model.Upsert;
import org.springframework.core.io.Resource;

public class JsonReader implements Reader {

	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

	@Override
	public List<Upsert> read(final Resource resource) {
		try (final InputStream inputStream = resource.getInputStream()) {
			return OBJECT_MAPPER.readValue(inputStream, new TypeReference<>() {});
		} catch (final IOException exception) {
			throw new JpaUpsertException("Unable to read upsert.", exception);
		}
	}
}
