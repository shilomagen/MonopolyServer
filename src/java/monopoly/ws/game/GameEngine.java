/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package monopoly.ws.game;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Timer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import monopoly.ws.cell.Buyable;
import monopoly.ws.cell.Cell;
import monopoly.ws.cell.CellModel;
import monopoly.ws.cell.PropertyCell;
import monopoly.ws.cell.TransportationCell;
import monopoly.ws.cell.UtilityCell;
import monopoly.ws.data.Assets;
import monopoly.ws.data.Card;
import monopoly.ws.data.City;
import monopoly.ws.data.CountryGame;
import monopoly.ws.data.MontaryCard;
import monopoly.ws.engine.InitiateGame;
import monopoly.ws.events.EventManager;
import monopoly.ws.events.InternalEvent;
import monopoly.ws.player.Player;
import monopoly.ws.player.PlayersManager;
import monopoly.ws.utility.EventMessages;
import static monopoly.ws.utility.EventMessages.GET_OUT_OF_JAIL_CARD_MESSAGE;
import static monopoly.ws.utility.EventMessages.GO_TO_START_MESSAGE;
import static monopoly.ws.utility.EventMessages.LANDED_START_SQUARE_MESSAGE;
import static monopoly.ws.utility.EventMessages.MOVE_MESSAGE;
import static monopoly.ws.utility.EventMessages.PARKED_MESSAGE;
import static monopoly.ws.utility.EventMessages.PASSED_START_SQUARE_MESSAGE;
import static monopoly.ws.utility.EventMessages.PLAYER_GOT_OUT_OF_JAIL_MESSAGE;
import static monopoly.ws.utility.EventMessages.PLAYER_GO_TO_JAIL_MESSAGE;
import static monopoly.ws.utility.EventMessages.PLAYER_IN_JAIL_MESSAGE;
import static monopoly.ws.utility.EventMessages.PLAYER_LOST_MESSAGE;
import static monopoly.ws.utility.EventMessages.PLAYER_PAID_JACKPOT_MESSAGE;
import static monopoly.ws.utility.EventMessages.PLAYER_PAID_TO_PLAYER_MESSAGE;
import static monopoly.ws.utility.EventMessages.PLAYER_RETIRED_MESSAGE;
import static monopoly.ws.utility.EventMessages.ROLL_MESSAGE;
import static monopoly.ws.utility.EventMessages.TAKE_MONEY_FROM_JACKPOT_MESSAGE;
import static monopoly.ws.utility.EventMessages.WINNER_MESSAGE;
import monopoly.ws.utility.EventTimer;
import monopoly.ws.utility.EventTypes;
import static monopoly.ws.utility.EventTypes.PLAYER_WANTS_TO_USE_CARD;
import monopoly.ws.utility.GameConstants;
import static monopoly.ws.utility.GameConstants.TIME_OUT;
import static monopoly.ws.utility.GameConstants.TOTAL_CELL;
import monopoly.ws.utility.PairOfDice;
import ws.monopoly.Event;
import ws.monopoly.EventType;
import ws.monopoly.PlayerStatus;
import ws.monopoly.PlayerType;

/**
 *
 * @author ShiloMangam
 */
public class GameEngine {

    private MonopolyGame game;
    private ObservableList<InternalEvent> internalEventList;
    private PlayersManager playerManager;
    private Player currentPlayer;
    private CellModel cellModel;
    private InitiateGame initiator;
    private LinkedList<Card> surpriseDeck;
    private LinkedList<Card> warrantDeck;
    private Card currentCard;
    private EventManager eventManager;
    private EventTimer eventTimer;
    private Timer timer;
    private ExecutorService taskExecutor;

    public GameEngine(MonopolyGame _game) {
        this.game = _game;
        this.internalEventList = FXCollections.observableArrayList();
        this.cellModel = this.game.getGameBoard().getCellModel();
        this.timer = new Timer();

        taskExecutor = Executors.newFixedThreadPool(3);

    }

    public void startObserv() {
        this.internalEventList = FXCollections.observableArrayList();
        internalEventList.addListener(new ListChangeListener<InternalEvent>() {
            @Override
            public void onChanged(ListChangeListener.Change c) {
                while (c.next()) {
                    if (c.wasAdded()) {
                        InternalEvent event = internalEventList.get(internalEventList.size() - 1);
                        System.out.println(event.getType());
                        eventHandler(event);
                    }
                }
            }
        });

    }

