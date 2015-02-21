#hegira-generator

Generates entities on a remote Datastore instance for Hegira test.

###Bulild
Just run:

```
mvn clean package
```
An executable jar is created.

###Usage
To run the program execute it a har:

```
java -jar target/generator-1.0-SNAPSHOT-jar-with-dependencies.jar
```
This will print the usage.

Two are the possible usages:

- `-c` or `--clean` will perform a complete cleanup of the remote Datastore deleting all the entities of all Kind(s).
- `-a <arg>` or `--amount <arg>`  will generate on the remote Datastore the given number of entities.

###Configure remote datastore
Datastore instance must be configured in two places:

- [Clean.java](https://github.com/Arci/hegira-generator/blob/master/src/main/java/it/polimi/hegira/command/Clean.java) modifying constants accordingly
- [persistence.xml](https://github.com/Arci/hegira-generator/blob/master/src/main/java/it/polimi/hegira/command/Clean.java) modifying properties, for GAE extension documentation see [here](https://github.com/Arci/kundera-gae-datastore);

Then rebuild the project.