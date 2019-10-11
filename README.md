# Measure

This is an umbrella project. The primary goal is to offer ability to users to submit long-running batch jobs throw web  interface. The user specifies minimal job details. 
Consider varities of jobs -- running test suites, taking databse measurements or running performance benchmark. Moreover, running such job with combinations of configuration parameters.    
   


## Project description

  Currently, the project contains a small portion that runs test suits via `nutest` on Nutanix clusters. The submitter does not specify the target cluster. This articact determines a cluster for optimal utlization.
 
### Project organization

This project is built as multi-module `maven` project. 

[INFO] parent                                                             [pom]
[INFO] lib-common                                                         [jar]
[INFO] lib-capacity                                                       [jar]
[INFO] lib-resource                                                       [jar]
[INFO] lib-scheduler                                                      [jar]
[INFO] lib-measure                                                        [jar]
[INFO] spring                                                             [jar]


###  Build

    The build the entitre project

    $ mvn clean install -DskipTests=true
    

### Run
   Once the project is built, run Spring Boot Application

   $ ./setup/run-spring.sh

  This will start a web application at http://localhost:8080
  
  
  ### View
  
     Go to http://localhost:8080/perspective.html





========= IGNORE BELOW ========



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
  
     $ git remote set-url origin git@github.com:ppoddar/measure.git
  
     $ git push origin master
  
    	 