    public void eventHandler(InternalEvent event) {
        int currentPlayerIndex = this.playerManager.getCurrentPlayer();
        this.currentPlayer = ((ArrayList<Player>) this.playerManager.getPlayers())
                .get(currentPlayerIndex);
        Cell cellToAction = null;
        switch (event.getType()) {
            case EventTypes.PLAY_TURN:
                if (this.playerManager.howManyActivePlayers() == 1) {
                    String winnerPlayerName = this.playerManager.getWinnerPlayer().getPlayerName();
                    Event winnerEvent;
                    winnerEvent = this.eventInitiator(0, WINNER_MESSAGE, winnerPlayerName,
                            0, 0, false, 0, false, null, 0, EventType.GAME_WINNER, 0);
                    this.eventManager.addEvent(winnerEvent);
                    this.game.shutdownGame();

                } else if (!currentPlayer.isBankruptOrRetired()) {
                    if (currentPlayer.isParked()) {
                        Event parkedEvent;
                        parkedEvent = this.eventInitiator(this.currentPlayer.getPosition(), PARKED_MESSAGE, this.currentPlayer.getPlayerName(), 0,
                                0, false, this.currentPlayer.getPosition(), false, null, 0, EventType.MOVE, 0);
                        this.eventManager.addEvent(parkedEvent);
                        currentPlayer.setIsParked(false);
                        internalEventList.add(new InternalEvent(EventTypes.TURN_FINISHED));
                    } else {
                        this.internalEventList.add(new InternalEvent(EventTypes.ROLL_DICE));
                    }
                } else {
                    internalEventList.add(new InternalEvent(EventTypes.TURN_FINISHED));
                }

                break;
            case EventTypes.TURN_FINISHED: {
                this.taskExecutor.execute(() -> {
                    this.playerManager.nextPlayer();
                });
                break;
            }

            case EventTypes.ROLL_DICE:
                PairOfDice.roll();
                int firstDie = PairOfDice.getFirstDice();
                int secondDie = PairOfDice.getSecondDice();
                Event rollDice;
                rollDice = this.eventInitiator(0, ROLL_MESSAGE, this.currentPlayer.getPlayerName(), firstDie, secondDie, false,
                        0, false, null, 0, EventType.DICE_ROLL, 0);
                this.eventManager.addEvent(rollDice);
                Event moveEvent;
                int currentPlace = this.currentPlayer.getPosition();
                int newPlace = (this.currentPlayer.getPosition() + firstDie + secondDie) % TOTAL_CELL;
                if (currentPlayer.isInJail() && firstDie != secondDie) {
                    moveEvent = this.eventInitiator(currentPlace, PLAYER_IN_JAIL_MESSAGE, this.currentPlayer.getPlayerName(), 0, 0, false,
                            currentPlace, false, null, 0, EventType.MOVE, 0);
                    this.eventManager.addEvent(moveEvent);
                    internalEventList.add(new InternalEvent(EventTypes.TURN_FINISHED));

                } else { //player can move
                    if (currentPlayer.isInJail() && firstDie == secondDie) {
                        currentPlayer.setInJail(false);
                        moveEvent = this.eventInitiator(currentPlace, PLAYER_GOT_OUT_OF_JAIL_MESSAGE, this.currentPlayer.getPlayerName(),
                                0, 0, true, newPlace, false, null, 0, EventType.MOVE, 0);
                    } else {
                        moveEvent = this.eventInitiator(currentPlace, MOVE_MESSAGE, this.currentPlayer.getPlayerName(),
                                0, 0, true, newPlace, false, null, 0, EventType.MOVE, 0);
                    }
                    this.eventManager.addEvent(moveEvent);
                    if (currentPlace > newPlace && newPlace != GameConstants.BASE_CELL) {
                        internalEventList.add(new InternalEvent(EventTypes.PASSED_START_CELL));
                    }
                    this.currentPlayer.setPosition(newPlace);
                    cellToAction = this.cellModel.getCells().get(newPlace);
                    cellToAction.playAction(this.currentPlayer);
                }
                break;

            case EventTypes.PASSED_START_CELL:
                this.currentPlayer.setMoney(this.currentPlayer.getMoney() + 200);
                Event passedStart;
                passedStart = this.eventInitiator(0, PASSED_START_SQUARE_MESSAGE, this.currentPlayer.getPlayerName(), 0, 0, false,
                        0, false, null, 0, EventType.PASSED_START_SQUARE, 0);
                this.eventManager.addEvent(passedStart);
                break;

            case EventTypes.ON_CITY: {
                PropertyCell cell = (PropertyCell) cellModel.getCells().get(currentPlayer.getPosition());
                if (cell.isHasOwner()) {
                    if (currentPlayer == cell.getOwner()) {
                        this.buyHouseProcedure(cell);
                    } else { // Player needs to pay the fine
                        int fine = this.calculateFine(cell);
                        this.payFine(currentPlayer, cell.getOwner(), fine);
                        internalEventList.add(new InternalEvent(EventTypes.TURN_FINISHED));
                    }
                } else {
                    this.buyPropertyProcedure(currentPlayer, cell);
                }
                break;
            }
            case EventTypes.ON_FREE_PARKING:
                this.setPlayerToFreeParking(currentPlayer);
                internalEventList.add(new InternalEvent(EventTypes.TURN_FINISHED));
                break;
            case EventTypes.ON_JAIL_FREE_PASS:
                if (internalEventList.get(internalEventList.size() - 3).getType() != EventTypes.ON_GO_TO_JAIL) {
                    internalEventList.add(new InternalEvent(EventTypes.TURN_FINISHED));
                }

                break;
            case EventTypes.ON_GO_TO_JAIL:
                if (currentPlayer.isHasFreeJailCard()) {
                    this.internalEventList.add(new InternalEvent(EventTypes.PLAYER_WANTS_TO_USE_CARD));
                } else {
                    currentPlayer.setInJail(true);
                    this.setPlayerNewLocation(currentPlayer, "Jail");
                    Event jailEvent;
                    jailEvent = this.eventInitiator(0, PLAYER_GO_TO_JAIL_MESSAGE, this.currentPlayer.getPlayerName(), 0, 0, false,
                            0, false, null, 0, EventType.GO_TO_JAIL, 0);
                    internalEventList.add(new InternalEvent(EventTypes.TURN_FINISHED));
                }

                break;
            case EventTypes.ON_TRANSPORTATION: {
                TransportationCell cell = (TransportationCell) cellModel.getCells().get(currentPlayer.getPosition());
                if (cell.isHasOwner()) {
                    Player cellOwner = cell.getOwner();
                    if (currentPlayer == cellOwner) {
                        internalEventList.add(new InternalEvent(EventTypes.TURN_FINISHED));
                    } else {
                        if (this.hasOwnerOwnAllTransportation(cellOwner)) {
                            this.payFine(currentPlayer, cellOwner, this.initiator.getAssets().getTransportationStayCost());
                        } else {
                            this.payFine(currentPlayer, cellOwner, cell.getData().getStayCost());
                        }
                        internalEventList.add(new InternalEvent(EventTypes.TURN_FINISHED));
                    }
                } else {
                    this.buyTransportationProcedure(currentPlayer, cell);
                }
                break;
            }

            case EventTypes.ON_UTILITY: {
                UtilityCell cell = (UtilityCell) cellModel.getCells().get(currentPlayer.getPosition());
                if (cell.isHasOwner()) {
                    Player cellOwner = cell.getOwner();
                    if (currentPlayer == cellOwner) {
                        internalEventList.add(new InternalEvent(EventTypes.TURN_FINISHED));
                    } else {
                        if (this.hasOwnerOwnAllUtilities(cellOwner)) {
                            this.payFine(currentPlayer, cellOwner, this.initiator.getAssets().getUtilityStayCost());
                        } else {
                            this.payFine(currentPlayer, cellOwner, cell.getData().getStayCost());
                        }
                        internalEventList.add(new InternalEvent(EventTypes.TURN_FINISHED));
                    }
                } else {
                    this.buyUtilityProcedure(currentPlayer, cell);
                }
                break;
            }

            case EventTypes.ON_START:
                currentPlayer.setMoney(currentPlayer.getMoney() + 400);
                Event landedStart;
                landedStart = this.eventInitiator(0, LANDED_START_SQUARE_MESSAGE, this.currentPlayer.getPlayerName(), 0, 0, false,
                        0, false, null, 0, EventType.LANDED_ON_START_SQUARE, 0);
                this.eventManager.addEvent(landedStart);
                internalEventList.add(new InternalEvent(EventTypes.TURN_FINISHED));
                break;
            case EventTypes.ON_SUPRISE: {
                this.currentCard = this.getSurpriseCard();
                Event surpriseEvent;
                surpriseEvent = this.eventInitiator(0, this.currentCard.toString(), this.currentPlayer.getPlayerName(), 0, 0, false,
                        0, false, null, 0, EventType.SURPRISE_CARD, 0);
                this.eventManager.addEvent(surpriseEvent);
                this.currentCard.surpriseAction(this.currentPlayer);
                internalEventList.add(new InternalEvent(EventTypes.TURN_FINISHED));
                break;

            }

            case EventTypes.ON_WARRANT:
                this.currentCard = this.getWarrantCard();
                Event warrantEvent;
                warrantEvent = this.eventInitiator(0, this.currentCard.toString(), this.currentPlayer.getPlayerName(), 0, 0, false,
                        0, false, null, 0, EventType.SURPRISE_CARD, 0);
                this.eventManager.addEvent(warrantEvent);
                this.currentCard.warrantAction(this.currentPlayer);
                internalEventList.add(new InternalEvent(EventTypes.TURN_FINISHED));
                break;
            case EventTypes.PLAYER_PAID_FINE: {
                Event playerPaidFine;
                playerPaidFine = this.eventInitiator(0, PLAYER_PAID_TO_PLAYER_MESSAGE, this.currentPlayer.getPlayerName(), 0, 0, false,
                        0, true, this.currentPlayer.getPaidPlayerName(), this.currentPlayer.getLastFine(), EventType.PAYMENT, 0);
                this.eventManager.addEvent(playerPaidFine);
                break;
            }
            case EventTypes.PLAYER_LOST_GAME:
                for (Player player : playerManager.getPlayers()) {
                    if (player.isBankruptOrRetired()) {
                        player.releasePlayerAssets();
                        Event playerLostEvent;
                        playerLostEvent = this.eventInitiator(0, PLAYER_LOST_MESSAGE, this.currentPlayer.getPlayerName(), 0, 0, false,
                                0, false, null, 0, EventType.PLAYER_LOST, 0);
                        this.eventManager.addEvent(playerLostEvent);

                    }
                }
                break;
            case EventTypes.PLAYER_RESIGN: {
                this.currentPlayer.getPlayerDetails().setStatus(PlayerStatus.RETIRED);
                this.currentPlayer.releasePlayerAssets();
                Event playerRetiredEvent;
                playerRetiredEvent = this.eventInitiator(0, PLAYER_RETIRED_MESSAGE, this.currentPlayer.getPlayerName(), 0, 0, false,
                        0, false, null, 0, EventType.PLAYER_RESIGNED, 0);
                this.eventManager.addEvent(playerRetiredEvent);
                this.internalEventList.add(new InternalEvent(EventTypes.TURN_FINISHED));
                break;
            }
            case EventTypes.START_TIMER:
                this.timer.schedule(new EventTimer(this), TIME_OUT * 1000);
                break;
            case EventTypes.PLAYER_WANTS_TO_BUY_BUYABLE: {
                Buyable cell = (Buyable) cellModel.getCells().get(currentPlayer.getPosition());
                currentPlayer.setMoney(currentPlayer.getMoney() - cell.getCost());
                cell.setHasOwner(true);
                cell.setOwner(currentPlayer);
                Event assetBought;
                assetBought = this.eventInitiator(0, EventMessages.ASSET_BOUGHT_MESSAGE, this.currentPlayer.getPlayerName(), 0, 0, false,
                        0, false, null, 0, EventType.ASSET_BOUGHT, 0);
                this.eventManager.addEvent(assetBought);
                internalEventList.add(new InternalEvent(EventTypes.TURN_FINISHED));
                break;
            }
            case EventTypes.PLAYER_DIDNT_WANT_TO_BUY:
                internalEventList.add(new InternalEvent(EventTypes.TURN_FINISHED));
                break;
            case EventTypes.PLAYER_WANTS_TO_BUY_HOUSE: {
                PropertyCell cell = (PropertyCell) cellModel.getCells().get(currentPlayer.getPosition());
                cell.setNumOfHouses(cell.getNumOfHouses() + 1);
                cell.getOwner().setMoney(cell.getOwner().getMoney() - cell.getData().getHouseCost());
                //TODO add house to client
                //this.boardController.addHouseToSpecificCell(currentPlayer.getPosition());
                Event houseBought;
                houseBought = this.eventInitiator(0, EventMessages.HOUSE_BOUGHT_MESSAGE, this.currentPlayer.getPlayerName(), 0, 0, false,
                        0, false, null, 0, EventType.HOUSE_BOUGHT, 0);
                this.eventManager.addEvent(houseBought);
                internalEventList.add(new InternalEvent(EventTypes.TURN_FINISHED));
                break;
            }
            case EventTypes.TAKE_MONEY_FROM_ALL_PLAYERS: {
                this.takeMoneyFromPlayers(currentPlayer, ((MontaryCard) this.currentCard).getSum());
                break;
            }
            case EventTypes.TAKE_MONEY_FROM_JACKPOT: {
                int sum = ((MontaryCard) this.currentCard).getSum();
                currentPlayer.setMoney(currentPlayer.getMoney() + sum);
                Event takeMoneyFromJackpot;
                takeMoneyFromJackpot = this.eventInitiator(0, TAKE_MONEY_FROM_JACKPOT_MESSAGE, this.currentPlayer.getPlayerName(), 0, 0, false,
                        0, false, null, sum, EventType.PAYMENT, 0);
                this.eventManager.addEvent(takeMoneyFromJackpot);
                break;
            }
            case EventTypes.GO_TO_START_CELL: {
                Event goToStart;
                goToStart = this.eventInitiator(this.currentPlayer.getPosition(), GO_TO_START_MESSAGE, this.currentPlayer.getPlayerName(), 0, 0, true,
                        GameConstants.BASE_CELL, false, null, 0, EventType.MOVE, 0);
                this.eventManager.addEvent(goToStart);
                this.currentPlayer.setPosition(GameConstants.BASE_CELL);
                currentPlayer.setMoney(currentPlayer.getMoney() + 400);
                Event startLand;
                startLand = this.eventInitiator(0, LANDED_START_SQUARE_MESSAGE, this.currentPlayer.getPlayerName(), 0, 0, false,
                        0, false, null, 0, EventType.LANDED_ON_START_SQUARE, 0);
                this.eventManager.addEvent(startLand);
                break;
            }
            case EventTypes.GO_TO_NEXT_SURPRISE: {
                int lastLocation = currentPlayer.getPosition();
                this.setPlayerNewLocation(currentPlayer, "NEXT_SURPRISE");
                if (lastLocation > currentPlayer.getPosition()) {
                    this.internalEventList.add(new InternalEvent(EventTypes.PASSED_START_CELL));

                }
                this.currentCard = this.getSurpriseCard();
                Event secondSurpriseEvent;
                secondSurpriseEvent = this.eventInitiator(0, this.currentCard.toString(), this.currentPlayer.getPlayerName(), 0, 0, false,
                        0, false, null, 0, EventType.SURPRISE_CARD, 0);
                this.eventManager.addEvent(secondSurpriseEvent);
                this.currentCard.surpriseAction(this.currentPlayer);
                break;
            }
            case EventTypes.GET_OUT_OF_JAIL_CARD: {
                Event getOutOfJailCard;
                getOutOfJailCard = this.eventInitiator(0, GET_OUT_OF_JAIL_CARD_MESSAGE, this.currentPlayer.getPlayerName(), 0, 0, false,
                        0, false, null, 0, EventType.GET_OUT_OF_JAIL_CARD, 0);
                this.eventManager.addEvent(getOutOfJailCard);
                currentPlayer.setHasFreeJailCard(true);
                currentPlayer.setJailFreeCard(this.currentCard);
                break;
            }

            case EventTypes.RETURN_CARD_TO_WARRANT_DECK: {
                this.returnToWarrantDeck();
                break;
            }
            case EventTypes.KILL_TIMER:
                this.timer.cancel();
                break;
            case EventTypes.RETURN_CARD_TO_SURPRISE_DECK: {
                this.returnSurpriseCardToDeck();
                break;
            }
            case EventTypes.GO_TO_NEXT_WARRANT: {

                this.setPlayerNewLocation(currentPlayer, "NEXT_WARRANT");
                this.cellModel.getCells().get(this.currentPlayer.getPosition()).playAction(this.currentPlayer);
                internalEventList.add(new InternalEvent(EventTypes.TURN_FINISHED));
                break;
            }
            case EventTypes.PAY_TO_ALL_PLAYERS: {
                int sum = ((MontaryCard) this.currentCard).getSum();
                int activePlayers = this.playerManager.howManyActivePlayers();
                int totalSum = sum * activePlayers;
                if (currentPlayer.getMoney() < totalSum) {
                    totalSum = currentPlayer.getMoney();
                    sum = totalSum / activePlayers;
                    this.payFineToEveryPlayer(currentPlayer, sum);
                    internalEventList.add(new InternalEvent(EventTypes.PLAYER_LOST_GAME));
                } else {
                    this.payFineToEveryPlayer(currentPlayer, sum);
                }

                break;
            }
            case EventTypes.PAY_TO_JACKPOT: {
                int sum = ((MontaryCard) this.currentCard).getSum();
                Event paidToJackpot;
                paidToJackpot = this.eventInitiator(0, PLAYER_PAID_JACKPOT_MESSAGE, this.currentPlayer.getPlayerName(), 0, 0, false,
                        0, false, null, sum, EventType.PAYMENT, 0);
                if (currentPlayer.getMoney() < sum) {
                    internalEventList.add(new InternalEvent(EventTypes.PLAYER_LOST_GAME));
                } else {
                    currentPlayer.setMoney(currentPlayer.getMoney() - sum);

                }
                this.eventManager.addEvent(paidToJackpot);
                internalEventList.add(new InternalEvent(EventTypes.TURN_FINISHED));
                break;
            }
            case EventTypes.PLAYER_WANTS_TO_USE_CARD:
                currentPlayer.setHasFreeJailCard(false);
                this.returnSurpriseCardToDeck(currentPlayer.getJailFreeCard());
                currentPlayer.setJailFreeCard(null);
                Event playerWantsToUseCardEvent;
                playerWantsToUseCardEvent = this.eventInitiator(0, PLAYER_WANTS_TO_USE_CARD, this.currentPlayer.getPlayerName(), 0, 0, false,
                        0, false, null, 0, EventType.PLAYER_USED_GET_OUT_OF_JAIL_CARD, 0);
                this.eventManager.addEvent(playerWantsToUseCardEvent);
                internalEventList.add(new InternalEvent(EventTypes.TURN_FINISHED));
                break;
            default:

                break;
        }

    }

