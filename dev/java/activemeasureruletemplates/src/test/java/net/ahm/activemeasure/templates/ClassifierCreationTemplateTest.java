package net.ahm.activemeasure.templates;

import static org.junit.Assert.assertTrue;
import net.ahm.rulesapp.templates.libraries.BaseTemplateTest;

public class ClassifierCreationTemplateTest extends BaseTemplateTest {
    static final long EXPEDTED_ID = 1138;

    @Override
    public ClassifierCreationTemplate getTemplateInstance() {
        return new ClassifierCreationTemplate(EXPEDTED_ID);
    }

    @Override
    protected void additionalAssertionsOnExecutableExpressions(
            String droolsString) {
        assertTrue("count not find the id number",
                droolsString.contains("" + EXPEDTED_ID));

        assertTrue("count not find an insert command",
                droolsString.contains("insert"));
    }
}
