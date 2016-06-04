package monopoly.ws.cell;

import monopoly.ws.data.Transportation;
import monopoly.ws.player.Player;



public class TransportationCell extends Cell implements Buyable{
	private Player owner;
	private boolean hasOwner;
	private Transportation data;
	
	
	
	public TransportationCell(String name, int position, Transportation data) {
		super(name, position);
		this.owner = null;
		this.hasOwner = false;
		this.data = data;
		data.setTransportationCell(this);
		
	}
	
	public Player getOwner() {
		return owner;
	}
	@Override
	public void setHasOwner(boolean hasOwner) {
		this.hasOwner = hasOwner;
	}
	public Transportation getData(){
		return this.data;
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
	
	
	
	
}
