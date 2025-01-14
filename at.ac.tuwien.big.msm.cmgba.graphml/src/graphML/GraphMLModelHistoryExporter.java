package graphML;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.stream.Stream;

import java.time.format.DateTimeFormatter;  
import java.time.LocalDateTime; 

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.emf.common.util.EList;

import at.ac.tuwien.big.msm.cmgba.graphml.Data;
import at.ac.tuwien.big.msm.cmgba.graphml.Edge;
import at.ac.tuwien.big.msm.cmgba.graphml.Graph;
import at.ac.tuwien.big.msm.cmgba.graphml.GraphML;
import at.ac.tuwien.big.msm.cmgba.graphml.Node;

public class GraphMLModelHistoryExporter {
	private GraphML graph;
	private String oldFile = null;
	private StringBuilder graphXMLBegining = null; 
	private StringBuilder graphXML = null; 
	private StringBuilder dataKeysXML = null; 
	private HashMap<String, String> dataKeys=null;
	private int timeNodeCount = 0;
	private String tncString;
	private HashMap<String,HashMap<String,String>> oldNodes = new HashMap<String,HashMap<String,String>>();
	private HashMap<String,HashMap<String,String>> oldEdges = new HashMap<String,HashMap<String,String>>();
	private HashMap<String,HashMap<String,String>> newNodes = new HashMap<String,HashMap<String,String>>();
	private HashMap<String,HashMap<String,String>> newEdges = new HashMap<String,HashMap<String,String>>();
	public String modelName = null;
	public String modelID = null;
	private File graphFile;
	private String graphOutput;
	DateTimeFormatter dtf;
	LocalDateTime now;
	private ArrayList<String> nodeAttributes = new ArrayList<String>();
	private ArrayList<String> edgeAttributes = new ArrayList<String>();
	public int nodeComparisons;
	public int edgeComparisons;
	public int numOfNodes;
	public int numOfEdges;
	
	int version = 0;
	public GraphMLModelHistoryExporter(GraphML graph) {
		this.graph = graph;
		graphXMLBegining = new StringBuilder(); 
		
		dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");  
		now = LocalDateTime.now();  
		
		graphFile = checkSnapshot();
		if(graphFile != null)
			readGraphFile(graphFile, true);
		
		graphXML = new StringBuilder(); 
		dataKeysXML = new StringBuilder(); 
		//this.dataKeysXML.append("<key id=\"ClassName\" for=\"node\" attr.name=\"ClassName\" attr.type=\"string\"/> \r\n");
		//this.dataKeysXML.append("<key id=\"ClassName\" for=\"edge\" attr.name=\"ClassName\" attr.type=\"string\"/> \r\n");
		
		dataKeys = new HashMap<>();
	}
	
	public GraphMLModelHistoryExporter(String content) {
		graphOutput = content;
		
		dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");  
		now = LocalDateTime.now();  
		dataKeysXML = new StringBuilder(); 
		//this.dataKeysXML.append("<key id=\"ClassName\" for=\"node\" attr.name=\"ClassName\" attr.type=\"string\"/> \r\n");
		//this.dataKeysXML.append("<key id=\"ClassName\" for=\"edge\" attr.name=\"ClassName\" attr.type=\"string\"/> \r\n");
		
		dataKeys = new HashMap<>();
	}
	
	
	public GraphMLModelHistoryExporter(GraphML graph, File modelFile, String modelName, String modelID, int version, String historyPath) {
		// TODO Auto-generated constructor stub
		this.graph = graph;
		this.modelID = modelID;
		this.modelName = modelName;
		this.version = version;
		graphXMLBegining = new StringBuilder(); 
		
		dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");  
		now = LocalDateTime.now();  
		
		graphFile = getParent(historyPath);
		if(graphFile != null)
			readGraphFile(graphFile, true);
		
		graphXML = new StringBuilder(); 
		dataKeysXML = new StringBuilder(); 
		//this.dataKeysXML.append("<key id=\"ClassName\" for=\"node\" attr.name=\"ClassName\" attr.type=\"string\"/> \r\n");
		//this.dataKeysXML.append("<key id=\"ClassName\" for=\"edge\" attr.name=\"ClassName\" attr.type=\"string\"/> \r\n");
		
		dataKeys = new HashMap<>();
	}
	
