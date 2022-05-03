package utilities;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;
import java.util.stream.Stream;

import at.ac.tuwien.big.msm.cmgba.graphml.Edge;
import at.ac.tuwien.big.msm.cmgba.graphml.Node;

public class ModelGraph {
	public int version;
	public String date;
	public String delta;
	public String status;
	public String content;
	public String uid;
	public ArrayList<Edge> edge;
	public ArrayList<Node> nodes;
	public String deltaSummary;
	public String summary;
	public String modelID;
	public String nodeId;
	public boolean matched;
	public String historyName;
	public int branch;
	public String path;
	
	public ModelGraph(int i, String uid, String content, String date, String modelID, String nodeId, boolean matched, String history, int branch) {
		this.version = i;
		this.uid = uid;
		this.content = content;
		this.date = date;
		this.modelID = uid;
		this.nodeId = nodeId;
		this.matched = matched;
		historyName = history;
		this.branch = branch;
		
		String historyPath = "export/" + history + "/b"+branch+"/";
		path = history + "/b" + branch+ "/v" + version;
		
		deltaSummary = summarizeDelta(historyPath);
		summary = createSummary(deltaSummary);
	}
	
	private String createSummary(String ds) {
		StringBuilder summary = new StringBuilder();
		summary.append(uid + "<br/>");
		summary.append("Uploaded: " + date + "<br/>");
		summary.append(ds);
		
		return summary.toString();
	}
	
	private String summarizeDelta(String historyPath) {
		
		StringBuilder summary = new StringBuilder();
	    String line;
	    
	    BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(historyPath + "branch_history.txt"));
			int i = 0;
			while ((line = br.readLine()) != null) {
				if(i>1 + version*9) {
					int n = Integer.parseInt(line);
					if(n>1) {
						summary.append(n + " Deleted Nodes <br/>");
					} else if(n>0) {
						summary.append(n + " Deleted Node <br/>");
					}
					n = Integer.parseInt(br.readLine());
					if(n>1) {
						summary.append(n + " Added Node(s) <br/>");
					} else if(n>0) {
						summary.append(n + " Added Node <br/>");
					}
					n = Integer.parseInt(br.readLine());
					if(n>1) {
						summary.append(n + " Modififed Nodes <br/>");
					} else if(n>0) {
						summary.append(n + " Modififed Node <br/>");
					}
					n = Integer.parseInt(br.readLine());
					if(n>1) {
						summary.append(n + " Deleted Edges <br/>");
					} else if(n>0) {
						summary.append(n + " Deleted Edge <br/>");
					}
					n = Integer.parseInt(br.readLine());
					if(n>1) {
						summary.append(n + " Added Edges <br/>");
					} else if(n>0) {
						summary.append(n + " Added Edge <br/>");
					}
					n = Integer.parseInt(br.readLine());
					if(n>1) {
						summary.append(n + " Modified Edges <br/>");
					} else if(n>0) {
						summary.append(n + " Modified Edge <br/>");
					}
					break;
				}
		        i++;
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		return summary.toString();
	}
	
	private boolean checkLine(String line, int i) {
		switch(i) {
		case 0:
			if(line.equals("Added Nodes")) {
				return true;
			}
			break;
		case 1:
			if(line.equals("Modified Nodes"))
				return true;
			break;
		case 2:
			if(line.equals("Deleted Edges"))
				return true;
			break;
		case 3:
			if(line.equals("Added Edges"))
				return true;
			break;
		case 4:
			if(line.equals("Modified Edges"))
				return true;
			break;
		default:
			break;
		}
		return false;
	}
}
