package org.sonar.plugins.clojure; /**
 * Created by shahadatm on 6/2/15.
 */

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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

    //Turn on or off the following
    private boolean fileFinder = true;
    private boolean eastwoodLint = true;
    private boolean kibitLint = true;

    private static final Log LOG = LogFactory.getLog(InspectClojureSensor.class);

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

    //==========================================================
    // Description: Runs bash cmd given cmd and directory
    // Input: Directory and CMD
    // Output: Shell output string
    //==========================================================
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
                System.out.println(inputLine);
                result += inputLine + "\n";
            }
            in.close();

        } catch (IOException e) {
            LOG.error("▂▃▅▇█▓▒░۩۞۩ EASTWOOD IO EXCEPTION ۩۞۩░▒▓█▇▅▃▂", e);

        } catch (Exception e) {
            LOG.error("▂▃▅▇█▓▒░۩۞۩ EASTWOOD EXCEPTION ۩۞۩░▒▓█▇▅▃▂", e);
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

        if (fileFinder) {
            System.out.println("●▬▬▬▬๑۩۩๑▬▬▬▬▬● Running file finder ●▬▬▬▬๑۩۩๑▬▬▬▬▬●");
            buildFileProperties(baseDirectory);
        }

        if (eastwoodLint) {
            System.out.println("●▬▬▬▬๑۩۩๑▬▬▬▬▬● Running Eastwood ●▬▬▬▬๑۩۩๑▬▬▬▬▬●");
            buildEastwoodLintProperties(baseDirectory);
        }

        if (kibitLint) {
            System.out.println("●▬▬▬▬๑۩۩๑▬▬▬▬▬● Running Kibit ●▬▬▬▬๑۩۩๑▬▬▬▬▬●");
            buildKibitLintProperties(baseDirectory);

        }


        System.out.println("●▬▬▬▬๑۩۩๑▬▬▬▬▬● Saving measures ●▬▬▬▬๑۩۩๑▬▬▬▬▬●");


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
        String kibitOutput = runCMD(baseDirectory, "lein kibit");
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

                    filePath.add(totalIssues, file);
                    fileLine.add(totalIssues, lineNum);
                    fileError.add(totalIssues, error);
                    totalIssues++;
                }
                System.out.println("Debug:____" + lineNum + "_____" + file + "______" + error);
            }

        }
    }

    //==========================================================
    // Description: Finds CLJ and CLJS file and store properties
    // Input: Base directory
    // Output: None
    //==========================================================
    public void buildFileProperties(String baseDirectory) {

        try {
            int count = 1;
            for (File file : findCLJFile(baseDirectory + "/src/")) {
                files.add("file://" + file.getCanonicalPath(), count);
                count++;
            }
            for (File file : findCLJFile(baseDirectory + "/test/")) {
                files.add("file://" + file.getCanonicalPath(), count);
                count++;
            }
        } catch (Exception e) {
            e.printStackTrace(System.out);
        }

    }

    //==========================================================
    // Description: Finds CLJ and CLJS file and store properties
    // Input: Base directory
    // Output: List of files
    //==========================================================
    public List<File> findCLJFile(String directory) {
        List<File> filesList = null;
        try {
            //Get qualityclj files.
            File dir = new File(directory);
            String[] extensions = new String[]{"clj", "cljs"};


            filesList = (List<File>) FileUtils.listFiles(dir, extensions, true);

        } catch (Exception e) {
            LOG.error("▂▃▅▇█▓▒░۩۞۩ FIND FILE: EXCEPTION ۩۞۩░▒▓█▇▅▃▂", e);
        }
        return filesList;

    }

    //==========================================================
    // Description: Parse eastwood output
    // Input: Base directory
    // Output: None
    //==========================================================
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
            LOG.error("▂▃▅▇█▓▒░۩۞۩ EASTWOOD IO EXCEPTION ۩۞۩░▒▓█▇▅▃▂", e);
        } catch (Exception e) {
            LOG.error("▂▃▅▇█▓▒░۩۞۩ EASTWOOD EXCEPTION ۩۞۩░▒▓█▇▅▃▂", e);

        }
    }

    public boolean shouldExecuteOnProject(Project project) {
       String baseDirectory = fileSystem.baseDir().toString();
       List<File> fileList = findCLJFile(baseDirectory);
    
       return fileList.isEmpty();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}