	private File getParent(String historyPath) {
		File folder = new File(historyPath);
		String history[] = folder.list();
		for(String h : history) {
			if(h.equals("v" + (version-1) + ".graphml"))
				return new File(historyPath + "/" + h);
		}
		return null;
	}
	
	private File checkSnapshot() {
		File folder = new File("snapshot/");
		String snapshots[] = folder.list();
		if(snapshots == null)
			return null;
		for(String snapshot : snapshots) {
			if(snapshot.equals(modelID + ".graphml"))
				return new File("snapshot/" + snapshot);
		}
		return null;
	}
	
	public void sortNewContent(boolean str) {
		long start = System.currentTimeMillis();
		Scanner sc;
		if(str)
			sc = new Scanner(graphOutput);
		else 
			sc = new Scanner(graphXML.toString());
		String line;
		String graphLine;
		while (sc.hasNextLine()) {
		  line = sc.nextLine();
		  if(line.substring(0,10).equals("<graph id=")) {
				//start new inner list on edge and node
				graphLine = line;
				newNodes.put(line, new HashMap<String,String>());
				newEdges.put(line, new HashMap<String,String>());
				while(!(line = sc.nextLine()).equals("</graph> ")) {
					if(line.substring(0,9).equals("<node id=")) {
						String nodeID = line.substring(9);
						String nodeContent = line;
						while(!(line = sc.nextLine()).equals("</node> ")) {
							nodeContent += "\n" + line;
						}
						nodeContent += "\n" + line;
						newNodes.get(graphLine).put(nodeID, nodeContent);
					} else if (line.substring(0,13).equals("<edge source=")) {
						String edgeLine = line;
						String edgeContent = line;
						while(!(line = sc.nextLine()).equals("</edge> ")) {
							edgeContent += "\n" + line;
						}
						edgeContent += "\n" + line;
						newEdges.get(graphLine).put(edgeLine, edgeContent);
					}
				}
			}
		}
		newNodes.forEach((key,value) -> {
			numOfNodes += value.size();
		});
		newEdges.forEach((key,value) -> {
			numOfEdges += value.size();
		});
		long finish = System.currentTimeMillis();
		System.out.println("Stopping time");
		long timeElapsed = finish - start;
		
		System.out.println("sorting time: " + timeElapsed);
	}
	
