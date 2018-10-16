##mongod db plugin
The mongo driver version, 3.2.2, included in the mongod db input and output steps doesn't support all of the functionality available in mongodb 3.6, such as the **BigDecimal** data type. This patch
allows all pdi versions below 8.0 to run version 3.6 of the mongo driver.

**NOTE:** Version 8.1 of the plugin seems to have the driver updated to 3.6.3 and hence this patch is no longer required

### Code changes
The code is a straightforward fork of the [mongodb plugin](https://github.com/pentaho/pentaho-mongodb-plugin) with the driver bumped up in the _pom.xml_ file
