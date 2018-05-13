public class Field{
	
	private Card cardType;		//holds the type of card in the field
	private int numCards = 0;	//holds the number of cards in the field
	private int fieldNumber;	//differentiates the two fields

	//constructor that sets field number
	public Field(int fieldNumber){
		setFieldNumber(fieldNumber);
	}

	//The getters and setters for card types
	public Card getCardType() {
		return cardType;
	}

	public void setCardType(Card cardType) {
		this.cardType = cardType;
	}

	//The getters and setters for number of cards
	public int getNumCards() {
		return numCards;
	}

	public void setNumCards(int numCards) {
		this.numCards = numCards;
	}

	//The getters and setters for field number
	public int getFieldNumber() {
		return fieldNumber;
	}

	public void setFieldNumber(int fieldNumber) {
		this.fieldNumber = fieldNumber;
	}
	
	//increments the number of cards in field when cards are planted
	public void increaseNumCards(int numCards) {
		this.numCards += numCards;
	}
	
	//checks if a card is valid to be planted in a field 
	public boolean cardCheck(Card card){
		return (cardType == null || cardType.getCardName() == card.getCardName()) ? true : false;
	}

	//checks if a field is empty
	public boolean isEmpty(){
		return (cardType == null) ? true : false;
	}

	//distributes coins to players when they harvest thier fields
	public int coinFromHarvest(){
		//checks the coin value of the field and returns the value
		for(int n = 4; n > 0; n--){
			if(numCards >= cardType.getCoin()[n])
				return n;
		}
		return 0;
	}
}