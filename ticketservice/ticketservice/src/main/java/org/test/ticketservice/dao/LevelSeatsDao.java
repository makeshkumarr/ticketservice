package org.test.ticketservice.dao;

import java.util.List;

import org.test.ticketservice.vo.IRowSeat;
import org.test.ticketservice.vo.LevelSeats;

public interface LevelSeatsDao {
	public void resetMockTable();
	public int getNextSeatHoldSequenceNumber();
	public LevelSeats getVenueLevelSeatsInfo(int levelId);
	public IRowSeat getRowSeatInfo(int levelId, int rowNumber, int seatNumber);
	public List<IRowSeat> getHeldSeats(int heldId);
	public List<IRowSeat> getReservedSeats(String confirmationCode);
	public List<IRowSeat> getAllLevelAvailableSeats();
	public List<IRowSeat> getLevelAvailableSeats(int levelId);
	public int countHeldSeats(int heldId);
	public int countAllLevelAvailableSeats();
	public int countLevelAvailableSeats(int levelId);
	public void reserveLevelSeat(int levelId, int rowNumber, int seatNumber, int holdId, String cutomerEmail, String confirmationCode);
	public boolean holdLevelSeat(int levelId, int rowNumber, int seatNumber, int seatHoldId);
	public void unreserveLevelSeat(int levelId, int rowNumber, int seatNumber);
	public void releaseHeldLevelSeat(int levelId, int rowNumber, int seatNumber, int holdId);
}
