package monopoly.ws.data;

import javafx.application.Platform;
import monopoly.ws.player.Player;
import monopoly.ws.utility.EventTypes;

public class GetOutOfJailCard extends Card {

    public GetOutOfJailCard(String cardText, int cardCode) {
        super(cardText, cardCode);
    }

    public String toString() {
        return cardText;
    }

    @Override
    public void surpriseAction(Player currentPlayer) {

        currentPlayer.getGame().addEventToEngine(EventTypes.GET_OUT_OF_JAIL_CARD);
    }

    @Override
    public void warrantAction(Player currentPlayer) {
    }

}
