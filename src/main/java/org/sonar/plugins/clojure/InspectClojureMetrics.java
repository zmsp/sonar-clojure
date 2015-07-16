package org.sonar.plugins.clojure; /**
 * Created by shahadatm on 6/2/15.
 */

import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.measures.Metric;
import org.sonar.api.measures.Metrics;

import java.util.Arrays;
import java.util.List;

public class InspectClojureMetrics implements Metrics {

    //Holds file path of problematic files
    public static final Metric ISSUES_FILE =
            new Metric.Builder(
                    "clojure-issues-file", // metric identifier
                    "File path", // metric name
                    Metric.ValueType.DATA) // metric data type
                    .setDescription("List of issues")
                    .setQualitative(false)//True=Tells sonar to colorize the metric trending icon
                    .setDomain(CoreMetrics.DOMAIN_GENERAL)
                    .create();
    //Holds line numbers of problematic files
    public static final Metric ISSUES_LINE =
            new Metric.Builder(
                    "clojure-issues-line", // metric identifier
                    "Line number", // metric name
                    Metric.ValueType.DATA) // metric data type
                    .setDescription("File lines that have issues")
                    .setQualitative(false)//True=Tells sonar to colorize the metric trending icon
                    .setDomain(CoreMetrics.DOMAIN_GENERAL)
                    .create();
    //Holds detail error of problematic files
    public static final Metric ISSUES_ERROR =
            new Metric.Builder(
                    "clojure-issues-error", // metric identifier
                    "Error details", // metric name
                    Metric.ValueType.DATA) // metric data type
                    .setDescription("Error details for line")
                    .setQualitative(false)//True=Tells sonar to colorize the metric trending icon
                    .setDomain(CoreMetrics.DOMAIN_GENERAL)
                    .create();
    //Holds number of issues found
    public static final Metric ISSUES_COUNT =
            new Metric.Builder(
                    "clojure-issues-count", // metric identifier
                    "Count of issues", // metric name
                    Metric.ValueType.INT) // metric data type
                    .setDescription("Number of clojure issues")
                    .setQualitative(false)//True=Tells sonar to colorize the metric trending icon
                    .setDomain(CoreMetrics.DOMAIN_GENERAL)
                    .create();


    //Holds scanned file local URL. Isn't being used in widget
    public static final Metric FILES =
            new Metric.Builder(
                    "html-filnumix-link", // metric identifier
                    "HTML Files", // metric name
                    Metric.ValueType.DATA) // metric data type
                    .setDescription("Link to qualityclj files")
                    .setQualitative(true)//True=Tells sonar to colorize the metric trending icon
                    .setDomain(CoreMetrics.DOMAIN_GENERAL)
                    .create();


    //Instantiates metrics
    public List<Metric> getMetrics() {
        return Arrays.asList(
                ISSUES_FILE, ISSUES_ERROR, ISSUES_LINE, ISSUES_COUNT, FILES);
    }


}
