package org.test.ticketservice.services.impl;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

import org.test.ticketservice.dao.LevelSeatsDao;
import org.test.ticketservice.dao.impl.LevelSeatsDaoImpl;
import org.test.ticketservice.services.TicketService;
import org.test.ticketservice.vo.IRowSeat;
import org.test.ticketservice.vo.SeatHold;

public class TicketServiceImpl implements TicketService {
	
	
	private static final int VENUE_MAX_LEVEL = 4;

	private static final int VENUE_MIN_LEVEL = 1;
	
	private LevelSeatsDao levelDao = new LevelSeatsDaoImpl();

	@Override
	public int numSeatsAvailable(Optional<Integer> venueLevel) {
		if(venueLevel !=null && venueLevel.isPresent()) {
			return levelDao.countLevelAvailableSeats(venueLevel.get());
		}else {
			return levelDao.countAllLevelAvailableSeats();
		}
	}

	@Override
	public SeatHold findAndHoldSeats(int numSeats, Optional<Integer> minLevel,
			Optional<Integer> maxLevel, String customerEmail) {
		ReentrantLock lock = new ReentrantLock();
		lock.lock();
		try {
		int holdId = levelDao.getNextSeatHoldSequenceNumber();
		int attemptCounts = 0;
		while(attemptCounts<3) {
			holdSeats(numSeats, minLevel, maxLevel, holdId);
			/**
				 * Verify all requested seats are held..
				 */
			List<IRowSeat> heldSeats = levelDao.getHeldSeats(holdId);
			
			if(heldSeats.size() == numSeats) {
				SeatHold seatHold = new SeatHold(holdId, heldSeats, customerEmail);
//				String ids = "";
//				for(IRowSeat seat:heldSeats) {
//					ids = ids + "####"+seat.getLevelId() +":"+seat.getRowNumber()+":"+seat.getSeatNumber();
//				}
//				System.out.println("holdId: "+holdId+ids);
				return seatHold;
			}
			else {
				System.out.println("\n\nReleasing seats..."+holdId+ "  Size: "+heldSeats.size());
				heldSeats.forEach(e -> {levelDao.releaseHeldLevelSeat(e.getLevelId(), e.getRowNumber(), e.getSeatNumber(), holdId);});
			}
			attemptCounts++;
			Thread.sleep(100L);
		}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			lock.unlock();
		}

		return null;
	}
	
	private void holdSeats(int numSeats, Optional<Integer> minLevel,
			Optional<Integer> maxLevel, int holdId) {

		Map<Integer, Integer> holdCountMap = new ConcurrentHashMap<Integer, Integer>();

		int minimumLevel = minLevel.orElse(VENUE_MIN_LEVEL);
		int maximumLevel = maxLevel.orElse(VENUE_MAX_LEVEL);
		
		List<IRowSeat> levelAvailableSeats = null;
		int availableSeatsCount = 0;
		boolean isSeatAssigned = false;
		for(int levelCount=minimumLevel; levelCount<maximumLevel && !isSeatAssigned; levelCount++) {

			availableSeatsCount = levelDao.countLevelAvailableSeats(levelCount);

			if(availableSeatsCount>=numSeats && !isSeatAssigned) {
				
				ReentrantLock lock = new ReentrantLock();
				lock.lock();
				levelAvailableSeats = levelDao.getLevelAvailableSeats(levelCount);
				try{
				for(IRowSeat availableSeat:levelAvailableSeats) {
					if(levelDao.holdLevelSeat(availableSeat.getLevelId(), availableSeat.getRowNumber(), availableSeat.getSeatNumber(),holdId)) {
						holdCountMap.compute(holdId, (key,value)->value==null?1:value+1);
					}
					if(holdCountMap.getOrDefault(holdId, 0) == numSeats) {
						isSeatAssigned = true;
						break;
					}
		
				}
				}finally{
					lock.unlock();
				}
	
			}
		}
		if(!isSeatAssigned) {
			/*
			 * if seats are not available in a level
			 */
			availableSeatsCount = levelDao.countAllLevelAvailableSeats();

			if(availableSeatsCount>=(numSeats-holdCountMap.getOrDefault(holdId, 0))) {

				ReentrantLock lock = new ReentrantLock();
				lock.lock();
				levelAvailableSeats = levelDao.getAllLevelAvailableSeats();
				try{
				for(IRowSeat availableSeat:levelAvailableSeats) {
					if(availableSeat.getLevelId()>= minimumLevel && availableSeat.getLevelId() <= maximumLevel &&
							levelDao.holdLevelSeat(availableSeat.getLevelId(), availableSeat.getRowNumber(), availableSeat.getSeatNumber(),holdId)) {
						holdCountMap.compute(holdId, (key,value)->value==null?1:value+1);
					}
					if(holdCountMap.getOrDefault(holdId, 0)  == numSeats) {
						isSeatAssigned = true;
						break;
					}
						
				}
				}finally{
					lock.unlock();
				}
			}
		}
	}
	@Override
	public String reserveSeats(int seatHoldId, String customerEmail) {
		List<IRowSeat> heldSeats = levelDao.getHeldSeats(seatHoldId);
		String confirmationCode = UUID.randomUUID().toString();
		heldSeats.forEach(e -> {levelDao.reserveLevelSeat(e.getLevelId(), e.getRowNumber(), e.getSeatNumber(), seatHoldId, customerEmail, confirmationCode);});
		List<IRowSeat> reservedSeats = levelDao.getReservedSeats(confirmationCode);
		
		if(reservedSeats.size() == heldSeats.size()) {
			return confirmationCode;
		}
		return null;
	}

}
