package org.sonar.plugins.clojure;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.logging.Level;


public class Logger {

    private static Logger instance;

    static {
        instance = new Logger();
    }

    private final Log log;

    private Logger() {
        log = LogFactory.getLog(InspectClojure.class);
    }

    public static Logger getInstance() {
        return instance;
    }

    public void error(String message, Object o) {
        log.error(message, (Exception) o);
    }

    public void info(String message) {
        log.info(message);
    }
}
