Description
----
This project was made to fulfill the request of WalletHub's Assessment.

The request was:

* To write a parser in Java that parses web server access log file, loads the log to MySQL and checks if a given IP makes more than a certain number of requests for the given duration. 

  Java
  ----

  (1) Create a java tool that can parse and load the given log file to MySQL. The delimiter of the log file is pipe (|)

  (2) The tool takes "startDate", "duration" and "threshold" as command line arguments. "startDate" is of "yyyy-MM-dd.HH:mm:ss" format, "duration" can take only "hourly", "daily" as inputs and "threshold" can be an integer.

  (3) This is how the tool works:

      java -cp "parser.jar" com.ef.Parser --startDate=2017-01-01.13:00:00 --duration=hourly --threshold=100
	
	  The tool will find any IPs that made more than 100 requests starting from 2017-01-01.13:00:00 to 2017-01-01.14:00:00 (one hour) and print them to console AND also load them to another MySQL table with comments on why it's blocked.

	  java -cp "parser.jar" com.ef.Parser --startDate=2017-01-01.13:00:00 --duration=daily --threshold=250

	  The tool will find any IPs that made more than 250 requests starting from 2017-01-01.13:00:00 to 2017-01-02.13:00:00 (24 hours) and print them to console AND also load them to another MySQL table with comments on why it's blocked.


  SQL
  ---

  (1) Write MySQL query to find IPs that mode more than a certain number of requests for a given time period.

      Ex: Write SQL to find IPs that made more than 100 requests starting from 2017-01-01.13:00:00 to 2017-01-01.14:00:00.

  (2) Write MySQL query to find requests made by a given IP.
 	

  LOG Format
  ----------
  Date, IP, Request, Status, User Agent (pipe delimited, open the example file in text editor)

  Date Format: "yyyy-MM-dd HH:mm:ss.SSS"

  Also, please find attached a log file for your reference. 

  The log file assumes 200 as hourly limit and 500 as daily limit, meaning:

  (1) When you run your parser against this file with the following parameters

    java -cp "parser.jar" com.ef.Parser --startDate=2017-01-01.15:00:00 --duration=hourly --threshold=200

  The output will have 192.168.11.231. If you open the log file, 192.168.11.231 has 200 or more requests between 2017-01-01.15:00:00 and 2017-01-01.15:59:59

  (2) When you run your parser against this file with the following parameters

    java -cp "parser.jar" com.ef.Parser --startDate=2017-01-01.00:00:00 --duration=daily --threshold=500

  The output will have  192.168.102.136. If you open the log file, 192.168.102.136 has 500 or more requests between 2017-01-01.00:00:00 and 2017-01-01.23:59:59


  Deliverables
  ------------

  (1) Java program that can be run from command line
	
      java -cp "parser.jar" com.ef.Parser --accesslog=/path/to/file --startDate=2017-01-01.13:00:00 --duration=hourly --threshold=100 

  (2) Source Code for the Java program

  (3) MySQL schema used for the log data

  (4) SQL queries for SQL test

To make all these requests possible, I used:

- Java 8: to handle the main code development.
  Spring Batch: To handle file processing, data parsing and database processing. This framework made possible and easier the process of parsing the data and saving in database, of the logs. If you need to know about Spring Batch please check out the documentation in the following link: https://projects.spring.io/spring-batch/
  Spring: To do the necessary configurations of Spring Batch via annotations. If you need to know about Spring Framework please check out the documentation in the following link: https://projects.spring.io/spring-framework/
- Mysql Database: To store the information requested.
- Gradle: To download dependencies and compile the project.

Program Execution
----
System prerequisite:
- Java 8
- MySql Data base
- Gradle

Here you will find the program compiled as parser.jar. 

Here we use Spring batch framework to accomplish our goal of loading a really big web server log and analyze its data. 
In this case determine what IP address to block.
  
**application.properties** file has the program default configuration parameters that are:

**Connection String.** Here it uses a database named _**wallethub**_ with
port 3305 and as you can see if it does not exist it will create the database.

        datasource.url=jdbc:mysql://localhost:3305/wallethub?createDatabaseIfNotExist=true
        username to connect to mysql
        datasource.username=root
        password to connect to mysql
        datasource.password=mysql
        
**Chunk size for batch process**

        application.job.chunkSize=1000

This program is designed to run and create the whole schema thanks to Liquibase, but in case you want to run it on your own
you can find the schema creation in **schema.sql**.

The file named **queries.sql** contains queries for testing


Compile source code
----

If you want to compile the source code type :

        gradle clean heavyJar
