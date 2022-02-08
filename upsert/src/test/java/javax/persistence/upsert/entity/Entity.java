package javax.persistence.upsert.entity;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
public class Entity {

	private String string;
	private int integer;
	private Type type;
}
