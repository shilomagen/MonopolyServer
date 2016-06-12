package monopoly.ws.player;

import java.util.ArrayList;
import monopoly.ws.data.Card;
import monopoly.ws.data.City;
import monopoly.ws.data.Transportation;
import monopoly.ws.data.Utility;
import monopoly.ws.game.MonopolyGame;
import ws.monopoly.PlayerDetails;


public interface Player {
	public void setPosition(int position);
	public boolean canPlay();
	public boolean isBankruptOrRetired();
	public boolean isInJail();
	public void setInJail(boolean val);
	public boolean isParked();
	public void setIsParked(boolean val);
	public int getPosition();
	public String getPlayerName();
	public void setMoney(int money);
	public int getMoney();
	public ArrayList<City> getPlayerCities();
	public void setPlayerCities(ArrayList<City> playerCities);
	public ArrayList<Transportation> getPlayerTrans();
	public void setPlayerTrans(ArrayList<Transportation> playerTrans);
	public ArrayList<Utility> getPlayerUtil();
	public void setPlayerUtil(ArrayList<Utility> playerUtil);
//	public boolean getAnswer();
	public void setIsBankrupt(boolean b);
	public boolean isHasFreeJailCard();
	public void setHasFreeJailCard(boolean hasFreeJailCard);
	public void setJailFreeCard(Card freeJailCard);
	public Card getJailFreeCard();
	public ArrayList<Transportation> getTransportation();
	public ArrayList<Utility> getUtilites();
	public PlayerData getData();
	public int getLastFine();
	public void setLastFine(int lastFine, Player PlayerPaidTo);
	public String getPaidPlayerName();
	public void releasePlayerAssets();
        public PlayerDetails getPlayerDetails();
        public MonopolyGame getGame();
        public void setGame(MonopolyGame playerGame);
}
