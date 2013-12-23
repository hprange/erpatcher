package com.erpatcher;

/**
 * @author <a href="mailto:hprange@gmail.com.br">Henrique Prange</a>
 */
public class ERPatcherError extends Error {
    public ERPatcherError() {
        super();
    }

    public ERPatcherError(String message, Throwable cause) {
        super(message, cause);
    }

    public ERPatcherError(String message) {
        super(message);
    }

    public ERPatcherError(Throwable cause) {
        super(cause);
    }
}
