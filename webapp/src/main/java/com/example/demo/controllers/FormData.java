package com.example.demo.controllers;

import java.util.ArrayList;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

public class FormData {
	private long id;
	private String content;
	private String attribute;
	private String[] attributes;
	private String type;
	private String value;
	public Node[] nodes;
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date startdate;
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date enddate;
	
	private String version;

	public FormData() {
	}
	
	public FormData(String attribute, String type) {
		this.attribute = attribute;
		this.type = type;
	}
	
	public FormData(Node[] nd) {
		this.nodes = nd;
	}
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
	public String getAttribute() {
		return attribute;
	}

	public void setAttribute(String attribute) {
		this.attribute = attribute;
	}
	
	public String getValue() {
		return value;
	}
	
	public Node[] getNodes() {
		return nodes;
	}

	public void setNodes(Node[] nodes) {
		this.nodes = nodes;
	}
	
	public Date getStartDate() {
		return startdate;
	}

	public void setStartDate(Date startdate) {
		this.startdate = startdate;
	}
	
	public Date getEndDate() {
		return enddate;
	}

	public void setEndDate(Date enddate) {
		this.enddate = enddate;
	}
	
	public void printContent() {
		System.out.println("content:" + content);
		System.out.println("attr:" + attribute);
		System.out.println("type:" + type);
		System.out.println("value:" + value);
		System.out.println("start:" + startdate);
		System.out.println("end:" + enddate);
		if(attributes!=null) {
			for(String s : attributes) {
				System.out.println(s);
			}
		}
		if(nodes!=null) {
			for(Node n : nodes) {
				System.out.println(n.getAttribute());
			}
		}
	}
	
	public String[] getAttributes() {
		// TODO Auto-generated method stub
		return attributes;
	}

	public void setAttributes(String[] attributes) {
		// TODO Auto-generated method stub
		this.attributes = attributes;
	}
	
}
