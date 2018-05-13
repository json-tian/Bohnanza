//Tony's AI
//Author: Tony Huang
//Note: in the comments the player that the AI is controlling is referred to "me"
//		for example the fields of the player is referred to as "my fields"
public class AI3 extends Player implements Decisions{

	/**constructor**/
	AI3(int playerNum, Pile pile) {
		//calling the constructor of super to set player number and pile
		super(playerNum, pile);
		 
	}
	
	/**determine how many of the given card is in my hand**/
	public int numCardsInHand(Card card){
	
		int numCards = 0; //counter for the number of cards
		
		for(int c = 0; c < hand.getHandPile().size(); c++) //loop through each individual card in my hand
			if(hand.getHandPile().get(c) == card) //if the current card equals to the given card
				numCards++; //increase the counter by 1
		
		return numCards; //return the number of the given card in my hand
	}
	
	/**determine how many cards are needed in that field to get the next coin value**/
	public int numCardsUntilNextCoin(Field field){
		
		if(field.coinFromHarvest() < 4){ //if coin from harvest is less than 4
			//number of card(s) needed in TOTAL in order to get the next coin value
			int numCardForNextCoin = field.getCardType().getCoin()[field.coinFromHarvest() + 1];
			int numCardInField = field.getNumCards(); //number of cards in the given field
			
			return numCardForNextCoin - numCardInField; //return number of cards needed to get the next coin value
		}
		
		return 0; //if 4 coins can be harvested then return 0 cards needed
		
	}
	
	/**calculate the value of the given card**/
	public double cardValue(Card card){
		
		//if the card is null return a negative value
		if(card == null)
			return -1.0;
		
		double availableCards = card.getNumberOfCards(); //number of given card still accessible
		double playerCards = numCardsInHand(card); //number of given card that I have
		double reward = 0; //max amount of coins that I will receive based on available cards 
		double cardsNeeded = 0; //number of cards needed to get the reward
		
		/**determine how many of that card is still available**/
		//determine number of that card in other player's fields
		for(int p = 0; p < 4; p++) //loop through each individual player
			for(int f = 0; f < 2; f++) //loop through each field of each player
				if(p != playerNum && allFields[p][f].getCardType() == card) //if the player is not me and that card matches the card(s) in my field
					availableCards -= allFields[p][f].getNumCards(); //subtract the cards in my field from available cards
				
		//determine number of that card in the discard pile
		for(int c = 0; c < pile.getDiscardPile().size(); c++) //loop through each individual card in the discard pile
			if(pile.getDiscardPile().get(c) == card) //if the current card in the discard equals to that card
				availableCards--; //subtract 1 from available cards
				
		/** determine the amount of that card my player have**/
		//determine number of that card in my fields
		for(int f = 0; f < fields.length; f++) //loop through my fields
			if(fields[f].getCardType() == card) //if the current field contains that card
				playerCards += fields[f].getNumCards(); //add the number of that card in my field to player cards
		
		//determine number of that card in my trading areas
		for(int t = 0; t < tradingAreas.length; t++) //loop through my trading areas
			if(!tradingAreas[t].isEmpty() && tradingAreas[t].getCard() == card) //if the current trading area is not empty and it contains that card
				playerCards ++; //increase player cards
				
		/** determine the max amount of cards you need for the max amount of coins and max amount of coins (based on available cards)**/
		for(int c = 1; c < 5; c++) //loop through each coin value
			if(availableCards >= card.getCoin()[c]){ //if available cards in greater or equal to the amount of cards needed for max coin value
				reward = c; //set max coin value to reward
				cardsNeeded = card.getCoin()[c]; //set number of cards needed for max coin value to cards needed
			}			
	
		/**calculate card potential**/
		return playerCards / cardsNeeded * reward; //the amount of coins I will receive based on the given card 
			
	}
	
	/**pick a field to plant the given card**/
	@Override
	public Field pickFieldToPlant(Card card){
		
		// if field 1 contains the card or field 1 is empty and field 2 does not contain the card
		if (fields[0].isEmpty() && card != fields[1].getCardType() || fields[0].getCardType() == card){
			return fields[0]; //plant to field 1
		// if field 2 contains the card or field 2 is empty and field 1 does not contain the card
		}else if(fields[1].isEmpty() && card != fields[0].getCardType() || fields[1].getCardType() == card){
			return fields[1]; //plant to field 2
			//if the card in field 1 is valued more than the card in field 2
		}else if(cardValue(fields[0].getCardType()) > cardValue(fields[1].getCardType())){
			return fields[1]; //plant to field 2	
			//if the card in field 2 is valued more than the card in field 1
		}else if (cardValue(fields[1].getCardType()) > cardValue(fields[0].getCardType())){
			return fields[0]; //plant to field 1
		//if my hand does not contain the card in field 1 and I am more than 1 card away from next coin value
		}else if (!hand.getHandPile().contains(fields[0].getCardType()) && numCardsUntilNextCoin(fields[0]) > 1){
			return fields[0]; //plant to field 1
			//if my hand does not contain the card in field 2 and I am more than 1 card away from next coin value
		}else if (!hand.getHandPile().contains(fields[1].getCardType()) && numCardsUntilNextCoin(fields[1]) > 1){
			return fields[1]; //plant to field 2
		}
		
		return fields[(int) Math.random()]; //randomly select a field to plant
		
	}

