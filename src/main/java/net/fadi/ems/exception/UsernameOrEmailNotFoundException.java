package net.fadi.ems.exception;

public class UsernameOrEmailNotFoundException extends RuntimeException {
    public UsernameOrEmailNotFoundException(String message) {
        super(message);
    }
}
