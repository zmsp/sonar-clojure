package org.sonar.plugins.clojure;

import org.apache.commons.io.FileUtils;
import org.sonar.api.batch.Sensor;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.config.Settings;
import org.sonar.api.measures.Measure;
import org.sonar.api.measures.PropertiesBuilder;
import org.sonar.api.resources.Project;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


//Sensor must implement Sensor class
public class InspectClojureSensor implements Sensor {

    private static final Logger LOG = Logger.getInstance();
    //Turn on or off the following
    private boolean fileFinder = true;
    private boolean eastwoodLint = true;
    private boolean kibitLint = true;
    //Properties containing error info
    private PropertiesBuilder<String, Integer> files = new PropertiesBuilder<String, Integer>();
    private PropertiesBuilder<Integer, String> filePath = new PropertiesBuilder<Integer, String>();
    private PropertiesBuilder<Integer, String> fileLine = new PropertiesBuilder<Integer, String>();
    private PropertiesBuilder<Integer, String> fileError = new PropertiesBuilder<Integer, String>();


    //Counter for number of issues
    private int totalIssues = 0;

    //IDK
    private FileSystem fileSystem;

    //Constructor for sensor
    public InspectClojureSensor(Settings settings, FileSystem fileSystem) {
        this.fileSystem = fileSystem;
    }

