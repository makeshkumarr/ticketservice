package org.test.ticketservice.vo;

import java.io.Serializable;
import java.util.Optional;

public interface IRowSeat extends Serializable{

	public default boolean isReserved() {
		return false;
	}
	
	public default boolean isHeld() {
		return false;
	}
	
	public default int getSeatHoldId() {
		return -1;
	}
	
	public default Optional<String> getCustomerEmail() {
		String customerEmail = null;
		return Optional.ofNullable(customerEmail);
	}
	
	public default Optional<String> getConfirmationCode() {
		String confirmationCode = null;
		return Optional.ofNullable(confirmationCode);
	}
	
	public int getLevelId();

	public int getRowNumber();

	public int getSeatNumber();
}
