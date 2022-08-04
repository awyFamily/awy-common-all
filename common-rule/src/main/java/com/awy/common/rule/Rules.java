package com.awy.common.rule;

import com.awy.common.rule.enums.RuleChainNodeTypeNum;
import com.awy.common.rule.memory.MemoryRuleFactory;
import com.awy.common.rule.model.RuleModel;
import com.awy.common.util.utils.CollUtil;
import lombok.extern.slf4j.Slf4j;

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
@Slf4j
public class Rules {

    private Map<String,IRule> repository;

    private RuleFactory ruleFactory;

    public Rules() {
        this(new MemoryRuleFactory());
    }

    public Rules(RuleFactory ruleFactory) {
        this.ruleFactory = ruleFactory;
        this.repository = new ConcurrentHashMap<>();
    }
    public void registry(RuleModel ruleModel) {
        IRule rule = this.ruleFactory.create(ruleModel);
        this.registry(rule);
    }

    public void registries(List<RuleModel> ruleModels) {
        if (CollUtil.isEmpty(ruleModels)) {
            return;
        }
        ruleModels.forEach(this::registry);
    }

    public void registry(IRule rule) {
        log.info("registry rule group : {} name : {} ",rule.getGroupName(),rule.getName());
        repository.put(rule.getName(),rule);
    }

    public void unRegistry(String name) {
        repository.remove(name);
    }

    public void unRegistry(IRule rule) {
        repository.remove(rule.getName());
    }

    public IRule  getRulesByName(String name) {
        return repository.get(name);
    }

    public List<IRule> getRulesByGroupName(String groupName) {
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
                log.info("not met condition , rule type : {} ,rule name : {}",rule.getType(),rule.getName());
                return false;
            }
            //当前节点满足,直接跳出
            if (RuleChainNodeTypeNum.one_success == rule.getChainNodeType()) {
                break;
            }
        }
        for (IRule rule : rules) {
            rule.successCallback(key,content);
        }
        return true;
    }

}