    /**
     * Runs bash cmd given cmd and directory
     *
     * @param dir     Directory
     * @param leinCmd bash cmd
     * @return Shell output string
     */
    private static String runCMD(String dir, String leinCmd) {

        String cmdStr = "cd " + dir + "\n" + leinCmd;
        String[] cmd = {"/bin/sh", "-c", cmdStr};
        String result = null;
        try {
            Process p = Runtime.getRuntime().exec(cmd);
            BufferedReader in =
                    new BufferedReader(new InputStreamReader(p.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                LOG.info(inputLine);
                result += inputLine + "\n";
            }
            in.close();

        } catch (IOException e) {
            LOG.error("▂▃▅▇█▓▒░Parsing exception░▒▓█▇▅▃▂", e);

        } catch (Exception e) {
            LOG.error("▂▃▅▇█▓▒░Parsing exception░▒▓█▇▅▃▂", e);
        }
        return result;
    }

    //==========================================================
    // Description: Runs shell
    // Input: Directory and CMD
    // Output: Shell output string
    //==========================================================

    public void analyse(Project project, SensorContext sensorContext) {
        String baseDirectory = fileSystem.baseDir().toString();
        LOG.info("Clojure project detected, running sonar-clojure");
        if (fileFinder) {
            LOG.info("●▬▬▬▬▬▬▬▬▬● Running file finder ●▬▬▬▬▬▬▬▬▬●");
            buildFileProperties(baseDirectory);
        }

        if (eastwoodLint) {
            LOG.info("●▬▬▬▬▬▬▬▬▬● Running Eastwood ●▬▬▬▬▬▬▬▬▬●");
            buildEastwoodLintProperties(baseDirectory);
        }

        if (kibitLint) {
            LOG.info("●▬▬▬▬▬▬▬▬▬● Running Kibit ●▬▬▬▬▬▬▬▬▬●");
            buildKibitLintProperties(baseDirectory);

        }


        LOG.info("●▬▬▬▬▬▬▬▬▬● Saving measures ●▬▬▬▬▬▬▬▬▬●");


        sensorContext.saveMeasure(new Measure(InspectClojureMetrics.ISSUES_COUNT, (double) totalIssues));
        sensorContext.saveMeasure(new Measure(InspectClojureMetrics.ISSUES_LINE, fileLine.buildData()));
        sensorContext.saveMeasure(new Measure(InspectClojureMetrics.ISSUES_ERROR, fileError.buildData()));
        sensorContext.saveMeasure(new Measure(InspectClojureMetrics.ISSUES_FILE, filePath.buildData()));

        sensorContext.saveMeasure(new Measure(InspectClojureMetrics.FILES, files.buildData()));


    }

    //==========================================================
    // Description: Builds data based on kibit output
    // Input: Project directory
    // Output: None
    //==========================================================
    private void buildKibitLintProperties(String baseDirectory) {

        try {

            String kibitOutput = runCMD(baseDirectory, "lein kibit");
            if (!"At".contains(kibitOutput)) {
                LOG.info("No error found from kibit analysis");
                return;
            }
            String[] kibitOutputSplit = kibitOutput.split("At");
            final Pattern MY_PATTERN = Pattern.compile(":\\d+:");
            for (String o : kibitOutputSplit) {
                String[] kibitOutputSplitByPattern = o.split(MY_PATTERN.toString());
                if (kibitOutputSplitByPattern.length > 1) {
                    String lineNum = null;
                    String error = null;
                    String file = null;
                    Matcher m = MY_PATTERN.matcher(o);
                    while (m.find()) {
                        lineNum = m.group(0).replace(":", "");
                    }
                    file = kibitOutputSplitByPattern[0].replace(baseDirectory, "");
                    error = kibitOutputSplitByPattern[1];

                    if (lineNum != null && error != null && file != null) {

                        //Add parallel properties builder

                        filePath.add(totalIssues, file);
                        fileLine.add(totalIssues, lineNum);
                        fileError.add(totalIssues, error);
                        totalIssues++;
                    }

                }

            }


        } catch (NullPointerException e) {
            LOG.error("▂▃▅▇█▓▒░ KIBIT IO EXCEPTION  ░▒▓█▇▅▃▂", e);
        } catch (Exception e) {
            LOG.error("▂▃▅▇█▓▒░ KIBIT EXCEPTION  ░▒▓█▇▅▃▂", e);

        }
    }

    /**
     * Finds CLJ and CLJS file and store properties
     *
     * @param baseDirectory Base directory
     */
    public void buildFileProperties(String baseDirectory) {

        try {
            int fileNum = 1;
            for (File file : findCLJFile(baseDirectory)) {
                files.add("file://" + file.getCanonicalPath(), fileNum);
                fileNum++;
            }
        } catch (Exception e) {
            LOG.error("▂▃▅▇█▓▒░Building file properties error░▒▓█▇▅▃▂", e);
        }

    }

    /**
     * Description: Finds CLJ and CLJS file and store properties
     *
     * @param directory base directory
     * @return list of clj and cljs files
     */
    public List<File> findCLJFile(String directory) {
        List<File> filesList = null;
        try {
            //Get qualityclj files.
            File dir = new File(directory);
            String[] extensions = new String[]{"clj", "cljs"};
            filesList = (List<File>) FileUtils.listFiles(dir, extensions, true);

        } catch (Exception e) {
            LOG.error("▂▃▅▇█▓▒░ FIND FILE: EXCEPTION  ░▒▓█▇▅▃▂", e);
        }
        return filesList;

    }

    /**
     * Description: Run and parse eastwood output
     * @param baseDirectory Base directory
     */
    private void buildEastwoodLintProperties(String baseDirectory) {

        String output = "";

        try {
            Pattern p = Pattern.compile("[^:]+:\\d+:\\d+:.*");
            output = runCMD(baseDirectory, "lein eastwood");
            BufferedReader br = new BufferedReader(new StringReader(output));
            List<String> lines = new ArrayList<String>();
            String line;
            while ((line = br.readLine()) != null) {
                Matcher m = p.matcher(line);
                while (m.find()) {
                    lines.add(m.group());
                }
            }
            for (String temp : lines) {
                String[] tokens = temp.split(":");
                filePath.add(totalIssues, tokens[0]);
                fileLine.add(totalIssues, tokens[1]);
                fileError.add(totalIssues, temp);
                totalIssues++;
            }
        } catch (IOException e) {
            LOG.error("▂▃▅▇█▓▒░ EASTWOOD IO EXCEPTION  ░▒▓█▇▅▃▂", e);
        } catch (Exception e) {
            LOG.error("▂▃▅▇█▓▒░ EASTWOOD EXCEPTION  ░▒▓█▇▅▃▂", e);

        }
    }


    //TODO write a method to check if it should execute on project
    /* public Boolean checkShouldExecuteOnProject() {

         String baseDirectory = fileSystem.baseDir().toString();

        FileSystem fs = fileSystem;

        fs.hasFiles(fs.predicates().hasLanguage("clj"));
        return !findCLJFile(baseDirectory).isEmpty();
    }*/
    public boolean shouldExecuteOnProject(Project project) {
        File f = new File(fileSystem.baseDir().toString() + "/project.clj");
        boolean execute = f.exists() && !f.isDirectory();
        if (execute) {
            return true;
        } else {
            LOG.info(f.getPath().toString() + " Does not exist. Skipped running InspectClojure");
            return false;
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}