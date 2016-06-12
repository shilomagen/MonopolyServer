package monopoly.ws.cell;

import monopoly.ws.data.Utility;
import monopoly.ws.player.Player;
import static monopoly.ws.utility.EventTypes.ON_UTILITY;



public class UtilityCell extends Cell implements Buyable{
	private Player owner;
	private boolean hasOwner;
	private Utility data;
	
	public UtilityCell(String name, int position, Utility data) {
		super(name, position);
		this.owner = null;
		this.hasOwner = false;
		this.data = data;
		data.setUtilityCell(this);
		
	}

	public Player getOwner() {
		return owner;
	}
	
	@Override
	public void setHasOwner(boolean hasOwner) {
		this.hasOwner = hasOwner;
	}
	public Utility getData() {
		return data;
	}
	public void setData(Utility data) {
		this.data = data;
	}
	@Override
	public int getCost() {
		return this.data.getCost();
	}
	@Override
	public void setOwner(Player owner) {
		this.owner = owner;
		
	}

	@Override
	public boolean isHasOwner() {
		return this.hasOwner;
	}

    @Override
    public void playAction(Player currentPlayer) {
        currentPlayer.getGame().addEventToEngine(ON_UTILITY);
    }
}