    private void buyHouseProcedure(PropertyCell property) {
        boolean playerOwnCountry = this.isPlayerOwnCountry(property);
        boolean playerCouldBuyHouse = this.playerCouldBuyHouse(property);
        boolean playerNotHaveMaxHouses = property.getNumOfHouses() < 3;
        if (playerOwnCountry && playerCouldBuyHouse && playerNotHaveMaxHouses) {
            if (currentPlayer.getPlayerDetails().getType().equals(PlayerType.HUMAN)) {
                String infoString = property.getName() + "_" + property.getData().getHouseCost();
                Event promptHouseEvent;
                promptHouseEvent = this.eventInitiator(0, infoString, this.currentPlayer.getPlayerName(), 0, 0, false,
                        0, false, null, 0, EventType.PROPMPT_PLAYER_TO_BY_HOUSE, GameConstants.TIME_OUT);
                this.eventManager.addEvent(promptHouseEvent);
                this.internalEventList.add(new InternalEvent(EventTypes.START_TIMER));
            } else {
                this.internalEventList.add(new InternalEvent(EventTypes.PLAYER_WANTS_TO_BUY_HOUSE));
            }

        } else {
            internalEventList.add(new InternalEvent(EventTypes.TURN_FINISHED));
        }

    }

    private boolean isPlayerOwnCountry(PropertyCell property) {
        Assets gameAssets = this.initiator.getAssets();
        int playerCitiesOnCountry = 0;
        LinkedList<CountryGame> theCountries = gameAssets.getTheCountries();
        String countryName = property.getData().getCountry();
        int citiesByCountry = howManyCitiesInCountry(theCountries, countryName);
        if (citiesByCountry == 0) {
            internalEventList.add(new InternalEvent(EventTypes.ERROR));
        } else {
            playerCitiesOnCountry = getPlayerCitiesInCountry(property.getOwner(), countryName);
        }
        return (playerCitiesOnCountry == citiesByCountry);
    }

