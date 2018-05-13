import java.util.ArrayList;

//Every player has a hand whcih they can see
public class Hand{
	
	//An array list that holds the cards in each players hand.
	private ArrayList<Card> handPile = new ArrayList<Card>();
	
	//constructor
	public Hand(){
	}
	
	//The getters and setters for the hand
	public ArrayList<Card> getHandPile() {
		return handPile;
	}

	public void setHandPile(ArrayList<Card> handPile) {
		this.handPile = handPile;
	}
	
	//adds a card to the players hand
	public void addCard(Card card){
		handPile.add(card);
	}
	
	//removes a card from the players hand
	public void removeCard(int index){
		handPile.remove(index);
	}
	
	//gets a specific card from the hand
	public Card getCard(int index){
		return handPile.get(index);
	}
	
}