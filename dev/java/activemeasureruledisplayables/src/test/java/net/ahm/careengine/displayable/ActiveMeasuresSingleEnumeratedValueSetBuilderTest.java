package net.ahm.careengine.displayable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import net.ahm.careengine.domain.medicalcase.MedicalCase;
import net.ahm.rulesapp.displayables.type.RelatedAttribute;
import net.ahm.rulesapp.displayables.type.SingleSelection;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

@ContextConfiguration(locations = "/META-INF/amRulesDisplayablesBeans.xml")
public class ActiveMeasuresSingleEnumeratedValueSetBuilderTest extends
        AbstractJUnit4SpringContextTests {

    @Autowired
    CareEngineEnumeratedValueSetBuilderContext ctx;

    @Test
    public void testInitialization() {
        assertNotNull(ctx);
    }

    @Test
    public void testGetName_Classifier_Ids() {
        assertEquals("Classifier_Ids",
                ActiveMeasuresSingleEnumeratedValueSetBuilder.CLASSIFIER_IDS
                        .getName());
    }

    @Test
    public void testGetName() {
        for (CommonSingleEnumeratedValueSetBuilderIF builder : ActiveMeasuresSingleEnumeratedValueSetBuilder.ALL_BUILDERS) {
            String name = builder.getName();
            assertNotNull(name);
            assertFalse("The name for " + builder
                    + " should not contain a space", name.contains(" "));
        }
    }

    @Test
    public void testGetRelatedAttributes() {
        for (CommonSingleEnumeratedValueSetBuilderIF builder : ActiveMeasuresSingleEnumeratedValueSetBuilder.ALL_BUILDERS) {
            List<RelatedAttribute> relatedAttributes = builder
                    .getRelatedAttributes();
            assertNotNull("The related attributes for " + builder
                    + " should not be null", relatedAttributes);
            assertFalse("The related attributes for " + builder
                    + " should not be empty", relatedAttributes.isEmpty());
        }
    }

    @Test
    public void testGetDAO() {
        assertNotNull(ctx.getDao());
    }

    @Test
    public void testGetItems() {
        for (CommonSingleEnumeratedValueSetBuilderIF builder : ActiveMeasuresSingleEnumeratedValueSetBuilder.ALL_BUILDERS) {
            List<SingleSelection> items = builder.getItems(ctx);
            assertNotNull("The items for " + builder + " should not be null",
                    items);

            assertFalse("The items for " + builder + " should not be empty",
                    items.isEmpty());
        }
    }

    @Test
    public void testDIAGNOSIS_RELATED_GROUP_ELEMENT_IDS() {
        assertTrue(
                "Should contain this version of the builder",
                ActiveMeasuresSingleEnumeratedValueSetBuilder.ALL_BUILDERS
                .contains(ActiveMeasuresSingleEnumeratedValueSetBuilder.DIAGNOSIS_RELATED_GROUP_ELEMENT_IDS));

        assertFalse(
                "Should NOT contain this version of the builder",
                ActiveMeasuresSingleEnumeratedValueSetBuilder.ALL_BUILDERS
                        .contains(CommonSingleEnumeratedValueSetBuilder.DIAGNOSIS_RELATED_GROUP_ELEMENT_IDS));

        List<RelatedAttribute> relatedAttributeList = ActiveMeasuresSingleEnumeratedValueSetBuilder.DIAGNOSIS_RELATED_GROUP_ELEMENT_IDS
                .getRelatedAttributes();
        assertEquals("Not the expected size", 2, relatedAttributeList.size());

        boolean matchFound = false;
        for (RelatedAttribute ra : relatedAttributeList) {
            if (ra.getTypeContainingAttribute().equals(MedicalCase.class)) {
                matchFound = true;
            }
        }
        assertTrue("This should be related to a MedicalCase field", matchFound);
    }
}
