package com.awy.common.rule;

/**
 * @author yhw
 * @date 2022-08-01
 */
public abstract class AbstractRule<T> implements IRule<T> {

    private String name;
    private String groupName;
    private int priority;

    public AbstractRule(String name, int priority, String groupName) {
        this.name = name;
        this.groupName = groupName;
        this.priority = priority;
    }

    @Override
    public String getGroupName() {
        return this.groupName;
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

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
}
