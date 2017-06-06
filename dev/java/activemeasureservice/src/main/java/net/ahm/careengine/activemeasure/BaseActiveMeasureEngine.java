package net.ahm.careengine.activemeasure;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import net.ahm.careengine.activemeasure.justification.ActiveMeasureJustificationListener;
import net.ahm.careengine.activemeasure.justification.JustifiedClassifier;
import net.ahm.careengine.activemeasure.justification.JustifiedComorbidClinicalConditionCategory;
import net.ahm.careengine.activemeasure.justification.JustifiedHierarchicalClinicalConditionCategory;
import net.ahm.careengine.domain.fact.Fact;
import net.ahm.careengine.domain.justification.RuleJustification;
import net.ahm.careengine.domain.measures.active.ActiveMeasure;
import net.ahm.careengine.domain.measures.active.FactLevelMeasureDenominator;
import net.ahm.careengine.domain.measures.active.FactLevelMeasureNumerator;
import net.ahm.careengine.domain.member.ActiveMeasuresMemberInfo;
import net.ahm.careengine.domain.member.MemberProfile;
import net.ahm.careengine.eventprocessing.engine.drools.DroolsEventListener;
import net.ahm.careengine.eventprocessing.engine.drools.URLBasedDroolsCareEngine;
import net.ahm.careengine.ruleengine.DateTimeUnit;
import net.ahm.careengine.util.CEPropertyReader;
import net.ahm.careengine.util.GlobalConstants;
import net.ahm.careengine.util.GlobalFunctions;

import org.apache.log4j.Logger;
import org.drools.command.Command;
import org.drools.command.CommandFactory;
import org.drools.runtime.StatelessKnowledgeSession;

/**
 * The quality measure implementation of the care engine.
 * 
 * @author Xsu
 * 
 */