    private int getPlayerCitiesInCountry(Player owner, String countryName) {
        ArrayList<City> playerCities = owner.getPlayerCities();
        int cityCounter = 0;
        for (City city : playerCities) {
            if (city.getCountry().equals(countryName)) {
                cityCounter++;
            }
        }
        return cityCounter;
    }

    private int howManyCitiesInCountry(LinkedList<CountryGame> theCountries, String countryName) {
        for (CountryGame country : theCountries) {
            if (country.getName().equals(countryName)) {
                return country.getCitiesNum();
            }
        }
        return 0;
    }

    private boolean playerCouldBuyHouse(PropertyCell property) {
        return (property.getData().getHouseCost() >= property.getOwner().getMoney());
    }

    private int calculateFine(PropertyCell theCell) {
        int numOfHouses = theCell.getNumOfHouses();
        switch (numOfHouses) {
            case 0:
                return theCell.getData().getStayCost();
            case 1:
                return theCell.getData().getStayCost();
            case 2:
                return theCell.getData().getStayCost2();
            case 3:
                return theCell.getData().getStayCost3();
            default:
                return 0;
        }

    }

    private void payFine(Player payer, Player owner, int theFine) {
        if (!owner.isBankruptOrRetired()) {
            if (payer.getMoney() < theFine) {
                theFine = payer.getMoney();
                this.setPlayerOutOfTheGame(payer);
            } else {
                payer.setMoney(payer.getMoney() - theFine);
            }
            owner.setMoney(owner.getMoney() + theFine);
            payer.setLastFine(theFine, owner);
        }
        internalEventList.add(new InternalEvent(EventTypes.PLAYER_PAID_FINE));

    }

