package net.ahm.activemeasure.ruletemplate.json;

import net.ahm.rulesapp.json.RulesAppRepositoryJsonMapper;

import com.fasterxml.jackson.databind.module.SimpleModule;

public class ActiveMeasureJSONMapper extends RulesAppRepositoryJsonMapper {
    public static final ActiveMeasureJSONMapper INSTANCE = new ActiveMeasureJSONMapper();

    public static ActiveMeasureJSONMapper getInstance() {
        return INSTANCE;
    }

    private ActiveMeasureJSONMapper() {
        super();
    }

    @Override
    public void setProjectSpecificJacksonOBjectMapping(SimpleModule module) {
        addActiveMeasureTemplateMixIns(module);
    }

    private void addActiveMeasureTemplateMixIns(SimpleModule module) {
        // do nothing for now
    }
}
