package com.labidc.gray.deploy.exception;

/**
 * 自定义运行时不需检查异常
 **/
public class DiscoveryServerException extends RuntimeException {

    public DiscoveryServerException() {
        super();
    }

    public DiscoveryServerException(String message) {
        super(message);
    }

    public DiscoveryServerException(String message, Throwable cause) {
        super(message, cause);
    }

    public DiscoveryServerException(Throwable cause) {
        super(cause);
    }
}
