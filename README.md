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
To run the program execute it a har:

```
java -jar target/generator-1.0-jar-with-dependencies.jar
```
This will print the usage.

Two are the possibilities:

- `-c` or `--clean` will perform a complete cleanup of the remote Datastore deleting all the entities of all Kind(s).
- `-a <arg>` or `--amount <arg>`  will generate on the remote Datastore the given number of entities.

Datastore cleanup is exploited querying the Datastore through the [Metadata API](https://cloud.google.com/appengine/docs/java/datastore/metadataqueries) retrieving all the persisted Kind(s) and then deleting all entities for each Kind.
###Configure remote datastore
Datastore instance must be configured in two places:

- [Clean.java](https://github.com/Arci/hegira-generator/blob/master/src/main/java/it/polimi/hegira/command/Clean.java) modifying constants accordingly
- [persistence.xml](https://github.com/Arci/hegira-generator/blob/master/src/main/java/it/polimi/hegira/command/Clean.java) modifying properties, for GAE extension documentation see [here](https://github.com/Arci/kundera-gae-datastore)

Then rebuild the project.
