package com.cjljohnson.PetriNetter.model;

public class Place {
	
	private int tokens;
	private int capacity;
	private int index;
	private String name;
	
	public Place() {
		
	}
	
	public Place(int tokens, int capacity, int index, String name) {
		this.tokens = tokens;
		this.capacity = capacity;
		this.index = index;
		this.name = name;
	}

	public int getTokens() {
		return tokens;
	}

	public void setTokens(int tokens) {
		this.tokens = tokens;
	}

	public int getCapacity() {
		return capacity;
	}

	public void setCapacity(int capacity) {
		this.capacity = capacity;
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
	
//	public Place(int tokens, int capacity, int index, String name) {
//		
//	}
	
	
}
