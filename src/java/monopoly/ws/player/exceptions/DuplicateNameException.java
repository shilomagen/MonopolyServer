package monopoly.ws.player.exceptions;

public class DuplicateNameException extends RuntimeException{

    public DuplicateNameException() {
	super ("Name already exists");
    }
}
