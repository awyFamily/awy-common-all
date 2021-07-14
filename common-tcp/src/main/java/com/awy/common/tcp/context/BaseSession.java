package com.awy.common.tcp.context;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author yhw
 * @date 2021-07-14
 */
@NoArgsConstructor
@Data
public class BaseSession {

    private String sessionId;

    public BaseSession(String sessionId){
        this.sessionId = sessionId;
    }

    /*public BaseSession(IGenerateSessionID generate){
        this.sessionId = generate.generate();
    }*/
}
