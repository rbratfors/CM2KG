package utilities;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.io.FileUtils;
import org.jdom2.JDOMException;
import org.opengroup.archimate.xmlexchange.XMLModelImporter;
import org.opengroup.archimate.xmlexchange.XMLModelParserException;

import com.archimatetool.model.IArchimateModel;

import at.ac.tuwien.big.msm.cmgba.graphml.GraphML;
import graphML.GraphMLModelExporter;
import graphML.GraphMLModelHistoryExporter;
import transformations.Archimate2GraphML;

public class ArchiUtility {
	
	File modelFile;
	File graphFile;
	File outputFile;
	String graphXML;
	public int version = 0;
	public int histories = 0;
	String modelName;
	String modelID;
	String[] nodeAttributes;
	String[] edgeAttributes;
	public int nodeC, edgeC, nodes, edges;
	
	long startTime = System.currentTimeMillis();
	
	long estimatedTime = System.currentTimeMillis() - startTime;
	
	public void setFiles(File mFile, File gFile) {
		modelFile = mFile;
		graphFile = gFile;
		modelName = getName(mFile);
		modelID = getID(mFile);
	}
	
	public void setFile(File mFile) {
		modelFile = mFile;
		modelName = getName(mFile);
		modelID = getID(mFile);
	}
	
	public void setHistory(String path) {
		version = countVersionsInFolder(path);
	}
	
	public void transform(String filename) {

		try {
			XMLModelImporter importer = new XMLModelImporter();
			IArchimateModel model = importer.createArchiMateModel(modelFile);

			Archimate2GraphML archi2Graphml = new Archimate2GraphML(model);

			archi2Graphml.convert();
			GraphML graphml = archi2Graphml.getGraphml();

			GraphMLModelExporter modelExporter = new GraphMLModelExporter(graphml);
			try {
				modelExporter.exportGraph();
				graphXML = modelExporter.getGraphXML();

				// if no filename is provided do not create file
				if (!filename.equals(""))
					outputFile = modelExporter.saveFile(filename);

			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		} catch (IOException | JDOMException | XMLModelParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public String historyTransform(String historyPath) {
		
		try {
        	XMLModelImporter importer = new XMLModelImporter();
            IArchimateModel model = importer.createArchiMateModel(modelFile);
            
            Archimate2GraphML archi2Graphml = new Archimate2GraphML(model);
            
            boolean archivedModel = true;
            
            archi2Graphml.convert();
            version = countVersionsInFolder(historyPath);
    		GraphML graphml = archi2Graphml.getGraphml();
    		GraphMLModelHistoryExporter modelExporter;
            modelExporter = new GraphMLModelHistoryExporter(graphml, modelFile, modelName, modelID, version, historyPath);
    		try {
    			modelExporter.exportGraph();
    			graphXML = modelExporter.getGraphXML();
    			
    			//System.out.println(graphXML);
    			String fileUid;
    			String filename;
    			modelExporter.sortNewContent(false);
    			
    			nodeAttributes = modelExporter.getNodeAttributes().toArray(new String[0]);
    			edgeAttributes = modelExporter.getEdgeAttributes().toArray(new String[0]);
    			/*
    			System.out.println("------ATTRIBUTES------");
    			for(String n : nodeAttributes) {
    				System.out.println(n);
    			}
    			for(String e : edgeAttributes) {
    				System.out.println(e);
    			}*/
    			
    			if(!modelExporter.compareGraphs(historyPath,false)) {
    				fileUid = "v" + version;
        			filename = historyPath + fileUid + ".graphml";
    				outputFile = modelExporter.saveFile(filename, true);
    				version++;
    			} else if(version>0){
    				//Graph has no changes since last upload
    				fileUid = modelID + "_v" + (version-1);
    			} else {
    				fileUid = modelID + "_v0";
    			}
    			nodeC = modelExporter.nodeComparisons;
    			edgeC = modelExporter.edgeComparisons;
    			nodes = modelExporter.numOfNodes;
    			edges = modelExporter.numOfEdges;
    			graphXML = modelExporter.getGraphOutput();
    			
    			return fileUid;
    		} catch (IOException e1) {
    			// TODO Auto-generated catch block
    			e1.printStackTrace();
    		}
    		
		} catch (IOException | JDOMException | XMLModelParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public File getFile() {
		return outputFile;
	}
	
	public String getGraphXML() {
		return graphXML;
	}
	
	public int countVersions(String id) {
		version = 0;
		if(id==null)
			id=modelID + "_v";
		File folder = new File("export/My History/b0");
		String exports[] = folder.list();
		for(String file : exports) {
			if(file.contains(id))
				version++;
		}
		return version;
	}
	
	public int countVersionsInFolder(String path) {
		version = 0;
		File folder = new File(path);
		String exports[] = folder.list();
		if(exports == null)
			return version;
		for(String file : exports) {
			if(file.contains(".graphml"))
				version++;
		}
		return version;
	}
	
	public String getName(File file) {
		try (BufferedReader br = new BufferedReader(new FileReader(file))){
			String line = "";
			while((line = br.readLine()) != null) {
				if(line.contains("<name xml")) {
					int i = line.indexOf(">");
					int j = line.indexOf("</name>");
					return line.substring(i+1,j).replaceAll(" ", "_");
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public String getID(File file) {
		try (BufferedReader br = new BufferedReader(new FileReader(file))){
			String line = "";
			line = br.readLine();
			line = br.readLine();
			int i = line.indexOf("identifier=");
			int j = line.length();
			System.out.println(line.substring(i+12,j-2));
			return line.substring(i+12,j-2);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public String[] getDeltas() {
		File folder = new File("delta/");
		String exports[] = folder.list();
		for(String file : exports) {
			if(file.contains(modelID + "_v"))
				version++;
		}
		return null;
	}
	
	public String graphML2String(String file) {
		StringBuilder sb = new StringBuilder();
		try (BufferedReader br = new BufferedReader(new FileReader(file))){
			String line;
			while((line = br.readLine()) != null) {
				sb.append(line).append("\n");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return sb.toString();
	}
	
	public String[] getNodeAttributes() {
		return nodeAttributes;
	}
	
	public String[] getEdgeAttributes() {
		return edgeAttributes;
	}
	
	public void separateAttributes(String attributes) {
		attributes = "";
	}
}
