package monopoly.ws.player.exceptions;

public class NoHumanPlayerException extends RuntimeException{

    public NoHumanPlayerException() {
	super ("You must have at least one human player");
    }
}
