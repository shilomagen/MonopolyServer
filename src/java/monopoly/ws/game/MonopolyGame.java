/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package monopoly.ws.game;

import java.util.ArrayList;
import java.util.List;
import monopoly.ws.events.EventManager;
import monopoly.ws.player.Player;
import monopoly.ws.player.PlayerData;
import monopoly.ws.player.PlayersManager;
import monopoly.ws.player.exceptions.DuplicateNameException;
import monopoly.ws.player.exceptions.EmptyNameException;
import monopoly.ws.player.exceptions.NullPictureException;
import ws.monopoly.GameDetails;
import ws.monopoly.GameStatus;
import ws.monopoly.PlayerDetails;

/**
 *
 * @author ShiloMangam
 */
public class MonopolyGame {
    private final String gameName;
    private PlayersManager playersManager;
    private int humanPlayers;
    private int pcPlayers;
    private boolean isWait;
    private int playersNum;
    private GameDetails gameDetails;
    private EventManager eventManager;
    
    
    
    public MonopolyGame(String name, int hmnPly, int pcPly){
        this.gameName = name;
        this.humanPlayers = hmnPly;
        this.pcPlayers = pcPly;
        this.playersManager = new PlayersManager(hmnPly, pcPly);
        this.playersManager.createPCPlayers();
        this.isWait = true;
        this.playersNum = hmnPly + pcPly;
        this.gameDetails = new GameDetails();
        this.setGameDetails();
        this.eventManager = new EventManager();
    }

    /**
     * @return the gameName
     */
    public String getGameName() {
        return gameName;
    }

    public int addPlayerToGame(String playerName) throws DuplicateNameException, EmptyNameException, NullPictureException {
        if (this.gameDetails.getStatus()==GameStatus.WAITING){
            PlayerData playerData = this.playersManager.addPlayer(playerName, true, "Male", null, null);
            this.gameDetails.setJoinedHumanPlayers(this.gameDetails.getJoinedHumanPlayers()+1);
            if (this.gameDetails.getHumanPlayers()==this.gameDetails.getJoinedHumanPlayers())
                this.gameDetails.setStatus(GameStatus.ACTIVE);
            return playerData.getID();
        }
        // the game already started
        return 0;
    }

   

    public boolean isReadyToGo() {
        return this.gameDetails.getStatus()==GameStatus.ACTIVE;
 
    }

    private void setGameDetails() {
        this.gameDetails.setComputerizedPlayers(this.pcPlayers);
        this.gameDetails.setHumanPlayers(this.humanPlayers);
        this.gameDetails.setJoinedHumanPlayers(0);
        this.gameDetails.setName(this.gameName);
        this.gameDetails.setStatus(GameStatus.WAITING);
    }
    
    public GameDetails getGameDetails(){
        return this.gameDetails;
    }

    public Player getPlayerById(int playerId) {
        for (Player player : this.playersManager.getPlayers()){
            if (player.getData().getID()==playerId)
                return player;
        }
        return null;
    }

    public List<PlayerDetails> getAllPlayersDetails() {
        List<PlayerDetails> playerDetailsList = new ArrayList<>();
        for (Player player : this.playersManager.getPlayers()){
            playerDetailsList.add(player.getPlayerDetails());
        }
        return playerDetailsList;
    }
    
    public EventManager getEventManager(){
        return this.eventManager;
    }

    public boolean isPlayerInThisGameById(int playerId) {
        for (Player player : this.playersManager.getPlayers()){
            if (player.getData().getID()==playerId){
                return true;
            }
        }
        return false;
    }


    
    
   
}
