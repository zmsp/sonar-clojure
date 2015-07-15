# InspectClojure Sonar plugin
InspectClojure uses eastwood to analyze Clojure code. Project is still under construction.

### Contributors
[Zobair Shahadat] (https://github.com/zmsp/)
[Javier Hilty] (https://github.com/hiltyj/)

### Prerequisites

You will need to install [docker-sonar image](https://github.com/harbur/docker-sonarqube) and create a shared volume by adding the following onto the docker-compose.yml file under sonarqube. 

`  volumes:
    - /opt/sonar/extensions:/opt/sonar/extensions`

Need to have [Eastwood](https://github.com/jonase/eastwood) and [Kibit](https://github.com/jonase/kibit) added to the projects.clj file as plugins.


### 1. Get the plugin

#### Download the jar
[Download](HTTP://example.com)

#### Compiling from Source
* Clone this project
* CD to the project
* Run `mvn package`
* Find the resulting jar in the target folder

### 2. Placing the jar file 

Place the jar produced from above step, found on the target folder, onto `/opt/sonar/extensions/plugins`

### 3. Run 
Run docker-compose up from your [docker-sonar image](https://github.com/harbur/docker-sonarqube) folder

### 4. Using the plugin

* Create a sonar-project.properties file and [Run](http://docs.sonarqube.org/display/SONAR/Analyzing+Source+Code) sonar-runner (See example folder for a sample)
    * Add the line `sonar.import_unknown_files=true` see the [example](tree/master/Example/)
* Open Sonar `localhost:9000` on the browser 
* Place Clojure widget to the dashboard
* Optional: Place Size Metrics widget to the dashboard and resize the dashboard

