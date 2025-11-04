package org.example.sampleproblemspringweb.exception;

import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

import java.net.URI;

public class UserNotFoundException extends AbstractThrowableProblem {

    private static final URI TYPE = URI.create("https://example.org/problems/user-not-found");

    public UserNotFoundException(Long userId) {
        super(
            TYPE,
            "User Not Found",
            Status.NOT_FOUND,
            String.format("User with id %d not found", userId)
        );
    }
}

