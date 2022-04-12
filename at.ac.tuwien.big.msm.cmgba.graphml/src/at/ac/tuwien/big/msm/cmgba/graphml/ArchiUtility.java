package at.ac.tuwien.big.msm.cmgba.graphml;

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

import transformations.Archimate2GraphML;

public class ArchiUtility {
	
	File modelFile;
	File graphFile;
	File outputFile;
	String graphXML;
	public int version = 0;
	String modelName;
	String modelID;
	String[] nodeAttributes;
	String[] edgeAttributes;
	
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
	
	public String transform() {
		
		try {
        	XMLModelImporter importer = new XMLModelImporter();
            IArchimateModel model = importer.createArchiMateModel(modelFile);
            
            Archimate2GraphML archi2Graphml = new Archimate2GraphML(model);
            
            boolean archivedModel = true;
            
            archi2Graphml.convert();
            version = countVersions(modelID + "_v");
    		GraphML graphml = archi2Graphml.getGraphml();
    		GraphMLModelExporter modelExporter;
            modelExporter = new GraphMLModelExporter(graphml, modelFile, modelName, modelID, version);
    		try {
    			modelExporter.exportGraph();
    			graphXML = modelExporter.getGraphXML();
    			
    			//System.out.println(graphXML);
    			String fileUid;
    			String filename;
    			modelExporter.sortNewContent();
    			
    			nodeAttributes = modelExporter.getNodeAttributes().toArray(new String[0]);
    			edgeAttributes = modelExporter.getEdgeAttributes().toArray(new String[0]);
    			System.out.println("------ATTRIBUTES------");
    			for(String n : nodeAttributes) {
    				System.out.println(n);
    			}
    			for(String e : edgeAttributes) {
    				System.out.println(e);
    			}
    			
    			if(!modelExporter.compareGraphs()) {
    				fileUid = modelID + "_v" + version;
        			System.out.println("fileUid: " + fileUid);
        			filename = "export/" + fileUid + ".graphml";
    				outputFile = modelExporter.saveFile(filename, true);
    				System.out.println("Saved to export: " + filename);
    				File tmpFile = modelExporter.saveFile("snapshot/" + modelID + ".graphml", true);
    				version++;
    			} else if(version>0){
    				//Graph has no changes since last upload
    				fileUid = modelID + "_v" + (version-1);
    			} else {
    				fileUid = modelID + "_v0";
    			}
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
		File folder = new File("export/");
		String exports[] = folder.list();
		for(String file : exports) {
			if(file.contains(id))
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
