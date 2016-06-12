package monopoly.ws.cell;

import monopoly.ws.data.Card;
import monopoly.ws.player.Player;
import static monopoly.ws.utility.EventTypes.ON_SUPRISE;



public class SupriseCardCell extends Cell{
	private Card data;
	public SupriseCardCell(String name, int position){
		super(name, position);
		this.data = null;
	}

	@Override
	public void playAction(Player currentPlayer) {
		currentPlayer.getGame().addEventToEngine(ON_SUPRISE);
		
	}
	
	
}
