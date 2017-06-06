package net.ahm.careengine.activemeasure.parsing;

import java.util.Set;

import org.apache.commons.lang.StringUtils;

public class RuleParserUtil {
    static boolean containsAny(Set<String> stringCollection, String containedInStr){
        for(String s: stringCollection){
            if(StringUtils.containsAny(containedInStr, s)){
               return true; 
            }
        }
        return false;
    }
}
