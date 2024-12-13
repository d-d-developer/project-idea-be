package project_idea.idea.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import project_idea.idea.payloads.error.ErrorsResponseDTO;
import project_idea.idea.payloads.error.ErrorsResponseWithErrorsListDTO;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class ExceptionsHandler {

	@ExceptionHandler(BadRequestException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST) // 400
	public ErrorsResponseDTO handleBadrequest(BadRequestException ex) {
		return new ErrorsResponseDTO(ex.getMessage(), LocalDateTime.now());
	}

	@ExceptionHandler(UnauthorizedException.class)
	@ResponseStatus(HttpStatus.UNAUTHORIZED) // 401
	public ErrorsResponseDTO handleUnauthorized(UnauthorizedException ex) {
		return new ErrorsResponseDTO("Unauthorized: " + ex.getMessage(), LocalDateTime.now());
	}

	@ExceptionHandler(AuthorizationDeniedException.class)
	@ResponseStatus(HttpStatus.FORBIDDEN) // 403
	public ErrorsResponseDTO handleForbidden(AuthorizationDeniedException ex) {
		return new ErrorsResponseDTO("Forbidden: " + ex.getMessage(), LocalDateTime.now());
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST) // 400
	public ErrorsResponseDTO handleIllegalArgument(HttpMessageNotReadableException ex) {
		return new ErrorsResponseDTO(ex.getMessage(), LocalDateTime.now());
	}

	@ExceptionHandler(NotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND) // 404
	public ErrorsResponseDTO handleNotFound(NotFoundException ex) {
		return new ErrorsResponseDTO(ex.getMessage(), LocalDateTime.now());
	}

	@ExceptionHandler(IllegalArgumentException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST) // 400
	public ErrorsResponseDTO handleIllegalArgument(IllegalArgumentException ex) {
		return new ErrorsResponseDTO("Invalid argument: " + ex.getMessage(), LocalDateTime.now());
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST) // 400
	public ErrorsResponseWithErrorsListDTO handleValidationExceptions(MethodArgumentNotValidException ex) {
		Map<String, String> errors = new HashMap<>();
		ex.getBindingResult().getAllErrors().forEach((error) -> {
			String fieldName = ((FieldError) error).getField();
			String errorMessage = error.getDefaultMessage();
			errors.put(fieldName, errorMessage);
		});
		return new ErrorsResponseWithErrorsListDTO("Validation failed", LocalDateTime.now(), errors);
	}

	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR) // 500
	public ErrorsResponseDTO handleGeneric(Exception ex) {
		ex.printStackTrace();
		return new ErrorsResponseDTO("Internal Server error!", LocalDateTime.now());
	}
}
