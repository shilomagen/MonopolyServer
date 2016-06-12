package monopoly.ws.cell;

import monopoly.ws.player.Player;
import static monopoly.ws.utility.EventTypes.ON_START;

public class StartCell extends Cell {
	public StartCell(String name, int position){
		super(name, position);
	}
	@Override
	public void playAction(Player currentPlayer) {
		currentPlayer.getGame().addEventToEngine(ON_START);
		
	}
}
