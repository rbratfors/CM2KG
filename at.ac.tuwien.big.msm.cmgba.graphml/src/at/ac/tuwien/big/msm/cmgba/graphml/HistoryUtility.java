package at.ac.tuwien.big.msm.cmgba.graphml;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Stream;

public class HistoryUtility {
	public File saveFile(String filename, String output) throws IOException {

		File file = new File(filename);
		BufferedWriter writer = null;
		try {
		    writer = new BufferedWriter(new FileWriter(file));
		    writer.write(output);
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
	
	public String nodesToGraphML(String whole, String nodes, ArrayList<Integer> versions, String prefix) {
		String[] lines = whole.split("\r\n|\r|\n");
		StringBuilder graphml = new StringBuilder();
		
		prefix = prefix + "-";
		for(String l : lines) {
			if(l.contains("<node id="))
				break;
			graphml.append(l);
			graphml.append(System.getProperty("line.separator"));
		}
		graphml.append(nodes);
		
		if(versions.size()>1) {
			for(int i=versions.size()-1;i>0;i--) {
				graphml.append("<edge source=\"" + prefix + versions.get(i) + "\" target=\"" + prefix + versions.get(i-1) + "\"> \r\n"
							+ 	"<data key=\"ReferenceName\">source</data> \r\n"
							+ 	"<data key=\"Label\">Previous</data> \r\n"
							+ 	"<data key=\"d6\">\r\n"
							+ 	"        <y:ShapeNode>\r\n"
							+ 	"          <y:Geometry height=\"30.0\" width=\"30.0\" x=\"746.6867968000003\" y=\"-12.487705599997412\"/>\r\n"
							+ 	"          <y:Fill color=\"#FFCC00\" transparent=\"false\"/>\r\n"
							+ 	"          <y:BorderStyle color=\"#000000\" raised=\"false\" type=\"line\" width=\"1.0\"/>\r\n"
							+ 	"          <y:NodeLabel alignment=\"center\" autoSizePolicy=\"content\" fontFamily=\"Dialog\" fontSize=\"12\" fontStyle=\"plain\" hasBackgroundColor=\"false\" hasLineColor=\"false\" height=\"18.701171875\" horizontalTextPosition=\"center\" iconTextGap=\"4\" modelName=\"custom\" textColor=\"#000000\" verticalTextPosition=\"bottom\" visible=\"true\" width=\"40.703125\" x=\"-5.3515625\" y=\"5.6494140625\">Previous<y:LabelModel>\r\n"
							+ 	"              <y:SmartNodeLabelModel distance=\"4.0\"/>\r\n"
							+ 	"            </y:LabelModel>\r\n"
							+ 	"            <y:ModelParameter>\r\n"
							+ 	"              <y:SmartNodeLabelModelParameter labelRatioX=\"0.0\" labelRatioY=\"0.0\" nodeRatioX=\"0.0\" nodeRatioY=\"0.0\" offsetX=\"0.0\" offsetY=\"0.0\" upX=\"0.0\" upY=\"-1.0\"/>\r\n"
							+ 	"            </y:ModelParameter>\r\n"
							+ 	"          </y:NodeLabel>\r\n"
							+ 	"          <y:Shape type=\"rectangle\"/>\r\n"
							+ 	"        </y:ShapeNode>\r\n"
							+ 	"      </data> \r\n"
							+ 	"</edge> \r\n");
			}
		}
		graphml.append("</graph> \r\n" + "</graphml>");
		return graphml.toString();
	}
	
	private String getEdges(String nodes) {
		
		return nodes;
	}
	
	public String readContent(File file) {
		String content = "";
		try (BufferedReader br = new BufferedReader(new FileReader(file))){
			StringBuilder sb = new StringBuilder();
			String line = "";
			while((line = br.readLine()) != null) {
				sb.append(line);
				sb.append("\n");
			}
			content = sb.toString();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return content;
	}
	
	public String getDate(String path, int version) {
		String date = "";
		
		try (Stream<String> lines = Files.lines(Paths.get(path))) {
		    date = lines.skip(1 + version*9).findFirst().get();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return date;
	}
	
	public void getHistories()  {
		File folder = new File("export/");
		String exports[] = folder.list();
		for(String file : exports) {
			if(file.contains("history")) {
				
			}
				
		}
	}

	public float[] getBranchCoords(String rect) {
		String[] s = rect.split("\\s+");
		float[] coords = {Float.parseFloat(s[1])+2, Float.parseFloat(s[0])};
		return coords;
	}
	public float getBranchY(String rect) {
		String y = rect.split("\\s+")[0];
		return Float.parseFloat(y);
	}
}
