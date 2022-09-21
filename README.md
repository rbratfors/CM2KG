# Historization of Enterprise Architecture Models
Repository for the degree project *Historization of Enterprise Architecture Models and Analysis Results for Optimization* by Robin Bråtfors.

## Web UI
To start the Web UI navigate to `webapp` folder and start the Spring Application. The default port the app is running is 8080. Default app can be accessed `http://localhost:8080/`.

## Requirements
In order to use Neo4j the database connection should be configured in `at.ac.tuwien.big.msm.cmgba.graphml\src\graphDb\neo4jConnector.java`:
```java
private Driver driver;
private String uri = "bolt://localhost:7687";
private String user = "neo4j";
private String password = "admin";
```
