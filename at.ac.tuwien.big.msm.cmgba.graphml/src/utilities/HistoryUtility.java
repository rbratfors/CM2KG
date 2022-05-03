package utilities;

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
	public File[] histories;
	public String[] historyNames;
	public int numOfHistories;
	public boolean newBranch;
	public String currentBranch;
	public int numOfBranches;
	
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
	
	public File[] getHistories()  {
		File folder = new File("export/");
		histories = folder.listFiles(File::isDirectory);
		numOfHistories = histories.length;
		
		return histories;
	}
	
	public String[] getHistoryNames(File[] histories) {
		String[] names = new String[histories.length];
		for(int i=0;i<names.length;i++) {
			names[i] = histories[i].getName();
		}
		
		return names;
	}
	
	public int maxBranchSize(String historizationName) {
		String historizationPath = "export/"+historizationName;
		int max = 0;
		for (int i=0;i<numOfBranches;i++) {
			int current = new File(historizationPath + "/b" + i).list().length-1;
			if(current > max)
				max = current;
		}
		return max;
	}
	
	public int countBranches(String historizationName) {
		String historizationPath = "export/"+historizationName;
		File folder = new File(historizationPath);
		File[] directories = folder.listFiles(File::isDirectory);
		if(directories != null) {
			numOfBranches = 0;
			for(File dir : directories) {
				numOfBranches++;
				System.out.println(dir.toString());
			}
		} else {
			System.out.println("null: " + historizationPath);
		}
		return numOfBranches;
	}

	public float[] readUpdate(String update) {
		String[] s = update.split("\\s+");
		newBranch = (s[0].equals("b"));
		float[] coords = {Float.parseFloat(s[1])+2, Float.parseFloat(s[2])}; 
		if(!newBranch)
			currentBranch = s[0];
		System.out.println("READ: " + newBranch + " " + currentBranch);
		return coords;
	}
	public float getBranchY(String rect) {
		String y = rect.split("\\s+")[0];
		return Float.parseFloat(y);
	}
	
	public String[] getNodeAttributes(File file) {
		ArrayList<String> attributes = new ArrayList<String>();
		System.out.println("YOOO");
		try (BufferedReader br = new BufferedReader(new FileReader(file))){
			String line = "";
			while((line = br.readLine()) != null) {
				if(line.contains("<key id=\"")) {
					do {
						String[] split = line.split("\\s+");
						if(split[2].contains("node")) {
							String a = split[1].substring(4,split[1].length()-1);
							System.out.println(a);
							attributes.add(a);
						}
					} while ((line = br.readLine()).contains("<key id=\""));
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return attributes.toArray(new String[0]);
	}
}
