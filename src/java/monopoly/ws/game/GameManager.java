/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package monopoly.ws.game;

import java.util.ArrayList;
import java.util.List;
import monopoly.ws.player.Player;
import ws.monopoly.DuplicateGameName;
import ws.monopoly.DuplicateGameName_Exception;
import ws.monopoly.GameDetails;
import ws.monopoly.GameDoesNotExists;
import ws.monopoly.GameDoesNotExists_Exception;
import ws.monopoly.GameStatus;
import ws.monopoly.InvalidParameters;
import ws.monopoly.InvalidParameters_Exception;
import ws.monopoly.PlayerDetails;

/**
 *
 * @author ShiloMangam
 */
public class GameManager {

    private GameModel gameModel;

    public GameManager() {
        this.gameModel = new GameModel();
    }

    public void addGame(MonopolyGame gameToAdd) throws DuplicateGameName_Exception {
        if (gameModel.isGameExist(gameToAdd)) {
            throw new DuplicateGameName_Exception("Duplicated game name", new DuplicateGameName());
        }
        gameModel.addGame(gameToAdd);

    }

    public MonopolyGame getGameByGameName(String gameName) throws GameDoesNotExists_Exception {
        for (MonopolyGame game : gameModel.getGameModel()) {
            if (gameName.equals(game.getGameName())) {
                return game;
            }
        }
        throw new GameDoesNotExists_Exception(gameName + " Doesn't exist", new GameDoesNotExists());
    }

    public int addPlayerToGame(String gameName, String playerName) throws GameDoesNotExists_Exception {
        MonopolyGame game = this.getGameByGameName(gameName);
        if (game == null) {
            throw new GameDoesNotExists_Exception(gameName + " Doesn't exist!", new GameDoesNotExists());
        }
        int playerID = game.addPlayerToGame(playerName);
        return playerID;
    }

    public boolean shouldGameBeLaunched(String gameName) throws GameDoesNotExists_Exception {
        return this.getGameByGameName(gameName).isReadyToGo();
    }

    public GameDetails getGameDetailsByGameName(String gameName) throws GameDoesNotExists_Exception {
        MonopolyGame game = this.getGameByGameName(gameName);
        if (game == null) {
            throw new GameDoesNotExists_Exception(gameName + " Doesn't exist!", new GameDoesNotExists());
        }
        return game.getGameDetails();
    }

    public Player getPlayerById(int playerId) throws GameDoesNotExists_Exception {
        for (MonopolyGame game : this.gameModel.getGameModel()){
             Player player = game.getPlayerById(playerId);
             if (player != null)
                 return player;
        }
        throw new GameDoesNotExists_Exception("Player ID: " + playerId + " is not in game!", new GameDoesNotExists());
           
        
    }

    public List<PlayerDetails> getAllPlayerDetailsByGameName(String gameName) throws GameDoesNotExists_Exception {
       return this.getGameByGameName(gameName).getAllPlayersDetails();
    }

    public List<String> getAllWaitingGames() {
        List<String> waitingGames = new ArrayList<>();
        for (MonopolyGame game : this.gameModel.getGameModel()){
            if (game.getGameDetails().getStatus()==GameStatus.WAITING){
                waitingGames.add(game.getGameName());
            }
        }
        return waitingGames;
        
    }

    public MonopolyGame getGameByPlayerId(int playerId) throws InvalidParameters_Exception {
        for (MonopolyGame game : this.gameModel.getGameModel()){
             if (game.isPlayerInThisGameById(playerId))
                 return game;
        }
        throw new InvalidParameters_Exception("We couldnt find this player", new InvalidParameters());
    }

}