    private void setPlayerOutOfTheGame(Player loser) {
        loser.setMoney(0);
        loser.setIsBankrupt(true);
        internalEventList.add(new InternalEvent(EventTypes.PLAYER_LOST_GAME));

    }

    private void buyPropertyProcedure(Player currentPlayer, PropertyCell theCell) {
        boolean playerCouldBuy = theCell.getData().getCost() <= currentPlayer.getMoney();
        if (playerCouldBuy) {
            if (currentPlayer.getPlayerDetails().getType().equals(PlayerType.HUMAN)) {
                String infoString = "City_" + theCell.getName() + "_" + theCell.getData().getCost();
                Event promptAssetEvent;
                promptAssetEvent = this.eventInitiator(0, infoString, this.currentPlayer.getPlayerName(), 0, 0, false,
                        0, false, null, 0, EventType.PROPMT_PLAYER_TO_BY_ASSET, GameConstants.TIME_OUT);
                this.eventManager.addEvent(promptAssetEvent);
                this.internalEventList.add(new InternalEvent(EventTypes.START_TIMER));
            } else { //player is computer 
                 this.internalEventList.add(new InternalEvent(EventTypes.PLAYER_WANTS_TO_BUY_BUYABLE));
            }
        } else {
            internalEventList.add(new InternalEvent(EventTypes.TURN_FINISHED));
        }

    }