public class BaseActiveMeasureEngine
        extends
        URLBasedDroolsCareEngine<ActiveMeasureCommandInput, ActiveMeasureCommandOutput, ActiveMeasureCommandConfiguration>
        implements ActiveMeasureEngine {

    private static final Logger LOGGER                        = Logger.getLogger(BaseActiveMeasureEngine.class);
    static final String         DEFAULT_PACKAGE_LOCATION      = "classpath:activeMeasuresPackages/qualitymeasures.*.drl";
    static final String         DEFAULT_PACKAGE_TYPE          = DRL_FILES_IN_RESOURCES;
    static final String         DEFAULT_MEASURE_NAME_BEGINING = "net.ahm.activemeasure.qualitymeasures.";

    @Override
    protected void setEngineGlobal(StatelessKnowledgeSession session,
            ActiveMeasureCommandInput input, ActiveMeasureCommandOutput output,
            ActiveMeasureCommandConfiguration configuration) {
        Date measurementEndDate = configuration.getMeasurementEndDate();

        Date months1BeforeMeasurementEndDate = getDateInPastFromMeasurementEndDate(
                measurementEndDate, 1, DateTimeUnit.MONTH);
        Date months3BeforeMeasurementEndDate = getDateInPastFromMeasurementEndDate(
                measurementEndDate, 3, DateTimeUnit.MONTH);
        Date months6BeforeMeasurementEndDate = getDateInPastFromMeasurementEndDate(
                measurementEndDate, 6, DateTimeUnit.MONTH);
        Date months11BeforeMeasurementEndDate = getDateInPastFromMeasurementEndDate(
                measurementEndDate, 11, DateTimeUnit.MONTH);
        Date months12BeforeMeasurementEndDate = getDateInPastFromMeasurementEndDate(
                measurementEndDate, 12, DateTimeUnit.MONTH);
        Date months13BeforeMeasurementEndDate = getDateInPastFromMeasurementEndDate(
                measurementEndDate, 13, DateTimeUnit.MONTH);
        Date months24BeforeMeasurementEndDate = getDateInPastFromMeasurementEndDate(
                measurementEndDate, 24, DateTimeUnit.MONTH);
        Date months25BeforeMeasurementEndDate = getDateInPastFromMeasurementEndDate(
                measurementEndDate, 25, DateTimeUnit.MONTH);
        Date months27BeforeMeasurementEndDate = getDateInPastFromMeasurementEndDate(
                measurementEndDate, 27, DateTimeUnit.MONTH);
        Date years3BeforeMeasurementEndDate = getDateInPastFromMeasurementEndDate(
                measurementEndDate, 3, DateTimeUnit.YEAR);
        Date years5BeforeMeasurementEndDate = getDateInPastFromMeasurementEndDate(
                measurementEndDate, 5, DateTimeUnit.YEAR);
        Date years10BeforeMeasurementEndDate = getDateInPastFromMeasurementEndDate(
                measurementEndDate, 10, DateTimeUnit.YEAR);

        addGlobalToSession(session, "measurementEndDate", measurementEndDate);
        addGlobalToSession(session, "measurementStartDate",
                configuration.getMeasurementStartDate());
        addGlobalToSession(session, "months1BeforeMeasurementEndDate",
                months1BeforeMeasurementEndDate);
        addGlobalToSession(session, "months3BeforeMeasurementEndDate",
                months3BeforeMeasurementEndDate);
        addGlobalToSession(session, "months6BeforeMeasurementEndDate",
                months6BeforeMeasurementEndDate);
        addGlobalToSession(session, "months11BeforeMeasurementEndDate",
                months11BeforeMeasurementEndDate);
        addGlobalToSession(session, "months12BeforeMeasurementEndDate",
                months12BeforeMeasurementEndDate);
        addGlobalToSession(session, "months13BeforeMeasurementEndDate",
                months13BeforeMeasurementEndDate);
        addGlobalToSession(session, "months24BeforeMeasurementEndDate",
                months24BeforeMeasurementEndDate);
        addGlobalToSession(session, "months25BeforeMeasurementEndDate",
                months25BeforeMeasurementEndDate);
        addGlobalToSession(session, "months27BeforeMeasurementEndDate",
                months27BeforeMeasurementEndDate);
        addGlobalToSession(session, "years3BeforeMeasurementEndDate",
                years3BeforeMeasurementEndDate);
        addGlobalToSession(session, "years5BeforeMeasurementEndDate",
                years5BeforeMeasurementEndDate);
        addGlobalToSession(session, "years10BeforeMeasurementEndDate",
                years10BeforeMeasurementEndDate);
        addGlobalToSession(session, "activeMeasureBuilder",
                configuration.getActiveMeasureBuilder());
        addGlobalToSession(session, "classifierBuilder",
                configuration.getClassifierBuilder());
        addGlobalToSession(session, "hccCategoryBuilder",
                configuration.getHierarchicalClinicalConditionCategoryBuilder());
        addGlobalToSession(session, "factLevelMeasureBuilder",
                configuration.getFactLevelMeasureBuilder());
        addGlobalToSession(session, "riskStratificationMeasureWeightsBuilder",
                configuration.getRiskStratificationMeasureWeightsBuilder());

    }

    protected void addGlobalToSession(StatelessKnowledgeSession session,
            String globalName, Object value) {
        session.setGlobal(globalName, value);
    }

    private Date getDateInPastFromMeasurementEndDate(Date measurementEndDate,
            int no, DateTimeUnit dateTimeUnit) {
        Date calculatedDate = GlobalFunctions.startDayOfRangeEndingOn(
                measurementEndDate, no, dateTimeUnit);
        return calculatedDate;
    }

    public static BaseActiveMeasureEngine getInstance() {
        // not using a singleton
        return new BaseActiveMeasureEngine();
    }

    protected BaseActiveMeasureEngine(String url, String ruleflow,
            String resourcesTypeString, String username, String password,
            String expectedPackageNameNameBeginingString) {
        super(url, ruleflow, resourcesTypeString, username, password,
                expectedPackageNameNameBeginingString);
    }

    protected BaseActiveMeasureEngine() {
        this(getURLStringFromProperties(), null,
                getResourceTypeFromProperties(), getUsernameFromProperties(),
                getPasswordFromProperties(),
                getExpectedPackageNameBeginingStringFromProperties());
    }

    protected static String getURLStringFromProperties() {
        String url = CEPropertyReader.getCEv2Property(
                GlobalConstants.KB_PACKAGE, DEFAULT_PACKAGE_LOCATION);
        return url;
    }

    protected static String getResourceTypeFromProperties() {
        String resourceType = CEPropertyReader.getCEv2Property(
                GlobalConstants.KB_PACKAGE_TYPE, DEFAULT_PACKAGE_TYPE);
        return resourceType;
    }

    @Override
    protected void addRuleflowToCommandList(List<Command> cmds) {
        // currently do nothing
    }

    private void updateWithConfiguration(Object toBeUpdate,
            ActiveMeasureCommandConfiguration configuration) {
        if (toBeUpdate instanceof ActiveMeasureCommandConfigurationUpdateable) {
            ((ActiveMeasureCommandConfigurationUpdateable) toBeUpdate)
                    .updateWithCongifuration(configuration);
        }
    }

    @SuppressWarnings("rawtypes")
    @Override
    public List<Command> getCommands(ActiveMeasureCommandInput input,
            ActiveMeasureCommandOutput output,
            ActiveMeasureCommandConfiguration configuration) {
        List<Command> cmds = new ArrayList<Command>();

        MemberProfile profile = input.getMemberProfile();

        ActiveMeasuresMemberInfo memberInfo = profile.getMemberInfo();
        updateWithConfiguration(memberInfo, configuration);
        cmds.add(CommandFactory.newInsert(memberInfo));

        addFactsToCommandList(cmds, profile.getAllergyEvents());
        addFactsToCommandList(cmds, profile.getDiagnosticEvents());
        addFactsToCommandList(cmds, profile.getDrugEvents());
        addFactsToCommandList(cmds, profile.getDrugDispensingEvents());
        addFactsToCommandList(cmds, profile.getLabEvents());
        addFactsToCommandList(cmds, profile.getPatientDerivedEvents());
        addFactsToCommandList(cmds, profile.getProcedureEvents());
        addFactsToCommandList(cmds, profile.getClaimHeaders());
        addFactsToCommandList(cmds, profile.getHieEncounters());
        addFactsToCommandList(cmds, profile.getFeedbacks());
        addFactsToCommandList(cmds, profile.getMedicalCases());

        cmds.add(CommandFactory.newInsert(output));

        // The configuration should be the last object inserted into the working
        // memory, as this is used to improve the performance of certain rules.
        cmds.add(CommandFactory.newInsert(configuration));
        addRuleflowToCommandList(cmds); // insert ruleflow

        return cmds;
    }

    /**
     * Add the facts in the collection into the Command list. The collection as
     * one object should be inserted into the session. Then each member in the
     * collection should also be inserted into the session.
     * 
     * @param commands
     *            command list to be prepared for the drools engine
     * @param events
     *            events to be added as command
     */
    private <F extends Fact> void addFactsToCommandList(List<Command> commands,
            Collection<F> facts) {
        commands.add(CommandFactory.newInsert(facts));
        for (F fact : facts) {
            commands.add(CommandFactory.newInsert(fact));
        }
    }

    @Override
    public Collection<DroolsEventListener> getDroolsEventListeners(
            ActiveMeasureCommandOutput output,
            ActiveMeasureCommandConfiguration configuration) {
        List<DroolsEventListener> listeners = new ArrayList<DroolsEventListener>();
        ActiveMeasureJustificationListener listener = new ActiveMeasureJustificationListener(
                output, configuration);
        listeners.add(listener);
        return listeners;
    }

    @Override
    public void execute(ActiveMeasureCommandInput input,
            ActiveMeasureCommandOutput output,
            ActiveMeasureCommandConfiguration configuration) throws Exception {
        super.execute(input, output, configuration);

        // log justifications
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("All justifications:");
            Collection<ActiveMeasure> activeMesures = output
                    .getActiveMeasures();

            LOGGER.debug("All active measures:");
            for (ActiveMeasure measure : activeMesures) {
                LOGGER.debug("For measure id:" + measure.getMeasureId());
                Collection<RuleJustification> justfications = measure
                        .getJustifications();
                for (RuleJustification justification : justfications) {
                    LOGGER.debug(justification);
                }
            }

            LOGGER.debug("All classifier rules:");
            Collection<JustifiedClassifier> justifiedClassifiers = output
                    .getJustifiedClassifiers();
            for (JustifiedClassifier justClassifier : justifiedClassifiers) {
                Collection<RuleJustification> justifications = justClassifier
                        .getJustifications();
                for (RuleJustification singleJustification : justifications) {
                    LOGGER.debug(singleJustification);
                }
            }

            LOGGER.debug("All hierarchicalClinicalConditionCategory rules:");
            Collection<JustifiedHierarchicalClinicalConditionCategory> justifiedHierarchicalClinicalConditionCategories = output
                    .getJustifiedHierarchicalClinicalConditionCategories();
            for (JustifiedHierarchicalClinicalConditionCategory justHcc : justifiedHierarchicalClinicalConditionCategories) {
                Collection<RuleJustification> justifications = justHcc
                        .getJustifications();
                for (RuleJustification singleJustification : justifications) {
                    LOGGER.debug(singleJustification);
                }
            }
            
            LOGGER.debug("All ComorbidClinicalConditionCategory rules:");
            Collection<JustifiedComorbidClinicalConditionCategory> justifiedComorbidClinicalConditionCategories = output
                    .getJustifiedComorbidClinicalConditionCategories();
            for (JustifiedComorbidClinicalConditionCategory justCC : justifiedComorbidClinicalConditionCategories) {
                Collection<RuleJustification> justifications = justCC
                        .getJustifications();
                for (RuleJustification singleJustification : justifications) {
                    LOGGER.debug(singleJustification);
                }
            }

            LOGGER.debug("All event level measures:");
            for (FactLevelMeasureDenominator denom : output
                    .getFactLevelMeasureDenominators()) {
                LOGGER.debug("Denom for measure id:" + denom.getMeasureId()
                        + " and primary fact:"
                        + denom.getPrimaryOriginationFact());
                for (RuleJustification denomJustification : denom
                        .getJustifications()) {
                    LOGGER.debug(denomJustification);
                }
                for (FactLevelMeasureNumerator numerator : denom
                        .getNumerators()) {
                    LOGGER.debug("Numerator for measure id:"
                            + numerator.getMeasureId() + " and primary fact:"
                            + numerator.getPrimaryFact());
                    for (RuleJustification numJustification : numerator
                            .getJustifications()) {
                        LOGGER.debug(numJustification);
                    }
                }
            }
        }
    }


    protected static String getExpectedPackageNameBeginingStringFromProperties() {
        return CEPropertyReader.getCEv2Property(
                GlobalConstants.KB_ACTIVE_MEASURE_PACKAGE_NAME_BEGINING,
                DEFAULT_MEASURE_NAME_BEGINING);
    }

    protected static String getPasswordFromProperties() {
        final String password = CEPropertyReader
                .getCEv2Property(GlobalConstants.KB_PASSWORD);
        return password;
    }

    protected static String getUsernameFromProperties() {
        final String username = CEPropertyReader
                .getCEv2Property(GlobalConstants.KB_USER_NAME);
        if (username == null) {
            return "";
        }
        return username;
    }

    @Override
    public void postDroolsProcessing(ActiveMeasureCommandInput arg0,
            ActiveMeasureCommandOutput arg1,
            ActiveMeasureCommandConfiguration arg2) throws Exception {
        // do nothing
    }
}
