package com.awy.common.rule.redis;

import cn.hutool.core.util.StrUtil;
import com.awy.common.rule.FixedNumberRule;
import com.awy.common.rule.enums.RuleChainNodeTypeNum;
import com.awy.common.rule.model.FixedNumberRuleModel;
import lombok.Setter;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.concurrent.TimeUnit;

/**
 * @author yhw
 * @date 2022-08-02
 */
public class RedisFixedNumberRule extends FixedNumberRule<FixedNumberRuleModel> {

    @Setter
    private StringRedisTemplate redisTemplate;
    @Setter
    private long timeout;

    public RedisFixedNumberRule(String name, int priority, StringRedisTemplate redisTemplate) {
        this(name,priority,"default",redisTemplate);
    }

    public RedisFixedNumberRule(String name, int priority, String groupName, StringRedisTemplate redisTemplate) {
        this(name, priority,groupName,RuleChainNodeTypeNum.fail_end,redisTemplate);
    }

    public RedisFixedNumberRule(String name, int priority, String groupName,RuleChainNodeTypeNum ruleChainNodeTypeNum, StringRedisTemplate redisTemplate) {
        super(name, priority, 3,groupName,ruleChainNodeTypeNum);
        this.timeout = 60;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public int getNumber(String key) {
        String str = redisTemplate.opsForValue().get(key);
        return StrUtil.isBlank(str) ? 0 : Integer.valueOf(str);
    }

    @Override
    public Boolean increment(String key) {
        String str = redisTemplate.opsForValue().get(key);
        if (StrUtil.isNotBlank(str)) {
            int number = Integer.valueOf(str);
            number++;
            redisTemplate.opsForValue().set(key,number + "",redisTemplate.getExpire(key, TimeUnit.SECONDS));
            return true;
        }
        redisTemplate.opsForValue().set(key,"1",timeout,TimeUnit.SECONDS);
        return true;
    }

    @Override
    public void buildRuleConfig(FixedNumberRuleModel model) {
        if (model == null) {
            return;
        }
        setFixedNumber(model.getFixedNumber());
        setTimeout(model.getTimeout());
    }
}
