package ru.ifmo.is.together.common.errors;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {
  @ExceptionHandler({ DetailedApiError.class })
  public ResponseEntity<Object> handleDetailedApiError(DetailedApiError ex) {
    logger.info(ex.getClass().getName());
    final ApiError apiError = ex.getApiError();
    return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
  }

  // 401
  @ExceptionHandler(value = { PolicyViolationError.class, PolicyViolationError.class })
  protected ResponseEntity<Object> handleConflict(RuntimeException ex, WebRequest request) {
    logger.info(ex.getClass().getName());
    final ApiError apiError = new ApiError(HttpStatus.UNAUTHORIZED, ex.getLocalizedMessage(), "Policy Violation");
    return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
  }

  @ExceptionHandler({ AuthenticationException.class })
  public ResponseEntity<Object> handleAuthenticationException(Exception ex) {
    logger.info(ex.getClass().getName());
    final ApiError apiError = new ApiError(HttpStatus.UNAUTHORIZED, ex.getLocalizedMessage(), "Authentication failed");
    return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
  }

  // 403
  @ExceptionHandler({ AccessDeniedException.class })
  public ResponseEntity<Object> handleAccessDeniedException(Exception ex) {
    logger.info(ex.getClass().getName());
    final ApiError apiError = new ApiError(
      HttpStatus.FORBIDDEN,
      ex.getLocalizedMessage(),
      "You have not been granted authority to perform this action"
    );
    return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
  }

  @ExceptionHandler({ TokenExpiredException.class })
  public ResponseEntity<Object> handleTokenRefreshException(Exception ex) {
    logger.info(ex.getClass().getName());
    final ApiError apiError = new ApiError(
      HttpStatus.FORBIDDEN,
      ex.getLocalizedMessage(),
      "Failed for token"
    );
    return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
  }

  // 422
  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(final MethodArgumentNotValidException ex, final HttpHeaders headers, final HttpStatusCode status, final WebRequest request) {
    logger.info(ex.getClass().getName());
    final List<String> errors = new ArrayList<>();
    for (final FieldError error : ex.getBindingResult().getFieldErrors()) {
      errors.add(error.getField() + ": " + error.getDefaultMessage());
    }
    for (final ObjectError error : ex.getBindingResult().getGlobalErrors()) {
      errors.add(error.getObjectName() + ": " + error.getDefaultMessage());
    }
    final ApiError apiError = new ApiError(HttpStatus.UNPROCESSABLE_ENTITY, ex.getLocalizedMessage(), errors);
    return handleExceptionInternal(ex, apiError, headers, apiError.getStatus(), request);
  }

  @Override
  protected ResponseEntity<Object> handleTypeMismatch(TypeMismatchException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
    logger.info(ex.getClass().getName());
    final String error = ex.getValue() + " value for " + ex.getPropertyName() + " should be of type " + ex.getRequiredType();

    final ApiError apiError = new ApiError(HttpStatus.UNPROCESSABLE_ENTITY, ex.getLocalizedMessage(), error);
    return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
  }

  @ExceptionHandler({ ConstraintViolationException.class })
  public ResponseEntity<Object> handleConstraintViolation(final ConstraintViolationException ex, final WebRequest request) {
    logger.info(ex.getClass().getName());
    final List<String> errors = new ArrayList<>();
    for (final ConstraintViolation<?> violation : ex.getConstraintViolations()) {
      errors.add(violation.getRootBeanClass().getName() + " " + violation.getPropertyPath() + ": " + violation.getMessage());
    }

    final ApiError apiError = new ApiError(HttpStatus.UNPROCESSABLE_ENTITY, ex.getLocalizedMessage(), errors);
    return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
  }

  // 400
  @Override
  protected ResponseEntity<Object> handleMissingServletRequestPart(final MissingServletRequestPartException ex, final HttpHeaders headers, final HttpStatusCode status, final WebRequest request) {
    logger.info(ex.getClass().getName());
    final String error = ex.getRequestPartName() + " part is missing";
    final ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, ex.getLocalizedMessage(), error);
    return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
  }

  @Override
  protected ResponseEntity<Object> handleMissingServletRequestParameter(final MissingServletRequestParameterException ex, final HttpHeaders headers, final HttpStatusCode status, final WebRequest request) {
    logger.info(ex.getClass().getName());
    final String error = ex.getParameterName() + " parameter is missing";
    final ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, ex.getLocalizedMessage(), error);
    return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
  }

  @ExceptionHandler({ MethodArgumentTypeMismatchException.class })
  public ResponseEntity<Object> handleMethodArgumentTypeMismatch(final MethodArgumentTypeMismatchException ex, final WebRequest request) {
    logger.info(ex.getClass().getName());
    final String error = ex.getName() + " should be of type " + ex.getRequiredType().getName();

    final ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, ex.getLocalizedMessage(), error);
    return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
  }

  // 404
  @Override
  protected ResponseEntity<Object> handleNoHandlerFoundException(final NoHandlerFoundException ex, final HttpHeaders headers, final HttpStatusCode status, final WebRequest request) {
    logger.info(ex.getClass().getName());
    final String error = "No handler found for " + ex.getHttpMethod() + " " + ex.getRequestURL();

    final ApiError apiError = new ApiError(HttpStatus.NOT_FOUND, ex.getLocalizedMessage(), error);
    return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
  }

  @ExceptionHandler({ ResourceNotFoundException.class })
  public ResponseEntity<Object> handleResourceNotFoundException(Exception ex) {
    logger.info(ex.getClass().getName());
    final ApiError apiError = new ApiError(
      HttpStatus.NOT_FOUND,
      ex.getLocalizedMessage(),
      "Resource not found"
    );
    return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
  }

  // 405
  @Override
  protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(final HttpRequestMethodNotSupportedException ex, final HttpHeaders headers, final HttpStatusCode status, final WebRequest request) {
    logger.info(ex.getClass().getName());
    final StringBuilder builder = new StringBuilder();
    builder.append(ex.getMethod());
    builder.append(" method is not supported for this request. Supported methods are ");
    Objects.requireNonNull(ex.getSupportedHttpMethods()).forEach(t -> builder.append(t).append(" "));

    final ApiError apiError = new ApiError(HttpStatus.METHOD_NOT_ALLOWED, ex.getLocalizedMessage(), builder.toString());
    return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
  }

  // 409
  @ExceptionHandler({ ResourceAlreadyExists.class })
  public ResponseEntity<Object> handleResourceAlreadyExists(Exception ex) {
    logger.info(ex.getClass().getName());
    final ApiError apiError = new ApiError(
      HttpStatus.CONFLICT,
      ex.getLocalizedMessage(),
      "Resource already exists"
    );
    return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
  }

  // 410
  @ExceptionHandler({ StaleRequestException.class })
  public ResponseEntity<Object> handleStaleRequestException(Exception ex) {
    logger.info(ex.getClass().getName());
    final ApiError apiError = new ApiError(
      HttpStatus.GONE,
      ex.getLocalizedMessage(),
      "You are trying to perform an action whose time limit has expired"
    );
    return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
  }

  // 415
  @Override
  protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(final HttpMediaTypeNotSupportedException ex, final HttpHeaders headers, final HttpStatusCode status, final WebRequest request) {
    logger.info(ex.getClass().getName());
    final StringBuilder builder = new StringBuilder();
    builder.append(ex.getContentType());
    builder.append(" media type is not supported. Supported media types are ");
    ex.getSupportedMediaTypes().forEach(t -> builder.append(t).append(" "));

    final ApiError apiError = new ApiError(HttpStatus.UNSUPPORTED_MEDIA_TYPE, ex.getLocalizedMessage(), builder.substring(0, builder.length() - 2));
    return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
  }

  // 429
  @ExceptionHandler({ TooManyRequests.class })
  public ResponseEntity<Object> handleTooManyRequests(Exception ex) {
    logger.info(ex.getClass().getName());
    final ApiError apiError = new ApiError(
      HttpStatus.TOO_MANY_REQUESTS,
      ex.getLocalizedMessage(),
      "Too many requests. Try later"
    );
    return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
  }

  // 500
  @ExceptionHandler({ Exception.class })
  public ResponseEntity<Object> handleAll(final Exception ex, final WebRequest request) {
    logger.info(ex.getClass().getName());
    logger.error("error", ex);
    final ApiError apiError = new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, ex.getLocalizedMessage(), "error occurred");
    return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
  }
}
