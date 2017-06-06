package net.ahm.careengine.displayable.json;

import net.ahm.rulesapp.displayables.utils.JsonMapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

public class DisplayableJsonMapper extends JsonMapper {

    public static ObjectMapper getJsonObjectMapper() {
        return ObjectMapperHolder.INSTANCE;
    }

    @Override
    protected SimpleModule createModule() {
        SimpleModule module = super.createModule();
        return module;
    }

    private static class ObjectMapperHolder {
        static final ObjectMapper INSTANCE = new DisplayableJsonMapper()
                                                   .createObjectMapper();
    }
}
