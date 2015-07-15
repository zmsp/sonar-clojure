package org.sonar.plugins.clojure;

import org.sonar.api.Properties;
import org.sonar.api.Property;
import org.sonar.api.SonarPlugin;

import java.util.Arrays;
import java.util.List;

/**
 * Created by shahadatm on 6/2/15.
 */
@Properties({
        @Property(
                key = InspectClojure.MY_PROPERTY,
                name = "SonarInspectClojure",
                description = "Inspect Clojure code using eastwood and kibit",
                defaultValue = "Inspect Clojure"

        )

})
public class InspectClojure extends SonarPlugin {
    public static final String MY_PROPERTY = "sonar.example.myproperty";

    public List getExtensions() {
        return Arrays.asList(
                InspectClojureMetrics.class,
                InspectClojureSensor.class,
                InspectClojureDashboardWidget.class);
    }
}
