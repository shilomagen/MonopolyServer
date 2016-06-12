/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package monopoly.ws.game;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author ShiloMangam
 */
public class GameModel {
    private HashSet<MonopolyGame> gameModel;
    
    public GameModel(){
        this.gameModel = new HashSet<>();
    }
    
    public boolean isGameExist(MonopolyGame monopolyGame){
        for (MonopolyGame game : this.gameModel){
            if (monopolyGame.getGameName().equals(game.getGameName()))
                return true;
        }
        return false;
    }
    
    public void addGame(MonopolyGame gameToAdd){
        this.gameModel.add(gameToAdd);
        System.out.println("Game " + gameToAdd.getGameName() + " successfully created on server");
    }

    /**
     * @return the gameModel
     */
    public HashSet<MonopolyGame> getGameModel() {
        return this.gameModel ;
    }
}
