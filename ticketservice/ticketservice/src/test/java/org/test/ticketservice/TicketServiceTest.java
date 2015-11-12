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
		ticketService.reserveSeats(seatHold.getSeatHoldId(), customerEmail);
		List<IRowSeat> reservedSeats = levelDao.getReservedSeats(customerEmail);
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
		ticketService.reserveSeats(seatHold1.getSeatHoldId(), customerEmail);
		List<IRowSeat> reservedSeats = levelDao.getReservedSeats(customerEmail);
		assertEquals(numSeatsNeeded, reservedSeats.size());
		assertTrue(checkConfirmationKeyIsAvailable(reservedSeats));

		SeatHold seatHold2 = ticketService.findAndHoldSeats(numSeatsNeeded, Optional.of(1), Optional.of(2), customerEmail);
		assertEquals(6240,ticketService.numSeatsAvailable(Optional.empty()));	
		heldSeats = levelDao.getHeldSeats(seatHold2.getSeatHoldId());
		assertEquals(numSeatsNeeded, heldSeats.size());
		ticketService.reserveSeats(seatHold2.getSeatHoldId(), customerEmail);
		reservedSeats = levelDao.getReservedSeats(customerEmail);
		assertEquals(numSeatsNeeded * 2, reservedSeats.size());
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
		ticketService.reserveSeats(seatHold1.getSeatHoldId(), customerEmail1);
		List<IRowSeat> reservedSeats = levelDao.getReservedSeats(customerEmail1);
		assertEquals(numSeatsNeeded, reservedSeats.size());
		
		SeatHold seatHold2 = ticketService.findAndHoldSeats(numSeatsNeeded, Optional.of(1), Optional.of(2), customerEmail2);
		assertEquals(6240,ticketService.numSeatsAvailable(Optional.empty()));	
		heldSeats = levelDao.getHeldSeats(seatHold2.getSeatHoldId());
		assertEquals(numSeatsNeeded, heldSeats.size());
		ticketService.reserveSeats(seatHold2.getSeatHoldId(), customerEmail2);
		reservedSeats = levelDao.getReservedSeats(customerEmail2);
		assertEquals(numSeatsNeeded, reservedSeats.size());
		assertTrue(checkConfirmationKeyIsAvailable(reservedSeats));

	}
	
	@Test
	public void test10() throws InterruptedException, ExecutionException{
		test(10,10,0,1);
	}
	
	@Test
	public void testThread1() throws InterruptedException, ExecutionException{
		levelDao.resetMockTable();
		test(6250,1,0,1);
	}
	
	@Test
	public void testThread2() throws InterruptedException, ExecutionException{
		levelDao.resetMockTable();
		test(3125,2,0,1);
	}
	
	@Test
	public void testThread3() throws InterruptedException, ExecutionException{
		levelDao.resetMockTable();
		test(2083,3,0,1);
	}
	
	@Test
	public void testThread_HoldAfterExpiraryTime() throws InterruptedException, ExecutionException{
		test(1,30,35000,0);
	}
	
	@Test
	public void testThread_HoldBeforeExpiraryTime() throws InterruptedException, ExecutionException{
		levelDao.resetMockTable();
		test(1,30,25000,1);
	}
	
    private void test(final int threadCount, final int numSeatsPerThread, final int sleepTime, final int threadFactor) throws InterruptedException, ExecutionException {
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
        // Check for exceptions
        for (Future<Integer> future : futures) {
            // Throws an exception if an exception was thrown by the task.
            resultList.add(future.get());
        }
        // Validate the IDs
        assertEquals(threadCount, futures.size());
        List<Integer> expectedList = new ArrayList<Integer>(threadCount);
        for (int i = 0; i < threadCount; i++) {
            expectedList.add(i);
        }
        Collections.sort(resultList);
//        assertEquals(expectedList, resultList);
        Thread.sleep(sleepTime);
        for( Integer holdId:resultList) {
            ticketService.reserveSeats(holdId, customerEmail);
        }
		List<IRowSeat> reservedSeats = levelDao.getReservedSeats(customerEmail);
		assertEquals(threadCount * numSeatsPerThread * threadFactor, reservedSeats.size());
		assertTrue(checkConfirmationKeyIsAvailable(reservedSeats));
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
