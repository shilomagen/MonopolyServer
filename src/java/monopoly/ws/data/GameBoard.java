/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package monopoly.ws.data;

import java.util.LinkedList;
import monopoly.ws.cell.CellModel;
import monopoly.ws.cell.FreeParkingCell;
import monopoly.ws.cell.GoToJailCell;
import monopoly.ws.cell.JailFreePassCell;
import monopoly.ws.cell.PropertyCell;
import monopoly.ws.cell.StartCell;
import monopoly.ws.cell.SupriseCardCell;
import monopoly.ws.cell.TransportationCell;
import monopoly.ws.cell.UtilityCell;
import monopoly.ws.cell.WarrantCardCell;
import monopoly.ws.game.MonopolyGame;
import monopoly.ws.utility.PositionHelper;

/**
 *
 * @author ShiloMangam
 */
public class GameBoard {
    private MonopolyGame game;
    private BoardData boardData;
    private Assets gameAssets;
    private CellModel cellModel;

    public GameBoard(MonopolyGame game) {
        this.game = game;
        this.cellModel = new CellModel();

    }

    public void loadTheBoard() {
        boardData = this.game.getInitiator().getBoardData();
        gameAssets = this.game.getInitiator().getAssets();
        LinkedList<CountryGame> theCountries = gameAssets.getTheCountries();
        LinkedList<Transportation> transportation = gameAssets.getTransportation();
        LinkedList<Utility> utilities = gameAssets.getUtility();
//		surpriseDeck = InitiateGame.getSupriseCards();
//		warrantDeck = InitiateGame.getWarrantCards();
        this.LoadDataOnCells(theCountries, transportation, utilities);

    }

    private void LoadDataOnCells(LinkedList<CountryGame> theCountries, LinkedList<Transportation> transportation,
            LinkedList<Utility> utilities) {
        int placeOnBoard = 0;
        int countryCounter = 0, cityCounter = 0;
        int transPlace = 0;
        int utilityPlace = 0;
        int suprisePlace = 0;
        int warrantPlace = 0;
        LinkedList<CellData> cellList = boardData.getTheBoard();
        PositionHelper newHelper = new PositionHelper();
        for (CellData dataCell : cellList) {
            String cellType = dataCell.getType();
            if (cellType.equals("Start Square")) {
                getCellModel().addCell(new StartCell("StartCell", placeOnBoard));
                placeOnBoard++;
            } else if (cellType.equals("CITY")) {
                if (cityCounter >= theCountries.get(countryCounter).getCitiesNum()) {
                    countryCounter++;
                    cityCounter = 0;
                }
                City cityToData = theCountries.get(countryCounter).getCities().get(cityCounter);
                getCellModel().addCell(new PropertyCell(cityToData.getName(), placeOnBoard, cityToData));
                cityCounter++;
                placeOnBoard++;
            } else if (cellType.equals("SURPRISE")) {
                getCellModel().addCell(new SupriseCardCell("SupriseCell", placeOnBoard));
                suprisePlace++;
                placeOnBoard++;
            } else if (cellType.equals("WARRANT")) {
                getCellModel().addCell(new WarrantCardCell("WarrantCell", placeOnBoard));
                warrantPlace++;
                placeOnBoard++;
            } else if (cellType.equals("TRANSPORTATION")) {
                getCellModel().addCell(new TransportationCell(transportation.get(transPlace).getName(), placeOnBoard,
                        transportation.get(transPlace)));
                placeOnBoard++;
                transPlace++;
            } else if (cellType.equals("GotoJail")) {

                getCellModel().addCell(new GoToJailCell("Go To Jail", placeOnBoard));
                placeOnBoard++;

            } else if (cellType.equals("Parking")) {
                getCellModel().addCell(new FreeParkingCell("Free Parking", placeOnBoard));
                placeOnBoard++;
            } else if (cellType.equals("UTILITY")) {
                getCellModel().addCell(new UtilityCell(utilities.get(utilityPlace).getName(), placeOnBoard,
                        utilities.get(utilityPlace)));
                placeOnBoard++;
                utilityPlace++;
            } else if (cellType.equals("Jail")) {
                getCellModel().addCell(new JailFreePassCell("Jail Free Pass", placeOnBoard));
                placeOnBoard++;
            }
        }

    }

    /**
     * @return the cellModel
     */
    public CellModel getCellModel() {
        return cellModel;
    }

    /**
     * @param cellModel the cellModel to set
     */
    public void setCellModel(CellModel cellModel) {
        this.cellModel = cellModel;
    }

}