	// convert to stringbuilder
	public void readGraphFile(File file, boolean old) {
		long start = System.currentTimeMillis();
		try (BufferedReader br = new BufferedReader(new FileReader(file))){
			String line = "";
			String graphLine = "";
			while((line = br.readLine()) != null) {
				if(line.substring(0,10).equals("<graph id=")) {
					//start new inner list on edge and node
					graphLine = line;
					if(old) {
						oldNodes.put(line, new HashMap<String,String>());
						oldEdges.put(line, new HashMap<String,String>());
					} else {
						newNodes.put(line, new HashMap<String,String>());
						newEdges.put(line, new HashMap<String,String>());
					}
					while(!(line = br.readLine()).equals("</graph> ")) {
						if(line.substring(0,9).equals("<node id=")) {
							String nodeID = line.substring(9);
							String nodeContent = line;
							while(!(line = br.readLine()).equals("</node> ")) {
								nodeContent += "\n" + line;
							}
							nodeContent += "\n" + line;
							if(old)
								oldNodes.get(graphLine).put(nodeID, nodeContent);
							else 
								newNodes.get(graphLine).put(nodeID, nodeContent);
						} else if (line.substring(0,13).equals("<edge source=")) {
							String edgeLine = line;
							String edgeContent = line;
							while(!(line = br.readLine()).equals("</edge> ")) {
								edgeContent += "\n" + line;
							}
							edgeContent += "\n" + line;
							if(old)
								oldEdges.get(graphLine).put(edgeLine, edgeContent);
							else
								newEdges.get(graphLine).put(edgeLine, edgeContent);
						}
					}
				}
			}
			newNodes.forEach((key,value) -> {
				numOfNodes += value.size();
			});
			newEdges.forEach((key,value) -> {
				numOfEdges += value.size();
			});
			
			long finish = System.currentTimeMillis();
			System.out.println("Stopping time");
			long timeElapsed = finish - start;
			
			System.out.println("reading time: " + timeElapsed);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public boolean compareGraphs(String historyPath, boolean addProperty) {
		System.out.println("Starting compare time");
		long start = System.currentTimeMillis();
		nodeComparisons = 0;
		edgeComparisons = 0;
		boolean newHistory = false;
		if(!addProperty) {
			graphOutput = graphXML.toString();
			if(graphFile == null) {
				newHistory = true;
			}
		}

		boolean modification = false;
		
		if(!newHistory) {
			ArrayList<String> deletedNodes = new ArrayList<String>();
			ArrayList<String> addedNodes = new ArrayList<String>();
			ArrayList<String> modifiedNodes = new ArrayList<String>();
			ArrayList<String> deletedEdges = new ArrayList<String>();
			ArrayList<String> addedEdges = new ArrayList<String>();
			ArrayList<String> modifiedEdges = new ArrayList<String>();
			
			for (String key : oldNodes.keySet()) {
				if(newNodes.containsKey(key)) {
					HashMap<String, String> oldN = oldNodes.get(key);
					HashMap<String, String> newN = newNodes.get(key);
					HashMap<String, String> oldE = oldEdges.get(key);
					HashMap<String, String> newE = newEdges.get(key);
					
					//check nodes
					for(String nKey : oldN.keySet()) {
						nodeComparisons++;
						if(newN.containsKey(nKey)) {
							String[] split = oldN.get(nKey).split("\n",4);
							
							String newStr;
							if(addProperty)
								newStr = newN.get(nKey).split("\n",3)[2];
							else 
								newStr = newN.get(nKey).split("\n",2)[1];
							if(newStr.equals(split[3])) {
								if(addProperty) {
									String l = newN.get(nKey).split("\n", 2)[0];
									int i = graphOutput.indexOf(l)+l.length();
									graphOutput = insertString(graphOutput, "<data key=\"Delta\">Same</data>\n", i);
								}
								else {
									String l = newN.get(nKey).split("\n", 2)[0];
									int i = graphOutput.indexOf(l)+l.length();
									graphOutput = insertString(graphOutput, "\n<data key=\"Delta\">Same</data>", i);
									graphOutput = insertString(graphOutput, split[1], graphOutput.indexOf(split[0])+split[0].length());
								}
							} else {
								modifiedNodes.add(oldN.get(nKey));
								String l = newN.get(nKey).split("\n", 2)[0];
								int i = graphOutput.indexOf(l)+l.length();
								if(addProperty)
									graphOutput = insertString(graphOutput, "<data key=\"Delta\">Modified</data>\n", i);
								else {
									graphOutput = insertString(graphOutput, "<data key=\"LastUpdate\">" + dtf.format(now) + "</data>\n" + "<data key=\"Delta\">Modified</data>", i);
								}
								modification = true;
							}
						} else {
							deletedNodes.add(oldN.get(nKey));
							if(addProperty) {
								int i = graphOutput.indexOf("<node id");
								String[] split = oldN.get(nKey).split("\n",2);
								String s = split[0] + "\n<data key=\"Delta\">Deleted</data>\n" + split[1] + "\n";
								graphOutput = insertString(graphOutput, s, i-1);
							}
							modification = true;
						}
						newN.remove(nKey);
					}
					for(String nKey : newN.keySet()) {
						if(!oldN.containsKey(nKey)) {
							nodeComparisons++;
							addedNodes.add(newN.get(nKey));
							String l = newN.get(nKey).split("\n", 2)[0];
							int i = graphOutput.indexOf(l)+l.length();
							if(addProperty)
								graphOutput = insertString(graphOutput, "<data key=\"Delta\">Added</data>\n", i);
							else {
								graphOutput = insertString(graphOutput, "<data key=\"LastUpdate\">" + dtf.format(now) + "</data>\n" + "<data key=\"Delta\">Added</data>", i);
							}
							modification = true;
						}
					}
					
					//check edges
					for(String eKey : oldE.keySet()) {
						edgeComparisons++;
						if(newE.containsKey(eKey)) {
							String[] split = oldE.get(eKey).split("\n",4);
							
							String newStr;
							if(addProperty)
								newStr = newE.get(eKey).split("\n",3)[2];
							else 
								newStr = newE.get(eKey).split("\n",2)[1];
							if(newStr.equals(split[3])) {
								if(addProperty) {
									String l = newE.get(eKey).split("\n", 2)[0];
									int i = graphOutput.indexOf(l)+l.length();
									graphOutput = insertString(graphOutput, "<data key=\"Delta\">Same</data>\n", i);
								} else {
									String l = newE.get(eKey).split("\n", 2)[0];
									int i = graphOutput.indexOf(l)+l.length();
									graphOutput = insertString(graphOutput, "\n<data key=\"Delta\">Same</data>", i);
									graphOutput = insertString(graphOutput, split[1], graphOutput.indexOf(split[0])+split[0].length());
								}
							} else {
								modifiedEdges.add(oldE.get(eKey));
								String l = newE.get(eKey).split("\n", 2)[0];
								int i = graphOutput.indexOf(l)+l.length();
								if(addProperty)
									graphOutput = insertString(graphOutput, "<data key=\"Delta\">Modified</data>\n", i);
								else {
									graphOutput = insertString(graphOutput, "<data key=\"LastUpdate\">" + dtf.format(now) + "</data>\n" + "<data key=\"Delta\">Modified</data>", i);
								}
								modification = true;
							}
						} else {
							deletedEdges.add(oldE.get(eKey));
							if(addProperty) {
								int i = graphOutput.indexOf("</graph>");
								String[] split = oldE.get(eKey).split("\n",2);
								String s = split[0] + "\n<data key=\"Delta\">Deleted</data>\n" + split[1] + "\n";
								graphOutput = insertString(graphOutput, s, i-1);
							}
							modification = true;
						}
						newE.remove(eKey);
					}
					for(String eKey : newE.keySet()) {
						if(!oldE.containsKey(eKey)) {
							edgeComparisons++;
							addedEdges.add(newE.get(eKey));
							String l = newE.get(eKey).split("\n", 2)[0];
							int i = graphOutput.indexOf(l)+l.length();
							if(addProperty)
								graphOutput = insertString(graphOutput, "<data key=\"Delta\">Added</data>\n", i);
							else
								graphOutput = insertString(graphOutput, "<data key=\"LastUpdate\">" + dtf.format(now) + "</data>\n" + "<data key=\"Delta\">Added</data>", i);
							modification = true;
						}
					}
					
				} else {
					modification = true;
				}
			}
		
			if(modification && historyPath != null) {
				StringBuilder summary = new StringBuilder();
				summary.append(version + "\n");
				summary.append(dtf.format(now) + "\n");
				summary.append(deletedNodes.size() + "\n");
				summary.append(addedNodes.size() + "\n");
				summary.append(modifiedNodes.size() + "\n");
				summary.append(deletedEdges.size() + "\n");
				summary.append(addedEdges.size() + "\n");
				summary.append(modifiedEdges.size() + "\n");
				updateHistoryFile(summary.toString(), historyPath);
				
				/*
				StringBuilder sb = new StringBuilder("");
				sb.append(getList("Deleted Nodes", deletedNodes));
				sb.append(getList("Added Nodes", addedNodes));
				sb.append(getList("Modified Nodes", modifiedNodes));
				sb.append(getList("Deleted Edges", deletedEdges));
				sb.append(getList("Added Edges", addedEdges));
				sb.append(getList("Modified Edges", modifiedEdges));
				writeDelta(sb.toString());
				*/
				long finish = System.currentTimeMillis();
				System.out.println("Stopping compare time");
				long timeElapsed = finish - start;
				System.out.println("Compare time: " + timeElapsed);
				return false;
			} else if (addProperty) {
				try {
					saveFile("export/comparison.graphml", true);
					return false;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} else {
			StringBuilder summary = new StringBuilder();
			summary.append(version + "\n");
			summary.append(dtf.format(now) + "\n");
			summary.append("0\n");
			summary.append(newNodes.get(newNodes.keySet().toArray()[0]).size() + "\n");
			summary.append("0\n");
			summary.append("0\n");
			summary.append(newEdges.get(newNodes.keySet().toArray()[0]).size() + "\n");
			summary.append("0\n");
			updateHistoryFile(summary.toString(), historyPath);
			
			long finish = System.currentTimeMillis();
			System.out.println("Stopping compare time");
			long timeElapsed = finish - start;
			System.out.println("Compare time: " + timeElapsed);
			return false;
		}
		
		long finish = System.currentTimeMillis();
		System.out.println("Stopping compare time");
		long timeElapsed = finish - start;
		System.out.println("Compare time: " + timeElapsed);
		return true;
	}
	
	private void updateHistoryFile(String summary, String historyPath) {
	    FileWriter fw;
		try {
			fw = new FileWriter(historyPath + "branch_history.txt", true);
			BufferedWriter bw = new BufferedWriter(fw);
		    bw.write(summary);
		    bw.newLine();
		    bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private String insertString(String ogString, String insert, int index) {
	        String newString = ogString.substring(0, index + 1)
                    + insert
                    + ogString.substring(index + 1);
	        
	        return newString;
	    }
	
	private StringBuilder getList(String action, ArrayList<String> list) {
		StringBuilder sb = new StringBuilder(action + "\n");
		if(list.isEmpty())
			return sb;
		for(String str : list) {
			sb.append(str + "\n");
		}
		return sb;
	}
	
	private void writeDelta(String delta) {
		String fileName = "delta/" + modelID + "_v" + version + ".txt";
		
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(new File(fileName)))) {
			writer.write(delta);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void exportGraph(String filename) throws IOException {
		this.buildGraphXML();
		
		File file = new File(filename);
		BufferedWriter writer = null;
		try {
		    writer = new BufferedWriter(new FileWriter(file));
		    writer.write(graphXML.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
		    if (writer != null) {
		    	writer.close();
		    }
		}
		
	}
	
	
	public void exportGraph() {
		this.buildGraphXML();
	}
	
	
	public String getGraphXML(){
		
		return this.graphXML.toString();
	}
	
	public String getGraphOutput(){
		
		return this.graphOutput;
	}
	
	public File getFile() throws IOException {

		File file = new File("output.graphml");
		BufferedWriter writer = null;
		try {
		    writer = new BufferedWriter(new FileWriter(file));
		    writer.write(graphXML.toString());
		    
		    return file;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} finally {
		    if (writer != null) 
		    	writer.close();
		}
	}
	
	public File saveFile(String filename, boolean str) throws IOException {

		File file = new File(filename);
		BufferedWriter writer = null;
		try {
		    writer = new BufferedWriter(new FileWriter(file));
		    if(str) {
		    	writer.write(graphOutput);
		    }
		    else {
		    	writer.write(graphXML.toString());
		    }
		    return file;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} finally {
		    if (writer != null) {
		    	writer.close();
		    }
		}
	}
	
	private void buildGraphXML() {
		this.graphXMLBegining.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?> \r\n");
		
		this.graphXMLBegining.append("<graphml xmlns=\"http://graphml.graphdrawing.org/xmlns\" \r\n"
				+ "xmlns:java=\"http://www.yworks.com/xml/yfiles-common/1.0/java\" \r\n"
				+ "xmlns:sys=\"http://www.yworks.com/xml/yfiles-common/markup/primitives/2.0\" \r\n"
				+ "xmlns:x=\"http://www.yworks.com/xml/yfiles-common/markup/2.0\" \r\n"
				+ "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" \r\n"
				+ "xmlns:y=\"http://www.yworks.com/xml/graphml\" \r\n"
				+ "xmlns:yed=\"http://www.yworks.com/xml/yed/3\" \r\n"
				+ "xsi:schemaLocation=\"http://graphml.graphdrawing.org/xmlns http://www.yworks.com/xml/schema/graphml/1.1/ygraphml.xsd\">  \r\n"
				
				+ "  <key attr.name=\"Description\" attr.type=\"string\" for=\"graph\" id=\"d0\"/>\r\n"
				+ "  <key for=\"port\" id=\"d1\" yfiles.type=\"portgraphics\"/>\r\n"
				+ "  <key for=\"port\" id=\"d2\" yfiles.type=\"portgeometry\"/>\r\n"
				+ "  <key for=\"port\" id=\"d3\" yfiles.type=\"portuserdata\"/>\r\n"
				+ "  <key attr.name=\"url\" attr.type=\"string\" for=\"node\" id=\"d4\"/>\r\n"
				+ "  <key attr.name=\"description\" attr.type=\"string\" for=\"node\" id=\"d5\"/>\r\n"
				+ "  <key for=\"node\" id=\"d6\" yfiles.type=\"nodegraphics\"/>\r\n"
				+ "  <key for=\"graphml\" id=\"d7\" yfiles.type=\"resources\"/>\r\n"
				+ "  <key attr.name=\"url\" attr.type=\"string\" for=\"edge\" id=\"d8\"/>\r\n"
				+ "  <key attr.name=\"description\" attr.type=\"string\" for=\"edge\" id=\"d9\"/>\r\n"
				+ "  <key for=\"edge\" id=\"d10\" yfiles.type=\"edgegraphics\"/> \r\n");
		
		for(Graph g : graph.getGraphs()) {
			this.addGraph(g);
		}
		this.graphXML.append("</graphml> \r\n");
		
		this.graphXMLBegining.append(dataKeysXML);
		this.graphXMLBegining.append(graphXML);
		
		graphXML = graphXMLBegining;
	}
	
	private void addGraph(Graph g) {
		//open graph
		String line = "<graph id=\""+g.getId()+"\" edgedefault=\"directed\"> \r\n";
		this.graphXML.append(line);
		
		// \n might cause trouble?
		//newNodes.put(line, new HashMap<String,String>());
		//newEdges.put(line, new HashMap<String,String>());
		
		//iterate all node elements
		for(Node n: g.getNodes()) {
			this.addNode(n, line);
		}
		
		//iterate all edge elements
		for(Edge e: g.getEdges()) {
			this.addEdge(e, line);
		}
		
		//close graph
		this.graphXML.append("</graph> \r\n");
	}
	
	private void addNode(Node n, String graphLine) {
		//open node
		String line = "<node id=\""+n.getId()+"\"> \r\n";
		if(graphFile == null)
			line += "<data key=\"LastUpdate\">" + dtf.format(now) + "</data> \r\n" + "<data key=\"Delta\">Added</data>\n";
		this.graphXML.append(line);
		
		String nodeContent = line;
		
		//check if node has subgraph (will cause problems for delta check)
		if(n.getGraph() != null) {
			this.addGraph(n.getGraph());
		}
		
		//iterate all attributes
		for(Data d: n.getDataAttributes()) {
			nodeContent = this.addDataKey(d, n, nodeContent);
			//this.addDataAttribute(d);
		}
		//close node
		String closingLine = "</node> \r\n";
		//newNodes.get(graphLine).put(n.getId(), nodeContent + closingLine);
		this.graphXML.append(closingLine);
	}
	
	private void addEdge(Edge e, String graphLine) {
		//open edge
		
		if(e.getSource() == null || e.getTarget() == null) {
			return;
		}
		
		String line = "<edge source=\""+e.getSource().getId()+"\" target=\""+e.getTarget().getId()+"\"> \r\n";
		if(graphFile == null)
			line += "<data key=\"LastUpdate\">" + dtf.format(now) + "</data> \r\n" + "<data key=\"Delta\">Added</data>\n";
		this.graphXML.append(line);
		
		String edgeContent = line;
		//iterate all attributes
		for(Data d: e.getDataAttributes()) {
			edgeContent = this.addDataKey(d, e, edgeContent);
			//this.addDataAttribute(d);
		}
		
		//close edge
		String closingLine = "</edge> \r\n";
		//newEdges.get(graphLine).put(line, edgeContent + closingLine);
		this.graphXML.append(closingLine);
	}
	
	private String addDataAttribute(String key, Data d, String line) {
		//this.graphXML.append("<data key=\""+key+"\">"+d.getValue()+"</data> \r\n");
		String l = "";
		if(key.equals("Label")) {
			l = "<data key=\""+key+"\">"+d.getValue()+"</data> \r\n";
			l += "<data key=\"d6\">\r\n"
					+ "        <y:ShapeNode>\r\n"
					+ "          <y:Geometry height=\"30.0\" width=\"30.0\" x=\"746.6867968000003\" y=\"-12.487705599997412\"/>\r\n"
					+ "          <y:Fill color=\"#FFCC00\" transparent=\"false\"/>\r\n"
					+ "          <y:BorderStyle color=\"#000000\" raised=\"false\" type=\"line\" width=\"1.0\"/>\r\n"
					+ "          <y:NodeLabel alignment=\"center\" autoSizePolicy=\"content\" fontFamily=\"Dialog\" fontSize=\"12\" fontStyle=\"plain\" hasBackgroundColor=\"false\" hasLineColor=\"false\" height=\"18.701171875\" horizontalTextPosition=\"center\" iconTextGap=\"4\" modelName=\"custom\" textColor=\"#000000\" verticalTextPosition=\"bottom\" visible=\"true\" width=\"40.703125\" x=\"-5.3515625\" y=\"5.6494140625\">"+d.getValue()+"<y:LabelModel>\r\n"
					+ "              <y:SmartNodeLabelModel distance=\"4.0\"/>\r\n"
					+ "            </y:LabelModel>\r\n"
					+ "            <y:ModelParameter>\r\n"
					+ "              <y:SmartNodeLabelModelParameter labelRatioX=\"0.0\" labelRatioY=\"0.0\" nodeRatioX=\"0.0\" nodeRatioY=\"0.0\" offsetX=\"0.0\" offsetY=\"0.0\" upX=\"0.0\" upY=\"-1.0\"/>\r\n"
					+ "            </y:ModelParameter>\r\n"
					+ "          </y:NodeLabel>\r\n"
					+ "          <y:Shape type=\"rectangle\"/>\r\n"
					+ "        </y:ShapeNode>\r\n"
					+ "      </data> \r\n";
			this.graphXML.append(l);
		}
		else {
			l = "<data key=\""+key+"\">"+d.getValue()+"</data> \r\n";
			this.graphXML.append(l);
		}
		return line+l;
	}
	
	private String addDataKey(Data d, Edge e, String line) {
		//String keyName = e.getName()+"_"+d.getKey();
		String keyName = d.getKey().replace(".", "_").replace(":", "_");
		if(d.getKey().equals("ClassName") || d.getKey().equals("Label"))
			keyName = d.getKey();
		
		/*if(dataKeys.get("edge_"+keyName) == null) {
			dataKeys.put("edge_"+keyName, keyName);
			this.dataKeysXML.append("<key id=\""+keyName+"\" for=\"edge\" attr.name=\""+keyName+"\" attr.type=\"string\"/> \r\n");
		}*/
		
		if(dataKeys.get(keyName) == null) {
			dataKeys.put(keyName, keyName);
			String l = "<key id=\""+keyName+"\" for=\"edge\" attr.name=\""+keyName+"\" attr.type=\"string\"/> \r\n";
			this.dataKeysXML.append(l);
			line += l;
		}
		
		if(!edgeAttributes.contains(keyName))
			edgeAttributes.add(keyName);
		
		return this.addDataAttribute(keyName, d, line);	
	}
	
	private String addDataKey(Data d, Node n, String line) {
		//String keyName = n.getName()+"_"+d.getKey();
		String keyName = d.getKey().replace(".", "_").replace(":", "_");
		if(d.getKey().equals("ClassName") || d.getKey().equals("Label"))
			keyName = d.getKey();
		
		/*if(dataKeys.get("node_"+keyName) == null) {
			dataKeys.put("node_"+keyName, keyName);
			this.dataKeysXML.append("<key id=\""+keyName+"\" for=\"node\" attr.name=\""+keyName+"\" attr.type=\"string\"/> \r\n");
		}*/
		
		if(dataKeys.get(keyName) == null) {
			dataKeys.put(keyName, keyName);
			String l = "<key id=\""+keyName+"\" for=\"node\" attr.name=\""+keyName+"\" attr.type=\"string\"/> \r\n";
			this.dataKeysXML.append(l);
			line += l;
		}
		
		if(!nodeAttributes.contains(keyName))
			nodeAttributes.add(keyName);
		
		return this.addDataAttribute(keyName, d, line);
	}
	
	public ArrayList<String> getNodeAttributes() {
		return nodeAttributes;
	}
	
	public ArrayList<String> getEdgeAttributes() {
		return edgeAttributes;
	}
}
