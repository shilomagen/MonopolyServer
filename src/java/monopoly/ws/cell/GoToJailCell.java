package monopoly.ws.cell;

import monopoly.ws.player.Player;
import static monopoly.ws.utility.EventTypes.ON_GO_TO_JAIL;

public class GoToJailCell extends Cell {

    public GoToJailCell(String name, int position) {
        super(name, position);
    }

    @Override
    public void playAction(Player currentPlayer) {
        currentPlayer.getGame().addEventToEngine(ON_GO_TO_JAIL);
    }

}
