package org.test.ticketservice.dao.impl;

import java.util.List;

import org.test.ticketservice.dao.LevelSeatsDao;
import org.test.ticketservice.dao.tables.MockLevelRowsTable;
import org.test.ticketservice.dao.tables.MockVenueLevelTable;
import org.test.ticketservice.vo.IRowSeat;
import org.test.ticketservice.vo.LevelSeats;

public class LevelSeatsDaoImpl implements LevelSeatsDao{

	@Override
	public LevelSeats getVenueLevelSeatsInfo(int levelId) {
		return MockVenueLevelTable.INSTANCE.getLevelSeats(levelId);
	}

	@Override
	public IRowSeat getRowSeatInfo(int levelId, int rowNumber, int seatNumber) {
		return MockLevelRowsTable.INSTANCE.getLevelRowSeat(levelId, rowNumber, seatNumber);
	}

	@Override
	public List<IRowSeat> getAllLevelAvailableSeats() {
		return MockLevelRowsTable.INSTANCE.getAllLevelAvailableSeats();
	}

	@Override
	public List<IRowSeat> getLevelAvailableSeats(int levelId) {
		return MockLevelRowsTable.INSTANCE.getLevelAvailableSeats(levelId);
	}
	
	@Override
	public void reserveLevelSeat(int levelId, int rowNumber, int seatNumber, int holdId, String cutomerEmail, String confirmationCode) {
		MockLevelRowsTable.INSTANCE.reserveSeat(levelId, rowNumber, seatNumber, holdId, cutomerEmail, confirmationCode);
	}

	@Override
	public boolean holdLevelSeat(int levelId, int rowNumber, int seatNumber, int seatHoldId) {
		return MockLevelRowsTable.INSTANCE.holdSeat(levelId, rowNumber, seatNumber, seatHoldId);
	}

	@Override
	public void unreserveLevelSeat(int levelId, int rowNumber, int seatNumber) {
		MockLevelRowsTable.INSTANCE.unreserveSeat(levelId, rowNumber, seatNumber);
	}

	@Override
	public void releaseHeldLevelSeat(int levelId, int rowNumber, int seatNumber, int holdId) {
		MockLevelRowsTable.INSTANCE.releaseSeat(levelId, rowNumber, seatNumber,holdId);		
	}

	@Override
	public int countAllLevelAvailableSeats() {
		return MockLevelRowsTable.INSTANCE.countAllLevelAvailableSeats();
	}

	@Override
	public int countLevelAvailableSeats(int levelId) {
		return MockLevelRowsTable.INSTANCE.countAllLevelAvailableSeats(levelId);
	}

	@Override
	public List<IRowSeat> getHeldSeats(int heldId) {
		return MockLevelRowsTable.INSTANCE.getHeldSeats(heldId);
	}
	
	@Override
	public List<IRowSeat> getReservedSeats(String confirmationCode) {
		return MockLevelRowsTable.INSTANCE.getReservedSeats(confirmationCode);
	}

	@Override
	public int countHeldSeats(int heldId) {
		return MockLevelRowsTable.INSTANCE.countHeldSeats(heldId);
	}

	@Override
	public int getNextSeatHoldSequenceNumber() {
		return MockLevelRowsTable.INSTANCE.getNextSeatHoldSequenceNumber();
	}

	@Override
	public void resetMockTable() {
		MockLevelRowsTable.INSTANCE.reset();
		
	}

}
