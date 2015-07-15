default:
	mvn package
	cp target/clojure.sonar-1.0-SNAPSHOT.jar /opt/sonar/extensions/plugins/clojure.sonar.jar
	cd Example/orion
	echo Makesure to start sonarqube server before you start
	sonar-runner
	
	
