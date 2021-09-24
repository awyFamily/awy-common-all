package com.awy.common.tcp.handler;

import com.awy.common.tcp.codec.BaseMessage;
/**
 * @author yhw
 * @date 2021-07-14
 */
public interface IBusinessProcess {

    /**
     *
     * @param message has break
     * @return boolean
     */
    boolean  process(BaseMessage message);

    boolean hasSupport(BaseMessage message);

    int sortAsc();

    default boolean pipeline(BaseMessage message){
        if (this.hasSupport(message)) {
            return this.process(message);
        }
        return false;
    }
}
