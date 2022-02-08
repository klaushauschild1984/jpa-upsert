package javax.persistence.upsert.reader;

import java.util.List;
import javax.persistence.upsert.model.Upsert;
import org.springframework.core.io.Resource;

public interface Reader {
	List<Upsert> read(Resource resource);
}
