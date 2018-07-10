package com.cjljohnson.PetriNetter.model;

public class Transition {

	private int index;
	private String name;
	
	public Transition() {
		
	}
	
	public Transition(int index, String name) {
		this.index = index;
		this.name = name;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
}
