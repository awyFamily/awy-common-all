package com.awy.common.log.listener;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * 定义日志记录事件
 */
public class LogNoteEvent extends ApplicationEvent {

    @Getter
    private LogMessage logMessage;

    /**
     * Create a new ApplicationEvent.
     * @param source the object on which the event initially occurred (never {@code null})
     */
    public LogNoteEvent(Object source, LogMessage logMessage) {
//    public LogNoteEvent(LogMessage source) {
        super(source);
        this.logMessage = logMessage;
    }


}
