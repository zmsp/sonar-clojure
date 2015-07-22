package org.sonar.plugins.clojure;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class KibitParserTest {


    String s = "At /home/shahadatm/Workspace/ClojureQuality/qualityclj/src/clj/qualityclj/handler.clj:37:\n" +
            "Consider using:\n" +
            "  (wrap-defaults\n" +
            "    (routes import-routes home-routes repo-routes app-routes)\n" +
            "    config)\n" +
            "instead of:\n" +
            "  (-> (routes import-routes home-routes repo-routes app-routes)\n" +
            "   (wrap-defaults config))\n" +
            "\n" +
            "At /home/shahadatm/Workspace/ClojureQuality/qualityclj/src/clj/qualityclj/imports/highlight.clj:13:\n" +
            "Consider using:\n" +
            "  (zero? (:exit result))\n" +
            "instead of:\n" +
            "  (= 0 (:exit result))\n" +
            "\n" +
            "At /home/shahadatm/Workspace/ClojureQuality/qualityclj/src/clj/qualityclj/imports/highlight.clj:45:\n" +
            "Consider using:\n" +
            "  (zero? (:exit result))\n" +
            "instead of:\n" +
            "  (= 0 (:exit result))\n" +
            "\n" +
            "At /home/shahadatm/Workspace/ClojureQuality/qualityclj/src/clj/qualityclj/models/db.clj:106:\n" +
            "Consider using:\n" +
            "  (seq\n" +
            "    (q\n" +
            "      '[:find ?file :in $ ?filepath :where [?file :file/path ?filepath]]\n" +
            "      (db @conn)\n" +
            "      filepath))\n" +
            "instead of:\n" +
            "  (not\n" +
            "    (empty?\n" +
            "      (q\n" +
            "        '[:find\n" +
            "          ?file\n" +
            "          :in\n" +
            "          $\n" +
            "          ?filepath\n" +
            "          :where\n" +
            "          [?file :file/path ?filepath]]\n" +
            "        (db @conn)\n" +
            "        filepath)))";

    @org.junit.Test
    public void testOne() throws Exception {

        String[] output = s.split("At");

        Pattern MY_PATTERN = Pattern.compile(":\\d+:");

        for (String o : output) {

            String[] line = o.split(MY_PATTERN.toString());
            String lineNum = "";
            String error = "";
            String file = "";

            if (line.length > 1) {
                Matcher m = MY_PATTERN.matcher(o);
                while (m.find()) {
                    lineNum = m.group(0).replace(":", "");

                }
                file = line[0];
                error = line[1];

            }


        }


    }

    public void testTwo() throws Exception {
    }
}
