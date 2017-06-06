package net.ahm.careengine;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.ahm.careengine.util.CareEngineTestUtilities;

import org.junit.BeforeClass;
import org.junit.Test;

public class ProfileTest {
    private static final String                                  COORECT_FILE_NAME       = "filter.jenkins@192.168.4.24.properties";
    private static final String                                  IP_PATTERN_STRING       = "[0-9]+\\.[0-9]+\\.[0-9]+\\.[0-9]+";
    private final static Map<String, Collection<PropertyHolder>> PROPERTY_FILTERS        = new HashMap<String, Collection<PropertyHolder>>();
    static final Pattern                                         FILTER_FILENAME_PATTERN = Pattern
                                                                                                 .compile("filter\\.[a-zA-Z0-9]+@"
                                                                                                         + IP_PATTERN_STRING
                                                                                                         + "\\.properties");
    static final Pattern                                         IP_PATTERN              = Pattern
                                                                                                 .compile(IP_PATTERN_STRING);
    static final Pattern                                         NOT_PORT_PATERN         = Pattern
                                                                                                 .compile("tcp://|:|localhost");

    private static final Map<String, String>                     DUPLICATE_NAMES;
    static {
        Map<String, String> tempMap = new HashMap<String, String>();
        tempMap.put("prakash", "PChandrasekaran");
        DUPLICATE_NAMES = Collections.unmodifiableMap(tempMap);
    }

    @BeforeClass
    public static void init() throws IOException {
        File filterFolder = new File("./src/main/filters");
        assertTrue("Can't find " + filterFolder, filterFolder.exists());
        File[] propertiesFiles = filterFolder.listFiles(new PropertiesFilter());
        for (File file : propertiesFiles) {
            String fileName = file.getName();
            Matcher m = IP_PATTERN.matcher(fileName);
            m.find();
            String ipString = m.group();
            String userName = m.replaceAll("");
            userName = userName.replace("filter.", "");
            userName = userName.replace("@.properties", "");
            Properties properties = new Properties();
            FileInputStream fis = new FileInputStream(file);
            try {
                properties.load(fis);
            } finally {
                fis.close();
            }
            PropertyHolder pHolder = new PropertyHolder(fileName, userName,
                    properties);
            addToColelctionMap(PROPERTY_FILTERS, ipString, pHolder);
        }
        assertFalse("There should be several files found",
                PROPERTY_FILTERS.isEmpty());
    }

    @Test
    public void testIP_PATTERN() {
        Matcher matcher = IP_PATTERN.matcher(COORECT_FILE_NAME);
        assertEquals(false, matcher.matches());
        assertEquals(true, matcher.find());
        assertEquals("192.168.4.24", matcher.group());
        assertEquals(false, matcher.find());

        matcher = IP_PATTERN.matcher("filter.jenkins@192.168.6.properties");
        assertEquals(false, matcher.find());
    }

    @Test
    public void testFILTER_FILENAME_PATTERN() {
        Matcher matcher = FILTER_FILENAME_PATTERN.matcher(COORECT_FILE_NAME);
        assertEquals(true, matcher.find());
        assertEquals(true, matcher.matches());
        assertEquals(COORECT_FILE_NAME, matcher.group());
        assertEquals(false, matcher.find());

        matcher = FILTER_FILENAME_PATTERN
                .matcher("filter.jenkins@192.168.6.properties");
        assertEquals(false, matcher.find());
    }

    @Test
    public void testPortsThatShouldNotBeShared() {
        Collection<String> errorMessages = new ArrayList<String>();
        for (Map.Entry<String, Collection<PropertyHolder>> entry : PROPERTY_FILTERS
                .entrySet()) {
            for (PortContainingPropertyNames propName : PortContainingPropertyNames.values()) {
                assertValuesNotSame(entry.getKey(), propName, entry.getValue(),
                        errorMessages);
            }
        }
        CareEngineTestUtilities.assertNoErrorMessages(
                "The following ports are shared by users on the same machine",
                errorMessages);
    }

