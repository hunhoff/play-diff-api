# play-diff-api

##################################################################################################################################
# WAES - Assignment Scalable Web
##################################################################################################################################
# Introduction
# As a WAES talent recruiter I want to be evaluate the coding skills of the candidate
#
# Goals
# Provide 2 http endpoints that accepts JSON base64 encoded binary data on both endpoints
# Provide another endpoint to show the diff-ed the  
#
# Expected results
# 	The endpoints should respect RestFull standards
# 	Inputs (create and update):
#		Endpoints: <host>/v1/diff/<ID>/left and <host>/v1/diff/<ID>/right
#		Header: content-type: application/json
#		Body: 
#		{
#		  "input":"<base64Value>"
#		}
# 	    Methods: POST and PUT
# 	    Validations: jsonFormat, parameter validation, is base64, not found (for update), conflict (for create)
#
#   Diff:
#       endpoints: <host>/v1/diff/<ID>
#	    Method: GET
#
#       Expected Results:
#         1. if equal return: 
#		     {
#		        "result": "inputs are equal"
#		     }
#         2. not of equal size
#            {
#               "result": "inputs have different sizes"
#            }
#         3. different base64 decode strings with same size
#            {
#               "result": "inputs have the same size",
#               "diff": "[Offset: <?> & Lenght: <?>, ...]"
#            }
#			 length represents the number of different chars in sequece
#
#      Suggestions for improvement:
#	     - improve API documentation (https://swagger.io/ is a good option)
#		 - improve code reuse with more functions/methods
#		 - add a in-memory db to save the content (it's fast and, probably can attend the needs)
#        - implement a delete method
#        - separate unit and functional tests on different classes
#         
##################################################################################################################################

##################################################################################################################################
# APP details and play framework
##################################################################################################################################

This application executes the difference of two base64 input strings 
The app was implemented using play framework (Please see the documentation about play https://www.playframework.com/documentation/latest/Home for more details)

## POSTMAN Collection
AlessandroDiff.postman_collection.json
https://www.getpostman.com/collections/b107c263905875554c73

## Running
Run this using [sbt](http://www.scala-sbt.org/).  You'll find a prepackaged version of sbt in the project directory:
```
sbt run
```

## Compile
```
sbt compile
```

## Test
```
sbt test
```

And then go to http://localhost:9000 to see the running web application.

## Controllers
- HomeController.java:
  This controller handle HTTP requests to the application's 

## Filters
- ExampleFilter.java
  A simple filter that adds a header to every response.
 
## test
- UnitTest.java 
  Class responsible to test the app (unit and functional)

##################################################################################################################################
# APP Playbook
##################################################################################################################################

JAVA version: 
	java version "1.8.0_131"
	Java(TM) SE Runtime Environment (build 1.8.0_131-b11)
	Java HotSpot(TM) 64-Bit Server VM (build 25.131-b11, mixed mode)
  
PROXY:
	The project was configured to use proxy and, this configuration was removed in the last commit.
	If PROXY configuration is required please add the below information in the config file as below:
	sbt-dist\conf\sbtconfig.txt
		-Dhttp.proxyHost=<host>
		-Dhttp.proxyPort=<port>
		-Dhttps.proxyHost=<host>
		-Dhttps.proxyPort=<port>

IDE:
The project is configured for eclipse
	project\plugins.sbt
	addSbtPlugin("com.typesafe.sbteclipse" % "sbteclipse-plugin" % "5.1.0")

	sbt.bat
		> compile
		> eclipse

##################################################################################################################################

##################################################################################################################################
# Unit and Functional testing results
##################################################################################################################################
[info] Test run started
[info] Test UnitTest.testForParameter started
[info] Test UnitTest.testBadRoute started
[info] Test UnitTest.testDifferentInputsSameSize started
[info] Test UnitTest.testDecodeInputs started
[info] Test UnitTest.testIndex started
[info] Test UnitTest.testSameSizeDifferentStringAtBegin started
[info] Test UnitTest.testJsonFormat started
[info] Test UnitTest.testSameSizeButAllDifferent started
[info] Test UnitTest.testNotFoundWhenDecoding started
[info] Test UnitTest.testDifferentInputs started
[info] Test UnitTest.testMissingRihtInputs started
[info] Test UnitTest.testUpdateInputs started
[info] Test UnitTest.testConflictsWhileCreatingInputs started
[info] Test UnitTest.testNotFoundWhenUpdating started
[info] Test UnitTest.testMissingLeftInputs started
[info] Test UnitTest.testCreateValidInputs started
[info] Test UnitTest.testSameBase64Inputs started
[info] Test UnitTest.testBase64 started
[info] Test UnitTest.testSameSizeDifferentStringMult started
[info] Test UnitTest.testSameSizeDifferentStringAtEnd started
[info] Test UnitTest.testSameString started
[info] Test UnitTest.testIndexByRoute started
[info] Test run finished: 0 failed, 0 ignored, 22 total, 5.161s
[info] Passed: Total 22, Failed 0, Errors 0, Passed 22
[success] Total time: 7 s, completed 13/08/2017 17:58:44
##################################################################################################################################
		 
		 
