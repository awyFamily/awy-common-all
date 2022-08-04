package com.awy.common.rule;

import com.awy.common.util.utils.CollUtil;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author yhw
 * @date 2022-08-02
 */
public class Rules {

    private Map<String,IRule> repository;

    public Rules() {
        this.repository = new ConcurrentHashMap<>();
    }

    public void registry(IRule rule) {
        repository.put(rule.getName(),rule);
    }

    public void unRegistry(IRule rule) {
        repository.remove(rule.getName());
    }

    public IRule  getRulesByName(String name) {
        return repository.get(name);
    }

    public List<IRule>  getRulesByGroupName(String groupName) {
        List<IRule> rules = new ArrayList<>();
        for (Map.Entry<String, IRule> entry : repository.entrySet()) {
            if (groupName.equals(entry.getValue().getGroupName())) {
                rules.add(entry.getValue());
            }
        }
        if (CollUtil.isEmpty(rules)) {
            return rules;
        }
        return rules.stream().sorted(Comparator.comparing(IRule::getPriority)).collect(Collectors.toList());
    }

    public boolean isSupportByGroup(String groupName,String key,String content) {
        List<IRule> rules = this.getRulesByGroupName(groupName);
        if (CollUtil.isEmpty(rules)) {
            return true;
        }
        for (IRule rule : rules) {
            if (!rule.isSupport(key,content)) {
                return false;
            }
        }
        return true;
    }


}