    private void setPlayerToFreeParking(Player currentPlayer) {
        //this.boardController.showMessage("Oh! " + currentPlayer.getPlayerName() + " You're PARKED! Wait one turn.");
        currentPlayer.setIsParked(true);

    }

    public void setPlayerNewLocation(Player currentPlayer, String string) {
        int newPlace = 0;
        int currentPlace = this.currentPlayer.getPosition();
        Event moveEvent = null;

        if (string.equals("NEXT_SURPRISE")) {
            newPlace = this.cellModel.getNextSurpriseOnBoard(currentPlayer.getPosition() + 1);
            moveEvent = this.eventInitiator(currentPlace, MOVE_MESSAGE, this.currentPlayer.getPlayerName(),
                    0, 0, true, newPlace, false, null, 0, EventType.MOVE, 0);
        } else if (string.equals("Jail")) {
            newPlace = this.cellModel.getPlaceOnBoardByName("Jail Free Pass");
            moveEvent = this.eventInitiator(currentPlace, MOVE_MESSAGE, this.currentPlayer.getPlayerName(),
                    0, 0, true, newPlace, false, null, 0, EventType.MOVE, 0);
        } else if (string.equals("NEXT_WARRANT")) {
            newPlace = this.cellModel.getNextWarrantOnBoard(currentPlayer.getPosition() + 1);
            moveEvent = this.eventInitiator(currentPlace, MOVE_MESSAGE, this.currentPlayer.getPlayerName(),
                    0, 0, true, newPlace, false, null, 0, EventType.MOVE, 0);
        }
        this.currentPlayer.setPosition(newPlace);
        this.eventManager.addEvent(moveEvent);
    }

