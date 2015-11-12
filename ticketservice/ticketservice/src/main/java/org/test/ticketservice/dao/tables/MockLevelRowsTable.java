package org.test.ticketservice.dao.tables;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import org.test.ticketservice.vo.HeldSeat;
import org.test.ticketservice.vo.IRowSeat;
import org.test.ticketservice.vo.LevelRowSeat;
import org.test.ticketservice.vo.ReservedSeat;

public enum MockLevelRowsTable {
	INSTANCE;
	
	Map<Integer,Map<Integer,Map<Integer,IRowSeat>>> venueLevelSeats = new ConcurrentHashMap<Integer, Map<Integer, Map<Integer,IRowSeat>>>();
	AtomicInteger seatHoldSequenceNumber = new AtomicInteger();
	
	/*
	 * Set up the initial data
	 */
	private MockLevelRowsTable() {
		constructTable();
	}
	
	public void reset() {
		constructTable();
	}
	
	private void constructTable() {
		venueLevelSeats.clear();
		IRowSeat rowSeat = null;
		//Orchestra level
		Map<Integer, Map<Integer, IRowSeat>> rowSeats = new ConcurrentHashMap<Integer, Map<Integer, IRowSeat>>();
		Map<Integer, IRowSeat> rowSeatMap = new ConcurrentHashMap<Integer, IRowSeat>();
		int levelId = 1;
		for(int rowCount=1; rowCount<=25; rowCount++) {
			rowSeatMap = new ConcurrentHashMap<Integer, IRowSeat>();
			for(int seatNumCount=1; seatNumCount<=50; seatNumCount++) {
				rowSeat = new LevelRowSeat(levelId, rowCount, seatNumCount);
				rowSeatMap.put(seatNumCount, rowSeat);
			}
			rowSeats.put(rowCount, rowSeatMap);
		}
		venueLevelSeats.put(levelId, rowSeats);
		
		//Main level
		rowSeats = new ConcurrentHashMap<Integer, Map<Integer, IRowSeat>>();
		rowSeatMap = new ConcurrentHashMap<Integer, IRowSeat>();
		levelId = 2;
		for(int rowCount=1; rowCount<=20; rowCount++) {
			rowSeatMap = new ConcurrentHashMap<Integer, IRowSeat>();
			for(int seatNumCount=1; seatNumCount<=100; seatNumCount++) {
				rowSeat = new LevelRowSeat(levelId, rowCount, seatNumCount);
				rowSeatMap.put(seatNumCount, rowSeat);
			}
			rowSeats.put(rowCount, rowSeatMap);
		}
		venueLevelSeats.put(levelId, rowSeats);
		
		//Balcony 1 level
		rowSeats = new ConcurrentHashMap<Integer, Map<Integer, IRowSeat>>();
		rowSeatMap = new ConcurrentHashMap<Integer, IRowSeat>();
		levelId = 3;
		for(int rowCount=1; rowCount<=15; rowCount++) {
			rowSeatMap = new ConcurrentHashMap<Integer, IRowSeat>();
			for(int seatNumCount=1; seatNumCount<=100; seatNumCount++) {
				rowSeat = new LevelRowSeat(levelId, rowCount, seatNumCount);
				rowSeatMap.put(seatNumCount, rowSeat);
			}
			rowSeats.put(rowCount, rowSeatMap);
		}
		venueLevelSeats.put(levelId, rowSeats);
		
		//Balcony 2 level
		rowSeats = new ConcurrentHashMap<Integer, Map<Integer, IRowSeat>>();
		rowSeatMap = new ConcurrentHashMap<Integer, IRowSeat>();
		levelId = 4;
		for(int rowCount=1; rowCount<=15; rowCount++) {
			rowSeatMap = new ConcurrentHashMap<Integer, IRowSeat>();
			for(int seatNumCount=1; seatNumCount<=100; seatNumCount++) {
				rowSeat = new LevelRowSeat(levelId, rowCount, seatNumCount);
				rowSeatMap.put(seatNumCount, rowSeat);
			}
			rowSeats.put(rowCount, rowSeatMap);
		}
		venueLevelSeats.put(levelId, rowSeats);
	}
	
