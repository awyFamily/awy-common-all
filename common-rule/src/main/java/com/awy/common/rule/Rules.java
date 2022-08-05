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

    public void unRegistryByGroupName(String groupName) {
        List<IRule> rules = new ArrayList<>();
        for (Map.Entry<String, IRule> entry : repository.entrySet()) {
            if (groupName.equals(entry.getValue().getGroupName())) {
                rules.add(entry.getValue());
            }
        }
        for (IRule rule : rules) {
            repository.remove(rule.getName());
        }
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
        boolean support;
        for (IRule rule : rules) {
            try {
                support = rule.isSupport(key, content);
            } catch (Exception e) {
                log.error("trigger error,rule type : {} ,rule name : {} , message : {}",rule.getType(),rule.getName(),e);
                support = false;
            }
            if (RuleChainNodeTypeNum.fail_end == rule.getChainNodeType() && !support) {
                log.debug("not met condition , rule type : {} ,rule name : {}",rule.getType(),rule.getName());
                return false;
            } else if (RuleChainNodeTypeNum.success_end == rule.getChainNodeType() && support) {
                //当前节点满足,直接跳出
                log.info("met condition , end process , rule type : {} ,rule name : {}",rule.getType(),rule.getName());
                break;
            } else if (RuleChainNodeTypeNum.fail_continue == rule.getChainNodeType()) {
                //忽略此次错误
                log.error("not met condition , fail ignore , rule type : {} ,rule name : {}",rule.getType(),rule.getName());
                continue;
            } else if (RuleChainNodeTypeNum.success_end_fail_continue == rule.getChainNodeType()) {
                if (support) {
                    log.info("met condition , end process , rule type : {} ,rule name : {}",rule.getType(),rule.getName());
                    break;
                }
                //错误忽略逻辑
                log.debug("not met condition , fail ignore , rule type : {} ,rule name : {}",rule.getType(),rule.getName());
                continue;
            }
            //
            if (!support) {
                log.debug("not met condition , rule type : {} ,rule name : {}",rule.getType(),rule.getName());
                return false;
            }
        }
        for (IRule rule : rules) {
            rule.successCallback(key,content);
        }
        return true;
    }

}
