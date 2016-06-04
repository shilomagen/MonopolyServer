package monopoly.ws.cell;

import monopoly.ws.data.Card;



public class WarrantCardCell extends Cell {
	private Card data;
	
	public WarrantCardCell(String name, int position){
		super(name, position);
		this.data = null;
	}

//	@Override
//	public void playAction(Player currentPlayer) {
//		GameController.warrantCardProcedure(this.data, currentPlayer);
//		
//	}
}
