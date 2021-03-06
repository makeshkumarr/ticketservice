package org.test.ticketservice.vo;

import java.time.LocalDateTime;

public class HeldSeat extends LevelRowSeat {

	private static final long serialVersionUID = 496461809585844020L;

	private int seatHoldId;
	private LocalDateTime seatHeldTime;
	
	public HeldSeat(int levelId, int rowNumber, int seatNumber, int seatHoldId) {
		super(levelId, rowNumber, seatNumber);
		this.seatHoldId = seatHoldId;
		seatHeldTime = LocalDateTime.now(); 
	}
	
	public HeldSeat(IRowSeat rowSeat, int seatHoldId){
		this(rowSeat.getLevelId(),rowSeat.getRowNumber(),rowSeat.getSeatNumber(), seatHoldId);
	}

	@Override
	public boolean isHeld() {
		if(LocalDateTime.now().compareTo(seatHeldTime.plusSeconds(30))<0){
			return true;
		}
		seatHoldId = -1;
		return false;
	}

	@Override
	public int getSeatHoldId() {
		return seatHoldId;
	}

}
