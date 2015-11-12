package org.test.ticketservice.vo;

import java.io.Serializable;

public class LevelSeats implements Serializable{

	private static final long serialVersionUID = 1720279830273254291L;
	
	private int levelId;
	private String levelName;
	private float price;
	private int rows;
	private int seatsPerRow;
	
	public LevelSeats(int levelId, String levelName, float price, int rows, int seatsPerRow) {
		this.levelId = levelId;
		this.levelName = levelName;
		this.price = price;
		this.rows = rows;
		this.seatsPerRow = seatsPerRow;
	}

	public int getLevelId() {
		return levelId;
	}

	public String getLevelName() {
		return levelName;
	}

	public float getPrice() {
		return price;
	}

	public int getRows() {
		return rows;
	}

	public int getSeatsPerRow() {
		return seatsPerRow;
	}

}
