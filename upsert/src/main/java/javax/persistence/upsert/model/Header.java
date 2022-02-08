package javax.persistence.upsert.model;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
public class Header {

	@Getter
	private String name;

	private String references;

	private String modifiers;

	public List<String> getReferences() {
		if (references == null) {
			return List.of();
		}
		return Arrays.stream(references.split(",")).collect(Collectors.toList());
	}

	public Map<Modifier, Object> getModifiers() {
		if (modifiers == null) {
			return Map.of();
		}
		return Arrays
			.stream(modifiers.split(","))
			.collect(
				Collectors.toMap(
					definition -> getModifier(definition.split("=")),
					definition -> {
						final String[] split = definition.split("=");
						final Modifier modifier = getModifier(split);
						if (split.length == 1) {
							return modifier.getDefaultValue();
						}
						return modifier.getEvaluate().apply(split[1].trim());
					}
				)
			);
	}

	private Modifier getModifier(final String[] definition) {
		return Modifier.valueOf(definition[0].trim().toUpperCase());
	}

	@Override
	public String toString() {
		return String.format(
			"%s(%s)[%s]",
			name,
			getReferences().stream().map(Object::toString).collect(Collectors.joining(", ")),
			getModifiers()
				.entrySet()
				.stream()
				.map(modifier -> String.format("%s = %s", modifier.getKey(), modifier.getValue()))
				.collect(Collectors.joining(", "))
		);
	}
}
