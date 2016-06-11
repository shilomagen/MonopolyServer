package monopoly.ws.cell;

import monopoly.ws.data.Card;
import monopoly.ws.player.Player;
import static monopoly.ws.utility.CellTypes.ON_SURPRISE;


public class SupriseCardCell extends Cell{
	private Card data;
	public SupriseCardCell(String name, int position){
		super(name, position);
		this.data = null;
	}

	@Override
	public void playAction(Player currentPlayer) {
		currentPlayer.getGame().addEventToEngine(ON_SURPRISE);
		
	}
	
	
}
