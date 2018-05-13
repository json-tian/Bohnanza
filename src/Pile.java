import java.util.ArrayList;
import java.util.Collections;

public class Pile {

	private Card[] cardArrayObject = new Card[9];				//Array of cards
	private ArrayList<Card> drawPile = new ArrayList<Card>();	//Holds the deck
	
	public ArrayList<Card> getDrawPile() {
		return drawPile;
	}

	public void setDrawPile(ArrayList<Card> drawPile) {
		this.drawPile = drawPile;
	}

	private ArrayList<Card> discardPile = new ArrayList<Card>();//Holds dicarded cards.
	private int refillCount = 0;								//Used to count when deck is refilled
	
	public Pile(){
		//
		for(int cardType = 1; cardType <=8; cardType++){
			cardArrayObject[cardType] = new Card(cardType);
			for(int numberOfCards = 1; numberOfCards <= cardArrayObject[cardType].getNumberOfCards();numberOfCards++){
				drawPile.add(cardArrayObject[cardType]);
			}
		}
		shuffleDrawPile();
	}
	
	public ArrayList<Card> getDiscardPile() {
		return discardPile;
	}

	public void setDiscardPile(ArrayList<Card> discardPile) {
		this.discardPile = discardPile;
	}

	//suffle the completed deck
	private void shuffleDrawPile(){
		Collections.shuffle(drawPile);
	}
	
	//removes card of top of deck and tells the player what card they recieve
	public Card drawCard(){		
		if (drawPile.size() > 0) { 			//If there are still cards left in deck, draw card
			Card tempCard = drawPile.get(0);
			drawPile.remove(0);
			return tempCard;
		} else {
			if (refillCount == 3) { //If the deck has already been refilled 3 times, end game
				return drawCard();  //THIS SHOULD BE ENDGAME();
			} else { 				//If still has cards, refill the deck and .
				refillDrawPile();
				refillCount ++;
				return drawCard();  //recurion
			}
		}
	}
	
	//discards a card chosen by the player
	public void addCardToDiscard(Card card){
		discardPile.add(card);
	}
	
	//Refills the draw pile from the discard pile.
	public void refillDrawPile(){ 
		for (int i = 0; i < discardPile.size(); i ++)
			drawPile.add(discardPile.get(i));
			
		shuffleDrawPile();
		discardPile.clear();
	}
	
	//setter and getter for the refill count
	public int getRefillCount() {
		return refillCount;
	}

	public void setRefillCount(int refillCount) {
		this.refillCount = refillCount;
	}
	
}



