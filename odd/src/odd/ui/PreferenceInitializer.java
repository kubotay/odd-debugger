package odd.ui;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;

public class PreferenceInitializer extends AbstractPreferenceInitializer {

    public static final String EVENT_FILTER_DELIMITER = ",";

    private static final String[] JAVA_APPLICATION_FILTERS = {
            "java.*",
            "javax.*",
            "com.sun.*",
            "sun.*"
    };

    private static final String[] JAVA_APPLET_FILTERS = {
            "java.*",
            "javax.*",
            "com.sun.*",
            "sun.*"
    };

    private static final String[] JUNIT_APPLICATION_FILTERS = {
            "java.*",
            "javax.*",
            "com.sun.*",
            "sun.*",
            "org.junit.*",
            "org.junit.internal.*",
            "org.junit.runner.*",
            "org.junit.runners.*",
            "org.eclipse.jdt.internal.junit.*",
            "org.eclipse.jdt.internal.junit4.*",
            "$Proxy*"
    };

    public static String convertFilters(String[] filters){
        String result = "";
        for (String filter : filters) {
            result += filter;
            result += EVENT_FILTER_DELIMITER;
        }
        return result;
    }

    @Override
    public void initializeDefaultPreferences() {
        // TODO Auto-generated method stub

    }

}
