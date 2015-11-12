package org.test.ticketservice;

import java.util.ArrayList;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.Before;
import org.junit.Test;
import org.test.ticketservice.dao.LevelSeatsDao;
import org.test.ticketservice.dao.impl.LevelSeatsDaoImpl;
import org.test.ticketservice.services.TicketService;
import org.test.ticketservice.services.impl.TicketServiceImpl;
import org.test.ticketservice.vo.IRowSeat;
import org.test.ticketservice.vo.SeatHold;

import static org.junit.Assert.*;
public class TicketServiceTest {
	TicketService ticketService = new TicketServiceImpl();
	private LevelSeatsDao levelDao = new LevelSeatsDaoImpl();

	@Before
	public void cleanup() {
		levelDao.resetMockTable();
	}
	@Test
	public void testInitialNumSeatsAvailable() {
		assertEquals(6250,ticketService.numSeatsAvailable(Optional.empty()));		
		assertEquals(1250,ticketService.numSeatsAvailable(Optional.of(1)));
		assertEquals(2000,ticketService.numSeatsAvailable(Optional.of(2)));
		assertEquals(1500,ticketService.numSeatsAvailable(Optional.of(3)));
		assertEquals(1500,ticketService.numSeatsAvailable(Optional.of(4)));

	}
	
	@Test
	public void testBasicFindAndHoldSeats() {
		int numSeatsNeeded = 1;
		String customerEmail = "makeshkumar.r@gmail.com";
		SeatHold seatHold = ticketService.findAndHoldSeats(numSeatsNeeded, Optional.of(1), Optional.of(2), customerEmail);
		assertEquals(6249,ticketService.numSeatsAvailable(Optional.empty()));	
		List<IRowSeat> heldSeats = levelDao.getHeldSeats(seatHold.getSeatHoldId());
		assertEquals(numSeatsNeeded, heldSeats.size());
		String confirmationCode = ticketService.reserveSeats(seatHold.getSeatHoldId(), customerEmail);
		List<IRowSeat> reservedSeats = levelDao.getReservedSeats(confirmationCode);
		assertEquals(numSeatsNeeded, reservedSeats.size());
		assertTrue(checkConfirmationKeyIsAvailable(reservedSeats));

	}
	
	@Test
	public void testMultipleFindAndHoldSeatsForSameUser() {
		int numSeatsNeeded = 5;
		String customerEmail = "makeshkumar.r@gmail.com";
		SeatHold seatHold1 = ticketService.findAndHoldSeats(numSeatsNeeded, Optional.of(1), Optional.of(2), customerEmail);
		assertEquals(6245,ticketService.numSeatsAvailable(Optional.empty()));	
		List<IRowSeat> heldSeats = levelDao.getHeldSeats(seatHold1.getSeatHoldId());
		assertEquals(numSeatsNeeded, heldSeats.size());
		String confirmationCode = ticketService.reserveSeats(seatHold1.getSeatHoldId(), customerEmail);
		List<IRowSeat> reservedSeats = levelDao.getReservedSeats(confirmationCode);
		assertEquals(numSeatsNeeded, reservedSeats.size());
		assertTrue(checkConfirmationKeyIsAvailable(reservedSeats));

		SeatHold seatHold2 = ticketService.findAndHoldSeats(numSeatsNeeded, Optional.of(1), Optional.of(2), customerEmail);
		assertEquals(6240,ticketService.numSeatsAvailable(Optional.empty()));	
		heldSeats = levelDao.getHeldSeats(seatHold2.getSeatHoldId());
		assertEquals(numSeatsNeeded, heldSeats.size());
		confirmationCode = ticketService.reserveSeats(seatHold2.getSeatHoldId(), customerEmail);
		reservedSeats = levelDao.getReservedSeats(confirmationCode);
		assertEquals(numSeatsNeeded, reservedSeats.size());
		assertTrue(checkConfirmationKeyIsAvailable(reservedSeats));

	}
	
