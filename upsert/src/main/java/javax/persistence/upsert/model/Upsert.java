package javax.persistence.upsert.model;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import javax.persistence.upsert.JpaUpsertException;
import javax.persistence.upsert.operation.Operation;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.StringUtils;

@Setter
public class Upsert {

	@Getter
	private Operation operation;

	private String entity;
	private String header;
	private List<String> data;

	public Class<?> getEntityType(final String entityPackage) {
		try {
			return Class.forName(String.format("%s.%s", entityPackage, entity));
		} catch (final ClassNotFoundException exception) {
			throw new JpaUpsertException(
				String.format("Unable to load entity class for %s from package %s", entity, entityPackage),
				exception
			);
		}
	}

	public List<Header> getHeaders() {
		return splitLine(header)
			.map(head -> {
				final int referencesStart = head.indexOf('(');
				final int referencesEnd = head.indexOf(')');
				final int modifiersStart = head.indexOf('[');
				final int modifiersEnd = head.indexOf(']');

				final Header.HeaderBuilder<?, ?> headerBuilder = Header.builder();

				// name
				if (referencesStart != -1 && referencesEnd != -1) {
					headerBuilder.name(head.substring(0, referencesStart).trim());
				} else if (modifiersStart != -1 && modifiersEnd != -1) {
					headerBuilder.name(head.substring(0, modifiersStart).trim());
				} else {
					headerBuilder.name(head.trim());
				}

				// references
				if (referencesStart != -1 && referencesEnd != -1) {
					headerBuilder.references(head.substring(referencesStart + 1, referencesEnd).trim());
				}

				// modifiers
				if (modifiersStart != -1 && modifiersEnd != -1) {
					headerBuilder.modifiers(head.substring(modifiersStart + 1, modifiersEnd).trim());
				}

				return headerBuilder.build();
			})
			.collect(Collectors.toList());
	}

	public List<List<String>> getData() {
		return data //
			.stream()
			.map(line ->
				splitLine(line) //
					.collect(Collectors.toList())
			)
			.collect(Collectors.toList());
	}

	private Stream<String> splitLine(final String line) {
		if (!StringUtils.hasText(line)) {
			return Stream.empty();
		}
		return Arrays.stream(line.split(";")).map(String::trim);
	}
}
