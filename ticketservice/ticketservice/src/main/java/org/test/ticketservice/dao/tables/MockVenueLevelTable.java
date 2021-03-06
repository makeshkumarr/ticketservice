package org.test.ticketservice.dao.tables;

import java.util.HashMap;
import java.util.Map;

import org.test.ticketservice.vo.LevelSeats;

public enum MockVenueLevelTable {
	INSTANCE;
	Map<Integer,LevelSeats> venueLevelSeats = new HashMap<Integer, LevelSeats>();
	
	/**
	 * Set up the initial table data.
	 */
	private MockVenueLevelTable() {
		LevelSeats level = new LevelSeats(1,"Orchestra", 100, 25, 50);
		venueLevelSeats.put(1, level);
		level = new LevelSeats(2,"Orchestra", 75, 20, 100);
		venueLevelSeats.put(2, level);
		level = new LevelSeats(3,"Main", 50, 15, 100);
		venueLevelSeats.put(3, level);
		level = new LevelSeats(4,"Balcony 1", 40, 15, 100);
		venueLevelSeats.put(4, level);
	}
	
	public LevelSeats getLevelSeats(int levelId) {
		return venueLevelSeats.get(levelId);
	}
}
