package cy.resources.exception;

import cy.dtos.CustomHandleException;
import cy.dtos.ResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.validation.FieldError;


@RestControllerAdvice
public class HandleException {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> inValidArguments(MethodArgumentNotValidException ex) {
        ex.printStackTrace();
        Map<Object, String> errors = new HashMap<>();
        //collect errors
        for (final FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        return ResponseEntity.badRequest()
                .body(ResponseDto.of(1, errors));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> resolverException(HttpMessageNotReadableException ex) {
        return ResponseEntity.badRequest()
                .body(ResponseDto.of(2, ex.getMessage()));
    }

    @ExceptionHandler(CustomHandleException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseDto handleError(CustomHandleException ex) {
        ex.printStackTrace();
        return ResponseDto.ofError(ex.getCode());
    }
}
