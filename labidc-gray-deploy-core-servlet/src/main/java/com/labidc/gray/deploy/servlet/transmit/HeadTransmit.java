package com.labidc.gray.deploy.servlet.transmit;

/***
 *
 * @author xiongchuang
 * @date 2018-01-15
 */
public interface HeadTransmit<S, N> {

    void transmit(S transmitSource, N needTransmit);
}
