package monopoly.ws.data;

import monopoly.ws.game.MonopolyGame;
import monopoly.ws.player.Player;
import monopoly.ws.utility.EventTypes;



public class GoToCard extends Card {
	String cellToGo;

	public GoToCard(String cardText, int cardCode, String cellToGo) {
		super(cardText, cardCode);
		this.cellToGo = cellToGo;
	}

	@Override
	public void surpriseAction(Player currentPlayer) {
            MonopolyGame game = currentPlayer.getGame();
		if (this.cellToGo.equals("START")) {
			game.addEventToEngine(EventTypes.RETURN_CARD_TO_SURPRISE_DECK);
			game.addEventToEngine(EventTypes.GO_TO_START_CELL);

		} else if (this.cellToGo.equals("NEXT_SURPRISE")) {
			game.addEventToEngine(EventTypes.RETURN_CARD_TO_SURPRISE_DECK);
			game.addEventToEngine(EventTypes.GO_TO_NEXT_SURPRISE);

		}
	}

	@Override
	public void warrantAction(Player currentPlayer) {
            MonopolyGame game = currentPlayer.getGame();
		if (this.cellToGo.equals("JAIL")) {
			game.addEventToEngine(EventTypes.RETURN_CARD_TO_WARRANT_DECK);
			game.addEventToEngine(EventTypes.ON_GO_TO_JAIL);

		} else if (this.cellToGo.equals("NEXT_WARRANT")) {
			game.addEventToEngine(EventTypes.RETURN_CARD_TO_WARRANT_DECK);
			game.addEventToEngine(EventTypes.GO_TO_NEXT_WARRANT);
			

		}
	}

	public String toString() {
		return String.format(this.cardText, this.cellToGo);
	}

}
