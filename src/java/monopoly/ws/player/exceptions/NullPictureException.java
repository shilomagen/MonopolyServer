package monopoly.ws.player.exceptions;

public class NullPictureException extends RuntimeException{
	public NullPictureException(){
		super("Please Select a Photo");
	}

}
