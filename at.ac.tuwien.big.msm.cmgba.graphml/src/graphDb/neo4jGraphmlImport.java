package graphDb;

public class neo4jGraphmlImport {

	private neo4jConnector connector = new neo4jConnector();

	private static String deleteAllNodes = "MATCH (n)\r\n" + "DETACH DELETE n";
	private String importGraphmlFile = "CALL apoc.import.graphml(\"http://localhost:8080/repository/graph/b2715d3a-9158-439b-8a2b-873089dd38d9\", {})";
	private static String setNodeLabels = "MATCH (n)\r\n" + "CALL apoc.create.addLabels( id(n), [ n.Delta ] )\r\n"
			+ "YIELD node\r\n" + "RETURN node";
	private static String setRelationshipLabels = "MATCH (f)-[rel]->(b)\r\n"
			+ "CALL apoc.refactor.setType(rel, rel.Label)\r\n" + "YIELD input, output\r\n" + "RETURN input, output";
	private static String getAllNodes = "MATCH (n)-[r]->(m)\r\n"
			+ "RETURN *";

	public neo4jGraphmlImport(String graphUid, String attr) {
		importGraphmlFile = "CALL apoc.import.graphml(\"http://localhost:8080/repository/graph/" + graphUid
				+ "\", {storeNodeIds: true})";
		setNodeLabels = "MATCH (n)\r\n" + "CALL apoc.create.addLabels( id(n), [ n." + attr + " ] )\r\n"
				+ "YIELD node\r\n" + "RETURN node";
	}

	public void initializeGraph() {
		connector.executeQuery(deleteAllNodes);
		connector.executeQuery(importGraphmlFile);
		connector.executeQuery(setNodeLabels);
		connector.executeQuery(setRelationshipLabels);
	}

}
