package monopoly.ws.cell;

import monopoly.ws.player.Player;



public interface Buyable {
	public Player getOwner();
	public int getCost();
	public String getName();
	public void setOwner(Player owner);
	public boolean isHasOwner();
	public void setHasOwner(boolean hasOwner);
}
