package org.test.ticketservice.vo;

import java.util.Optional;

public class ReservedSeat extends LevelRowSeat {

	private static final long serialVersionUID = 2352226128231609834L;
	
	private Optional<String> customerEmail;
	private Optional<String> confirmationCode;

	public ReservedSeat(int levelId, int rowNumber, int seatNumber, String customerEmail, String confirmationCode) {
		super(levelId, rowNumber, seatNumber);
		this.customerEmail = Optional.of(customerEmail);
		this.confirmationCode = Optional.of(confirmationCode);
	}

	public ReservedSeat(IRowSeat rowSeat, String customerEmail, String confirmationCode){
		this(rowSeat.getLevelId(),rowSeat.getRowNumber(),rowSeat.getSeatNumber(), customerEmail, confirmationCode);
	}
	
	@Override
	public boolean isReserved() {
		return true;
	}

	@Override
	public Optional<String> getConfirmationCode() {
		return confirmationCode;
	}

	@Override
	public Optional<String> getCustomerEmail() {
		return customerEmail;
	}
		
}
