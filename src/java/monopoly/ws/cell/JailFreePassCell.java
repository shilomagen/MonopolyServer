package monopoly.ws.cell;

import monopoly.ws.player.Player;
import static monopoly.ws.utility.EventTypes.ON_JAIL_FREE_PASS;


public class JailFreePassCell extends Cell {
	public JailFreePassCell(String name, int position){
		super(name, position);
	}
	@Override
	public void playAction(Player currentPlayer) {
		currentPlayer.getGame().addEventToEngine(ON_JAIL_FREE_PASS);
	}
}
