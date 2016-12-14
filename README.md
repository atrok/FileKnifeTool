
# FileKnifeTool
set of utilities to work with files

##INSTALLATION:


1. compile

*mvn clean install -Dmaven.test.skip=true*

to compile tests files:

*mvn test-compile*

2. create folder FileKnifeTool
3. copy over FileKnifeTool.jar file from /target folder to /FileKnifeTool folder
4. copy over /lib folder from /targetto /FileKnifeTool folder
5. in /FileKnifeTool folder create /conf folder
6. open FileKnifeTool.jar file and copy statistic.properties.ini file into /FileKnifeTool/conf folder
7. open FileKnifeTool.jar file and copy logback.xml file to /fileKnifeTool folder


##Usage


1. Parse log files

FileKnifeTool is a command line based utility designated to parse any tlib based log files (meaning the events logged by Genesys loglib, that's a part of any Genesys application)
During the parsing of the file it looks for timestamps to create an internal mapping of events and then to operate the statistics as per the defined sampling parameter. The resulting file should contain a statistical data delimited by comma (',') and saved as .csv file

Command line options

Usage: <main class> [options] [command] [command options]
  Commands:
  
    genesys       command to parse genesys config server files
      Usage: genesys [options]
        Options:
        * -d
             list of directories to be checked
        * -ext
             file extension to be looked (regular expression is expected)
          -out
             output filename
          -sample
             statdata sampling (1|10|60|24(h)) min(utes)
             Default: 10
          -statfile
             format of output file (csv|sql) 
             Default: statistic.properties.ini
             TODO 
             description needs to be adjusted. Designation of this file to provide stat metrics for internal stat engine to create a report file
          -style 
             style of processing (old|new)
             Default: new
             TODO obsolete needs to be dismissed as "new" used by default

    print      Usage: print [options]
        Options:
        * -d
             list of directories to be checked
        * -ext
             file extension to be looked
          -style
             style of processing (old|new)
             Default: new

    lms       command to parse alarms common files to retrieve log events and its categories
      Usage: lms [options]
        Options:
        * -d
             list of directories to be checked
        * -ext
             file extension to be looked (regular expression is expected)
          -format
             format of output file (csv|sql)
             Default: csv
          -out
             output filename
          -process
             data format for file handler (simple|record)
             Default: record
          -sample
             statdata sampling (1|10|60|24(h)) min
             Default: 10
          -sep
             String separator, '|' is separator by default
             Default: |
          -statfile
             format of output file (csv|sql)
             Default: statistic.properties.ini
          -style
             style of processing (old|new)
             Default: new

    delete      Usage: delete [options]
        Options:
        * -d
             list of directories to be checked
        * -ext
             file extension to be looked
          -style
             style of processing (old|new)
             Default: new

Examples:
*java -jar FileKnifeTool-0.0.1-SNAPSHOT.jar genesys -d D:\Share\distrib\3dparty\GarbageCleaner\logs -sample 10 -statfile statistic.properties.ini -ext .*\\.log* 

*java -jar FileKnifeTool-0.0.1-SNAPSHOT.jar genesys -d R:\Apple\1729536\unpacked  -ext "\.log" -sample 10 -out ss_mem.csv -statfile msgid.properties.ini*

*emphasized text*java -jar FileKnifeTool-0.0.1-SNAPSHOT.jar lms -d D:\Share\distrib\3dparty\GarbageCleaner\lms -ext ".+\.lms" -format sql

##Stat files

Stat file contains the defined metrics that determine the searching pattern and the calculation mechanizm of obtained data.
A set of metrics could be kept in the single stat file, in general case the resulting output should contain one column per metric. In case of no values the resulting output should contain 0 values in corresponding metric column.

In general Metric definition looks as follows:

    [<name> <optional calculation field>]
    stattype = <IncrementalStatistic|MaxStatistic|MinStatistic>
    regexp\=<regular expression of the log line to be looked for>
    field\=<field num>

Metric definition would vary depending on applied statistical type. Below are examples of possible stattypes

##Stat type definitions

### IncrementalStatistic
  it calculates the number of occurences of searched pattern 

Examples:

simple statistic to calculate the number of occurences of searched pattern defined by regexp

    [MSGCFG_OBJECTCHANGED2 sent]
    stattype=IncrementalStatistic
    regexp=.+Message MSGCFG_OBJECTCHANGED2 sent to.+


Next is the example of more advanced configuration where in name field we define an optional field parameter that's dynamic and is determined during runtime. Utility splits the string by spaces and we may access the certain column of split string.

For instance:

    22:44:38.480 Trc 24215 There are [1000] objects of type [CfgPerson] sent to the client [43] (application [default], type [SCE])

We want to calculate the number of times when more than >999 objects of any object type is sent to the client, and we want this statistic calculcated per object type. Object type is defined in 9th column, thus we specify ***$9*** argument in the name of metric definition

The metric definition would look like as follows

    [#NumGetRequests Objects of *$9* >999]
    stattype=IncrementalStatistic
    regexp=.+There are \\[[0-9]{4,}\\] objects of type.+

where

 - \$9 - Object type is defined in 9th column, thus we specify ***$9***  argument in the name of metric definition 
   
 - `\\[[0-9]{4,}\\]` - regular  expression that helps to narrow down to strings having more than 999  objects (ie having 4 and more digits)

Special use case of name parameter is reserved word ***$msgid***.

For instance: 

    [$msgID]
    stattype=IncrementalStatistic
    regexp=.+(Trc|Std|Int|Dbg).+

it would create columns for all log events which strings match the defined regular expression. 

### MaxStatistic/MinStatistic/SumStatistic
These stat types are common since all needs one more parameter defined - the field to be considered as a source of statistical data. These stattypes do not aggregate the found strings but operates with the data retrieved from the strings.

The names of these statypes speaks for themselves:
**MaxStatistic** - provide the max value out of the set of collected values on the time interval
**MinStatistic** - provide the min value out of the set of collected values on the time interval
**SumStatistic** - sums up the values of the set of collected values on the time interval

Parametrized metric naming applies as well.

For instance:

    22:44:38.480 Trc 24215 There are [1000] objects of type [CfgPerson] sent to the client [43] (application [default], type [SCE])

We want to know the max number of objects of any type sent to the client

    [#NumObjectSent per request: $9] 
    stattype=MaxStatistic
    regexp=.+There are \\[[0-9]{3,}\\] objects of type.+ 
    field=5 

where 

 - **\$9** - Object type is defined in 9th column, thus we specify $9 argument in the name of metric definition
 - **`\\[[0-9]{4,}\\]`** - regular expression that helps to narrow down to strings having more than 999 objects (ie having 4 and more digits)
 - **field=5** <== reference to 5th column in split string, 

Example: 
calculate the overall amount of objects of any type sent to the client

    [#SumObjectSent: $9]
    stattype=SumStatistic
    regexp=.+There are \\[[0-9]{3,}\\] objects of type.+
    field=5
Example:
get the minimal number of objects of any type sent to the client

    [#MinObjectSent: $9]
    stattype=MinStatistic
    regexp=.+There are \\[[0-9]{3,}\\] objects of type.+
    field=5

##DatPlot

There is a third party .Net tool called DatPlot to build reports based on ***.csv*** files.

Here are steps to follow to import and build the graphs using this tool

 - Install DatPlot
get it from  http://www.datplot.com/
 - Import data from data source
 - load file ***File->Load New DataSource***
 - Set parameters as follows
   - Get column names from 1  
  - Plot data starts line 2  
  - Column delimeter ***, (Comma)***  
- Number decimal symbol   ***.(Dot)***
 - set X-axis column ***Time***, Date/Time format ***YYYY/MM/DD hh:mm:ss.fff***
 
 - Plot the graph
 - right click on pane and select ***Data Curve -> Add***, then select the column to build graph from.

> Written with [StackEdit](https://stackedit.io/).
