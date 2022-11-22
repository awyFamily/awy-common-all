package com.awy.common.rule;

import cn.hutool.json.JSONUtil;
import com.awy.common.rule.enums.RuleChainNodeTypeNum;
import com.awy.common.rule.enums.DefaultRuleTypeEnum;
import com.awy.common.rule.memory.MemoryFixedNumberRule;
import com.awy.common.rule.model.FixedNumberRuleModel;
import com.awy.common.rule.model.FloatValueRuleModel;
import com.awy.common.rule.model.RuleModel;
import com.awy.common.rule.model.TimerRuleModel;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author yhw
 * @date 2022-08-04
 */
public class RulesTest {

    private Rules rules;

    @Before
    public void init() {
        rules = new Rules();
    }

    @Test
    public void getRulesByNameTest() {
        rules.registry(getTimerRuleModel());
        rules.registry(getFixedNumberRuleModel());
        rules.registry(getFloatValueRuleModel());

        Assert.assertTrue(rules.getRulesByName("1") instanceof MemoryFixedNumberRule);
    }

    @Test
    public void getRulesByGroupNameTest() {
        rules.registry(getTimerRuleModel());
        rules.registry(getFixedNumberRuleModel());
        rules.registry(getFloatValueRuleModel());

        List<IRule> ruleList = rules.getRulesByGroupName("default");
        Assert.assertTrue(ruleList.size() == 3);
//        for (IRule rule : ruleList) {
//            System.out.println(rule.getName() + "   "  + rule.getPriority());
//        }
    }

    @Test
    public void unRegistryTest() {
        rules.registry(getTimerRuleModel());
        rules.registry(getFixedNumberRuleModel());
        rules.registry(getFloatValueRuleModel());

        Assert.assertTrue(rules.getRulesByGroupName("default").size() == 3);
        rules.unRegistry("1");
        Assert.assertTrue(rules.getRulesByGroupName("default").size() == 2);
    }

    @Test
    public void isSupportByGroupTest() throws Exception {
        //场景1(单个规则)： 当针对一个事件, n 分钟只允许触发 1 次
        rules.registry(getTimerRuleModel());
        Assert.assertTrue(rules.isSupportByGroup("default","ABC",""));
        //未超时,不满足
        Assert.assertFalse(rules.isSupportByGroup("default","ABC",""));
        //新的 key 满足
        Assert.assertTrue(rules.isSupportByGroup("default","BCD",""));
        //未超时,不满足
        TimeUnit.SECONDS.sleep(2);
        Assert.assertFalse(rules.isSupportByGroup("default","ABC",""));
        //超时,满足
        TimeUnit.SECONDS.sleep(2);
        Assert.assertTrue(rules.isSupportByGroup("default","ABC",""));
        //超时,满足
        Assert.assertTrue(rules.isSupportByGroup("default","BCD",""));

    }

    @Test
    public void isSupportByGroup2Test() {
        //场景2(多个规则组合使用)： 在物联网场景下，例如空气温度达到了 20 °, 会触发一条告警, 下一次告警需要在上次告警基础上 上下浮动 5° 才进行下次告警, 一个时间段内，只允许告警 n 次(超过则不进行告警)
        rules.registry(getFixedNumberRuleModel());
        rules.registry(getFloatValueRuleModel());

        Map<String,String> floatMap = new HashMap<>();
        floatMap.put("a","10");
        Assert.assertTrue(rules.isSupportByGroup("default","ABC",JSONUtil.toJsonStr(floatMap)));

        floatMap = new HashMap<>();
        floatMap.put("a","10");
        Assert.assertFalse(rules.isSupportByGroup("default","ABC",JSONUtil.toJsonStr(floatMap)));

        floatMap = new HashMap<>();
        floatMap.put("a","15");
        Assert.assertTrue(rules.isSupportByGroup("default","ABC",JSONUtil.toJsonStr(floatMap)));

        floatMap = new HashMap<>();
        floatMap.put("a","20");
        Assert.assertTrue(rules.isSupportByGroup("default","ABC",JSONUtil.toJsonStr(floatMap)));

        floatMap = new HashMap<>();
        floatMap.put("a","25");
        Assert.assertFalse(rules.isSupportByGroup("default","ABC",JSONUtil.toJsonStr(floatMap)));

        floatMap = new HashMap<>();
        floatMap.put("a","20");
        Assert.assertFalse(rules.isSupportByGroup("default","ABC",JSONUtil.toJsonStr(floatMap)));
    }