	@Test
	public void testMultipleFindAndHoldSeatsForDifferentUsers() {
		int numSeatsNeeded = 5;
		String customerEmail1 = "makeshkumar.r@gmail.com";
		String customerEmail2 = "makesh.r@gmail.com";

		SeatHold seatHold1 = ticketService.findAndHoldSeats(numSeatsNeeded, Optional.of(1), Optional.of(2), customerEmail1);
		assertEquals(6245,ticketService.numSeatsAvailable(Optional.empty()));	
		List<IRowSeat> heldSeats = levelDao.getHeldSeats(seatHold1.getSeatHoldId());
		assertEquals(numSeatsNeeded, heldSeats.size());
		String confirmationCode = ticketService.reserveSeats(seatHold1.getSeatHoldId(), customerEmail1);
		List<IRowSeat> reservedSeats = levelDao.getReservedSeats(confirmationCode);
		assertEquals(numSeatsNeeded, reservedSeats.size());
		
		SeatHold seatHold2 = ticketService.findAndHoldSeats(numSeatsNeeded, Optional.of(1), Optional.of(2), customerEmail2);
		assertEquals(6240,ticketService.numSeatsAvailable(Optional.empty()));	
		heldSeats = levelDao.getHeldSeats(seatHold2.getSeatHoldId());
		assertEquals(numSeatsNeeded, heldSeats.size());
		confirmationCode = ticketService.reserveSeats(seatHold2.getSeatHoldId(), customerEmail2);
		reservedSeats = levelDao.getReservedSeats(confirmationCode);
		assertEquals(numSeatsNeeded, reservedSeats.size());
		assertTrue(checkConfirmationKeyIsAvailable(reservedSeats));

	}
	
	@Test
	public void testLevelSpecificSeatReservation() {
		int numSeatsNeeded = 5;
		int levelNeeded = 4;
		String customerEmail1 = "makeshkumar.r@gmail.com";

		SeatHold seatHold1 = ticketService.findAndHoldSeats(numSeatsNeeded, Optional.of(levelNeeded), Optional.of(levelNeeded), customerEmail1);
		assertEquals(6245,ticketService.numSeatsAvailable(Optional.empty()));	
		List<IRowSeat> heldSeats = levelDao.getHeldSeats(seatHold1.getSeatHoldId());
		assertEquals(numSeatsNeeded, heldSeats.size());
		String confirmationCode = ticketService.reserveSeats(seatHold1.getSeatHoldId(), customerEmail1);
		List<IRowSeat> reservedSeats = levelDao.getReservedSeats(confirmationCode);
		assertEquals(numSeatsNeeded, reservedSeats.size());
		for(IRowSeat reserved: reservedSeats) {
			assertTrue(levelNeeded == reserved.getLevelId());
		}

	}
	
	@Test
	public void testNoLevelSpecificSeatReservation() {
		int numSeatsNeeded = 5;
		String customerEmail1 = "makeshkumar.r@gmail.com";

		SeatHold seatHold1 = ticketService.findAndHoldSeats(numSeatsNeeded, Optional.empty(), Optional.empty(), customerEmail1);
		assertEquals(6245,ticketService.numSeatsAvailable(Optional.empty()));	
		List<IRowSeat> heldSeats = levelDao.getHeldSeats(seatHold1.getSeatHoldId());
		assertEquals(numSeatsNeeded, heldSeats.size());
		String confirmationCode = ticketService.reserveSeats(seatHold1.getSeatHoldId(), customerEmail1);
		List<IRowSeat> reservedSeats = levelDao.getReservedSeats(confirmationCode);
		assertEquals(numSeatsNeeded, reservedSeats.size());
		for(IRowSeat reserved: reservedSeats) {
			assertTrue(1 == reserved.getLevelId());
		}

	}
	