	/**decide whether to plant the given card (from hand) or not plant it**/
	@Override
	public boolean plantDecide(Card card) {
		
		for(int f = 0; f < 2; f++){ //Loop through both of my fields.
			
			if (fields[f].isEmpty() //If the field is empty.
					|| fields[f].getCardType() == card //If the field contains the card
					||cardValue(card) > cardValue(fields[f].getCardType()) //If the card is valued more than the card in my field.
					||!hand.getHandPile().contains(fields[f].getCardType()) && numCardsUntilNextCoin(fields[f]) <= 1){ //If I no longer have any card in my hand that is the same as my field and I can receive 1 coin if I harvest the field.
				
				return true; //Plant the card.
				
			}
			
		}
	
		return false; //If all cases above is false, do not plant the card.
		
	}
	
	/**decide whether to plant the given card (from trading area) or trade it**/
	public boolean plantDecideTA(Card card){
		
		//if field 1 is empty or field 2 is empty or field 1 contains the card or field 2 contains the card
		if (fields[0].isEmpty() || fields[1].isEmpty() || fields[0].getCardType() == card || fields[1].getCardType() == card)
			return true; //plant the card
		//if the card is valued more that the card both of my fields
		else if(cardValue(card) > cardValue(fields[0].getCardType()) || cardValue(card) > cardValue(fields[1].getCardType()))
			return true; //plant the card
		//if both cases above were not true	
		return false; //trade the card
		
	}
	
	/**pick a card from hand to trade with the given player**/
	@Override
	public Card pickCardToTrade(Player player){
		
		//if my hand contains both cards in other player's field
		if(hand.getHandPile().contains(player.getFields()[0].getCardType()) && hand.getHandPile().contains(player.getFields()[1].getCardType())){
			//compare the 2 cards and trade the card with the least value
			if(cardValue(player.getFields()[0].getCardType())>cardValue(player.getFields()[1].getCardType()))
				return player.getFields()[1].getCardType(); 
			else
				return player.getFields()[0].getCardType();
		//if my hand only contains the card in player's field 1
		}else if(hand.getHandPile().contains(player.getFields()[0].getCardType())){
			return player.getFields()[0].getCardType(); //trade the card that is in player's field 1
		//if my hand only contains the card in player's field 2
		}else if(hand.getHandPile().contains(player.getFields()[1].getCardType())){ 
			return player.getFields()[1].getCardType(); //trade the card that is in player's field 1
		}

		return player.getFields()[(int) Math.random()].getCardType(); //if everything else does not work return field 1 card
		
	}
	
	/**decide whether to send a trade request for the given card or not**/
	@Override
	public boolean sendTradeRequest(Card card, Player player){
		
		for(int f = 0; f < 2; f++){ //loop through both fields
			
			//if my hand contains cards in the other player's current field
			if(hand.getHandPile().contains(player.getFields()[f].getCardType())){
				//if the player's current field is not empty and the player's current field can harvest coins greater or equal to 2 and if I am going to pick that card to trade with the player
				if(!player.getFields()[f].isEmpty() && player.getFields()[f].coinFromHarvest() >= 2 && pickCardToTrade(player) == player.getFields()[f].getCardType()){
					return false; //do not send a trade request
				//if both of my fields are empty
				}else if(fields[0].isEmpty() || fields[1].isEmpty()){
					//compare the given card with the cards in my hand
					for(int c = 0; c < hand.getHandPile().size(); c++){
						//if the given card is valued more than any card in my hand
						if(cardValue(card) > cardValue(hand.getHandPile().get(c))){
							return true; //send trade request
						}
					}
				//if field 1 or field 2 contains the card
				}else if(card == fields[0].getCardType() || card == fields[1].getCardType()){
						return true; //send trade request
				//if the field is not empty and the card is valued more than the cards in my field
				}else if (!fields[0].isEmpty() && cardValue(card) > cardValue(fields[0].getCardType()) || !fields[0].isEmpty() && cardValue(card)>cardValue(fields[1].getCardType())){
					return true; //send trade request
				}
			}
		}

		return false; //if every case above is false then do not send trade request
		
	}
	
	/**decide whether to accept the offers from other players or not**/
	@Override
	public int acceptTradeRequest(Card[][] tradeCards) {

		int partnerNum = 0; //trading partner number (start with the human player)
		
		//search the the card with the best value from the offers of other players
		for(int p = 0; p < tradeCards.length; p++) //loop through the offers of each player
			if(p != playerNum) //if current player does not equal to me
				if(cardValue(tradeCards[partnerNum][0]) < cardValue(tradeCards[p][0])) //if the card of current player is valued more than the card of my trading partner
					partnerNum = p; //current player becomes my trading partner
					
		//decide whether the best card is better than the cards in my field
		if(cardValue(tradeCards[partnerNum][0]) > cardValue(fields[0].getCardType()) || cardValue(tradeCards[partnerNum][0]) > cardValue(fields[1].getCardType())) //if the best card is better than the cards in my field
			return partnerNum; //trade with my trading partner
		
		return -1; //if every case above is false deny the trade request
		
	}
	
}