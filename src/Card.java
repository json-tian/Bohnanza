import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.Arrays;
import java.util.Formatter;

public class Card {

	private String cardName;		//name of the card
	private int numberOfCards;		//the number of that card in the game
	private int[] coin = new int[5];//holds the coin values from the card
	
	//constructor
	public Card(int cardType){

		Scanner inputFile = null;

		try {

			inputFile = new Scanner(new File("cards.txt")).useDelimiter(",");
			
			//reads in text file by row
			for(int row = 1; row < cardType; row++){
				inputFile.nextLine();
			}
			
			//sets the card name, number of cards and the coin values
			setCardName(inputFile.next());
			setNumberOfCards(Integer.valueOf(inputFile.next()));
			for(int coinNumber = 1; coinNumber < 5; coinNumber++)
				coin[coinNumber] = Integer.valueOf(inputFile.next());
			

		} catch (FileNotFoundException error) {

			System.err.println("File not found - check the file name");

		}
		
	}

	//getters and setters for the card name
	public String getCardName() {
		return cardName;
	}

	public void setCardName(String cardName) {
		this.cardName = cardName;
	}

	//getters and setters for the number of cards
	public int getNumberOfCards() {
		return numberOfCards;
	}

	public void setNumberOfCards(int numberOfCards) {
		this.numberOfCards = numberOfCards;
	}

	//getters and setters for the coin values
	public int[] getCoin() {
		return coin;
	}

	public void setCoin(int[] coin) {
		this.coin = coin;
	}

}
