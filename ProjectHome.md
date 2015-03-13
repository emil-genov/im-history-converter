# Highly modular and extensible chat history converter. It provides: #
  * An core functionality including:
    * Model representing chat account, chat user and history item
    * Functionality to wire front-end and back-end plugins for whatever format we want to transcode
  * Front end plugin for Pidgin/libpurple history
  * Back end plugin for Gmail

# Easiest run #
  1. Download [program jar file](http://code.google.com/p/im-history-converter/downloads/detail?name=im-history-converter-1.0-jar-with-dependencies.jar&can=2&q=)
  1. Go to folder where you download it, open command prompt there, and execute:
```
java -jar im-history-converter-1.0-jar-with-dependencies.jar
```

# Easy run (for nonfamiliar with maven) #
  1. Download [fat jar](http://code.google.com/p/im-history-converter/downloads/detail?name=im-history-converter-1.0-jar-with-dependencies.jar&can=2&q=) and [config file](http://code.google.com/p/im-history-converter/downloads/detail?name=config.properties&can=2&q=), put them in same directory
  1. Edit config file (use [documentation](ConfigurationFileDoc.md))
  1. In command line change to dir, you've saved both files into. Run program wit:
```
java -jar im-history-converter-1.0-jar-with-dependencies.jar config.properties
```



# Easy build and run: #
  1. Checkout from SVN
  1. Build using:
```
mvn compile
```
  1. Edit configuration file: im-history-converter/src/main/resources/config.properties (for instructions [see documentation](ConfigurationFileDoc.md))
  1. Run converter with:
```
mvn exec:java 
```

# To build fat jar #
  1. Build using:
```
mvn package -PjarWithDeps
```
  1. After that run with:
```
java -jar target\im-history-converter-XXX.YYY-jar-with-dependencies.jar .\src\main\resources\config.properties
```
second parameter is path to configuration file.

# To extend the project #
  1. Add main jar of project to dependencies of your project:
```
<groupId>com.emilgenov</groupId>
<artifactId>im-history-converter</artifactId>
<version>1.0-SNAPSHOT</version>
```
  1. Create either front-end or back-end plugin, extending `api/FrontEnd or api/BackEnd`
  1. Compile and build your own extension jar
  1. Change config file to use your front-end and/or back-end class
  1. Run with:
```
java -cp im-history-converter-XXX.YYY-jar-with-dependencies.jar <your.extensions.jar> com.emilgenov.historyConverter.ConverterApp ./src/main/resources/config.properties
```