	@Test
	public void testBestAvailableSeats() {
		//Reserve all seats except 2 in Level 1
		int numSeatsNeeded = 1248; 
		int levelNeeded = 1;
		String customerEmail1 = "makeshkumar.r@gmail.com";

		SeatHold seatHold1 = ticketService.findAndHoldSeats(numSeatsNeeded, Optional.of(levelNeeded), Optional.of(levelNeeded), customerEmail1);
		assertEquals(2,ticketService.numSeatsAvailable(Optional.of(levelNeeded)));	
		List<IRowSeat> heldSeats = levelDao.getHeldSeats(seatHold1.getSeatHoldId());
		assertEquals(numSeatsNeeded, heldSeats.size());
		String confirmationCode = ticketService.reserveSeats(seatHold1.getSeatHoldId(), customerEmail1);
		List<IRowSeat> reservedSeats = levelDao.getReservedSeats(confirmationCode);
		assertEquals(numSeatsNeeded, reservedSeats.size());
		for(IRowSeat reserved: reservedSeats) {
			assertTrue(levelNeeded == reserved.getLevelId());
		}
		
		//Reserve all seats except 2 in Level 2
		numSeatsNeeded = 1998; 
		levelNeeded = 2;

		seatHold1 = ticketService.findAndHoldSeats(numSeatsNeeded, Optional.of(levelNeeded), Optional.of(levelNeeded), customerEmail1);
		assertEquals(2,ticketService.numSeatsAvailable(Optional.of(levelNeeded)));	
		heldSeats = levelDao.getHeldSeats(seatHold1.getSeatHoldId());
		assertEquals(numSeatsNeeded, heldSeats.size());
		confirmationCode = ticketService.reserveSeats(seatHold1.getSeatHoldId(), customerEmail1);
		reservedSeats = levelDao.getReservedSeats(confirmationCode);
		assertEquals(numSeatsNeeded, reservedSeats.size());
		for(IRowSeat reserved: reservedSeats) {
			assertTrue(levelNeeded == reserved.getLevelId());
		}
		
		//Reserve all seats except 3 in Level 3
		numSeatsNeeded = 1497; 
		levelNeeded = 3;

		seatHold1 = ticketService.findAndHoldSeats(numSeatsNeeded, Optional.of(levelNeeded), Optional.of(levelNeeded), customerEmail1);
		assertEquals(3,ticketService.numSeatsAvailable(Optional.of(levelNeeded)));	
		heldSeats = levelDao.getHeldSeats(seatHold1.getSeatHoldId());
		assertEquals(numSeatsNeeded, heldSeats.size());
		confirmationCode = ticketService.reserveSeats(seatHold1.getSeatHoldId(), customerEmail1);
		reservedSeats = levelDao.getReservedSeats(confirmationCode);
		assertEquals(numSeatsNeeded, reservedSeats.size());
		for(IRowSeat reserved: reservedSeats) {
			assertTrue(levelNeeded == reserved.getLevelId());
		}
		
		//Reserve all seats except 2 in Level 4
		numSeatsNeeded = 1498; 
		levelNeeded = 4;

		seatHold1 = ticketService.findAndHoldSeats(numSeatsNeeded, Optional.of(levelNeeded), Optional.of(levelNeeded), customerEmail1);
		assertEquals(2,ticketService.numSeatsAvailable(Optional.of(levelNeeded)));	
		heldSeats = levelDao.getHeldSeats(seatHold1.getSeatHoldId());
		assertEquals(numSeatsNeeded, heldSeats.size());
		confirmationCode = ticketService.reserveSeats(seatHold1.getSeatHoldId(), customerEmail1);
		reservedSeats = levelDao.getReservedSeats(confirmationCode);
		assertEquals(numSeatsNeeded, reservedSeats.size());
		for(IRowSeat reserved: reservedSeats) {
			assertTrue(levelNeeded == reserved.getLevelId());
		}
		
		//Only 9 seats available.. among those 3 seats are in Level 3. When 3 seats reservation requested level 3 seats should be reserved
		
		numSeatsNeeded = 3; 

		seatHold1 = ticketService.findAndHoldSeats(numSeatsNeeded, Optional.empty(), Optional.empty(), customerEmail1);
		assertEquals(6,ticketService.numSeatsAvailable(Optional.empty()));	
		heldSeats = levelDao.getHeldSeats(seatHold1.getSeatHoldId());
		assertEquals(numSeatsNeeded, heldSeats.size());
		confirmationCode = ticketService.reserveSeats(seatHold1.getSeatHoldId(), customerEmail1);
		reservedSeats = levelDao.getReservedSeats(confirmationCode);
		assertEquals(numSeatsNeeded, reservedSeats.size());
		for(IRowSeat reserved: reservedSeats) {
			assertTrue(3 == reserved.getLevelId());
		}
		
		//Only 6 seats available.. Each Level 1, Level 2 and Level 4 has 2 seats. When 2 seats reservation requested level 1 seats should be reserved
		
		numSeatsNeeded = 2; 

		seatHold1 = ticketService.findAndHoldSeats(numSeatsNeeded, Optional.empty(), Optional.empty(), customerEmail1);
		assertEquals(4,ticketService.numSeatsAvailable(Optional.empty()));	
		heldSeats = levelDao.getHeldSeats(seatHold1.getSeatHoldId());
		assertEquals(numSeatsNeeded, heldSeats.size());
		confirmationCode = ticketService.reserveSeats(seatHold1.getSeatHoldId(), customerEmail1);
		reservedSeats = levelDao.getReservedSeats(confirmationCode);
		assertEquals(numSeatsNeeded, reservedSeats.size());
		for(IRowSeat reserved: reservedSeats) {
			assertTrue(1 == reserved.getLevelId());
		}
		
		//Only 4 seats available.. Each Level 2 and Level 4 has 2 seats. When 2 seats reservation requested level 2 seats should be reserved
		
		numSeatsNeeded = 2; 

		seatHold1 = ticketService.findAndHoldSeats(numSeatsNeeded, Optional.empty(), Optional.empty(), customerEmail1);
		assertEquals(2,ticketService.numSeatsAvailable(Optional.empty()));	
		heldSeats = levelDao.getHeldSeats(seatHold1.getSeatHoldId());
		assertEquals(numSeatsNeeded, heldSeats.size());
		confirmationCode = ticketService.reserveSeats(seatHold1.getSeatHoldId(), customerEmail1);
		reservedSeats = levelDao.getReservedSeats(confirmationCode);
		assertEquals(numSeatsNeeded, reservedSeats.size());
		for(IRowSeat reserved: reservedSeats) {
			assertTrue(2 == reserved.getLevelId());
		}
		
		//Only 2 seats available.. Should not reserve any seats
		
		numSeatsNeeded = 3; 

		seatHold1 = ticketService.findAndHoldSeats(numSeatsNeeded, Optional.empty(), Optional.empty(), customerEmail1);
		assertNull(seatHold1);
				
		//Only 2 seats available.. Level 4 has 2 seats. When 2 seats reservation requested level 4 seats should be reserved
		
		numSeatsNeeded = 2; 

		seatHold1 = ticketService.findAndHoldSeats(numSeatsNeeded, Optional.empty(), Optional.empty(), customerEmail1);
		assertEquals(0,ticketService.numSeatsAvailable(Optional.empty()));	
		heldSeats = levelDao.getHeldSeats(seatHold1.getSeatHoldId());
		assertEquals(numSeatsNeeded, heldSeats.size());
		confirmationCode = ticketService.reserveSeats(seatHold1.getSeatHoldId(), customerEmail1);
		reservedSeats = levelDao.getReservedSeats(confirmationCode);
		assertEquals(numSeatsNeeded, reservedSeats.size());
		for(IRowSeat reserved: reservedSeats) {
			assertTrue(4 == reserved.getLevelId());
		}
	}
	