    private boolean hasOwnerOwnAllTransportation(Player owner) {
        int numberOfTrans = this.initiator.getAssets().getTransportation().size();
        return (owner.getTransportation().size() == numberOfTrans);

    }

    private void buyTransportationProcedure(Player currentPlayer, TransportationCell theCell) {
        boolean playerCouldBuy = theCell.getData().getCost() <= currentPlayer.getMoney();
        if (playerCouldBuy) {
            if (currentPlayer.getPlayerDetails().getType().equals(PlayerType.HUMAN)) {
                String infoString = "Transportation_" + theCell.getName() + "_" + theCell.getData().getCost();
                Event promptAssetEvent;
                promptAssetEvent = this.eventInitiator(0, infoString, this.currentPlayer.getPlayerName(), 0, 0, false,
                        0, false, null, 0, EventType.PROPMT_PLAYER_TO_BY_ASSET, GameConstants.TIME_OUT);
                this.eventManager.addEvent(promptAssetEvent);
                this.internalEventList.add(new InternalEvent(EventTypes.START_TIMER));
            } else {
                this.internalEventList.add(new InternalEvent(EventTypes.PLAYER_WANTS_TO_BUY_BUYABLE));
            }

        } else {
            internalEventList.add(new InternalEvent(EventTypes.TURN_FINISHED));
        }

    }

