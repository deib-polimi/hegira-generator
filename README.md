#hegira-generator

The program generates data and store them in a remote Datastore instance for Hegira test.
Data generation is provided through CPIM and Kundera GAE Datastore extension.

###Bulild
Just run:

```
mvn clean package
```
An executable jar is created.

###Usage
To run the program execute the generated jar with dependencies:

```
java -jar target/generator-1.0-jar-with-dependencies.jar
```
This will print the usage.

Two are the possibilities:

- `-c` or `--clean` will perform:
    - a complete cleanup of the remote Datastore deleting all the entities of all Kind(s)
    - a cleanup of current backed up files
- `-a <arg>` or `--amount <arg>`  will generate on the remote Datastore the given number of entities foreach table.

Datastore cleanup is exploited querying the Datastore through the [Metadata API](https://cloud.google.com/appengine/docs/java/datastore/metadataqueries) retrieving all the persisted Kind(s) and then deleting all entities for each Kind.

###State backup
State of generation is backed up inside the backup folder specified inside `migration.xml`.
Besides the file for sequence number range backup, a file named `generation` will be created, this file contains, one per line, the tables completely generated, those table will not be generated again if the program is executed again.

###Configuration
_Note that each configuration change requires a rebuild for the changes to be applied._

####Configure Datastore instance
To configure the Datastore instance, two files must be updated:

- [Clean.java](https://github.com/Arci/hegira-generator/blob/master/src/main/java/it/polimi/hegira/command/Clean.java) modifying constants accordingly
- [persistence.xml](https://github.com/Arci/hegira-generator/blob/master/src/main/resources/META-INF/persistence.xml) modifying properties, for GAE extension documentation see [here](https://github.com/Arci/kundera-gae-datastore)

#####Configure CPIM
Check the configuration for CPIM inside the [META-INF](https://github.com/Arci/hegira-generator/tree/master/src/main/resources/META-INF) folder.

Four are the required files:

- configuration.xml
- migration.xml
- persistence.xml
- queue.xml

since `persistence.xml` has been already configured for the remote Datastore instance, the only relevant file to be checked is the `migration.xml` file.
For the possible configuration available check the [template file](https://github.com/Arci/modaclouds-cpim-library/blob/master/templates/migration-template.xml).

Note that the sequence number range will be changed at runtime in order to limit the number of requests to zookeeper.
The runtime range will be _twice the amount_ of entities to generate unless the case in which is greater than 100, in which will be limited to 100.