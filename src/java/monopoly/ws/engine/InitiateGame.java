package monopoly.ws.engine;

import java.io.InputStream;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import monopoly.scheme.CardBase;
import monopoly.scheme.CityType;
import monopoly.scheme.GotoCard;
import monopoly.scheme.MonetaryCard;
import monopoly.scheme.Monopoly;
import monopoly.scheme.SimpleAssetType;
import monopoly.scheme.SquareBase;
import monopoly.scheme.SquareType;
import monopoly.ws.data.Assets;
import monopoly.ws.data.BoardData;
import monopoly.ws.data.Card;
import monopoly.ws.data.CellData;
import monopoly.ws.data.City;
import monopoly.ws.data.CountryGame;
import monopoly.ws.data.GetOutOfJailCard;
import monopoly.ws.data.GoToCard;
import monopoly.ws.data.MontaryCard;
import monopoly.ws.data.Transportation;
import monopoly.ws.data.Utility;
import monopoly.ws.utility.GameConstants;
import org.xml.sax.SAXException;




public class InitiateGame {

	private static Monopoly monopoly;
	private static Assets gameAssets;
	private static LinkedList<CountryGame> theCountries;
	private static LinkedList<Transportation> transportation;
	private static BoardData board;
	private static LinkedList<Utility> utility;
	private static LinkedList<Card> supriseCards;
	private static LinkedList<Card> warrantCards;



	public static void xmlLoad() throws SAXException {
		// get the Schema from the XSD file
		URL csdURL = InitiateGame.class.getResource("/" + GameConstants.RESOURCES_PATH + "/" + "monopoly_config.xsd");
		SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		Schema schema = schemaFactory.newSchema(csdURL);

		// get the XML content
		InputStream xmlInputStream = InitiateGame.class
				.getResourceAsStream("/" + GameConstants.RESOURCES_PATH + "/" + "monopoly_config.xml");

		try {
			JAXBContext context = JAXBContext.newInstance(Monopoly.class);
			Unmarshaller unmarshaller = context.createUnmarshaller();
			unmarshaller.setSchema(schema);

			monopoly = (Monopoly) unmarshaller.unmarshal(xmlInputStream);
			gameAssets = new Assets((int) monopoly.getAssets().getUtilities().getStayCost(),
					(int) monopoly.getAssets().getTransportations().getStayCost());

			theCountries = createCountries(monopoly);
			transportation = createTransportation(monopoly);
			utility = createUtility(monopoly);
			gameAssets.setAssets(theCountries, transportation, utility);
			board = new BoardData();
			board.setTheBoard(getMonopolyBoard(monopoly));
			supriseCards = createSupriseCards(monopoly);
			warrantCards = createWarrantCards(monopoly);	
			
		} catch (JAXBException exception) {
			exception.printStackTrace();
		}
	}

	public static LinkedList<CellData> getMonopolyBoard(Monopoly monopoly) {
		LinkedList<CellData> theBoard = new LinkedList<>();
		List<JAXBElement<? extends SquareBase>> monoBoard = monopoly.getBoard().getContent();
		for (JAXBElement<? extends SquareBase> tempSquare : monoBoard) {
			String className = tempSquare.getValue().getClass().getName();
			if (className.contains("StartSquareType")) {
				CellData tempCell = new CellData("Start Square");
				theBoard.add(tempCell);
			} else if (className == "monopoly.scheme.SquareType") {
				SquareType realOne = (SquareType) tempSquare.getValue();
				CellData tempCell = new CellData(realOne.getType());
				theBoard.add(tempCell);
			} else if (className == "monopoly.scheme.GotoJailSquareType") {
				CellData tempCell = new CellData("GotoJail");
				theBoard.add(tempCell);
			} else if (className == "monopoly.scheme.JailSlashFreeSpaceSquareType") {
				CellData tempCell = new CellData("Jail");
				theBoard.add(tempCell);
			} else if (className == "monopoly.scheme.ParkingSquareType") {
				CellData tempCell = new CellData("Parking");
				theBoard.add(tempCell);
			}
		}
		return theBoard;

	}

