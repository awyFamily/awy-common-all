package com.awy.common.rule;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.awy.common.rule.enums.DefaultRuleTypeEnum;
import com.awy.common.rule.memory.MemoryRuleFactory;
import com.awy.common.rule.model.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
        Assert.assertTrue(rule.handler("ABC",JSONUtil.toJsonStr(floatMap)));

        floatMap = new HashMap<>();
        floatMap.put("a","13");
        //condition not met
        Assert.assertFalse(rule.handler("ABC",JSONUtil.toJsonStr(floatMap)));

        floatMap = new HashMap<>();
        floatMap.put("a","14");
        Assert.assertFalse(rule.handler("ABC",JSONUtil.toJsonStr(floatMap)));

        floatMap = new HashMap<>();
        floatMap.put("a","15");
        //condition is met
        Assert.assertTrue(rule.handler("ABC",JSONUtil.toJsonStr(floatMap)));

        floatMap = new HashMap<>();
        floatMap.put("a","16");
        //condition not met
        Assert.assertFalse(rule.handler("ABC",JSONUtil.toJsonStr(floatMap)));

        //new key
        floatMap = new HashMap<>();
        floatMap.put("a","10");
        //first trigger
        Assert.assertTrue(rule.handler("BCC",JSONUtil.toJsonStr(floatMap)));

        floatMap = new HashMap<>();
        floatMap.put("a","16");
        //condition is met
        Assert.assertTrue(rule.handler("BCC",JSONUtil.toJsonStr(floatMap)));

        floatMap = new HashMap<>();
        floatMap.put("a","10");
        //condition is met
        Assert.assertTrue(rule.handler("BCC",JSONUtil.toJsonStr(floatMap)));
    }

    private RuleModel getFloatValueRuleModel() {
        FloatValueRuleModel floatValueRuleModel = new FloatValueRuleModel();
        Map<String,String> floatMap = new HashMap<>();
        floatMap.put("a","5");
        floatValueRuleModel.setFloatValueMaps(JSONUtil.toJsonStr(floatMap));

        RuleModel model = new RuleModel();
        model.setRuleType(DefaultRuleTypeEnum.FLOAT_VALUE.getId());
        model.setName("2");
        model.setPriority(2);
        model.setExpand(JSONUtil.toJsonStr(floatValueRuleModel));
        return model;
    }

    @Test
    public void fixedNumberRuleTest() throws Exception {
        IRule rule = ruleFactory.create(getFixedNumberRuleModel());

        long start = System.currentTimeMillis();
        Assert.assertTrue(rule.handler("ABC","1"));

        TimeUnit.SECONDS.sleep(2);
        System.out.println("true : " + (System.currentTimeMillis() - start) / 1000);
        Assert.assertTrue(rule.handler("ABC","1"));

        TimeUnit.SECONDS.sleep(2);
        System.out.println("false : " + (System.currentTimeMillis() - start) / 1000);
        Assert.assertFalse(rule.handler("ABC","1"));

        TimeUnit.SECONDS.sleep(2);
        System.out.println("true : " + (System.currentTimeMillis() - start) / 1000);
        Assert.assertTrue(rule.handler("ABC","1"));

        TimeUnit.SECONDS.sleep(1);
        System.out.println("true : " + (System.currentTimeMillis() - start) / 1000);
        Assert.assertTrue(rule.handler("ABC","1"));

        TimeUnit.SECONDS.sleep(2);
        System.out.println("false : " + (System.currentTimeMillis() - start) / 1000);
        Assert.assertFalse(rule.handler("ABC","1"));

        TimeUnit.SECONDS.sleep(2);
        System.out.println("true : " + (System.currentTimeMillis() - start) / 1000);
        Assert.assertTrue(rule.handler("ABC","1"));
    }

    private RuleModel getFixedNumberRuleModel() {
        FixedNumberRuleModel fixedNumberRuleModel = new FixedNumberRuleModel();
        fixedNumberRuleModel.setTimeout(3L);
        fixedNumberRuleModel.setFixedNumber(2);

        RuleModel model = new RuleModel();
        model.setRuleType(DefaultRuleTypeEnum.FIXED_NUMBER.getId());
        model.setName("1");
        model.setPriority(1);
        model.setExpand(JSONUtil.toJsonStr(fixedNumberRuleModel));
        return model;
    }

    @Test
    public void timerRuleTest() throws Exception {
        IRule rule = ruleFactory.create(getTimerRuleModel());

        long start = System.currentTimeMillis();
        Assert.assertTrue(rule.handler("ABC","1"));

        TimeUnit.SECONDS.sleep(2);
        System.out.println("false : " + (System.currentTimeMillis() - start) / 1000);
        Assert.assertFalse(rule.handler("ABC","1"));

        TimeUnit.SECONDS.sleep(3);
        System.out.println("true : " + (System.currentTimeMillis() - start) / 1000);
        Assert.assertTrue(rule.handler("ABC","1"));
    }


    private RuleModel getTimerRuleModel() {
        TimerRuleModel timerRuleModel = new TimerRuleModel();
        timerRuleModel.setTimeout(3L);

        RuleModel model = new RuleModel();
        model.setRuleType(DefaultRuleTypeEnum.TIMER.getId());
        model.setName("3");
        model.setPriority(3);
        model.setExpand(JSONUtil.toJsonStr(timerRuleModel));
        return model;
    }


    @Test
    public void spElRuleTest() {
        SpElModel spElModel = new SpElModel();
        spElModel.setHasAndConCondition(false);
        List<SpElSimpleModel> modelStrs = new ArrayList<>();
        SpElSimpleModel spElSimpleModel = SpElSimpleModel.create("at","32",">").or("12","<");
        modelStrs.add(spElSimpleModel);
        spElSimpleModel = SpElSimpleModel.create("ah","32",">").or("12","<");
        modelStrs.add(spElSimpleModel);
        spElModel.setEls(modelStrs);

        RuleModel model = new RuleModel();
        model.setRuleType(DefaultRuleTypeEnum.SP_EL.getId());
        model.setName("3");
        model.setPriority(3);
        model.setExpand(JSONUtil.toJsonStr(spElModel));

        IRule rule = ruleFactory.create(model);
        JSONObject condition = new JSONObject();
        condition.putOpt("at","10");
        condition.putOpt("ah","31");
        Assert.assertTrue(rule.handler("a",condition.toString()));
    }

    @Test
    public void spElRuleTest1() {
        SpElModel spElModel = new SpElModel();
        spElModel.setHasAndConCondition(true);
        List<SpElSimpleModel> modelStrs = new ArrayList<>();
        SpElSimpleModel spElSimpleModel = SpElSimpleModel.create("at","32",">").or("12","<");
        modelStrs.add(spElSimpleModel);
        spElSimpleModel = SpElSimpleModel.create("ah","32",">").or("12","<");
        modelStrs.add(spElSimpleModel);
        spElModel.setEls(modelStrs);

        RuleModel model = new RuleModel();
        model.setRuleType(DefaultRuleTypeEnum.SP_EL.getId());
        model.setName("3");
        model.setPriority(3);
        model.setExpand(JSONUtil.toJsonStr(spElModel));

        IRule rule = ruleFactory.create(model);
        JSONObject condition = new JSONObject();
        condition.putOpt("at","10");
        condition.putOpt("ah","31");
        Assert.assertFalse(rule.handler("a",condition.toString()));
    }

    @Test
    public void spElRuleTest2() {
        SpElModel spElModel = new SpElModel();
        spElModel.setHasAndConCondition(true);
        List<SpElSimpleModel> modelStrs = new ArrayList<>();
        SpElSimpleModel spElSimpleModel = SpElSimpleModel.create("at","32",">");
        modelStrs.add(spElSimpleModel);
        spElSimpleModel = SpElSimpleModel.create("ah","32",">");
        modelStrs.add(spElSimpleModel);
        spElModel.setEls(modelStrs);

        RuleModel model = new RuleModel();
        model.setRuleType(DefaultRuleTypeEnum.SP_EL.getId());
        model.setName("3");
        model.setPriority(3);
        model.setExpand(JSONUtil.toJsonStr(spElModel));

        IRule rule = ruleFactory.create(model);
        JSONObject condition = new JSONObject();
        condition.putOpt("at","33");
        condition.putOpt("ah","31");
        Assert.assertFalse(rule.handler("a",condition.toString()));
    }
}
