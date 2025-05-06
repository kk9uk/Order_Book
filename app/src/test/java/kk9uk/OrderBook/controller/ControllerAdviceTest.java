package kk9uk.OrderBook.controller;

import kk9uk.OrderBook.dto.ErrorDto;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

class ControllerAdviceTest {

    @Test
    void errorHandler_WhenRuntimeException_ReturnsErrorDtoWithMessage() {
        // Arrange
        ControllerAdvice controllerAdvice = new ControllerAdvice();
        RuntimeException mockedException = Mockito.mock(RuntimeException.class);
        Mockito.when(mockedException.getMessage()).thenReturn("Test error message");

        // Act
        ErrorDto result = controllerAdvice.errorHandler(mockedException);

        // Assert
        assertEquals("Test error message", result.error());
        Mockito.verify(mockedException).getMessage();
    }

    @Test
    void errorHandlerMethod_HasExceptionHandlerAnnotation() throws NoSuchMethodException {
        // Arrange
        Method method = ControllerAdvice.class.getMethod("errorHandler", RuntimeException.class);

        // Act
        ExceptionHandler annotation = method.getAnnotation(ExceptionHandler.class);

        // Assert
        assertNotNull(annotation, "Method should have @ExceptionHandler annotation");
        assertArrayEquals(new Class[]{RuntimeException.class}, annotation.value());
    }

    @Test
    void errorHandlerMethod_HasResponseStatusAnnotation() throws NoSuchMethodException {
        // Arrange
        Method method = ControllerAdvice.class.getMethod("errorHandler", RuntimeException.class);

        // Act
        ResponseStatus annotation = method.getAnnotation(ResponseStatus.class);

        // Assert
        assertNotNull(annotation, "Method should have @ResponseStatus annotation");
        assertEquals(HttpStatus.BAD_REQUEST, annotation.value());
    }
}