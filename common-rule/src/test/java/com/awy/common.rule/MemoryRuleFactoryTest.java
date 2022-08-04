package com.awy.common.rule;

import cn.hutool.json.JSONUtil;
import com.awy.common.rule.enums.RuleTypeEnum;
import com.awy.common.rule.memory.MemoryRuleFactory;
import com.awy.common.rule.model.FixedNumberRuleModel;
import com.awy.common.rule.model.FloatValueRuleModel;
import com.awy.common.rule.model.RuleModel;
import com.awy.common.rule.model.TimerRuleModel;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author yhw
 * @date 2022-08-04
 */
public class MemoryRuleFactoryTest {

    private RuleFactory ruleFactory;

    @Before
    public void init() {
        ruleFactory = new MemoryRuleFactory();
    }


    @Test
    public void floatValueRuleTest() {
        IRule rule = ruleFactory.create(getFloatValueRuleModel());

        Map<String,String> floatMap = new HashMap<>();
        floatMap.put("a","10");
//        System.err.printf("first trigger : %s \n", rule.isSupport("ABC",JSONUtil.toJsonStr(floatMap)));
        Assert.assertTrue(rule.isSupport("ABC",JSONUtil.toJsonStr(floatMap)));

        floatMap = new HashMap<>();
        floatMap.put("a","13");
        //condition not met
        Assert.assertFalse(rule.isSupport("ABC",JSONUtil.toJsonStr(floatMap)));

        floatMap = new HashMap<>();
        floatMap.put("a","14");
        Assert.assertFalse(rule.isSupport("ABC",JSONUtil.toJsonStr(floatMap)));

        floatMap = new HashMap<>();
        floatMap.put("a","15");
        //condition is met
        Assert.assertTrue(rule.isSupport("ABC",JSONUtil.toJsonStr(floatMap)));

        floatMap = new HashMap<>();
        floatMap.put("a","16");
        //condition not met
        Assert.assertFalse(rule.isSupport("ABC",JSONUtil.toJsonStr(floatMap)));

        //new key
        floatMap = new HashMap<>();
        floatMap.put("a","10");
        //first trigger
        Assert.assertTrue(rule.isSupport("BCC",JSONUtil.toJsonStr(floatMap)));

        floatMap = new HashMap<>();
        floatMap.put("a","16");
        //condition is met
        Assert.assertTrue(rule.isSupport("BCC",JSONUtil.toJsonStr(floatMap)));

        floatMap = new HashMap<>();
        floatMap.put("a","10");
        //condition is met
        Assert.assertTrue(rule.isSupport("BCC",JSONUtil.toJsonStr(floatMap)));
    }

    private RuleModel getFloatValueRuleModel() {
        FloatValueRuleModel floatValueRuleModel = new FloatValueRuleModel();
        Map<String,String> floatMap = new HashMap<>();
        floatMap.put("a","5");
        floatValueRuleModel.setFloatValueMaps(JSONUtil.toJsonStr(floatMap));

        RuleModel model = new RuleModel();
        model.setRuleType(RuleTypeEnum.FLOAT_VALUE.getId());
        model.setName("2");
        model.setPriority(2);
        model.setExpand(JSONUtil.toJsonStr(floatValueRuleModel));
        return model;
    }

    @Test
    public void fixedNumberRuleTest() throws Exception {
        IRule rule = ruleFactory.create(getFixedNumberRuleModel());

        long start = System.currentTimeMillis();
        Assert.assertTrue(rule.isSupport("ABC","1"));

        TimeUnit.SECONDS.sleep(2);
        System.out.println("true : " + (System.currentTimeMillis() - start) / 1000);
        Assert.assertTrue(rule.isSupport("ABC","1"));

        TimeUnit.SECONDS.sleep(2);
        System.out.println("false : " + (System.currentTimeMillis() - start) / 1000);
        Assert.assertFalse(rule.isSupport("ABC","1"));

        TimeUnit.SECONDS.sleep(2);
        System.out.println("true : " + (System.currentTimeMillis() - start) / 1000);
        Assert.assertTrue(rule.isSupport("ABC","1"));

        TimeUnit.SECONDS.sleep(1);
        System.out.println("true : " + (System.currentTimeMillis() - start) / 1000);
        Assert.assertTrue(rule.isSupport("ABC","1"));

        TimeUnit.SECONDS.sleep(2);
        System.out.println("false : " + (System.currentTimeMillis() - start) / 1000);
        Assert.assertFalse(rule.isSupport("ABC","1"));

        TimeUnit.SECONDS.sleep(2);
        System.out.println("true : " + (System.currentTimeMillis() - start) / 1000);
        Assert.assertTrue(rule.isSupport("ABC","1"));
    }

    private RuleModel getFixedNumberRuleModel() {
        FixedNumberRuleModel fixedNumberRuleModel = new FixedNumberRuleModel();
        fixedNumberRuleModel.setTimeout(3L);
        fixedNumberRuleModel.setFixedNumber(2);

        RuleModel model = new RuleModel();
        model.setRuleType(RuleTypeEnum.FIXED_NUMBER.getId());
        model.setName("1");
        model.setPriority(1);
        model.setExpand(JSONUtil.toJsonStr(fixedNumberRuleModel));
        return model;
    }

    @Test
    public void timerRuleTest() throws Exception {
        IRule rule = ruleFactory.create(getTimerRuleModel());

        long start = System.currentTimeMillis();
        Assert.assertTrue(rule.isSupport("ABC","1"));

        TimeUnit.SECONDS.sleep(2);
        System.out.println("false : " + (System.currentTimeMillis() - start) / 1000);
        Assert.assertFalse(rule.isSupport("ABC","1"));

        TimeUnit.SECONDS.sleep(3);
        System.out.println("true : " + (System.currentTimeMillis() - start) / 1000);
        Assert.assertTrue(rule.isSupport("ABC","1"));
    }


    private RuleModel getTimerRuleModel() {
        TimerRuleModel timerRuleModel = new TimerRuleModel();
        timerRuleModel.setTimeout(3L);

        RuleModel model = new RuleModel();
        model.setRuleType(RuleTypeEnum.TIMER.getId());
        model.setName("3");
        model.setPriority(3);
        model.setExpand(JSONUtil.toJsonStr(timerRuleModel));
        return model;
    }
}
