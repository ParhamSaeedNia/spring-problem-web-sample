package org.example.sampleproblemspringweb.exception;

import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

import java.net.URI;

public class EmailAlreadyExistsException extends AbstractThrowableProblem {

    private static final URI TYPE = URI.create("https://example.org/problems/email-already-exists");

    public EmailAlreadyExistsException(String email) {
        super(
            TYPE,
            "Email Already Exists",
            Status.CONFLICT,
            String.format("User with email %s already exists", email)
        );
    }
}

