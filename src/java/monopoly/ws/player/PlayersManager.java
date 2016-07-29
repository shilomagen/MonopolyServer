package monopoly.ws.player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import javafx.application.Platform;

import javafx.scene.image.Image;
import monopoly.ws.game.GameEngine;
import monopoly.ws.game.MonopolyGame;
import monopoly.ws.player.exceptions.DuplicateNameException;
import monopoly.ws.player.exceptions.EmptyNameException;
import monopoly.ws.player.exceptions.NoHumanPlayerException;
import monopoly.ws.player.exceptions.NullPictureException;
import monopoly.ws.utility.EventTypes;
import monopoly.ws.utility.GameConstants;
import ws.monopoly.PlayerStatus;

public class PlayersManager {

    private final PlayerModel playersModel;
    private int humanPlayers;
    private int pcPlayers;
    int currentPlayer = 0;
    private MonopolyGame game;

    public PlayersManager(int human, int pc, MonopolyGame _game) {
        playersModel = new PlayerModel();
        this.humanPlayers = human;
        this.pcPlayers = pc;
        this.game = _game;
    }

    public PlayerData addPlayer(String name, boolean isHuman, String gender, Image image, Image icon, MonopolyGame game)
            throws DuplicateNameException, EmptyNameException, NullPictureException {
        if (name == null || name.isEmpty()) {
            throw new EmptyNameException();
        }
        if (image == null) {
            //throw new NullPictureException();
        }
        PlayerData newPlayerData = new PlayerData(name, isHuman, gender, image, icon);
        if (playersModel.isPlayerExists(newPlayerData)) {
            throw new DuplicateNameException();
        } else if (newPlayerData.isHuman()) {
            HumanPlayer newPlayer = new HumanPlayer(newPlayerData);
            newPlayer.setGame(game);
            playersModel.addPlayer(newPlayer);
        } else {
            PcPlayer newPlayer = new PcPlayer(newPlayerData);
            newPlayer.setGame(game);
            playersModel.addPlayer(newPlayer);
        }

        return newPlayerData;
    }

    public int getCurrentPlayer() {
        return this.currentPlayer;
    }

    public synchronized void nextPlayer() {
        this.currentPlayer++;
        this.currentPlayer %= this.getPlayers().size();
        this.game.addEventToEngine(EventTypes.PLAY_TURN);

    }

    public Collection<Player> getPlayers() {
        ArrayList<Player> sortedPlayersList = new ArrayList<>(playersModel.getPlayers());
        Collections.sort(sortedPlayersList, new PlayerComparator());
        return sortedPlayersList;
    }

    public void createPCPlayers(MonopolyGame game) {
        for (int i = 0; i < this.pcPlayers; ++i) {
            this.addPlayer(GameConstants.PC_PLAYER_NAME + "_" + i, false, "PC", null, null, game);
        }
    }

    public void setPlayersToActive() {
        Collection<Player> players = playersModel.getPlayers();
        for (Player player : players) {
            player.getPlayerDetails().setStatus(PlayerStatus.ACTIVE);
        }
    }

    static class PlayerComparator implements Comparator<Player> {

        @Override
        public int compare(Player o1, Player o2) {
            return o1.getData().getName().compareTo(o2.getData().getName());
        }
    }

    public boolean isPlayersFullyLoaded() {
        return true;
    }

    public boolean isThereHumanPlayer() throws NoHumanPlayerException {
        Collection<Player> players = playersModel.getPlayers();
        for (Player player : players) {
            if (player.getData().isHuman()) {
                return true;
            }
        }
        throw new NoHumanPlayerException();
    }

    public int howManyActivePlayers() {
        int counter = 0;
        for (Player player : this.playersModel.getPlayers()) {
            if (!player.isBankruptOrRetired()) {
                counter++;
            }
        }
        return counter;
    }

    public Player getWinnerPlayer() {
        Player winnerPlayer = null;
        for (Player player : this.getPlayers()) {
            if (!player.isBankruptOrRetired()) {
                winnerPlayer = player;
            }
        }
        return winnerPlayer;
    }
}
