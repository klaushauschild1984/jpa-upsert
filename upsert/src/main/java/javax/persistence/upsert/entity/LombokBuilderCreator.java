package javax.persistence.upsert.entity;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;
import javax.persistence.upsert.JpaUpsertException;
import javax.persistence.upsert.model.Header;
import javax.persistence.upsert.model.Modifier;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

/**
 * Implementation of {@link Creator} utilizing <a href='https://projectlombok.org/'>Project Lombok's</a>
 * {@link lombok.Builder} / {@link lombok.experimental.SuperBuilder} pattern.
 */
@RequiredArgsConstructor
@Slf4j
public class LombokBuilderCreator implements Creator {

	private static final String BUILDER_METHOD_NAME = "builder";
	private static final String BUILD_METHOD_NAME = "build";
	private static final String IMPL_CLASSNAME_SUFFIX = "Impl";

	private final Finder finder;

	@Override
	public Object create(final Class<?> entityType, final List<Header> headers, final List<String> line) {
		log.debug("Create {} with {}", entityType.getSimpleName(), line);

		final Method builderMethod = ReflectionUtils.findMethod(entityType, BUILDER_METHOD_NAME);
		final Object builder = ReflectionUtils.invokeMethod(builderMethod, null);
		final Class<?> builderClass = builderClass(entityType, builder);

		IntStream
			.range(0, line.size())
			.forEach(index -> {
				final Header header = headers.get(index);
				final Method propertyMethod = Arrays
					.stream(builderClass.getMethods())
					.filter(method -> method.getName().equalsIgnoreCase(header.getName()))
					.findAny()
					.orElseThrow(() ->
						new JpaUpsertException(
							String.format("No builder method for %s at %s", header.getName(), entityType.getName())
						)
					);
				final Class<?> propertyType = propertyMethod.getParameterTypes()[0];
				String value = line.get(index).trim();
				if (!StringUtils.hasText(value)) {
					value = (String) header.getModifiers().get(Modifier.DEFAULT);
				}

				final Object property;
				if (!header.getReferences().isEmpty()) {
					property = finder.findBy(propertyType, header.getReferences(), value);
				} else {
					property = convert(value, propertyType);
				}
				ReflectionUtils.invokeMethod(propertyMethod, builder, property);
			});

		final Method buildMethod = ReflectionUtils.findMethod(builderClass, BUILD_METHOD_NAME);
		final Object entity = ReflectionUtils.invokeMethod(buildMethod, builder);
		log.debug("Created {}", entity);
		return entity;
	}

	private Class<?> builderClass(final Class<?> entityType, final Object builder) {
		try {
			final String builderImplClassName = builder.getClass().getName();
			final String builderClassName = builderImplClassName.substring(
				0,
				builderImplClassName.length() - IMPL_CLASSNAME_SUFFIX.length()
			);
			return Class.forName(builderClassName);
		} catch (final ClassNotFoundException exception) {
			throw new JpaUpsertException(
				String.format("Unable to access builder class for %s", entityType.getName()),
				exception
			);
		}
	}

	private Object convert(final String value, final Class<?> propertyType) {
		if (propertyType.equals(String.class)) {
			return value;
		}
		if (propertyType.equals(int.class) || propertyType.equals(Integer.class)) {
			return Integer.parseInt(value);
		}
		if (propertyType.isEnum()) {
			final Method valueOfMethod = ReflectionUtils.findMethod(propertyType, "valueOf", String.class);
			return ReflectionUtils.invokeMethod(valueOfMethod, null, value);
		}

		throw new JpaUpsertException(String.format("Unable to convert %s into %s", value, propertyType.getName()));
	}
}
