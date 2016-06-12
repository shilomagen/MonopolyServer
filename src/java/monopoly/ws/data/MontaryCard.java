package monopoly.ws.data;



import monopoly.ws.game.MonopolyGame;
import monopoly.ws.player.Player;
import monopoly.ws.utility.EventTypes;

public class MontaryCard extends Card {
	private int sum;

	public MontaryCard(String cardText, int cardCode, int sum) {
		super(cardText, cardCode);
		this.sum = sum;
	}

	public String toString() {
		return String.format(this.cardText, sum);
	}

	@Override
	public void surpriseAction(Player currentPlayer) {
             MonopolyGame game = currentPlayer.getGame();
		if (this.cardCode == 1) {
			game.addEventToEngine(EventTypes.RETURN_CARD_TO_SURPRISE_DECK);
			game.addEventToEngine(EventTypes.TAKE_MONEY_FROM_ALL_PLAYERS);

		} else if (this.cardCode == 2) {
			game.addEventToEngine(EventTypes.RETURN_CARD_TO_SURPRISE_DECK);
			game.addEventToEngine(EventTypes.TAKE_MONEY_FROM_JACKPOT);
		}
	}

	@Override
	public void warrantAction(Player currentPlayer) {
             MonopolyGame game = currentPlayer.getGame();
		if (this.cardCode == 1) {
			game.addEventToEngine(EventTypes.RETURN_CARD_TO_WARRANT_DECK);
			game.addEventToEngine(EventTypes.PAY_TO_ALL_PLAYERS);

		} else if (this.cardCode == 2) {
			game.addEventToEngine(EventTypes.RETURN_CARD_TO_WARRANT_DECK);
			game.addEventToEngine(EventTypes.PAY_TO_JACKPOT);
		}
	}

	public int getSum() {
		return this.sum;
	}

}
