package com.example.demo.controllers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import utilities.ModelGraph;
import graphDb.neo4jGraphmlImport;
import utilities.ADOxxUtility;
import utilities.ArchiUtility;
import utilities.HistoryUtility;
import utilities.PapyrusUMLUtility;
import utilities.Querying;

import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;
import java.util.UUID;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

//import org.apache.commons.io.FilenameUtils;

@Controller
@SessionAttributes({"message","models","modelVersions"})
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

		System.out.println("upload controller");
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
	public String uploadArchi(@RequestParam("mFile") MultipartFile mFile, RedirectAttributes redirectAttributes, Model model) {

		// redirectAttributes.addFlashAttribute("sum", a + b);
		
		try {
	
				String content = new String(mFile.getBytes());
				System.out.println("mFile content: ");
				// System.out.println(content);
				redirectAttributes.addFlashAttribute("uploadcontent", content);
				File tmpFile = new File("src/main/resources/targetFile.xml");
	
				try (OutputStream os = new FileOutputStream(tmpFile)) {
					os.write(mFile.getBytes());
				}
	
				ArchiUtility archiUtil = new ArchiUtility();
				archiUtil.setFile(tmpFile);
				/*
				UUID uuid = UUID.randomUUID();
				String uid = uuid.toString();
				String filename = "export/" + uid + ".graphml";
				*/
				version = archiUtil.countVersions(null);
				String fileUid = archiUtil.transform();
				// File outputFile = archiUtil.getFile();
				
				String outputContent = archiUtil.getGraphXML();
				//System.out.println(outputContent);
				redirectAttributes.addFlashAttribute("fileUid", fileUid);
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
		
		neo4jGraphmlImport neoImport = new neo4jGraphmlImport(uid);
		neoImport.initializeGraph();

		return "neovispreview_v2";
	}
	
	
	@RequestMapping(value = "historymenu", method = RequestMethod.GET)
	public String historyMenu(Model model, @ModelAttribute("modelVersions") String[] versions) {
		System.out.println("history menu");
		for(String n : versions)
			System.out.println("models: " + n);
		return "historymenu";
	}
	
	@PostMapping(value = "historymenu")
	public String historyMenuSelection(@RequestParam("mFile") MultipartFile mFile, RedirectAttributes redirectAttributes, Model model, SessionStatus status) {
		System.out.println("history menu post");
		try {
			HistoryUtility historyUtil = new HistoryUtility();
			ArchiUtility archiUtil = new ArchiUtility();
			String content = new String(mFile.getBytes());
			System.out.println("mFile content: ");
			// System.out.println(content);
			redirectAttributes.addFlashAttribute("uploadcontent", content);
			File tmpFile = new File("src/main/resources/targetFile.xml");
			redirectAttributes.addFlashAttribute("message", "TEST MESSAGE");
			try (OutputStream os = new FileOutputStream(tmpFile)) {
				os.write(mFile.getBytes());
			}
			
			archiUtil.setFile(tmpFile);
			String fileUid = archiUtil.transform();
			
			String outputContent = archiUtil.getGraphXML();
			//System.out.println(outputContent);
			redirectAttributes.addFlashAttribute("fileUid", fileUid);
			redirectAttributes.addFlashAttribute("outputcontent", outputContent);
			
			int numOfVersions = archiUtil.version;
			System.out.println("VERSIONS: " + numOfVersions);
			int i = fileUid.indexOf("_v");
			String prefix = fileUid.substring(0,i+2);
			String modelID = prefix.substring(0,prefix.length()-2);
			System.out.println("prefix: " + prefix);
			String date = "";
			File deltaFile;
			String delta = "";
			
			ModelGraph[] versions = new ModelGraph[numOfVersions];
			if(numOfVersions>1) {
				versions = new ModelGraph[numOfVersions];
				deltaFile = new File("delta/" + fileUid + ".txt");
				delta = historyUtil.readContent(deltaFile);
				date = historyUtil.getDate("export/" + modelID + "_history.txt", numOfVersions-1);
				versions[numOfVersions-1] = new ModelGraph(numOfVersions-1,fileUid,outputContent,date,delta,modelID,"lastNode",true);
				versionIDs = new String[versions.length];
				String node = "firstNode";
				for(int j=0;j<numOfVersions-1;j++) { 
					deltaFile = new File("delta/" + prefix+(j) + ".txt");
					delta = historyUtil.readContent(deltaFile);
					date = historyUtil.getDate("export/" + modelID + "_history.txt", (j));
					System.out.println(numOfVersions-1-j);
					versions[j] = new ModelGraph(j,prefix+(j),"",date,delta,modelID,node,true);
					versionIDs[j] = versions[j].uid;
					node = "";
				}
			} else {
				versions = new ModelGraph[1];
				date = historyUtil.getDate("export/" + modelID + "_history.txt", numOfVersions-1);
				versions[0] = new ModelGraph(0,fileUid,outputContent,date,"",modelID,"firstNode",true);
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
			fd.printContent();
			
			historyUtil.saveFile("export/current.graphml", outputContent);
			
			System.out.println("checking versions");
			if (!model.containsAttribute("versions")) {
				System.out.println("setting versions");
			    model.addAttribute("modelVersions", versions);
			    System.out.println("versions set");
			}
			
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

			return "uploadarchihistory";
		}
		
		@PostMapping(value = "upload/archihistory")
		public String uploadArchiHistory(@RequestParam("mFile") MultipartFile mFile, RedirectAttributes redirectAttributes, Model model, SessionStatus status) {

			// redirectAttributes.addFlashAttribute("sum", a + b);
			
			try {
				HistoryUtility historyUtil = new HistoryUtility();
				ArchiUtility archiUtil = new ArchiUtility();
				String content = new String(mFile.getBytes());
				System.out.println("mFile content: ");
				// System.out.println(content);
				redirectAttributes.addFlashAttribute("uploadcontent", content);
				File tmpFile = new File("src/main/resources/targetFile.xml");
				try (OutputStream os = new FileOutputStream(tmpFile)) {
					os.write(mFile.getBytes());
				}
				
				archiUtil.setFile(tmpFile);
				String fileUid = archiUtil.transform();
				// File outputFile = archiUtil.getFile();
				
				String outputContent = archiUtil.getGraphXML();
				//System.out.println(outputContent);
				redirectAttributes.addFlashAttribute("fileUid", fileUid);
				redirectAttributes.addFlashAttribute("outputcontent", outputContent);
				
				int numOfVersions = archiUtil.version;
				System.out.println("VERSIONS: " + numOfVersions);
				int i = fileUid.indexOf("_v");
				String prefix = fileUid.substring(0,i+2);
				String modelID = prefix.substring(0,prefix.length()-2);
				System.out.println("prefix: " + prefix);
				String date = "";
				File deltaFile;
				String delta = "";
				File contentFile;
				
				ModelGraph[] versions = new ModelGraph[numOfVersions];
				if(numOfVersions>1) {
					versions = new ModelGraph[numOfVersions];
					deltaFile = new File("delta/" + fileUid + ".txt");
					delta = historyUtil.readContent(deltaFile);
					date = historyUtil.getDate("export/" + modelID + "_history.txt", numOfVersions-1);
					versions[numOfVersions-1] = new ModelGraph(numOfVersions-1,fileUid,outputContent,date,delta,modelID,"lastNode",true);
					versionIDs = new String[versions.length];
					String node = "firstNode";
					for(int j=0;j<numOfVersions-1;j++) { 
						deltaFile = new File("delta/" + prefix+(j) + ".txt");
						delta = historyUtil.readContent(deltaFile);
						date = historyUtil.getDate("export/" + modelID + "_history.txt", (j));
						contentFile = new File("export/" + prefix+(j) + ".graphml");
						outputContent = historyUtil.readContent(contentFile);
						versions[j] = new ModelGraph(j,prefix+(j),outputContent,date,delta,modelID,node,true);
						versionIDs[j] = versions[j].uid;
						node = "";
					}
				} else {
					versions = new ModelGraph[1];
					date = historyUtil.getDate("export/" + modelID + "_history.txt", numOfVersions-1);
					versions[0] = new ModelGraph(0,fileUid,outputContent,date,"",modelID,"firstNode",true);
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
				fd.printContent();
				
				historyUtil.saveFile("export/current.graphml", outputContent);
				
				System.out.println("checking versions");
				if (!model.containsAttribute("versions")) {
					System.out.println("setting versions");
				    model.addAttribute("modelVersions", versions);
				    System.out.println("versions set");
				}
				
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
		formdata.printContent();
		
		for(ModelGraph v : modelVersions) {
			System.out.println(v.uid);
		}
		
		System.out.println();
		
		redirectAttributes.addFlashAttribute("outputcontent", modelVersions[0].content);
		return "uploadhistorypreview";
	}
	
	@PostMapping(value = "upload/fromhistorypreview")
	public String branchFromSelected(@RequestParam("mFile") MultipartFile mFile,@RequestParam("update") String updateButton,@ModelAttribute("modelVersions") ModelGraph[] modelVersions,RedirectAttributes redirectAttributes,Model model) {
		System.out.println("hello");
		try {
			HistoryUtility historyUtil = new HistoryUtility();
			ArchiUtility archiUtil = new ArchiUtility();
			String content = new String(mFile.getBytes());
			float[] branchCoords = new float[2];
			if(!updateButton.isEmpty())
				branchCoords = historyUtil.getBranchCoords(updateButton);
			System.out.println("RECT: " + branchCoords[0] + ","  + branchCoords[1]);
			// System.out.println(content);
			redirectAttributes.addFlashAttribute("coords", branchCoords);
			
			redirectAttributes.addFlashAttribute("uploadcontent", content);
			File tmpFile = new File("src/main/resources/targetFile.xml");
			try (OutputStream os = new FileOutputStream(tmpFile)) {
				os.write(mFile.getBytes());
			}
			
			archiUtil.setFile(tmpFile);
			/*
			UUID uuid = UUID.randomUUID();
			String uid = uuid.toString();
			String filename = "export/" + uid + ".graphml";
			*/
			String fileUid = archiUtil.transform();
			// File outputFile = archiUtil.getFile();
			
			String outputContent = archiUtil.getGraphXML();
			//System.out.println(outputContent);
			redirectAttributes.addFlashAttribute("fileUid", fileUid);
			redirectAttributes.addFlashAttribute("outputcontent", outputContent);
			
			historyUtil.countHistories("Yo");
			int numOfVersions = archiUtil.version;
			System.out.println("VERSIONS: " + numOfVersions);
			int i = fileUid.indexOf("_v");
			String prefix = fileUid.substring(0,i+2);
			String modelID = prefix.substring(0,prefix.length()-2);
			System.out.println("prefix: " + prefix);
			String date = "";
			File deltaFile;
			String delta = "";
			File contentFile;
			
			ModelGraph[] versions = new ModelGraph[numOfVersions];
			if(numOfVersions>1) {
				versions = new ModelGraph[numOfVersions];
				deltaFile = new File("delta/" + fileUid + ".txt");
				delta = historyUtil.readContent(deltaFile);
				date = historyUtil.getDate("export/" + modelID + "_history.txt", numOfVersions-1);
				versions[numOfVersions-1] = new ModelGraph(numOfVersions-1,fileUid,outputContent,date,delta,modelID,"lastNode",true);
				versionIDs = new String[versions.length];
				String node = "firstNode";
				for(int j=0;j<numOfVersions-1;j++) { 
					deltaFile = new File("delta/" + prefix+(j) + ".txt");
					delta = historyUtil.readContent(deltaFile);
					date = historyUtil.getDate("export/" + modelID + "_history.txt", (j));
					contentFile = new File("export/" + prefix+(j) + ".graphml");
					outputContent = historyUtil.readContent(contentFile);
					versions[j] = new ModelGraph(j,prefix+(j),outputContent,date,delta,modelID,node,true);
					versionIDs[j] = versions[j].uid;
					node = "";
				}
			} else {
				versions = new ModelGraph[1];
				date = historyUtil.getDate("export/" + modelID + "_history.txt", numOfVersions-1);
				versions[0] = new ModelGraph(0,fileUid,outputContent,date,"",modelID,"firstNode",true);
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
			fd.printContent();
			
			System.out.println("checking versions");
			if (!model.containsAttribute("versions")) {
				System.out.println("setting versions");
			    model.addAttribute("modelVersions", versions);
			    System.out.println("versions set");
			}
			
			historyUtil.saveFile("export/current.graphml", outputContent);
			
			return "redirect:/upload/historypreview";
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			redirectAttributes.addFlashAttribute("uploadcontent", "An error occured!");
			return "redirect:/upload/historypreview";
		}
	}
	
	@PostMapping(value = "upload/historypreview")
	public String uploadHistoryPreview(FormData formdata, String startdate, String enddate, String smell, String function, String fileUid, String vers, String action, RedirectAttributes redirectAttributes, Model model, @ModelAttribute("modelVersions") ModelGraph[] modelVersions, SessionStatus status) {
		System.out.println("action:" + action);
		System.out.println(model.toString());
		System.out.println("vers:" + vers);
		formdata.printContent();
		
		Date d = new Date();
		
		for(String s: nodeAttributes) {
			System.out.println(s);
		}
		
		HistoryUtility historyUtil = new HistoryUtility();
		
		ArchiUtility archiUtil = new ArchiUtility();
		String content = archiUtil.graphML2String("export/" + vers + ".graphml");
		int i = vers.indexOf("_v");
		System.out.println("i:" + i);
		String prefix = vers.substring(0,i+2);
		String modelID = prefix.substring(0,prefix.length()-2);
		
		ArrayList<Integer> vArray = new ArrayList<Integer>();
		String output = content;
		if(action != null) {
			int v = Integer.parseInt(vers.substring(i+2));
			System.out.println("v: " + v);
			// Fetch graph content
			int numOfVersions = archiUtil.countVersions(prefix);
			ModelGraph[] versions = new ModelGraph[numOfVersions];
			
			StringBuilder nodes = new StringBuilder();
			if(action.equals("all"))
			{
				
				String nodeID = "";
				Date updateDate = new Date();
				File deltaFile;
				for(int j=0;j<versions.length;j++) {
					deltaFile = new File("delta/" + prefix+(j) + ".txt");
					String delta = historyUtil.readContent(deltaFile);
					String filename = "export/" + prefix+(j) + ".graphml";
					content = archiUtil.graphML2String(filename);
					Querying query = new Querying(content, j);
					
					boolean matched = true;
					
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
							} else {
								matched = false;
							}
						}
					}
					String date = historyUtil.getDate("export/" + prefix.substring(0,prefix.length()-2) + "_history.txt", numOfVersions-1-j);
					versions[j] = new ModelGraph(j,prefix+(j),content,date,delta,modelID,"",matched);
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
				Querying query = new Querying(content);
				for(int j=0;j<nodeAttributes.length;j++) {
					String value = formdata.getAttributes()[j];
					if(!value.isBlank())
						nodes.append(query.returnNodes(nodeAttributes[j], value));
				} 
				File deltaFile;
				if(nodes.toString().isBlank()) {
					for(int j=0;j<versions.length;j++) { 
						String date = historyUtil.getDate("export/" + prefix.substring(0,prefix.length()-2) + "_history.txt", numOfVersions-1-j);
						deltaFile = new File("delta/" + prefix+(numOfVersions-1-j) + ".txt");
						String delta = historyUtil.readContent(deltaFile);
						if(numOfVersions-1-j==v) {
							
							versions[j] = new ModelGraph(v,vers,content,date,delta,modelID,"",true);
						}
						else
							versions[j] = new ModelGraph(numOfVersions-1-j,prefix+(numOfVersions-1-j),"",date,delta,modelID,"",true);
					}
				} else {
					for(int j=0;j<versions.length;j++) { 
						String date = historyUtil.getDate("export/" + prefix.substring(0,prefix.length()-2) + "_history.txt", numOfVersions-1-j);
						deltaFile = new File("delta/" + prefix+(numOfVersions-1-j) + ".txt");
						String delta = historyUtil.readContent(deltaFile);
						if(numOfVersions-1-j==v)
							versions[j] = new ModelGraph(v,vers,nodes.toString(),date,delta,modelID,"",true);
						else
							versions[j] = new ModelGraph(numOfVersions-1-j,prefix+(numOfVersions-1-j),"",date,delta,modelID,"",true);
					}
				}
				vArray.add(numOfVersions-1-v);
				output = historyUtil.nodesToGraphML(content, nodes.toString(), vArray, query.getNodeID());
				model.addAttribute("current", versions[numOfVersions-1-v]);
			}
			
			try {
				historyUtil.saveFile("export/current.graphml", output);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			System.out.println("checking versions");
			if (!model.containsAttribute("versions")) {
				System.out.println("setting versions");
			    model.addAttribute("modelVersions", versions);
			    System.out.println("versions set");
			}
			
			System.out.println("version: " + vers);	
			System.out.println("current: " + versions[numOfVersions-1-v].version);
			model.addAttribute("fileUid", vers);
			model.addAttribute("current", versions[numOfVersions-1]);
			model.addAttribute("versions", versions);
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
		neo4jGraphmlImport neoImport = new neo4jGraphmlImport(uid);
		neoImport.initializeGraph();

		return "neovispreview";

		// return "redirect:/repository/graph/"+uid;
	}
}