	public int getNextSeatHoldSequenceNumber() {
		return seatHoldSequenceNumber.getAndIncrement();
	}
	
	public IRowSeat getLevelRowSeat(int levelId, int rowNumber, int seatNumber) {
		Map<Integer,Map<Integer, IRowSeat>> levelRowsMap = venueLevelSeats.get(levelId);
		if(levelRowsMap == null) {
			return new LevelRowSeat(levelId,rowNumber, seatNumber);
		}
		Map<Integer, IRowSeat> rowSeatsMap = levelRowsMap.get(rowNumber);
		if(rowSeatsMap == null) {
			return new LevelRowSeat(levelId,rowNumber, seatNumber);
		}
		IRowSeat seat = rowSeatsMap.get(seatNumber);
		return seat;
	}
	
	public int countAllLevelAvailableSeats() {
		return getAllLevelAvailableSeats().size();
	}
	
	public int countAllLevelAvailableSeats(int levelId) {
		return getLevelAvailableSeats(levelId).size();
	}
	
	public List<IRowSeat> getAllLevelAvailableSeats() {
		List<IRowSeat> allAvailableSeats= new ArrayList<IRowSeat>();
		if(venueLevelSeats!=null && !venueLevelSeats.isEmpty()) {
			Collection<Map<Integer, Map<Integer, IRowSeat>>> venueLevelSeatsEntries = venueLevelSeats.values();
			venueLevelSeatsEntries.forEach(e -> {Collection<Map<Integer, IRowSeat>> rowSeatsMap = e.values();
						rowSeatsMap.forEach(a -> {Collection<IRowSeat> rowSeats = a.values();
						rowSeats.forEach(b -> {if(!(b.isHeld()|| b.isReserved())){allAvailableSeats.add(b);}});
						});
				});
		}
		return allAvailableSeats;
	}
	
	public List<IRowSeat> getHeldSeats(int holdId) {
		List<IRowSeat> allHeldSeats= new ArrayList<IRowSeat>();
		if(venueLevelSeats!=null && !venueLevelSeats.isEmpty()) {
			Collection<Map<Integer, Map<Integer, IRowSeat>>> venueLevelSeatsEntries = venueLevelSeats.values();
			venueLevelSeatsEntries.forEach(e -> {Collection<Map<Integer, IRowSeat>> rowSeatsMap = e.values();
						rowSeatsMap.forEach(a -> {Collection<IRowSeat> rowSeats = a.values();
						rowSeats.forEach(b -> {if(b.isHeld() && b.getSeatHoldId()==holdId){allHeldSeats.add(b);}});
						});
				});
		}
		return allHeldSeats;
	}
	
	public List<IRowSeat> getReservedSeats(String confirmationCode) {
		List<IRowSeat> allReservedSeats= new ArrayList<IRowSeat>();
		if(venueLevelSeats!=null && !venueLevelSeats.isEmpty()) {
			Collection<Map<Integer, Map<Integer, IRowSeat>>> venueLevelSeatsEntries = venueLevelSeats.values();
			venueLevelSeatsEntries.forEach(e -> {Collection<Map<Integer, IRowSeat>> rowSeatsMap = e.values();
						rowSeatsMap.forEach(a -> {Collection<IRowSeat> rowSeats = a.values();
						rowSeats.forEach(b -> {if(b.isReserved() && b.getConfirmationCode().get().equals(confirmationCode)){allReservedSeats.add(b);}});
						});
				});
		}
		return allReservedSeats;
	}	
	
	public int countHeldSeats(int holdId) {
		List<IRowSeat> allHeldSeats= new ArrayList<IRowSeat>();
		if(venueLevelSeats!=null && !venueLevelSeats.isEmpty()) {
			Collection<Map<Integer, Map<Integer, IRowSeat>>> venueLevelSeatsEntries = venueLevelSeats.values();
			venueLevelSeatsEntries.forEach(e -> {Collection<Map<Integer, IRowSeat>> rowSeatsMap = e.values();
						rowSeatsMap.forEach(a -> {Collection<IRowSeat> rowSeats = a.values();
						rowSeats.forEach(b -> {if(b.isHeld() && b.getSeatHoldId()== holdId){allHeldSeats.add(b);}});
						});
				});
		}
		return allHeldSeats.size();
	}
	