	@Test
	public void testThread0() throws InterruptedException, ExecutionException{
		test(10,10,0,false);
	}
	
	@Test
	public void testThread1() throws InterruptedException, ExecutionException{
		levelDao.resetMockTable();
		test(6250,1,0,false);
	}
	
	@Test
	public void testThread2() throws InterruptedException, ExecutionException{
		levelDao.resetMockTable();
		test(3125,2,0,false);
	}
	
	@Test
	public void testThread3() throws InterruptedException, ExecutionException{
		levelDao.resetMockTable();
		test(2083,3,0,false);
	}
	
	@Test
	public void testThread_HoldAfterExpiraryTime() throws InterruptedException, ExecutionException{
		test(1,30,35000, true);
	}
	
	@Test
	public void testThread_HoldBeforeExpiraryTime() throws InterruptedException, ExecutionException{
		levelDao.resetMockTable();
		test(1,30,25000,false);
	}
	
    private void test(final int threadCount, final int numSeatsPerThread, final int sleepTime, boolean isTestingExpiryTime) throws InterruptedException, ExecutionException {
    	 final String customerEmail = "makeshkumar.r@gmail.com";
    	 Callable<Integer> task = new Callable<Integer>() {
            @Override
            public Integer call() {
            	
            	 SeatHold seatHold = ticketService.findAndHoldSeats(numSeatsPerThread, Optional.of(1), Optional.of(4), customerEmail);
            	 if(seatHold == null) {
            		 return -1;
            	 }
            	 return seatHold.getSeatHoldId();
            }
        };
        List<Callable<Integer>> tasks = Collections.nCopies(threadCount, task);
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        List<Future<Integer>> futures = executorService.invokeAll(tasks);
        List<Integer> resultList = new ArrayList<Integer>(futures.size());
        for (Future<Integer> future : futures) {
            resultList.add(future.get());
        }
        // Validate the IDs
        assertEquals(threadCount, futures.size());
        List<Integer> expectedList = new ArrayList<Integer>(threadCount);
        for (int i = 0; i < threadCount; i++) {
            expectedList.add(i);
        }
        Collections.sort(resultList);

        Thread.sleep(sleepTime);
        
        List<Callable<String>> reserveTasks = new ArrayList<Callable<String>>(resultList.size());
        Callable<String> reserverTask = null; 
        for( Integer holdId:resultList) {
        	reserverTask = new Callable<String>() {
                @Override
                public String call() {
                	
                	 String confirmationCode = ticketService.reserveSeats(holdId, customerEmail);
                	 return confirmationCode;
                }
            };
            reserveTasks.add(reserverTask);
        }
        ExecutorService executorServiceReserver = Executors.newFixedThreadPool(threadCount);
        List<Future<String>> futuresReserver = executorServiceReserver.invokeAll(reserveTasks);
        for (Future<String> future : futuresReserver) {
        	List<IRowSeat> reservedSeats = levelDao.getReservedSeats(future.get());
        	if(!isTestingExpiryTime) {
        		assertEquals(numSeatsPerThread, reservedSeats.size());
        	}else {
        		assertEquals(0, reservedSeats.size());
        	}
        }
    }
    
    private boolean checkConfirmationKeyIsAvailable(List<IRowSeat> reservedSeats) {
    	for( IRowSeat seat:reservedSeats) {
    		if(!seat.getConfirmationCode().isPresent()) {
    			return false;
    		}
    	}
    	return true;
    }
}
