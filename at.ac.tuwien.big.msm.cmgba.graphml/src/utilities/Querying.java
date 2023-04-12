package utilities;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import at.ac.tuwien.big.msm.cmgba.graphml.Edge;

public class Querying {
	public String version;
	public String status;
	public String content;
	public Date updateDate;
	public ArrayList<Edge> edge;
	SimpleDateFormat formatter;
	int ver = -1;
	String nodeID;
	
	public Querying() {
		
	}
	
	public Querying(String content, int ver) {
		formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm");  
		this.content = content;
		this.ver = ver;
	}
	
	public Querying(String content) {
		formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm");  
		this.content = content;
	}
	
	//REDO
	public String returnNodes(String attributeName, String attributeValue, String[] lines) {
		int i = 0;
		int idIndex = 0;
		ArrayList<Integer> nameIndices = new ArrayList<Integer>();
		for(String l : lines) {
			if(l.contains("<node id="))
				idIndex = i;
			if(l.contains("<data key=\"" + attributeName + "\">" + attributeValue + "</data>")) {
				nameIndices.add(idIndex);
				break;
			}
			i++;
		}
		if(nameIndices.isEmpty()) {
			return "";
		}
		String nodes = "";
		String dateStr = "";
		//String[] nodes = new String[nameIndices.size()];
		for(int j = 0; j < nameIndices.size(); j++) {
			int index = nameIndices.get(j);
			ArrayList<String> tmp = new ArrayList<String>();
			String idLine = lines[index];
			if(ver!=-1) {
				int q = idLine.indexOf("\">");
				nodeID = idLine.substring(10,q);
				idLine = idLine.substring(0, q) + "-" + ver + idLine.substring(q, idLine.length()-1);
			}
			tmp.add(idLine);
			for(int l=index+1;l<lines.length;l++) {
				if(lines[l].contains("<node id="))
					break;
				tmp.add(lines[l]);
			}
			
			String[] nodeContent = tmp.toArray(new String[0]);
			if(dateStr.isEmpty())
				dateStr = lines[index+1].substring(23, 39);
			//nodes[j] = node;
			nodes = nodeContent[0];
			if(!nodes.isBlank())
				break;
		}
		try {
			updateDate = formatter.parse(dateStr);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//-19
		//+2
		return nodes;
	}
	
	public String getNodeID() {
		return nodeID;
	}
}
