package com.awy.common.ws.client.reader;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SimpleCloseReader implements CloseReader {

    @Override
    public void onClose() {
        System.out.println("channel close ................");
        log.info("channel close ................");
    }
}
