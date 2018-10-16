# mongod db plugin
The mongo driver version, 3.2.2, included in the mongod db input and output steps doesn't support all of the functionality available in mongodb 3.6, such as the **BigDecimal** data type. This patch allows all pdi versions below 8.1 to run version 3.6 of the mongo driver. [Issue 163](https://github.com/pentaho/pentaho-mongodb-plugin/issues/163)

_**NOTE:**_  Version 8.1 of the plugin seems to have the driver updated to 3.6.3 and hence this patch is no longer required

## Code changes
The code is a straightforward fork of the [mongodb plugin](https://github.com/pentaho/pentaho-mongodb-plugin) with the driver bumped up in the _pom.xml_ file

## PDI installation patch
The plugin is managed by Apache Karaf and hence the plugin has to be swapped after installation of PDI and the new mongo driver has to be registered

### Karaf
1. system/karaf/etc/startup.properties 
* The new mongo driver has to be registered here
2. system/karaf/system/org/mongodb/mongo-java-driver/3.6.1/mongo-java-driver-3.6.1.jar
* The new mongo driver has to be added to the managed plugins by karaf
3. system/karaf/system/pentaho/pentaho-mongodb-plugin/7.1.0.0-12/pentaho-mongodb-plugin-7.1.0.0-12.jar
* The patched mongo db plugin replaces the old plugin

### Lib
lib/mongo-java-driver-3.6.1.jar
* The mongo driver should also be included in the lib folder so that it is available in custom java steps

### Cache
Remember to remove the Karaf cache after installing the patch else it will not work