    private RuleModel getFixedNumberRuleModel() {
        FixedNumberRuleModel fixedNumberRuleModel = new FixedNumberRuleModel();
        fixedNumberRuleModel.setTimeout(300L);
        fixedNumberRuleModel.setFixedNumber(3);

        RuleModel model = new RuleModel();
        model.setRuleType(DefaultRuleTypeEnum.FIXED_NUMBER.getId());
        model.setName("1");
        model.setPriority(1);
        model.setExpand(JSONUtil.toJsonStr(fixedNumberRuleModel));
        return model;
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
    public void isSupportByGroup3Test() throws Exception {
        //场景3(多个规则组合使用)： 当满足浮动值,则直接判断成功;
        //当浮动值条件为空时候或不满足时, 则需要按超时触发(同时会更新浮动值条件)
        rules.registry(getFloatValueRuleModel3());
        rules.registry(getTimerRuleModel3());

        Map<String,String> floatMap = new HashMap<>();
        floatMap.put("a","10");
        Assert.assertTrue(rules.isSupportByGroup("default","ABC",JSONUtil.toJsonStr(floatMap)));

        floatMap = new HashMap<>();
        floatMap.put("a","15");
        Assert.assertTrue(rules.isSupportByGroup("default","ABC",JSONUtil.toJsonStr(floatMap)));

        floatMap = new HashMap<>();
        floatMap.put("a","15");
        Assert.assertFalse(rules.isSupportByGroup("default","ABC",JSONUtil.toJsonStr(floatMap)));

        floatMap = new HashMap<>();
        floatMap.put("a","20");
        Assert.assertTrue(rules.isSupportByGroup("default","ABC",JSONUtil.toJsonStr(floatMap)));

        floatMap = new HashMap<>();
        floatMap.put("a","20");
        Assert.assertFalse(rules.isSupportByGroup("default","ABC",JSONUtil.toJsonStr(floatMap)));

        TimeUnit.SECONDS.sleep(6);

        floatMap = new HashMap<>();
//        floatMap.put("a","20");
        //Assert.assertTrue
        Assert.assertTrue(rules.isSupportByGroup("default","ABC",JSONUtil.toJsonStr(floatMap)));
    }

    private RuleModel getFloatValueRuleModel3() {
        FloatValueRuleModel floatValueRuleModel = new FloatValueRuleModel();
        Map<String,String> floatMap = new HashMap<>();
        floatMap.put("a","5");
        floatValueRuleModel.setFloatValueMaps(JSONUtil.toJsonStr(floatMap));

        RuleModel model = new RuleModel();
        model.setRuleType(DefaultRuleTypeEnum.FLOAT_VALUE.getId());
        model.setRuleChainNodeType(RuleChainNodeTypeNum.success_end_fail_continue.getId());
        model.setName("2");
        model.setPriority(2);
        model.setExpand(JSONUtil.toJsonStr(floatValueRuleModel));
        return model;
    }

    private RuleModel getTimerRuleModel3() {
        TimerRuleModel timerRuleModel = new TimerRuleModel();
        timerRuleModel.setTimeout(3L);
//        timerRuleModel.setRuleChainNodeType(RuleChainNodeTypeNum.one_success.getId());

        RuleModel model = new RuleModel();
        model.setRuleType(DefaultRuleTypeEnum.TIMER.getId());
        model.setName("3");
        model.setPriority(3);
        model.setExpand(JSONUtil.toJsonStr(timerRuleModel));
        return model;
    }

    @Test
    public void isSupportByGroup4Test() throws Exception {
        //一段时间(例如一天)内最多3次，浮动值为5, 距离上一条规则(定时)超过30分钟
        rules.registry(getFloatValueRuleModel4());
        rules.registry(getTimerRuleModel4());
        rules.registry(getFixedNumberRuleModel4());

        //第一次执行满足条件
        Map<String,String> floatMap = new HashMap<>();
        floatMap.put("a","10");
        Assert.assertTrue(rules.isSupportByGroup("default","ABC",JSONUtil.toJsonStr(floatMap)));

        floatMap = new HashMap<>();
        floatMap.put("a","10");
        Assert.assertFalse(rules.isSupportByGroup("default","ABC",JSONUtil.toJsonStr(floatMap)));

        floatMap = new HashMap<>();
        floatMap.put("a","15");
        Assert.assertFalse(rules.isSupportByGroup("default","ABC",JSONUtil.toJsonStr(floatMap)));

        //超时满足条件
        TimeUnit.SECONDS.sleep(4);
        floatMap = new HashMap<>();
        floatMap.put("a","15");
        Assert.assertTrue(rules.isSupportByGroup("default","ABC",JSONUtil.toJsonStr(floatMap)));

        floatMap = new HashMap<>();
        floatMap.put("a","10");
        Assert.assertFalse(rules.isSupportByGroup("default","ABC",JSONUtil.toJsonStr(floatMap)));

        //超时满足条件
        TimeUnit.SECONDS.sleep(4);
        floatMap = new HashMap<>();
        floatMap.put("a","10");
        Assert.assertTrue(rules.isSupportByGroup("default","ABC",JSONUtil.toJsonStr(floatMap)));

        //超过最大次数,不满足
        TimeUnit.SECONDS.sleep(4);
        floatMap = new HashMap<>();
        floatMap.put("a","15");
        Assert.assertFalse(rules.isSupportByGroup("default","ABC",JSONUtil.toJsonStr(floatMap)));
    }

    private RuleModel getFixedNumberRuleModel4() {
        FixedNumberRuleModel fixedNumberRuleModel = new FixedNumberRuleModel();
        fixedNumberRuleModel.setTimeout(3000L);
        fixedNumberRuleModel.setFixedNumber(3);

        RuleModel model = new RuleModel();
        model.setRuleType(DefaultRuleTypeEnum.FIXED_NUMBER.getId());
        model.setName("1");
        model.setPriority(1);
        model.setExpand(JSONUtil.toJsonStr(fixedNumberRuleModel));
        return model;
    }

    private RuleModel getFloatValueRuleModel4() {
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

    private RuleModel getTimerRuleModel4() {
        TimerRuleModel timerRuleModel = new TimerRuleModel();
        timerRuleModel.setTimeout(3L);

        RuleModel model = new RuleModel();
        model.setRuleType(DefaultRuleTypeEnum.TIMER.getId());
        model.setName("3");
        model.setPriority(3);
        model.setExpand(JSONUtil.toJsonStr(timerRuleModel));
        return model;
    }


}
