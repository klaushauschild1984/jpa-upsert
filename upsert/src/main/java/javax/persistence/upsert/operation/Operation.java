package javax.persistence.upsert.operation;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Operation {
	INSERT(InsertPerformer.class),

	UPDATE(null),

	UPSERT(null),

	DELETE(null);

	private final Class<? extends Performer> performerType;
}
