package com.awy.common.rule;

/**
 * @author yhw
 * @date 2022-08-01
 */
public abstract class AbstractRule implements IRule {

    private String name;
    private int priority;

    public AbstractRule(String name, int priority) {
        this.name = name;
        this.priority = priority;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public int getPriority() {
        return this.priority;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }
}
