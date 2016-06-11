package monopoly.ws.cell;

import monopoly.ws.player.Player;

public abstract class Cell {
	private final String name;
	private final int position;

	public Cell(String name, int position){
		this.name = name;
		this.position = position;
	}

	public String getName() {
		return name;
	}

	public int getPosition() {
		return position;
	}
        
        public abstract void playAction(Player currentPlayer);


}
