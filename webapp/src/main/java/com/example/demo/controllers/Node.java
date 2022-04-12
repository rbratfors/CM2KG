package com.example.demo.controllers;

public class Node {
	private long id;
	public String content;
	private String attribute;
	private String[] attributes;
	private String type;
	public String value;

	public Node() {
	}
	
	public Node(String attribute, String type) {
		this.attribute = attribute;
		this.setType(type);
		
	}
	
	public Node(String[] attributes) {
		this.setAttributes(attributes);
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
	
	public void setValue(String value) {
		this.value = value;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String[] getAttributes() {
		return attributes;
	}

	public void setAttributes(String[] attributes) {
		this.attributes = attributes;
	}
}
