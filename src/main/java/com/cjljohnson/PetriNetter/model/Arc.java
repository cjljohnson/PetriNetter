package com.cjljohnson.PetriNetter.model;

import java.io.Serializable;

public class Arc implements Cloneable, Serializable {

	private static final long serialVersionUID = 1797941979839108515L;
	
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
