## Sonar Project Properties example
Need to have sonar.import_unknown_files=true in order to import clj and cljs files


Create a file called sonar-project.properties in the root directory of the clojure project.

```
# Required metadata

# Add an unique key
sonar.projectKey=MyClojureProjectKey

# Can be what ever
sonar.projectName=My Clojure Project Name
sonar.projectVersion=1.0

# Comma-separated paths to directories with sources (required)
sonar.sources=src/ , test/

# Must have this in order to import clj and cljs files form the sources
sonar.import_unknown_files=true

# Encoding of the source files
sonar.sourceEncoding=UTF-8
```
