package com.example.demo.controllers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;

import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import utilities.ModelGraph;
import graphDb.neo4jGraphmlImport;
import graphML.GraphMLModelExporter;
import graphML.GraphMLModelHistoryExporter;
import utilities.ADOxxUtility;
import utilities.ArchiUtility;
import utilities.HistoryUtility;
import utilities.PapyrusUMLUtility;
import utilities.Querying;

import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

//import org.apache.commons.io.FilenameUtils;

@Controller
@SessionAttributes({"message","models","modelVersions","histories","currentHistory","branches"})
public class Upload {
	int version = 0;
	String cont;
	String[] nodeAttributes;
	String[] versionIDs;

	@RequestMapping(value = "", method = RequestMethod.GET)
	public String home(Model model) {

		// model.addAttribute("sum", "to be calculated");
		System.out.println("home");
		HistoryUtility historyUtil = new HistoryUtility();
		return "home";
	}
	
	@RequestMapping(value = "upload", method = RequestMethod.GET)
	public String products(Model model) {

		// model.addAttribute("sum", "to be calculated");

		return "upload";
	}

	@PostMapping(value = "upload")
	public String saveProduct(@RequestParam("a") int a, @RequestParam("b") int b,
			RedirectAttributes redirectAttributes) {

		redirectAttributes.addFlashAttribute("sum", a + b);

		return "redirect:/upload";
	}

	// ARCHI-------------------------------------------------------------------------------------------
	@RequestMapping(value = "upload/archi", method = RequestMethod.GET)
	public String uploadArchi(Model model) {

		// model.addAttribute("sum", "to be calculated");

		return "uploadarchi";
	}

	@PostMapping(value = "upload/archi")
	public String uploadArchi(@RequestParam("mFile") MultipartFile file, RedirectAttributes redirectAttributes, Model model) {

		// redirectAttributes.addFlashAttribute("sum", a + b);
		
		try {
			String content = new String(file.getBytes());
			System.out.println("content: ");
			// System.out.println(content);
			redirectAttributes.addFlashAttribute("uploadcontent", content);

			File tmpFile = new File("src/main/resources/targetFile.xml");

			try (OutputStream os = new FileOutputStream(tmpFile)) {
				os.write(file.getBytes());
			}

			ArchiUtility archiUtil = new ArchiUtility();
			archiUtil.setFile(tmpFile);

			UUID uuid = UUID.randomUUID();
			String uid = uuid.toString();
			String filename = "export/" + uid + ".graphml";

			archiUtil.transform(filename);

			String outputContent = archiUtil.getGraphXML();
			System.out.println(outputContent);
			redirectAttributes.addFlashAttribute("fileUid", uid);
			redirectAttributes.addFlashAttribute("outputcontent", outputContent);
			
			return "redirect:/upload/preview";

		} catch (IOException e) {
			e.printStackTrace();
			redirectAttributes.addFlashAttribute("uploadcontent", "An error occured!");
			return "redirect:/upload/preview";
		}
	}
	
	// ADOxx-------------------------------------------------------------------------------------------
	@RequestMapping(value = "upload/adoxx", method = RequestMethod.GET)
	public String uploadADOxx(Model model) {

		// model.addAttribute("sum", "to be calculated");

		return "uploadadoxx";
	}

	@PostMapping(value = "upload/adoxx")
	public String uploadADOxx(@RequestParam("file") MultipartFile file, @RequestParam("dtdfile") MultipartFile dtdFile,
			RedirectAttributes redirectAttributes) {

		// redirectAttributes.addFlashAttribute("sum", a + b);
		try {

			String content = new String(file.getBytes());
			System.out.println("content: ");
			// System.out.println(content);
			redirectAttributes.addFlashAttribute("uploadcontent", content);

			// xml file
			File tmpFile = new File("src/main/resources/" + file.getOriginalFilename());

			try (OutputStream os = new FileOutputStream(tmpFile)) {
				os.write(file.getBytes());
			}

			// dtd file
			File tmpdtdFile = new File("src/main/resources/" + dtdFile.getOriginalFilename());
			try (OutputStream os = new FileOutputStream(tmpdtdFile)) {
				os.write(dtdFile.getBytes());
			}

			ADOxxUtility adoxxUtil = new ADOxxUtility();
			adoxxUtil.setFile(tmpFile);

			UUID uuid = UUID.randomUUID();
			String uid = uuid.toString();
			String filename = "export/" + uid + ".graphml";
			adoxxUtil.transform(filename);
			// File outputFile = archiUtil.getFile();

			String outputContent = adoxxUtil.getGraphXML();
			System.out.println(outputContent);
			redirectAttributes.addFlashAttribute("fileUid", uid);
			redirectAttributes.addFlashAttribute("outputcontent", outputContent);

			return "redirect:/upload/preview";

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			redirectAttributes.addFlashAttribute("uploadcontent", "An error occured!");
			return "redirect:/upload/preview";
		}
	}

	// ADOxx-------------------------------------------------------------------------------------------

	// papyrusUML-------------------------------------------------------------------------------------------
	@RequestMapping(value = "upload/papyrusuml", method = RequestMethod.GET)
	public String uploadPapyrusUML(Model model) {

		// model.addAttribute("sum", "to be calculated");

		return "uploadpapyrusuml";
	}

