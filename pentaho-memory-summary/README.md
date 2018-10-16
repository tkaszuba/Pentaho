# memory summary plugin
The memory summary plugin is a fork and an extension of the [Memory Group By](https://wiki.pentaho.com/display/EAI/Memory+Group+by) step allowing more than one grouping criteria to be used with the same aggregation functions. It funcitons the same way as the [proc summary](http://documentation.sas.com/?docsetId=proc&docsetTarget=p0aq3hsvflztfzn1xa2wt6s35oy6.htm&docsetVersion=9.4&locale=en) step in SAS Base.

In order to run different grouping criteria on the same aggregation functions the following steps need to be carried out with the Memory Group By Step

<img src="https://raw.githubusercontent.com/tkaszuba/pentaho/master/pentaho-memory-summary/samples/Memory%20Group%20By.png" width="50%" height="50%">

With large data sets and many grouping criteria this will lead to very poor performance or out of memory issues, due to the copying of the original data stream. 

The Memory summary plugin allows the same aggregation to be rerun against different grouping criteria on each incoming row once, requiring no copying 

<img src="https://raw.githubusercontent.com/tkaszuba/pentaho/master/pentaho-memory-summary/samples/Memory%20Summary.png" width="50%" height="50%">

The grouping criteria is defined in a transposed way compared to the memory group by step

<img src="https://raw.githubusercontent.com/tkaszuba/pentaho/master/pentaho-memory-summary/samples/Summary%20details.png" width="50%" height="50%">

