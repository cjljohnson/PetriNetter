package com.cjljohnson.PetriNetter.model;

public class Arc implements Cloneable {

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
	
	public Arc clone() {
        return new Arc(weight);
    }
}
