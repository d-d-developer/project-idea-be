package project_idea.idea.exceptions;

import java.util.UUID;

public class NotFoundException extends RuntimeException {
	public NotFoundException(UUID id) {
		super("The record with id " + id + " could not be found!");
	}

	public NotFoundException(String msg) {
		super(msg);
	}
}