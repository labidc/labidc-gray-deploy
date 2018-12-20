package com.labidc.gray.deploy.exception;
/**
 * @program: labidc-manager
 * @description: 自定义运行时不需检查异常
 * @author: ChenXingLiang
 * @date: 2018-11-08 19:19
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
