package com.workintech.course.exceptions;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
@Getter
@Setter
public class CourseException extends RuntimeException {
    private HttpStatus status;//kendimiz statusu dönelim diye

    public CourseException(String message,HttpStatus status) {
        super(message);
        this.status=status;
    }
}
