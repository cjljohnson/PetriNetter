package com.cjljohnson.PetriNetter.model;

public class Arc {

	private int weight;
	
	public Arc() {
		weight = 1;
	}
	
	public Arc(int weight) {
		this.weight = weight;
	}

	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}
}
