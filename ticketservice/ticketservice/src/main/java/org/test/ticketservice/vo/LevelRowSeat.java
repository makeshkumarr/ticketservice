package org.test.ticketservice.vo;

public class LevelRowSeat implements IRowSeat{

	private static final long serialVersionUID = -8855316060233353406L;
	
	private int levelId;
	private int rowNumber;
	private int seatNumber;

	public LevelRowSeat(int levelId, int rowNumber, int seatNumber) {
		this.levelId = levelId;
		this.rowNumber = rowNumber;
		this.seatNumber = seatNumber;
	}
	
	@Override
	public int getLevelId() {
		return levelId;
	}

	@Override
	public int getRowNumber() {
		return rowNumber;
	}

	@Override
	public int getSeatNumber() {
		return seatNumber;
	}
}
