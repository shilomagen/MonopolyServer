/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package monopoly.ws;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import monopoly.ws.game.MonopolyGame;
import javax.jws.WebService;
import monopoly.ws.game.GameManager;
import monopoly.ws.player.Player;
import monopoly.ws.utility.EventTypes;
import static monopoly.ws.utility.EventTypes.PLAYER_RESIGN;
import monopoly.ws.utility.GameConstants;
import ws.monopoly.Event;
import ws.monopoly.EventType;
import ws.monopoly.GameDoesNotExists_Exception;
import ws.monopoly.InvalidParameters;
import ws.monopoly.InvalidParameters_Exception;

/**
 *
 * @author ShiloMangam
 */
@WebService(serviceName = "MonopolyWebServiceService", portName = "MonopolyWebServicePort", endpointInterface = "ws.monopoly.MonopolyWebService", targetNamespace = "http://monopoly.ws/", wsdlLocation = "WEB-INF/wsdl/monopolyWS/MonopolyWebServiceService.wsdl")
public class monopolyWS {

    private GameManager gameManager;
    private static int playerID = 0;
    

    //ctor
    public monopolyWS() {
        this.gameManager = new GameManager();
        
        
    }

    /**
     * @return the playerID
     */
    public static int getPlayerID() {
        incPlayerID();
        return playerID;

    }

    /**
     * @param aPlayerID the playerID to set
     */
    public static void incPlayerID() {
        playerID++;
    }

    public java.util.List<ws.monopoly.Event> getEvents(int eventId, int playerId) throws ws.monopoly.InvalidParameters_Exception {
        MonopolyGame game = this.gameManager.getGameByPlayerId(playerId);
        return game.getEventManager().getEventListFromEventID(eventId);
    }

    public java.util.List<ws.monopoly.PlayerDetails> getPlayersDetails(java.lang.String gameName) throws ws.monopoly.GameDoesNotExists_Exception {
        return this.gameManager.getAllPlayerDetailsByGameName(gameName);
    }

    public java.util.List<java.lang.String> getWaitingGames() {
        return this.gameManager.getAllWaitingGames();
    }

    public void createGame(int computerizedPlayers, int humanPlayers, java.lang.String name) throws ws.monopoly.InvalidParameters_Exception, ws.monopoly.DuplicateGameName_Exception {
        if (!this.goodParamaters(computerizedPlayers, humanPlayers)) {
            throw new ws.monopoly.InvalidParameters_Exception("Invalid Paramaters", new InvalidParameters());
        }
        this.gameManager.addGame(new MonopolyGame(name, humanPlayers, computerizedPlayers));

    }

    public void resign(int playerId) throws ws.monopoly.InvalidParameters_Exception {
        Player player = this.gameManager.getPlayerById(playerId);
        player.getGame().addEventToEngine(PLAYER_RESIGN);
    }

    public int joinGame(java.lang.String gameName, java.lang.String playerName) throws ws.monopoly.InvalidParameters_Exception, ws.monopoly.GameDoesNotExists_Exception {
        int joinedPlayerID = this.gameManager.addPlayerToGame(gameName, playerName);
        if (this.gameManager.shouldGameBeLaunched(gameName)) {
            this.launchGame(gameName);
        }
        return joinedPlayerID;

    }

    public java.lang.String getBoardXML() {
        return this.readFileToString(GameConstants.XML_FILE_NAME);
    }

    public ws.monopoly.PlayerDetails getPlayerDetails(int playerId) throws ws.monopoly.GameDoesNotExists_Exception, ws.monopoly.InvalidParameters_Exception {
        return this.gameManager.getPlayerById(playerId).getPlayerDetails();

    }

    public java.lang.String getBoardSchema() {
        return this.readFileToString(GameConstants.SCHEME_FILE_NAME);
    }

    public ws.monopoly.GameDetails getGameDetails(java.lang.String gameName) throws ws.monopoly.GameDoesNotExists_Exception {
        return this.gameManager.getGameDetailsByGameName(gameName);
    }

    public void buy(int arg0, int arg1, boolean arg2) throws ws.monopoly.InvalidParameters_Exception {
        Player player = this.gameManager.getPlayerById(arg0);
        Event event = player.getGame().getEventManager().getEventById(arg1);
        MonopolyGame game = player.getGame();
        game.addEventToEngine(EventTypes.KILL_TIMER);
        if (arg2){
            if (event.getType()==EventType.PROPMPT_PLAYER_TO_BY_HOUSE){
                game.addEventToEngine(EventTypes.PLAYER_WANTS_TO_BUY_HOUSE);
            }else {
                game.addEventToEngine(EventTypes.PLAYER_WANTS_TO_BUY_BUYABLE);
            }
        } else{
            game.addEventToEngine(EventTypes.PLAYER_DIDNT_WANT_TO_BUY);
        }
        
        
        
        
        
    }

    private boolean goodParamaters(int computerizedPlayers, int humanPlayers) throws InvalidParameters_Exception {
        // we will check few things:
        //more than 2 players and less than 6
        boolean isPositive = (humanPlayers > 0 && computerizedPlayers > 0);
        boolean isAtLeastOneHumanPlayer = (humanPlayers >= 1);
        boolean isRangeOK = ((humanPlayers + computerizedPlayers) >= 2 && (humanPlayers + computerizedPlayers) <= 6);

        return isPositive && isAtLeastOneHumanPlayer && isRangeOK;

    }

    private void launchGame(String gameName) {
        try {
            MonopolyGame game = this.gameManager.getGameByGameName(gameName);
            Event startEvent = new Event();
            startEvent.setType(EventType.GAME_START);
            game.getEventManager().addEvent(startEvent);
            game.startRollingTheGame();
        } catch (GameDoesNotExists_Exception ex) {
            System.out.println("error launching the game!");
        }

    }

    private String readFileToString(String fileName) {
        String fileString = "";
        try {
            URL url = getClass().getResource("resources/" + fileName);
            Path schemePath = Paths.get(URI.create(url.toString()));
            Charset charset = Charset.forName("UTF-8");
            List<String> stringList = Files.readAllLines(schemePath, charset);
            fileString = String.join("\n", stringList);

        } catch (IOException ex) {
            System.out.println("Couldnt open file");
        }
        
        return fileString;
    }

}