    private void assertValuesNotSame(String ipAddress, PortContainingPropertyNames propName,
            Collection<PropertyHolder> propertyCollection, Collection<String> errorMessages) {
        Map<String, Collection<String>> matches = new HashMap<String, Collection<String>>();
        for (PropertyHolder propHolder : propertyCollection) {
            String value = propName.getValue(propHolder.property);
            if(value != null && !value.isEmpty()){
                addToColelctionMap(matches, value, propHolder.userName);
            }
        }
        for (Map.Entry<String, Collection<String>> entry : matches.entrySet()) {
            Collection<String> nameCol = entry.getValue();
            if (nameCol != null && nameCol.size() > 1
                    && !onlyContainesDuplicateNames(nameCol)) {
                StringBuilder errorMsg = new StringBuilder("On ")
                .append(ipAddress);
                boolean addComma = false;
                for (String name : nameCol) {
                    if (addComma) {
                        errorMsg.append(',');
                    }
                    errorMsg.append(' ');
                    errorMsg.append(name);
                    addComma = true;
                }
                errorMsg.append(" have the same value of ")
                .append(entry.getKey()).append(" for ")
                .append(propName.getCorrectedName());
                errorMessages.add(errorMsg.toString());
            }
        }
    }

    private boolean onlyContainesDuplicateNames(Collection<String> nameCol) {
        int namesRemoved = 0;
        for (Map.Entry<String, String> entry : DUPLICATE_NAMES.entrySet()) {
            if (nameCol.contains(entry.getKey())
                    && nameCol.contains(entry.getValue())) {
                namesRemoved++;
            }
        }
        return (nameCol.size() - namesRemoved) < 2;
    }

    protected static <K, V> void addToColelctionMap(Map<K, Collection<V>> map,
            K key, V value) {
        Collection<V> valueCollection = map.get(key);
        if (valueCollection == null) {
            valueCollection = new ArrayList<V>();
            map.put(key, valueCollection);
        }
        valueCollection.add(value);
    }

    protected static class PropertyHolder {
        protected final Properties property;
        protected final String     fileName;
        protected final String     userName;

        protected PropertyHolder(String fileName, String userName,
                Properties properties) {
            this.fileName = fileName;
            this.userName = userName;
            this.property = properties;
        }

        @Override
        public String toString() {
            return fileName;
        }
    }

    protected static class PropertiesFilter implements FilenameFilter {
        @Override
        public boolean accept(File dir, String name) {
            return FILTER_FILENAME_PATTERN.matcher(name).matches();
        }

    }

    protected static enum PortContainingPropertyNames {
        shared_AQ_BROKER_PORT("shared.AQ_BROKER_PORT") {
            @Override
            public String getValue(Properties properties) {
                return stripPortFromIP(super.getValue(properties));
            }
        },
        shared_AQ_ADMIN_PORT(
                "shared.AQ_ADMIN_PORT"), cev2_BROKERURL("cev2.BROKERURL") {
            @Override
            public String getValue(Properties properties) {
                return stripPortFromIP(super.getValue(properties));
            }
        },
        ods_PORT(
                "ods.PORT"), ods_ADMIN_PORT("ods.ADMIN_PORT"), cev2_CE_CXF_PORT(
                        "cev2.CE_CXF_PORT");

        private final String correctedName;

        private PortContainingPropertyNames(String correctedName) {
            this.correctedName = correctedName;
        }

        public String getCorrectedName() {
            return correctedName;
        }

        public String getValue(Properties properties) {
            String result = properties.getProperty(getCorrectedName());
            return result != null ? result : "";
        }

        protected String stripPortFromIP(String ipString) {
            String[] stringArray = ipString.split("\\?");
            String temp = IP_PATTERN.matcher(stringArray[0]).replaceAll("");
            return NOT_PORT_PATERN.matcher(temp).replaceAll("");
        }
    }
}
