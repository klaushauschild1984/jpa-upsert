package javax.persistence.upsert;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.StreamUtils;

@Slf4j
@UtilityClass
class JpaUpsertBanner {

	static void banner() {
		try {
			final Resource bannerResource = new ClassPathResource("upsert-banner.txt");
			final String rawBanner = StreamUtils.copyToString(bannerResource.getInputStream(), StandardCharsets.UTF_8);
			final Resource versionResource = new ClassPathResource("version.txt");
			final String rawVersion = StreamUtils.copyToString(versionResource.getInputStream(), StandardCharsets.UTF_8);
			final String version = String.format(
				"%sv(%s)",
				IntStream.range(0, 30 - rawVersion.length()).mapToObj(index -> " ").collect(Collectors.joining()),
				rawVersion
			);
			final String banner = String.format("%s :: upsert :: %s%n", rawBanner, version);
			log.info(banner);
		} catch (final IOException exception) {
			log.warn("Unable to initialize banner.", exception);
		}
	}
}
