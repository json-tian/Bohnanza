//Parth's AI
//This AI always makes the simpler and obvious choices first, then uses a scoring
//system based on the possible availiblitiy of the cards
public class AI2 extends Player implements Decisions{

	//constructor
	AI2(int playerNum, Pile pile) {
		super(playerNum, pile);
	}
	
	//counts the number of cards that are imediatily availible for me to plant
	public int countAvailableCards(Card card){
		int availableCards = 0;
		
		//counts the number of the particular card in hand
		for(int c = 0; c < hand.getHandPile().size(); c++){
			if(hand.getHandPile().get(c) == card)
				availableCards++;
		}
		
		//counts the number of the particular card in field.
		for (int x = 0; x < 2; x ++){
			for(int c = 0; c < fields.length; c++){
				if(fields[x].cardCheck(card) == true)
					availableCards++;
			}
		}
		
		return availableCards;
	}
	
	//counts the cards that I may recieve and plant in the future of this game.
	public int countPossibleCards(Card card){
		//the possible number of cards from the deck
		int possibleCards = card.getNumberOfCards();
		
		//subtract the cards that we know the location of
		possibleCards -= countAvailableCards(card) + countUnavailableCards(card);
		return possibleCards;
	}
	
	//counts the cards that I have no chance to plant this game
	public int countUnavailableCards(Card card){
		int unavailableCards = 0;
		
		//counts number of cards in all fields
		//loop through players
		for(int p = 0; p < 4; p++){
			//loop through fields
			for(int f = 0; f < 2; f++){
				//exclude
				if (p != playerNum){
					if(allFields[p][f].getCardType() == card)
						unavailableCards += allFields[p][f].getNumCards(); 
				}
			}
		}
		
		//add cards that have been discarded
		for(int c = 0; c < getPile().getDiscardPile().size(); c++)
			if(getPile().getDiscardPile().get(c) == card)
				unavailableCards++;
		
		return unavailableCards;
		
	}
	
	//calculate a score for each card based on thier availibility in the current game state
	public double scoreCard(Card card){

		int score = ((countAvailableCards(card) * 2) + (countPossibleCards(card))) / 104;
		return score;
	}
	
	//picks the field to plant a card
	@Override
	public Field pickFieldToPlant(Card currentCard){
			
		// if field 1 contains the same card type, pick field 1
		if (fields[0].getCardType() == currentCard){
			return fields[0];
		// else if field 2 contains the same card type, pick field 2
		}else if (fields[1].getCardType() == currentCard){
			return fields[1];
			
		//if field 1 is empty, pick field 1
		} else if (fields[0].isEmpty()){
			return fields[0];
		
		//if field 2 is empty, pick field 2 
		} else if (fields[1].isEmpty()){
			return fields[1];
			
		}else{
			//otherwise pick the field that has the card with a higher score
			if (scoreCard(fields[0].getCardType()) > scoreCard(fields[1].getCardType()))
				return fields[0];
			//otherwise pick the field that has the card with a higher score
			else if (scoreCard(fields[0].getCardType()) < scoreCard(fields[1].getCardType()))
				return fields[1];
			//pick the field with less unavailible cards
			else if (countUnavailableCards(fields[0].getCardType()) < countUnavailableCards(fields[1].getCardType()))
				return fields[0];
			//pick the field with less unavailible cards
			else if (countUnavailableCards(fields[0].getCardType()) > countUnavailableCards(fields[1].getCardType()))
				return fields[1];
			else
				return fields[0];
		}		
	}
	
	//decide whether or not to plant another card from hand
	@Override
	public boolean plantDecide(Card card){
		//plant if field is empty or if field has the same card present
		if (fields[0].isEmpty() || fields[1].isEmpty() || fields[0].getCardType() == card || fields[1].getCardType() == card)
			return true;
		return false;
	}
	
	//decides whether or not to send a trade request
	@Override
	public boolean sendTradeRequest(Card card){
		//if i can trade for a card in my fields do it.
		if(card == fields[0].getCardType() || card == fields[1].getCardType() && hand.getHandPile().contains(card) )
			return true;
		return false;
	}
	
	//selects a card to offer as a trade
	@Override
	public Card pickCardToTrade(Player player){
		
		//records the index of the worst cardd in hand
		int lowestScore = 0;
		
		//loops through hand and looks at cards.
		//picks the worst card that is still a valid trade
		for(int x = 0; x < hand.getHandPile().size(); x ++){
			for(int y = 0; y < 2; x ++){
				//and if the cards are not in either field 
				if (hand.getHandPile().get(x) != fields[y].getCardType()
						////and if the card is in the other players field
						&& (hand.getHandPile().get(x) == player.fields[y].getCardType()) && 
							//and if the card has the lowest score.
							scoreCard(hand.getHandPile().get(x)) < scoreCard(hand.getHandPile().get(lowestScore)))
					lowestScore = x;		
			}
		}
		//return the valid card with the lowest score
		return hand.getHandPile().get(lowestScore);
	}
	
	//decides whether or not, and selects player to trade with
	@Override
	//recieve an array of cards [players][card they give you,the card they want] and pick who/what to accept.
	public int acceptTradeRequest(Card[][] tradeCards) {
		
		//accept the player that will get you a plantable card
		for(int p = 0; p < tradeCards.length; p++){
			if(p != playerNum)
				if(tradeCards[p][0] == fields[0].getCardType() || tradeCards[p][0] == fields[1].getCardType())
					return p;
		}
		return -1;
	}
	
	//decides whether to plant or trade the following card in hand.
	public boolean plantDecideTA(Card card) {

		//if card in in my field plant it.
		if(fields[0].getCardType() == card || fields[1].getCardType() == card)
			return true;
		
		//otherwise plant it
		return false;
	}
}