    private boolean hasOwnerOwnAllUtilities(Player owner) {
        return (owner.getTransportation().size() == this.initiator.getAssets().getUtility().size());
    }

    private void buyUtilityProcedure(Player currentPlayer, UtilityCell theCell) {
        boolean playerCouldBuy = theCell.getData().getCost() <= currentPlayer.getMoney();
        if (playerCouldBuy) {
            if (currentPlayer.getPlayerDetails().getType().equals(PlayerType.HUMAN)) {
                String infoString = "Utility_" + theCell.getName() + "_" + theCell.getData().getCost();
                Event promptAssetEvent;
                promptAssetEvent = this.eventInitiator(0, infoString, this.currentPlayer.getPlayerName(), 0, 0, false,
                        0, false, null, 0, EventType.PROPMT_PLAYER_TO_BY_ASSET, GameConstants.TIME_OUT);
                this.eventManager.addEvent(promptAssetEvent);
                this.internalEventList.add(new InternalEvent(EventTypes.START_TIMER));
            } else {
                this.internalEventList.add(new InternalEvent(EventTypes.PLAYER_WANTS_TO_BUY_BUYABLE));
            }
        } else {
            internalEventList.add(new InternalEvent(EventTypes.TURN_FINISHED));
        }

    }

    /**
     * @param initiator the initiator to set
     */
    public void setInitiator(InitiateGame initiator) {
        this.initiator = initiator;
    }

    public void setDecks() {
        this.surpriseDeck = this.initiator.getSupriseCards();
        this.warrantDeck = this.initiator.getWarrantCards();
    }

    public Card getSurpriseCard() {
        Card theCard = this.surpriseDeck.get(0);
        this.surpriseDeck.remove(0);
        return theCard;
    }

    public Card getWarrantCard() {
        Card theCard = this.warrantDeck.get(0);
        this.warrantDeck.remove(0);
        return theCard;
    }

    public void takeMoneyFromPlayers(Player getter, int sum) {
        for (Player payer : this.playerManager.getPlayers()) {
            if (!(payer.isBankruptOrRetired())) {
                this.payFine(payer, getter, sum);
            }
        }

    }

    private void returnToWarrantDeck() {
        this.warrantDeck.addLast(this.currentCard);

    }

    private void returnSurpriseCardToDeck() {
        this.surpriseDeck.addLast(this.currentCard);

    }

    private void payFineToEveryPlayer(Player payer, int sum) {
        for (Player getter : this.playerManager.getPlayers()) {
            if (!(payer.isBankruptOrRetired())) {
                this.payFine(payer, getter, sum);
            }
        }

    }

    private void returnSurpriseCardToDeck(Card card) {
        this.surpriseDeck.addLast(card);

    }

    private Event eventInitiator(int boardSquareID, String eventMessage, String playerName, int firstDice, int secondDice, boolean playerMove,
            int nextBoardSquareID, boolean paymentFromUser, String paymentToPlayerName, int paymentAmount, EventType eventType, int timeout) {
        Event event = new Event();
        event.setBoardSquareID(boardSquareID);
        event.setEventMessage(eventMessage);
        event.setPlayerName(playerName);
        event.setFirstDiceResult(firstDice);
        event.setSecondDiceResult(secondDice);
        event.setPlayerMove(playerMove);
        event.setNextBoardSquareID(nextBoardSquareID);
        event.setPaymemtFromUser(paymentFromUser);
        event.setPaymentToPlayerName(paymentToPlayerName);
        event.setPaymentAmount(paymentAmount);
        event.setType(eventType);
        event.setTimeout(timeout);
        return event;
    }

    /**
     * @return the eventManager
     */
    public EventManager getEventManager() {
        return eventManager;
    }

    /**
     * @param eventManager the eventManager to set
     */
    public void setEventManager(EventManager eventManager) {
        this.eventManager = eventManager;
    }

    public void startGame() {

    }

    public void addEventToInternal(String event) {
        this.internalEventList.add(new InternalEvent(event));
    }

    public void setPlayerManager(PlayersManager playersManager) {
        this.playerManager = playersManager;
    }

}
