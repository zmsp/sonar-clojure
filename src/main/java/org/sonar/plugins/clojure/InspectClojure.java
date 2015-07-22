package org.sonar.plugins.clojure;

import org.sonar.api.SonarPlugin;

import java.util.Arrays;
import java.util.List;

/**
 * Created by shahadatm on 6/2/15.
 */
public class InspectClojure extends SonarPlugin {


    public List getExtensions() {
        return Arrays.asList(

                InspectClojureMetrics.class,
                InspectClojureSensor.class,
                InspectClojureDashboardWidget.class);
    }
}