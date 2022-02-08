package javax.persistence.upsert.model;

import java.util.function.Function;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Modifier {
	UNIQUE(Boolean::parseBoolean, true),

	DEFAULT(value -> value, null);

	private final Function<String, Object> evaluate;
	private final Object defaultValue;
}
