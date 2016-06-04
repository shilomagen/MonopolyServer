package monopoly.ws.cell;

import monopoly.ws.data.Card;


public class SupriseCardCell extends Cell{
	private Card data;
	public SupriseCardCell(String name, int position){
		super(name, position);
		this.data = null;
	}

//	@Override
//	public void playAction(Player currentPlayer) {
//		GameController.surpriseCardProcedure(this.data, currentPlayer);
//		
//	}
	
	
}
