package org.test.ticketservice.vo;

import java.io.Serializable;
import java.util.List;

public class SeatHold implements Serializable{

	private static final long serialVersionUID = 3888840549041404679L;

	private int seatHoldId;
	private String customerEmail;
	private List<IRowSeat> heldSeats;
	
	public SeatHold(int seatHoldId, List<IRowSeat> heldSeats, String customerEmail) {
		this.seatHoldId = seatHoldId;
		this.heldSeats = heldSeats;
		this.customerEmail = customerEmail;
	}

	public String getCustomerEmail() {
		return customerEmail;
	}

	public int getSeatHoldId() {
		return seatHoldId;
	}

	public List<IRowSeat> getHeldSeats() {
		return heldSeats;
	}
}
