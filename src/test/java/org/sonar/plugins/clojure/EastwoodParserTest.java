package org.sonar.plugins.clojure;


import java.io.BufferedReader;
import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class EastwoodParserTest {



    @org.junit.Test
    public void testOne() throws Exception {
//        URL url = Resources.getResource("1.txt");
//        String text = Resources.toString(url, Charsets.UTF_8);
//        List<String> lines = parse(text);
//        assertEquals(1, lines.size());
//        assertTrue(lines.get(0).startsWith("test/reg/core"));
        testTwo();
    }

    public void testTwo() throws Exception {

        String output = "";
        double totalIssues = 0;
        List <String> matches = new ArrayList<String>();
        try {


            Pattern p = Pattern.compile("[^:]+:\\d+:\\d+:.*");


            output = "== Eastwood 0.2.1 Clojure 1.6.0 JVM 1.8.0_45\n" +
                    "Directories scanned for source files:\n" +
                    "  env/dev/clj src/clj src/cljs test\n" +
                    "== Linting reg.handler ==\n" +
                    "== Linting reg.server ==\n" +
                    "== Linting reg.dev ==\n" +
                    "== Linting reg.core-test ==\n" +
                    "Entering directory `/home/shahadatm/Workspace/orion'\n" +
                    "test/reg/core_test.clj:91:5: constant-test: Test expression is always logical true or always logical false: (clojure.core/seq (clojure.core/list 1)) in form (if temp__4124__auto__ (clojure.core/let [more__35__auto__ temp__4124__auto__] (clojure.core/let [result__36__auto__ (clojure.core/apply clojure.core/= a__34__auto__ more__35__auto__)] (if result__36__auto__ (clojure.test/do-report {:type :pass, :expected a__34__auto__, :actual more__35__auto__, :message nil}) (clojure.test/do-report {:type :fail, :diffs (clojure.core/map clojure.core/vector more__35__auto__ (clojure.core/map (fn* [p1__33__37__auto__] (clojure.core/take 2 (clojure.data/diff a__34__auto__ p1__33__37__auto__))) more__35__auto__)), :expected a__34__auto__, :actual more__35__auto__, :message nil})) result__36__auto__)) (throw (java.lang.Exception. \"= expects more than one argument\")))\n" +
                    "== Warnings: 1 (not including reflection warnings)  Exceptions thrown: 0\n";

            BufferedReader br = new BufferedReader(new StringReader(output));





            String line;

            while((line=br.readLine())!=null) {
                    Matcher m = p.matcher (line);
                    while (m.find ()){
                        matches.add (m.group ());
                        System.out.println(m.group());

                    }

            }




        } catch (Exception e) {
            e.printStackTrace();

        }
    }
}
