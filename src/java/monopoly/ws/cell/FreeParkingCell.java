package monopoly.ws.cell;

import monopoly.ws.player.Player;
import static monopoly.ws.utility.EventTypes.ON_FREE_PARKING;

public class FreeParkingCell extends Cell {

	public FreeParkingCell(String name, int position){
		super(name, position);
	}
	@Override
	public void playAction(Player currentPlayer) {
		currentPlayer.getGame().addEventToEngine(ON_FREE_PARKING);
		
	}
	

}