	public static LinkedList<Card> createSupriseCards(Monopoly monopoly) {
		LinkedList<Card> supriseCard = new LinkedList<>();
		List<CardBase> supCard = monopoly.getSurprise().getSurpriseCards();
		for (CardBase tempCard : supCard) {
			Card newCard;
			if (tempCard != null) {
				String className = tempCard.getClass().getName();
				if (className.contains("MonetaryCard")) {
					newCard = new MontaryCard(((MonetaryCard) tempCard).getText(),
							(int) ((MonetaryCard) tempCard).getNum(), (int) ((MonetaryCard) tempCard).getSum());
					supriseCard.add(newCard);
				} else if (className.contains("GotoCard")) {
					newCard = new GoToCard(((GotoCard) tempCard).getText(), (int) ((GotoCard) tempCard).getNum(),
							((GotoCard) tempCard).getTo());
					supriseCard.add(newCard);
				} else if (className.contains("GetOutOfJailCard")) {
					newCard = new GetOutOfJailCard(tempCard.getText(), (int) tempCard.getNum());
					supriseCard.add(newCard);
				}
			}
		}

		return supriseCard;
	}

	public static LinkedList<Card> createWarrantCards(Monopoly monopoly) {
		LinkedList<Card> warrantCard = new LinkedList<>();
		List<CardBase> warCards = monopoly.getWarrant().getWarrantCards();
		for (CardBase tempCard : warCards) {
			Card newCard;
			if (tempCard != null) {
				String className = tempCard.getClass().getName();
				if (className.contains("MonetaryCard")) {
					newCard = new MontaryCard(((MonetaryCard) tempCard).getText(),
							(int) ((MonetaryCard) tempCard).getNum(), (int) ((MonetaryCard) tempCard).getSum());
					warrantCard.add(newCard);
				} else if (className.contains("GotoCard")) {
					newCard = new GoToCard(((GotoCard) tempCard).getText(), (int) ((GotoCard) tempCard).getNum(),
							((GotoCard) tempCard).getTo());
					warrantCard.add(newCard);
				}
			}
		}
		return warrantCard;
	}

	public static LinkedList<CountryGame> createCountries(Monopoly monopoly) {
		LinkedList<CountryGame> theCountries = new LinkedList<>();

		List<Monopoly.Assets.Countries.Country> countries = monopoly.getAssets().getCountries().getCountry();
		for (Monopoly.Assets.Countries.Country country : countries) {
			if (country != null) {
				CountryGame theCountry = new CountryGame(country.getName());
				LinkedList<City> theCities;
				theCities = createCities(country);
				theCountry.setCities(theCities);
				theCountry.setCitiesNum(theCities.size());
				theCountries.add(theCountry);
			}
		}
		return theCountries;

	}

	public static LinkedList<City> createCities(Monopoly.Assets.Countries.Country country) {
		LinkedList<City> theCities = new LinkedList<>();
		List<CityType> cityList = country.getCity();
		for (CityType tempCity : cityList) {
			if (tempCity != null) {
				City newCity = new City(country.getName(), tempCity.getName(), (int) tempCity.getCost(),
						(int) tempCity.getHouseCost(), (int) tempCity.getStayCost(), (int) tempCity.getStayCost1(),
						(int) tempCity.getStayCost2(), (int) tempCity.getStayCost3());
				theCities.add(newCity);
			}

		}
		return theCities;
	}

	public static LinkedList<Transportation> createTransportation(Monopoly monopoly) {
		LinkedList<Transportation> transportation = new LinkedList<>();
		List<SimpleAssetType> trans = monopoly.getAssets().getTransportations().getTransportation();
		for (SimpleAssetType tempAsset : trans) {
			if (tempAsset != null) {
				Transportation tempTrans = new Transportation(tempAsset.getName(), (int) tempAsset.getStayCost(),
						(int) tempAsset.getCost());
				transportation.add(tempTrans);
			}
		}
		return transportation;

	}

	public static LinkedList<Utility> createUtility(Monopoly monopoly) {
		LinkedList<Utility> utility = new LinkedList<>();
		List<SimpleAssetType> util = monopoly.getAssets().getUtilities().getUtility();
		for (SimpleAssetType tempAsset : util) {
			if (tempAsset != null) {
				Utility tempUtil = new Utility(tempAsset.getName(), (int) tempAsset.getStayCost(),
						(int) tempAsset.getCost());
				utility.add(tempUtil);
			}
		}
		return utility;
	}

	public static Assets getAssets() {
		return gameAssets;
	}

	public static BoardData getBoardData() {
		return board;
	}

	public static LinkedList<Card> getSupriseCards() {
		return supriseCards;
	}

	public static LinkedList<Card> getWarrantCards() {
		return warrantCards;
	}

}