	@PostMapping(value = "upload/papyrusuml")
	public String uploadPapyrusUML(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {

		// redirectAttributes.addFlashAttribute("sum", a + b);
		try {

			String content = new String(file.getBytes());
			System.out.println("content: ");
			// System.out.println(content);
			redirectAttributes.addFlashAttribute("uploadcontent", content);

			// xml file
			File tmpFile = new File("src/main/resources/" + file.getOriginalFilename());

			try (OutputStream os = new FileOutputStream(tmpFile)) {
				os.write(file.getBytes());
			}

			PapyrusUMLUtility papyrusUmlUtil = new PapyrusUMLUtility();
			papyrusUmlUtil.setFile(tmpFile);

			UUID uuid = UUID.randomUUID();
			String uid = uuid.toString();
			String filename = "export/" + uid + ".graphml";
			papyrusUmlUtil.transform(filename);
			// File outputFile = archiUtil.getFile();

			String outputContent = papyrusUmlUtil.getGraphXML();
			System.out.println(outputContent);
			redirectAttributes.addFlashAttribute("fileUid", uid);
			redirectAttributes.addFlashAttribute("outputcontent", outputContent);

			return "redirect:/upload/preview";

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			redirectAttributes.addFlashAttribute("uploadcontent", "An error occured!");
			return "redirect:/upload/preview";
		}
	}

	// papyrusUML-------------------------------------------------------------------------------------------

	@RequestMapping(value = "upload/preview", method = RequestMethod.GET)
	public String uploadPreview(Model model) {
		 
		return "uploadpreview";
	}

	/*
	 * @RequestMapping(value = "repository/graph/{uid}", method = RequestMethod.GET)
	 * public void getGraph(HttpServletResponse response, @PathVariable String uid)
	 * throws IOException {
	 * 
	 * Path filePath = Paths.get("export/", uid+".graphml");
	 * 
	 * String content = Files.readString(filePath);
	 * 
	 * response.setContentType("application/xml");
	 * response.setHeader("Content-Disposition","attachment;filename="+uid+
	 * ".graphml"); ServletOutputStream out = response.getOutputStream();
	 * out.println(content); out.flush(); out.close();
	 * 
	 * }
	 */
	
	@RequestMapping(value = "upload/preview/revert", method = RequestMethod.GET)
	public String revertGraphVersion(Model model) {
		
		return "redirect:/upload/archi";
	}

	@RequestMapping(value = "repository/graph/{uid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_XML_VALUE)
	@ResponseBody
	public FileSystemResource getGraph2(HttpServletResponse response, @PathVariable String uid) {

		response.setContentType("text/plain");
		return new FileSystemResource(new File("export/", uid + ".graphml")); // Or path to your file
		// return new FileSystemResource(new File("export/", "simple.graphml")); //Or
		// path to your file
	}

	@RequestMapping(value = "repository/graph/{uid}/neo4j", method = RequestMethod.GET)
	public String initializeNeo4jGraph(HttpServletResponse response, @PathVariable String uid, RedirectAttributes redirectAttributes) {
		System.out.println("Version: " + version);
		redirectAttributes.addAttribute("version", version);
		redirectAttributes.addFlashAttribute("uid", uid);
		
		//neo4jGraphmlImport neoImport = new neo4jGraphmlImport("My History/b0/v0/", "ClassName");
		
		neo4jGraphmlImport neoImport = new neo4jGraphmlImport(uid, "ClassName");
		neoImport.initializeGraph();

		return "neovispreview_v2";
	}
	
	@RequestMapping(value = "/neo4j/{history}/{branch}/{version}", method = RequestMethod.GET)
	public String initializeNeo4jGraph(HttpServletResponse response, @PathVariable String history, @PathVariable String branch, @PathVariable String version, RedirectAttributes redirectAttributes) {
		System.out.println("Version: " + version);
		redirectAttributes.addAttribute("version", version);
		HistoryUtility historyUtil = new HistoryUtility();
		File file = new File("export/" + history + "/" + branch + "/" + version + ".graphml");
		String content = historyUtil.readContent(file);
		System.out.println(content);
		try {
			historyUtil.saveFile("export/neo4j.graphml", content);
			neo4jGraphmlImport neoImport = new neo4jGraphmlImport("neo4j", "ClassName");
			neoImport.initializeGraph();
			return "neovispreview_v2";
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "neovispreview_v2";
		}
	}
	
	@RequestMapping(value = "/neo4j/{history}/{branch}/{version}/to/{history2}/{branch2}/{version2}", method = RequestMethod.GET)
	public String compareNeo4jGraph(HttpServletResponse response, @PathVariable String history, @PathVariable String branch, @PathVariable String version, @PathVariable String history2, @PathVariable String branch2, @PathVariable String version2, RedirectAttributes redirectAttributes) {
		System.out.println("Version: " + version);
		redirectAttributes.addAttribute("version", version);
		HistoryUtility historyUtil = new HistoryUtility();
		File file = new File("export/" + history + "/" + branch + "/" + version + ".graphml");
		File file2 = new File("export/" + history2 + "/" + branch2 + "/" + version2 + ".graphml");
		String content = historyUtil.readContent(file);
		String comparisonContent = historyUtil.readContent(file2);
		ArchiUtility archiUtil = new ArchiUtility();
		GraphMLModelHistoryExporter modelExporter = new GraphMLModelHistoryExporter(content);
		modelExporter.readGraphFile(file2, true);
		modelExporter.readGraphFile(file, false);
		modelExporter.compareGraphs(null, true);
		
		neo4jGraphmlImport neoImport = new neo4jGraphmlImport("comparison", "Delta");
		neoImport.initializeGraph();
		return "neovispreview_v2";
	}
	
	
	@RequestMapping(value = "historymenu", method = RequestMethod.GET)
	public String historyMenu(Model model) {
		System.out.println("history menu");
		HistoryUtility historyUtil = new HistoryUtility();
		
		File[] histories = historyUtil.getHistories();
		String[] historyNames = historyUtil.getHistoryNames(histories);
		model.addAttribute("histories", historyNames);
		
		return "historymenu";
	}
	
	@PostMapping(value = "historymenu")
	public String historyMenuSelection(@RequestParam("mFile") MultipartFile mFile, @RequestParam("historyName") String name, RedirectAttributes redirectAttributes, Model model, SessionStatus status) {
		
		System.out.println("history menu post");
		try {
			model.addAttribute("currentHistory", name);
			
			System.out.println("history menu post");
			System.out.println(name);
			System.out.println(model.getAttribute("currentHistory"));
			HistoryUtility historyUtil = new HistoryUtility();
			ArchiUtility archiUtil = new ArchiUtility();
			
			File[] histories = historyUtil.getHistories();
			String[] historyNames = historyUtil.getHistoryNames(histories);
			String content = new String(mFile.getBytes());
			System.out.println("mFile content: ");
			// System.out.println(content);
			redirectAttributes.addFlashAttribute("uploadcontent", content);
			File tmpFile = new File("src/main/resources/targetFile.xml");
			
			try (OutputStream os = new FileOutputStream(tmpFile)) {
				os.write(mFile.getBytes());
			}
			String historyPath = "";
			
			archiUtil.setFile(tmpFile);
			String fileUid = archiUtil.historyTransform(historyPath);
			
			String outputContent = archiUtil.getGraphXML();
			//System.out.println(outputContent);
			redirectAttributes.addFlashAttribute("fileUid", fileUid);
			redirectAttributes.addFlashAttribute("outputcontent", outputContent);
			
			int numOfVersions = archiUtil.version;
			System.out.println("Number of versions: " + numOfVersions);
			String date = "";
			
			
			String historyName = model.getAttribute("currentHistory").toString();
			int numOfBranches = historyUtil.countBranches(historyName);
			int maxBranchSize = historyUtil.maxBranchSize(historyName);
			System.out.println("Number of brances: " + numOfBranches);
			System.out.println("Largestt branch size: " + maxBranchSize);
			ModelGraph[][] bra = new ModelGraph[numOfBranches][maxBranchSize];
			String[] parents = new String[numOfBranches];
			ModelGraph[] versions = new ModelGraph[numOfVersions];
			for(int i=0;i<numOfBranches;i++) {
				historyPath = "export/" + model.getAttribute("currentHistory") + "/b" + i + "/";
				parents[i] = historyUtil.getParent(historyPath + "branch_history.txt");
				if(numOfVersions>1) {
					versions = new ModelGraph[numOfVersions];
					date = historyUtil.getDate(historyPath + "branch_history.txt", numOfVersions-1);
					versions[numOfVersions-1] = new ModelGraph(numOfVersions-1,fileUid,outputContent,date,"v","lastNode",true,name,0,"");
					versionIDs = new String[versions.length];
					String node = "firstNode";
					for(int j=0;j<numOfVersions-1;j++) { 
						date = historyUtil.getDate(historyPath + "branch_history.txt", (j));
						versions[j] = new ModelGraph(j,"b0"+"v"+(j),"",date,"v",node,true,name,0,"");
						versionIDs[j] = versions[j].uid;
						node = "";
					}
					versions[0].parent = parents[i];
					bra[i] = versions;
				} else {
					versions = new ModelGraph[1];
					date = historyUtil.getDate(historyPath + "branch_history.txt", numOfVersions-1);
					versions[0] = new ModelGraph(0,"b0v0",outputContent,date,"v","firstNode",true,name,0,"");
					versions[0].parent = parents[i];
					bra[i] = versions;
				}
			}
			redirectAttributes.addFlashAttribute("current", versions[numOfVersions-1]);
			redirectAttributes.addFlashAttribute("versions", versions);
			
			String[] nAttr = archiUtil.getNodeAttributes();
			String[] eAttr = archiUtil.getEdgeAttributes();
			nodeAttributes = nAttr;
			ArrayList<Node> nd = new ArrayList<Node>();
			for(String n : nAttr) {
				nd.add(new Node(n, "node"));
			}
			FormData fd = new FormData(nd.toArray(new Node[0]));
			
			for(Node n: fd.getNodes()) {
				System.out.println(n.getAttribute() + " " + n.getValue());
			}
			
			fd.setAttributes(new String[nAttr.length]);
			redirectAttributes.addFlashAttribute("formdata", fd);
			redirectAttributes.addFlashAttribute("attributeNames", nodeAttributes);
			redirectAttributes.addFlashAttribute("attributes", nAttr);
			model.addAttribute("formdata", fd);
			fd.printContent();
			
			historyUtil.saveFile("export/current.graphml", outputContent);
			
			System.out.println("Checking versions");
			if (!model.containsAttribute("versions")) {
				System.out.println("Setting versions");
			    model.addAttribute("modelVersions", versions);
			    System.out.println("Versions set");
			}
			
			model.addAttribute("branches", bra);
			
			return "redirect:/upload/historypreview";
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			redirectAttributes.addFlashAttribute("uploadcontent", "An error occured!");
			return "redirect:/upload/historypreview";
		}
	}
	
	@RequestMapping(value = "uploadhistory", method = RequestMethod.GET)
	public String productsHistory(Model model) {
		System.out.println("uploadhistory controller");
		return "uploadhistory";
	}
	
	@PostMapping(value = "uploadhistory")
	public String saveProductHistory(RedirectAttributes redirectAttributes, Model model) {
		HistoryUtility historyUtil = new HistoryUtility();
		ArchiUtility archiUtil = new ArchiUtility();

		return "redirect:/uploadhistory";
	}
	
	// ARCHI HISTORY-------------------------------------------------------------------------------------------
		@RequestMapping(value = "upload/archihistory", method = RequestMethod.GET)
		public String uploadArchiHistory(Model model) {

			// model.addAttribute("sum", "to be calculated");
			System.out.println("archi request");
			return "uploadarchihistory";
		}
		
		@PostMapping(value = "upload/archihistory")
		public String uploadArchiHistory(@RequestParam(name = "mFile", required=false) MultipartFile mFile, @RequestParam("historyName") String name, @RequestParam("submitBtn") int submit, @RequestParam(name = "newHistory", required=false) String newHistoryName,RedirectAttributes redirectAttributes, Model model, SessionStatus status) {

			// redirectAttributes.addFlashAttribute("sum", a + b);
			
			try {
				System.out.println("Starting time");
				long start = System.currentTimeMillis();
				int elements = 0;
				HistoryUtility historyUtil = new HistoryUtility();
				ArchiUtility archiUtil = new ArchiUtility();
				System.out.println("archi history");
				
				boolean newHistory = false;
				boolean uploadModel = false;
				switch(submit) {
				case 0:
					newHistory = true;
					uploadModel = true;
					break;
				case 1:
					newHistory = false;
					uploadModel = true;
					break;
				case 2:
					newHistory = false;
					uploadModel = false;
					break;
				default:
					break;
				}
				
				if(newHistory) {
					model.addAttribute("currentHistory", newHistoryName);
					new File("export/"+newHistoryName+"/b0/").mkdirs();
					historyUtil.createHistoryFile("export/"+newHistoryName+"/b0/", "root");
				} else {
					model.addAttribute("currentHistory", name);
				}
				
				File[] histories = historyUtil.getHistories();
				String[] historyNames = historyUtil.getHistoryNames(histories);
				String historyPath = "export/" + model.getAttribute("currentHistory") + "/b0/";
				String outputContent = "";
				if(uploadModel) {
					String content = new String(mFile.getBytes());	
					redirectAttributes.addFlashAttribute("uploadcontent", content);
					File tmpFile = new File("src/main/resources/targetFile.xml");
					try (OutputStream os = new FileOutputStream(tmpFile)) {
						os.write(mFile.getBytes());
					}
					archiUtil.setFile(tmpFile);
					String fileUid = archiUtil.historyTransform(historyPath);
					redirectAttributes.addFlashAttribute("fileUid", fileUid);
					outputContent = archiUtil.getGraphXML();
					//System.out.println(outputContent);
					redirectAttributes.addFlashAttribute("outputcontent", outputContent);
					int i = fileUid.indexOf("_v");
				} else {
					archiUtil.setHistory(historyPath);
					File contentFile = new File(historyPath + "v"+(archiUtil.version-1) + ".graphml");
					outputContent = historyUtil.readContent(contentFile);
				}
				
				int numOfVersions = archiUtil.version;
				System.out.println("Number of versions: " + numOfVersions);
				String date = "";
				File contentFile;
				
				
				String historyName = model.getAttribute("currentHistory").toString();
				int numOfBranches = historyUtil.countBranches(historyName);
				int maxBranchSize = historyUtil.maxBranchSize(historyName);
				System.out.println("Number of branches: " + numOfBranches);
				System.out.println("Largest branch size: " + maxBranchSize);
				ArrayList<ArrayList<ModelGraph>> branches = new ArrayList<ArrayList<ModelGraph>>();
				ModelGraph[][] bra = new ModelGraph[numOfBranches][maxBranchSize];
				String[] parents = new String[numOfBranches];
 				for(int i=0;i<numOfBranches;i++) {
					historyPath = "export/" + model.getAttribute("currentHistory") + "/b" + i + "/";
					int numOfVersionsInBranch = archiUtil.countVersionsInFolder(historyPath);
					ArrayList<ModelGraph> versions = new ArrayList<ModelGraph>();
					ModelGraph[] vs = new ModelGraph[numOfVersionsInBranch];
					parents[i] = historyUtil.getParent(historyPath + "branch_history.txt");
					if(numOfVersionsInBranch>1) {
						String node = "firstNode";
						for(int j=0;j<numOfVersionsInBranch;j++) {
							date = historyUtil.getDate(historyPath + "branch_history.txt", (j));
							System.out.println("Versions of branch: " + historyPath + " - " + date);
							contentFile = new File(historyPath + "v"+ j + ".graphml");
							outputContent = historyUtil.readContent(contentFile);
							versions.add(new ModelGraph(j,"b"+i+"v"+(j),outputContent,date,"v",node,true,historyName,i,""));
							vs[j] = new ModelGraph(j,"b"+i+"v"+(j),outputContent,date,"v",node,true,historyName,i,"");
							node = "";
						}
						vs[0].parent = parents[i];
						branches.add(versions);
						bra[i] = vs;
						System.out.println("Added branch of size: " + versions.size());
					} else {
						date = historyUtil.getDate(historyPath + "branch_history.txt", (0));
						contentFile = new File(historyPath + "v0.graphml");
						outputContent = historyUtil.readContent(contentFile);
						ModelGraph v = new ModelGraph(0,"b"+i+"v0",outputContent,date,"v","firstNode",true,historyName,i,"");
						v.parent = parents[i];
						versions.add(v);
						branches.add(versions);
						bra[i][0] = v;
						System.out.println("Added one branch of size: " + versions.size());
					}
				}
				//ArrayList<ModelGraph> current = branches.get(0);
	 			redirectAttributes.addFlashAttribute("current", bra[0][maxBranchSize-1]);
				redirectAttributes.addFlashAttribute("versions", bra[0]);
				redirectAttributes.addFlashAttribute("branchList", bra);
				
				model.addAttribute("branches", bra);
				
				model.addAttribute("modelVersions", bra[0]);
				
				model.addAttribute("filter","None");
				
				model.addAttribute("parents", parents);
				
				if(uploadModel) {
					String[] nAttr = archiUtil.getNodeAttributes();
					String[] eAttr = archiUtil.getEdgeAttributes();
					nodeAttributes = nAttr;
					ArrayList<Node> nd = new ArrayList<Node>();
					for(String n : nAttr) {
						nd.add(new Node(n, "node"));
					}
					FormData fd = new FormData(nd.toArray(new Node[0]));
					
					for(Node n: fd.getNodes()) {
						System.out.println(n.getAttribute() + " " + n.getValue());
					}
					
					fd.setAttributes(new String[nAttr.length]);
					redirectAttributes.addFlashAttribute("formdata", fd);
					redirectAttributes.addFlashAttribute("attributeNames", nodeAttributes);
					redirectAttributes.addFlashAttribute("attributes", nAttr);
					model.addAttribute("formdata", fd);
					fd.printContent();
				
				historyUtil.saveFile("export/current.graphml", outputContent);
				} else if (numOfVersions > 1){
					
					File file = new File(historyPath + "v0.graphml");
					nodeAttributes = historyUtil.getNodeAttributes(file);
					ArrayList<Node> nd = new ArrayList<Node>();
					for(String n : nodeAttributes) {
						nd.add(new Node(n, "node"));
					}
					FormData fd = new FormData(nd.toArray(new Node[0]));
					fd.setAttributes(new String[nodeAttributes.length]);
					redirectAttributes.addFlashAttribute("formdata", fd);
					redirectAttributes.addFlashAttribute("attributeNames", nodeAttributes);
					redirectAttributes.addFlashAttribute("attributes", nodeAttributes);
					model.addAttribute("formdata", fd);
				}
				
				long finish = System.currentTimeMillis();
				System.out.println("Stopping time");
				long timeElapsed = finish - start;
				
				System.out.println("Elapsed time: " + timeElapsed);
				
				return "redirect:/upload/historypreview";
	
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			redirectAttributes.addFlashAttribute("uploadcontent", "An error occured!");
			return "redirect:/upload/historypreview";
		}
	}
	
	@RequestMapping(value = "upload/historypreview", method = RequestMethod.GET)
	public String uploadHistoryPreview(FormData formdata, Model model, RedirectAttributes redirectAttributes, @ModelAttribute("modelVersions") ModelGraph[] modelVersions, SessionStatus status) {
		System.out.println("---------------Upload history preview--------------");
		redirectAttributes.addFlashAttribute("formdata", model.getAttribute("formdata"));
		redirectAttributes.addFlashAttribute("outputcontent", modelVersions[0].content);
		return "uploadhistorypreview";
	}
	
	@PostMapping(value = "upload/fromhistorypreview")
	public String branchFromSelected(@RequestParam("mFile") MultipartFile mFile,@RequestParam("update") String updateAction,@ModelAttribute("modelVersions") ModelGraph[] modelVersions,RedirectAttributes redirectAttributes,Model model) {
		try {
			System.out.println("Starting time");
			long start = System.currentTimeMillis();
			String historyName = model.getAttribute("currentHistory").toString();
			HistoryUtility historyUtil = new HistoryUtility();
			ArchiUtility archiUtil = new ArchiUtility();
			String content = new String(mFile.getBytes());
			float[] branchCoords = new float[2];
			String parentId = "";
			if(!updateAction.isEmpty()) {
				parentId = historyUtil.readUpdate(updateAction);
			}
			
			boolean createNewBranch = historyUtil.newBranch;
			String historyPath = "";
			int numOfBranches = historyUtil.countBranches(historyName);
			
			if(createNewBranch) {
				new File("export/"+historyName+"/b"+(numOfBranches)).mkdirs();
				historyPath = "export/" + historyName+"/b"+(numOfBranches)+"/";
				historyUtil.currentBranch = "b"+(numOfBranches);
				historyUtil.createHistoryFile(historyPath, parentId);
				numOfBranches++;
			} else {
				historyPath = "export/" + model.getAttribute("currentHistory") + "/"+historyUtil.currentBranch+"/";
			}
			
			// System.out.println(content);
			redirectAttributes.addFlashAttribute("coords", branchCoords);
			
			redirectAttributes.addFlashAttribute("uploadcontent", content);
			File tmpFile = new File("src/main/resources/targetFile.xml");
			try (OutputStream os = new FileOutputStream(tmpFile)) {
				os.write(mFile.getBytes());
			}
			historyPath = "export/" + model.getAttribute("currentHistory") +"/" + historyUtil.currentBranch+"/";
			archiUtil.setFile(tmpFile);
			String fileUid = archiUtil.historyTransform(historyPath);
			int nodeComparisons = archiUtil.nodeC;
			int edgeComparisons = archiUtil.edgeC;
			// File outputFile = archiUtil.getFile();
			
			String outputContent = archiUtil.getGraphXML();
			//System.out.println(outputContent);
			redirectAttributes.addFlashAttribute("fileUid", fileUid);
			redirectAttributes.addFlashAttribute("outputcontent", outputContent);
			
			int numOfVersions = archiUtil.version;
			String date = "";
			File contentFile;
			
			int maxBranchSize = historyUtil.maxBranchSize(historyName);
			ArrayList<ArrayList<ModelGraph>> branches = new ArrayList<ArrayList<ModelGraph>>();
			ModelGraph[][] bra = new ModelGraph[numOfBranches][maxBranchSize];
			String[] parents = new String[numOfBranches];
			for(int i=0;i<numOfBranches;i++) {
				historyPath = "export/" + model.getAttribute("currentHistory") + "/b" + i + "/";
				int numOfVersionsInBranch = archiUtil.countVersionsInFolder(historyPath);
				ArrayList<ModelGraph> versions = new ArrayList<ModelGraph>();
				ModelGraph[] vs = new ModelGraph[numOfVersionsInBranch];
				parents[i] = historyUtil.getParent(historyPath + "branch_history.txt");
				if(numOfVersionsInBranch>1) {
					String node = "firstNode";
					for(int j=0;j<numOfVersionsInBranch;j++) {
						date = historyUtil.getDate(historyPath + "branch_history.txt", (j));
						contentFile = new File(historyPath + "v"+ j + ".graphml");
						outputContent = historyUtil.readContent(contentFile);
						versions.add(new ModelGraph(j,"b"+i+"v"+(j),outputContent,date,"v",node,true,historyName,i,""));
						vs[j] = new ModelGraph(j,"b"+i+"v"+(j),outputContent,date,"v",node,true,historyName,i,"");
						node = "";
						if(j==0) {
							vs[j].parent = parents[i];
						}
					}
					branches.add(versions);
					bra[i] = vs;
				} else {
					date = historyUtil.getDate(historyPath + "branch_history.txt", (0));
					contentFile = new File(historyPath + "v0.graphml");
					outputContent = historyUtil.readContent(contentFile);
					ModelGraph v = new ModelGraph(0,"b"+i+"v0",outputContent,date,"v","firstNode",true,historyName,i,"");
					v.parent = parents[i];
					versions.add(v);
					bra[i][0] = v;
				}
			}
			//ArrayList<ModelGraph> current = branches.get(0);
			redirectAttributes.addFlashAttribute("current", bra[0][maxBranchSize-1]);
			redirectAttributes.addFlashAttribute("versions", bra[0]);
			redirectAttributes.addFlashAttribute("branchList", bra);
			
			model.addAttribute("branches", bra);
			
			model.addAttribute("modelVersions", bra[0]);
			
			model.addAttribute("parents", parents);
			
			model.addAttribute("filter","None");
			
			String[] nAttr = archiUtil.getNodeAttributes();
			String[] eAttr = archiUtil.getEdgeAttributes();
			nodeAttributes = nAttr;
			ArrayList<Node> nd = new ArrayList<Node>();
			for(String n : nAttr) {
				nd.add(new Node(n, "node"));
			}
			FormData fd = new FormData(nd.toArray(new Node[0]));
			
			/*
			for(Node n: fd.getNodes()) {
				System.out.println(n.getAttribute() + " " + n.getValue());
			}
			*/
			fd.setAttributes(new String[nAttr.length]);
			redirectAttributes.addFlashAttribute("formdata", fd);
			redirectAttributes.addFlashAttribute("attributeNames", nodeAttributes);
			redirectAttributes.addFlashAttribute("attributes", nAttr);
			fd.printContent();
			
			model.addAttribute("formdata", fd);
			
			historyUtil.saveFile("export/current.graphml", outputContent);
			long finish = System.currentTimeMillis();
			System.out.println("Stopping time");
			long timeElapsed = finish - start;
			
			System.out.println("Elapsed time: " + timeElapsed);
			System.out.println("Size of file: " + mFile.getSize());
			System.out.println("Node comparisons: " + nodeComparisons);
			System.out.println("Edge comparisons: " + edgeComparisons);
			System.out.println("Nodes: " + archiUtil.nodes);
			System.out.println("Edges: " + archiUtil.edges);
			System.out.println("Total: " + (archiUtil.nodes + archiUtil.edges));
			
			return "redirect:/upload/historypreview";
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			redirectAttributes.addFlashAttribute("uploadcontent", "An error occured!");
			return "redirect:/upload/historypreview";
		}
	}
	
	@PostMapping(value = "upload/historypreview")
	public String uploadHistoryPreview(FormData formdata, String startdate, String enddate, String smell, String function, String fileUid, String action, RedirectAttributes redirectAttributes, Model model, @ModelAttribute("modelVersions") ModelGraph[] modelVersions, SessionStatus status) throws ParserConfigurationException, SAXException, IOException {
		System.out.println(model.toString());
		String verString = "v0";
		int branchNumber = 0;
		formdata.printContent();
		String historyName = model.getAttribute("currentHistory").toString();
		Date d = new Date();
		
		String historyPath = "export/" + model.getAttribute("currentHistory") + "/b0/";
		HistoryUtility historyUtil = new HistoryUtility();
		
		ArchiUtility archiUtil = new ArchiUtility();
		String content = archiUtil.graphML2String(historyPath + verString + ".graphml");
		String[] contentArray = historyUtil.graphML2StringArray(historyPath + verString + ".graphml");
		ArrayList<Integer> vArray = new ArrayList<Integer>();
		String output = content;
		
		int numOfBranches = historyUtil.countBranches(historyName);
		int maxBranchSize = historyUtil.maxBranchSize(historyName);
		ModelGraph[][] bra = new ModelGraph[numOfBranches][maxBranchSize];
		
		System.out.println("Starting time");
		long start = System.currentTimeMillis();
		if(action != null) {
			int v = Integer.parseInt(verString.substring(1));
			
			StringBuilder nodes = new StringBuilder();
			if(action.equals("delta")) {
				String nodeID = "";
				Date updateDate = new Date();
				String[] parents = new String[numOfBranches];
				for(int i=0;i<numOfBranches;i++) {
					historyPath = "export/" + model.getAttribute("currentHistory")+"/b"+i+"/";
					int numOfVersions = archiUtil.countVersionsInFolder(historyPath);
					ModelGraph[] versions = new ModelGraph[numOfVersions];
					parents[i] = historyUtil.getParent(historyPath + "branch_history.txt");
					boolean deleted = false;
					for(int j=0;j<versions.length;j++) {
						String filename = historyPath + "v"+(j) + ".graphml";
						content = archiUtil.graphML2String(filename);
						contentArray = historyUtil.graphML2StringArray(filename);
						Querying query = new Querying(content, j);
						
						boolean matched = true;
						String delta = "";
						for(int k=0;k<nodeAttributes.length;k++) {
							String value = formdata.getAttributes()[k];
							if(!value.isBlank()) {
								String node = query.returnNodes(nodeAttributes[k],value,contentArray);
								if(!node.isBlank()) {
									if(updateDate.compareTo(query.updateDate)!=0) {
										nodes.append(node);
										updateDate = query.updateDate;
										vArray.add(j);
									}
									delta = historyUtil.getDelta(new File(filename), value);
									deleted = false;
								} else {
									matched = false;
									if(deleted)
										delta = "Same";
									else
										delta = "Deleted";
									deleted = true;
								}
							}
						}
						String date = historyUtil.getDate(historyPath + "branch_history.txt", j);
						versions[j] = new ModelGraph(j,"b"+i+"v"+(j),content,date,"v","",matched,historyName,0,delta);
						versions[j].filterDelta = delta;
						if(j==0) {
							versions[j].parent = parents[i];
						}
					}
					versions[0].parent = parents[i];
					bra[i] = versions;
				}
			}
			else if(action.equals("all"))
			{
				String nodeID = "";
				Date updateDate = new Date();
				String[] parents = new String[numOfBranches];
				for(int i=0;i<numOfBranches;i++) {
					historyPath = "export/" + model.getAttribute("currentHistory")+"/b"+i+"/";
					int numOfVersions = archiUtil.countVersionsInFolder(historyPath);
					ModelGraph[] versions = new ModelGraph[numOfVersions];
					parents[i] = historyUtil.getParent(historyPath + "branch_history.txt");
					for(int j=0;j<versions.length;j++) {
						String filename = historyPath + "v"+(j) + ".graphml";
						content = archiUtil.graphML2String(filename);
						contentArray = historyUtil.graphML2StringArray(filename);
						Querying query = new Querying(content, j);
						
						boolean matched = true;
						String delta = "";
						for(int k=0;k<nodeAttributes.length;k++) {
							String value = formdata.getAttributes()[k];
							if(!value.isBlank()) {
								String node = query.returnNodes(nodeAttributes[k],value,contentArray);
								if(!node.isBlank()) {
									if(updateDate.compareTo(query.updateDate)!=0) {
										nodes.append(node);
										updateDate = query.updateDate;
										vArray.add(j);
									}
									delta = historyUtil.getDelta(new File(filename), value);
								} else {
									matched = false;
								}
							}
						}

						String date = historyUtil.getDate(historyPath + "branch_history.txt", j);
						versions[j] = new ModelGraph(j,"b"+i+"v"+(j),content,date,"v","",matched,historyName,0,delta);
						if(j==0) {
							versions[j].parent = parents[i];
						}
					}
					bra[i] = versions;
				}
				/*
				String nodeID = "";
				Date updateDate = new Date();
				File deltaFile;
				
				for(int j=0;j<versions.length;j++) { 
					deltaFile = new File("delta/" + prefix+(j) + ".txt");
					String delta = historyUtil.readContent(deltaFile);
					String filename = "export/" + prefix+(j) + ".graphml";
					content = archiUtil.graphML2String(filename);
					Querying query = new Querying(content, j);
					for(int k=0;k<nodeAttributes.length;k++) {
						String value = formdata.getAttributes()[k];
						if(!value.isBlank()) {
							String node = query.returnNodes(nodeAttributes[k],value);
							if(!node.isBlank()) {
								if(updateDate.compareTo(query.updateDate)!=0) {
									nodes.append(node);
									updateDate = query.updateDate;
									vArray.add(j);
									System.out.println("new update");
								}
							}
						}
					}
					String date = historyUtil.getDate("export/" + prefix.substring(0,prefix.length()-2) + "_history.txt", numOfVersions-1-j); //Fix this
					nodeID = query.getNodeID();
					versions[numOfVersions-1-j] = new ModelGraph(j,prefix+(j),"",date,delta,modelID,"");
				}
				ModelGraph all = new ModelGraph(numOfVersions-1, prefix.substring(0,prefix.length() - 1)+"all", nodes.toString(),"","",modelID,"");
				model.addAttribute("current", all);
				output = historyUtil.nodesToGraphML(content, nodes.toString(), vArray, nodeID);*/
			} else {
				/*
				Querying query = new Querying(content);
				for(int j=0;j<nodeAttributes.length;j++) {
					String value = formdata.getAttributes()[j];
					if(!value.isBlank())
						nodes.append(query.returnNodes(nodeAttributes[j], value));
				}
				if(nodes.toString().isBlank()) {
					for(int j=0;j<versions.length;j++) { 
						String date = historyUtil.getDate(historyPath + "branch_history.txt", numOfVersions-1-j);
						if(numOfVersions-1-j==v) {
							versions[j] = new ModelGraph(v,vers,content,date,"v","",true,historyName,0,"");
						}
						else
							versions[j] = new ModelGraph(numOfVersions-1-j,"b"+branchNumber+"v"+(numOfVersions-1-j),"",date,"v","",true,historyName,0,"");
					}
				} else {
					for(int j=0;j<versions.length;j++) { 
						String date = historyUtil.getDate(historyPath + "branch_history.txt", numOfVersions-1-j);
						if(numOfVersions-1-j==v)
							versions[j] = new ModelGraph(v,vers,nodes.toString(),date,"v","",true,historyName,0,"");
						else
							versions[j] = new ModelGraph(numOfVersions-1-j,"b"+branchNumber+"v"+(numOfVersions-1-j),"",date,"v","",true,historyName,0,"");
					}
				}
				vArray.add(numOfVersions-1-v);
				output = historyUtil.nodesToGraphML(content, nodes.toString(), vArray, query.getNodeID());
				model.addAttribute("current", versions[numOfVersions-1-v]);
				*/
			}
			
			long finish = System.currentTimeMillis();
			System.out.println("Stopping time");
			long timeElapsed = finish - start;
			
			System.out.println("Elapsed time: " + timeElapsed);
			
			try {
				historyUtil.saveFile("export/current.graphml", output);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			System.out.println("Checking versions");
			if (!model.containsAttribute("versions")) {
				System.out.println("Setting versions");
			    model.addAttribute("modelVersions", bra[0]);
			    System.out.println("Versions set");
			}
			
			model.addAttribute("branches", bra);
			model.addAttribute("filter",action);
			model.addAttribute("formdata", formdata);
			model.addAttribute("attributeNames", nodeAttributes);
			model.addAttribute("attributes", formdata.getAttributes());
			
		}
		// redirectAttributes.addFlashAttribute("sum", a + b);
		
		
		return "uploadhistorypreview";
	}
	
	@RequestMapping(value = "upload/querycurrent", method = RequestMethod.GET)
	public String queryCurrent(Model model, RedirectAttributes redirectAttributes) {
		// model.addAttribute("sum", "to be calculated");

		System.out.println("querycurrent");
		return "querycurrent";
	}
	
	@RequestMapping(value = "current/{uid}/neo4j", method = RequestMethod.GET)
	public String initializeNeo4jHistoryGraph(HttpServletResponse response, @PathVariable String uid, RedirectAttributes redirectAttributes) {
		System.out.println("Version: " + uid);
		redirectAttributes.addFlashAttribute("uid", uid);
		redirectAttributes.addFlashAttribute("versions", versionIDs);
		neo4jGraphmlImport neoImport = new neo4jGraphmlImport(uid, "ClassName");
		neoImport.initializeGraph();

		return "neovispreview";

		// return "redirect:/repository/graph/"+uid;
	}
}
