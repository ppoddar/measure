# measue
# Benchmarking and Performce Measurments

## Project description
 
 This project builds a service to measure database
 performance under configurable workloads.
 
### Project organization

     $ mvn -Dexec.executable='echo' -Dexec.args='${project.artifactId}' exec:exec -q

 
The project is organized into following sub-modules.

  1. lib-repo: a library related to 
  		datawarehouse that stores store measured data. 
  		Packaged as a jar archieve
  		
  2. lib-measure: a library provides
  		core functionalty of collecting 
  		measurements for  snapshot or benchmark.
  		Packaged as a jar archieve
  		
  3. lib-resource
  
  4. spring: a micro-service to provide underlysing
     	measurement utilty for web access.
     
### Build

		$ mvn clean install
		
### Run

     $ cd ~/workspace/measure-parent
     $ ./setup/run-spring.sh 
     
     $ open  http://localhost:8080/
     
## Developement

      $ cd measure-parent/public
      
  edit files directly, refresh the page
  
  
  ### Git commit
  
  git remote set-url origin git@github.com:ppoddar/measure.git
  
  git push origin master
  
    	 