	public List<IRowSeat> getLevelAvailableSeats(int levelId) {
		List<IRowSeat> allAvailableSeats= new ArrayList<IRowSeat>();
		Map<Integer, Map<Integer, IRowSeat>> levelSeats = venueLevelSeats.get(levelId);
		if(levelSeats!=null && !levelSeats.isEmpty()) {
			Collection<Map<Integer, IRowSeat>> venueLevelSeatsEntries = levelSeats.values();
			venueLevelSeatsEntries.forEach(e -> {Collection<IRowSeat> rowSeats = e.values();
						rowSeats.forEach(b -> {if(!(b.isHeld()|| b.isReserved())){allAvailableSeats.add(b);}});
			});
		}
		return allAvailableSeats;
	}	
	
	public synchronized void reserveSeat(int levelId, int rowNumber, int seatNumber, int holdId, String customerEmail, String confirmationCode) {
		Map<Integer, Map<Integer, IRowSeat>> levelSeats = venueLevelSeats.get(levelId);
		if(levelSeats!=null && !levelSeats.isEmpty()) {
			Map<Integer, IRowSeat> rowSeats = levelSeats.get(rowNumber);
			if(rowSeats!=null && !rowSeats.isEmpty()) {
				IRowSeat seat = rowSeats.get(seatNumber);
				if(seat.isHeld() && holdId == seat.getSeatHoldId()) {
					rowSeats.put(seatNumber, new ReservedSeat(levelId,rowNumber,seatNumber,customerEmail, confirmationCode));
				}
			}
		}
	}
	
	public synchronized boolean holdSeat(int levelId, int rowNumber, int seatNumber, int seatHoldId) {
		Map<Integer, Map<Integer, IRowSeat>> levelSeats = venueLevelSeats.get(levelId);
		if(levelSeats!=null && !levelSeats.isEmpty()) {
			Map<Integer, IRowSeat> rowSeats = levelSeats.get(rowNumber);
			if(rowSeats!=null && !rowSeats.isEmpty()) {

					IRowSeat seat = rowSeats.get(seatNumber);
					if(!seat.isHeld()) {
						rowSeats.put(seatNumber, new HeldSeat(levelId,rowNumber,seatNumber,seatHoldId));
						return true;
					}else {
						return false;
					}
				
			}
		}
		return false;
	}
	
	public synchronized void unreserveSeat(int levelId, int rowNumber, int seatNumber) {
		Map<Integer, Map<Integer, IRowSeat>> levelSeats = venueLevelSeats.get(levelId);
		if(levelSeats!=null && !levelSeats.isEmpty()) {
			Map<Integer, IRowSeat> rowSeats = levelSeats.get(rowNumber);
			if(rowSeats!=null && !rowSeats.isEmpty()) {
				IRowSeat seat = rowSeats.get(seatNumber);
				if(seat.isReserved()) {
					rowSeats.put(seatNumber, new LevelRowSeat(levelId,rowNumber,seatNumber));
				}
			}
		}
	}
	
	public synchronized void releaseSeat(int levelId, int rowNumber, int seatNumber, int heldId) {
		Map<Integer, Map<Integer, IRowSeat>> levelSeats = venueLevelSeats.get(levelId);
		if(levelSeats!=null && !levelSeats.isEmpty()) {
			Map<Integer, IRowSeat> rowSeats = levelSeats.get(rowNumber);
			if(rowSeats!=null && !rowSeats.isEmpty()) {

				IRowSeat seat = rowSeats.get(seatNumber);
				if(seat.isHeld() && seat.getSeatHoldId() == heldId) {
					rowSeats.put(seatNumber, new LevelRowSeat(levelId,rowNumber,seatNumber));
				}
				
			}
		}
	}
}
