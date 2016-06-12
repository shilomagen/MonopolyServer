/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package monopoly.ws.utility;

import java.util.TimerTask;
import monopoly.ws.game.GameEngine;

/**
 *
 * @author ShiloMangam
 */
public class EventTimer extends TimerTask {
    private GameEngine gameEngine;

    public EventTimer(GameEngine engine) {
        this.gameEngine = engine;
    }

    @Override
    public void run() {
        System.out.println("Starting countdown");
        this.gameEngine.addEventToInternal(EventTypes.PLAYER_RESIGN);

    }

}
