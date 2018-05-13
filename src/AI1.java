import java.util.ArrayList;
import java.util.Random;

//Jason's AI
//Author: Jason Tian
//Overall AI Strategy: Selfish/Conservative - If a trade or interaction with another player benefits me, I accept.
public class AI1 extends Player implements Decisions{

	AI1(int playerNum, Pile pile) {
		super(playerNum, pile);
	}
	
	@Override
	/*
	Function: AI picks which field to plant the card in (field 1 or field 2)
	Steps:
		1) If card is same as field, or empty, plant it in that field
		2) If the card is different from the fields
			2.1) If all your cards in your hand are different from the fields, harvest one with highest coin value
				2.1.1) If same coin value, use probability formula: (total - discard - other player fields)/(cards for next coin value) - harvest lower probability
				2.1.2) If probability is the same, harvest random field
			2.2) If all some cards in your hand are same as the field, hypothetically add hand cards to field and harvest lower coin value
				2.2.1) If hand and field still has same coin value: harvest whichever one is the farthest away from hand
	*/
	public Field pickFieldToPlant(Card currentCard){
		//CASE 1) If card is same as field, or empty, plant it in that field
		//if player's field 1 is empty or the field contains the same card type
		if (fields[0].isEmpty() || fields[0].getCardType() == currentCard){
			return fields[0];	//return field 1
		//if player's field 2 is empty or the field contains the same card type
		} else if (fields[1].isEmpty() || fields[1].getCardType() == currentCard){
			return fields[1];	//return field 2
		} else {
			//CASE 2) If the card is different from the fields
			boolean handField = false;	//handField - TRUE if there are cards in hand same as field, otherwise FALSE

			int handfield1 = 0;	//handfields[0] - Number of cards in hand same as fields[0]
			int handfield2 = 0;	//handfields[1] - Number of cards in hand same as fields[1]

			//Looping through hand to see if they match with field
			for (int x = 0; x < hand.getHandPile().size(); x ++) {
				if (hand.getCard(x) == fields[0].getCardType()) {
					//Count how many times they match
					handfield1 ++;
					handField = true;
				} else if (hand.getCard(x) == fields[1].getCardType()) {
					handfield2 ++;
					handField = true;
				}
			}

			//CASE 2.1) If all your cards in your hand are different from the fields, harvest one with highest coin value
			//If hands are different from fields
			if (handField == false) {
				//If you get more coins from field 1, then harvest field 1
				if(fields[0].coinFromHarvest() > fields[1].coinFromHarvest())
					return fields[0];
				//If you get more coins from field 2, then harvest field 2
				else if (fields[1].coinFromHarvest() > fields[0].coinFromHarvest())
					return fields[1];

				//CASE 2.1.1) If same coin value, use probability formula: (total - discard - other player fields)/(cards for next coin value)
				else {
					//Applying the formula (total - discard - other player fields)/(cards for next coin value)

					Random rand = new Random();

					int totalField1 = fields[0].getCardType().getNumberOfCards();	//Counter for how many total cards in rotation
					int totalField2 = fields[1].getCardType().getNumberOfCards();

					totalField1 -= retrieveDiscardPile(fields[0].getCardType());	//Retrieving number of cards same as field in discard pile
					totalField2 -= retrieveDiscardPile(fields[1].getCardType());

					totalField1 -= subtractOtherFields(0);	//Subtracting the number of cards same as field from other player fields
					totalField2 -= subtractOtherFields(1);

					//Formula: (total - discard - other player fields)/(cards for next coin value)
					double probabilityfield1 = totalField1 / ((fields[0].getCardType().getCoin()[fields[0].coinFromHarvest()+1]) - fields[0].getNumCards());
					double probabilityfield2 = totalField2 / ((fields[1].getCardType().getCoin()[fields[1].coinFromHarvest()+1]) - fields[1].getNumCards());

					//Harvest whichever one has lower probability
					//If fields[0] is higher than fields[1]
					if (probabilityfield1 > probabilityfield2)
						return fields[1];
					//If fields[0] is lower than fields[1]
					else if (probabilityfield1 < probabilityfield2)
						return fields[0];
					//If both fields have same probability, randomly select field
					else {
						int r = rand.nextInt(2) + 1;
						if (r == 1)
							return fields[0];
						else
							return fields[1];
					}
				}
			//CASE 2.2) If all some cards in your hand are same as the field, hypothetically add hand cards to field and harvest lower coin value
			} else if (handField == true) {

				int handAndfields1 = fields[0].getNumCards() + handfield1;	//Number of field cards in hand and field
				int handAndfields2 = fields[0].getNumCards() + handfield2;

				int handAndFieldCoin1 = calculateCoinValue(handAndfields1, fields[0]);	//Coin value when field and hand are combined
				int handAndFieldCoin2 = calculateCoinValue(handAndfields1, fields[1]);

				//Comparing coin values from hand and field, harvest the one with less coin value
				if (handAndFieldCoin1 > handAndFieldCoin2)
					return fields[1];
				else if (handAndFieldCoin1 < handAndFieldCoin2)
					return fields[0];

				//CASE 2.2.1) If hand and field still has same coin value: harvest whichever one is the farthest away from hand
				//Looping through hand to find the last hand that has that the same cardtype as the field
				else {
					for (int i = 0; i < hand.getHandPile().size(); i ++) {
						if (hand.getCard(i) == fields[0].getCardType())
							//Subtract it from hand and field total
							handAndfields1 --;
						else if (hand.getCard(i) == fields[1].getCardType())
							handAndfields2 --;

						//Retrieving coin values for when hand and field are combined
						if (handAndfields1 == fields[1].getCardType().getCoin()[handAndFieldCoin1]) 
							return fields[1];
						else if (handAndfields2 == fields[1].getCardType().getCoin()[handAndFieldCoin2]) 
							return fields[0];
					}
				}
			}
		}
		return fields[0];
	}

	@Override
	/*
	Function: Decides whether or not to plant a second card from AI's hand
	Steps:
		1) If empty, continue to plant
		2) If card exists in field already, plant it
		3) otherwise, if field(s) are one card away from next coin value, keep card in hopes get another card for field
		4) else, do not plant a second card
	*/
	public boolean plantDecide(Card card) {
		//CASE 1) If empty, continue to plant
		if (fields[0].getNumCards() == 0 || fields[1].getNumCards() == 0)
			return true;

		//CASE 2) If card exists in field already, plant it
		if (card == fields[0].getCardType() || card == fields[1].getCardType())
			return true;

		//CASE 3) otherwise, if field(s) are one card away from next coin value, keep card in hopes get another card for field
		int field1CoinValue = calculateCoinValue(fields[0].getNumCards(), fields[0]);
		int field2CoinValue = calculateCoinValue(fields[1].getNumCards(), fields[1]);

		//If the field only is not 1 card away from next coin value, plant
		if (((fields[0].getCardType().getCoin()[field1CoinValue + 1]) - 1) > fields[0].getNumCards())
			return true;
		else if (((fields[1].getCardType().getCoin()[field2CoinValue + 1]) - 1) > fields[1].getNumCards())
			return true;

		//CASE 4) if none of the above are true, do not plant
		return false;
	}

	@Override
	/*
	Function: picks a card from hand to trade with "trader"
	Strategy: Pick the card that benefits me, ignore opponents (conservative)
	Steps:
	Since you can only offer a card they have in their field, you may have up to 2 different card choices
	1) If you only have one card that's same as their field, offer that card
	2) If you have one of each card of the player's field, hypothetically add both types of cards with your hand and check coin value. Trade lower coin value
		3.1) If both have same coin value, use formula (Total-Discard-Other player's field) / (Cards for next coin value)
		3.2) If formula gives same probability, pick card randomly farthest away from hand
	 */
	public Card pickCardToTrade(Player trader){
		ArrayList<Integer> trader1Field = new ArrayList<Integer>();		//Holds hand index where hand cards are same as trader's fields
		ArrayList<Integer> trader2Field = new ArrayList<Integer>();

		//Adding index whenever there's a card in hand that matches trader's fields
		for(int n = 0; n < hand.getHandPile().size(); n++){
			if((hand.getHandPile().get(n) == trader.getFields()[0].getCardType()))
				trader1Field.add(n);
			else if ((hand.getHandPile().get(n) == trader.getFields()[1].getCardType()))
				trader2Field.add(n);
		}
		
		//CASE 1) If you only have one card that's same as their field, offer that card
		if (trader1Field.size() == 0 && trader2Field.size() == 1)
			return hand.getCard(trader2Field.get(0));
		else if (trader1Field.size() == 1 && trader2Field.size() == 0)
			return hand.getCard(trader1Field.get(0));
		else if (trader1Field.size() >= 1 && trader2Field.size() >= 1) {

			//CASE 2) If you have one of each card of the player's field, hypothetically add both types of cards with your hand and check coin value. Trade lower coin value
			//Checking coin value of cards in hand of trader's field types
			int coinValueOfHand1 = calculateCoinValue(trader1Field.size(), trader.getFields()[0]);	//Coin values of hand cards of trader's field types
			int coinValueOfHand2 = calculateCoinValue(trader2Field.size(), trader.getFields()[1]);

			//If card type of trader's field1 gives more coins than trader's field 2, trade field 2 card
			if (coinValueOfHand1 > coinValueOfHand2)
				return hand.getCard(trader2Field.get(0));
			//If card type of trader's field2 gives more coins than trader's field 1, trade field 1 card
			else if (coinValueOfHand2 > coinValueOfHand1)
				return hand.getCard(trader1Field.get(0));
			else {
				//CASE 3.1) If both have same coin value, use formula (Total-Discard-Other player's field) / (Cards for next coin value)
				//			To determine probability for getting next coin value
				//Applying the formula
				int cardsInRotation1 = trader.getFields()[0].getCardType().getNumberOfCards();		//Counter for how many total cards in rotation
				int cardsInRotation2 = trader.getFields()[1].getCardType().getNumberOfCards();

				int currentCoinValue1 = 0;
				int currentCoinValue2 = 0;

				//Removing cards from discard pile from total cards
				cardsInRotation1 -= retrieveDiscardPile(trader.getFields()[0].getCardType());
				cardsInRotation2 -= retrieveDiscardPile(trader.getFields()[1].getCardType());

				//Removing cards from other player's fields
				cardsInRotation1 -= subtractOtherFields(0);
				cardsInRotation2 -= subtractOtherFields(1);

				//Getting current coinvalue for both fields to compare it to future coinvalue
				currentCoinValue1 = calculateCoinValue(trader1Field.size(), fields[0]);
				currentCoinValue2 = calculateCoinValue(trader2Field.size(), fields[1]);

				//Calculating probability
				//Formula:(Total-Discard-Other player's field) / (Cards for next coin value)
				double probabilityfield1 = cardsInRotation1 / (trader.getFields()[0].getCardType().getCoin()[currentCoinValue1+1] - trader1Field.size());
				double probabilityfield2 = cardsInRotation2 / (trader.getFields()[1].getCardType().getCoin()[currentCoinValue2+1] - trader1Field.size());

				//Trade whichever one has lower probability
				if (probabilityfield1 > probabilityfield2)
					return hand.getCard(trader2Field.get(trader2Field.size()-1)); //returning last card of that type from hand (to maximize coins)
				//If fields[0] is lower than fields[1]
				else if (probabilityfield1 < probabilityfield2)
					return hand.getCard(trader1Field.get(trader1Field.size()-1));

				//If both fields have same probability, randomly select field
				else {
					Random rand = new Random();

					int r = rand.nextInt(2) + 1;
					if (r == 1)
						return hand.getCard(trader2Field.get(trader2Field.size()-1));
					else
						return hand.getCard(trader1Field.get(trader1Field.size()-1));
				}
			}
		}	
		return hand.getCard(0);
	}

	@Override
	/* 	
	Function: Whether or not AI should offer trade to another player
	Strategy: If trading is possible, do the trade
	Steps: 	
		1) If the card matches my field, and if there's a card in my hand that is not in my field and has in their field, send true
		2) If both fields are empty, and the offered card is the same as the first or second card in my hand, send trade
			2.1) Otherwise, if the card matches my field, and if there's a card in my hand that is not in my field and has in their field, send true
	 */
	public boolean sendTradeRequest(Card card, Player player){
		//CASE 1) If the card matches my field, and if there's a card in my hand that is not in my field and has in their field, send true
		//Loops through hand to make sure not to offer a card from hand that I have in my field
		for (int i = 0; i < hand.getHandPile().size(); i ++) {
			//If the card I'm going to offer is the same as the card, do not send offer
			if (hand.getCard(i) != card) {
				if ((hand.getCard(i) != fields[0].getCardType()) && (hand.getCard(i) != fields[1].getCardType())) {
					//Checks that there's a card that matches trader's field (validate)
					if ((player.getFields()[0].getCardType() == hand.getCard(i)) || (player.getFields()[1].getCardType() == hand.getCard(i))) {
						//If the trading area card is equal to my fields, accept
						if(card == fields[0].getCardType() || card == fields[1].getCardType())
							return true;
						else {
							
							//CASE 2) If both fields are empty, and the offered card is the same as the first or second card in my hand, send trade
							if (fields[0].isEmpty() == true && fields[1].isEmpty() == true) {
								if (hand.getCard(0) == card || hand.getCard(1) == card)
									return true;
							//CASE 2.1) Otherwise, if the card matches my field, and if there's a card in my hand that is not in my field and has in their field, send true
							} else if (fields[0].isEmpty() == true || fields[1].isEmpty() == true) {
								if (hand.getCard(0) == card)
									return true;
							}
						}
					}
				}
			}
		}
		return false;	//If not accepted yet, decline
	}

	@Override
	/*
	Function: Decides whether or not to trade or to plant the card from trading area 
	TRUE: Plant	FALSE: Trade
	Steps:
		1) If the card exists in the field already, plant it
		2) Otherwise, try to trade it off
	 */
	
	public boolean plantDecideTA(Card card) {
		//CASE 1) If card exists in field already, plant it
		if (card == fields[0].getCardType() || card == fields[1].getCardType())
			return true;
		return false;	//CASE 2) Otherwise, try to trade it off
	}

	@Override
	/*
	Function: Decides which trade to accept, if any at all
	Steps:
		1) Loop through trades, and see if any are same as your fields. Decline if none are the same
		2) If there is only one trade left, then accept that trade
		3) If there are multiple trades left and are all the same card, randomly select one
			3.1) If there are multiple trades left and different card offered, select favoured card by calculating coin value after card has been traded
			3.2) If coin values are equal, use formula (Total - Discard - fields from other players) / (cards until next coin value)
			3.3) If probability is equal, select any field
	 */
	public int acceptTradeRequest(Card[][] tradeCards) {

		//boolean for each player's offer - FALSE: decline TRUE: may accept trade
		boolean[] tradeStatus = new boolean[4];

		//Looping through all the players
		for (int i = 0; i < 4; i ++) {
			if (i != 1) {
				tradeStatus[i] = true;
				//CASE 1) Loop through trades, and see if any are same as your fields. Decline if none are the same
				if (tradeCards[i][0] != fields[0].getCardType() && tradeCards[i][0] != fields[0].getCardType())
					tradeStatus[i] = false;
			}
		}
		//CASE 2) If there is only one trade left, then accept that trade
		//If only player 0 is valid, trade with them
		if (tradeStatus[0] == true && tradeStatus[2] == false && tradeStatus[3] == false)
			return 0;
		//If only player 2 is valid, trade with them
		else if (tradeStatus[0] == false && tradeStatus[2] == true && tradeStatus[3] == false) //method returns 2 if there's only 1 trade remaining
			return 2;

		//If only player 3 is valid, trade with them
		else if (tradeStatus[0] == false && tradeStatus[2] == false && tradeStatus[3] == true) //method returns 2 if there's only 1 trade remaining
			return 3;
		
		//If neither card is same as field, do not trade
		if (tradeStatus[0] == false && tradeStatus[2] == false && tradeStatus[3] == false)
			return -1;

		//CASE 3) If there are multiple trades left and are all the same card, randomly select one
		//If all players are valid, check offer and choose favoured card
		else if (tradeStatus[0] == true && tradeStatus[2] == true && tradeStatus[3] == true) {
			if (tradeCards[0][0] == tradeCards[2][0] && tradeCards[2][0] == tradeCards[3][0]) {
				Random random = new Random();
				int r = random.nextInt(3) + 1;
				if (r == 1)
					return 0;
				else if (r == 2)
					return 2;
				else if (r == 3)
					return 3;
			} else {
				//CASE 3.1) If there are multiple trades left and different card offered, select favoured card by calculating coin value after card has been traded
				//Checking coin value of each field after trade (if it occurs)
				int coinValue1AfterTrade = calculateCoinValue(fields[0].getNumCards() + 1, fields[0]);
				int coinValue2AfterTrade = calculateCoinValue(fields[1].getNumCards() + 1, fields[1]);

				//Accept the card that has higher coin value after trading
				if (coinValue1AfterTrade > coinValue2AfterTrade) {
					if (tradeCards[0][0] == fields[0].getCardType())
						return 0;
					else if (tradeCards[2][0] == fields[0].getCardType())
						return 2;
					else if (tradeCards[3][0] == fields[0].getCardType())
						return 3;
				} else if (coinValue2AfterTrade > coinValue1AfterTrade) {
					if (tradeCards[0][0] == fields[1].getCardType())
						return 0;
					else if (tradeCards[2][0] == fields[1].getCardType())
						return 2;
					else if (tradeCards[3][0] == fields[1].getCardType())
						return 3;
					
				//CASE 3.2) If coin values are equal, use formula (Total - Discard - fields from other players) / (cards until next coin value)
				} else {
					int totalField1 = fields[0].getCardType().getNumberOfCards();		//Counter for how many total cards in rotation
					int totalField2 = fields[1].getCardType().getNumberOfCards();

					totalField1 -= retrieveDiscardPile(fields[0].getCardType());
					totalField2 -= retrieveDiscardPile(fields[1].getCardType());

					totalField1 -= subtractOtherFields(0);
					totalField2 -= subtractOtherFields(1);

					//Formula: (total - discard - other player fields)/(cards for next coin value)
					double probabilityField1 = totalField1 / ((fields[0].getCardType().getCoin()[fields[0].coinFromHarvest()+1]) - fields[0].getNumCards());
					double probabilityField2 = totalField2 / ((fields[1].getCardType().getCoin()[fields[1].coinFromHarvest()+1]) - fields[1].getNumCards());

					//Choose whichever one has higher probability
					//If first field has higher probability than second field, accept any offer equal to first field
					//Or if probability is the same, just accept the first field
					if (probabilityField1 > probabilityField2 || probabilityField1 == probabilityField2) {
						if (tradeCards[0][0] == fields[0].getCardType())
							return 0;
						else if (tradeCards[2][0] == fields[0].getCardType())
							return 2;
						else if (tradeCards[3][0] == fields[0].getCardType())
							return 3;
					}
					//If second field has higher probability than first field, accept any offer equal to second field
					if (probabilityField2 > probabilityField1) {
						if (tradeCards[0][0] == fields[1].getCardType())
							return 0;
						else if (tradeCards[2][0] == fields[1].getCardType())
							return 2;
						else if (tradeCards[3][0] == fields[1].getCardType())
							return 3;
					}
				}	
			}
		}
		//cancel all trades if not yet accepted
		return -1;
	}

	//Function: returns number of cards in discard pile that are the same as the fieldcard
	public int retrieveDiscardPile(Card fieldCard) {
		int counter = 0;
		//Retreiving cards from discard pile
		for (int y = 0; y < pile.getDiscardPile().size(); y++) {
			if (pile.getDiscardPile().get(y) == fieldCard)
				//Count number of cards same as fieldCard in the discardPile
				counter++;
		}
		return counter;
	}

	//Function: counts how many other player's cards in fields are the same as my field card type
	public int subtractOtherFields(int fieldNum) {
		int counter = 0;
		for (int i = 0; i < 4; i ++) {
			//Ignoring player 1 (myself)
			if (i != 1) {
				//If player's cards in fields are the same as my field card type
				if (allFields[i][fieldNum].getCardType() == fields[fieldNum].getCardType())
					//Add to counter
					counter += allFields[i][fieldNum].getNumCards();
			}
		}
		return counter;
	}

	//Function: calculates the coin value given a number of cards and cardtype
	public int calculateCoinValue(int numCard, Field field) {
		//Getting currentcoinvalue for both fields
		for(int n = 4; n > 0; n--) {
			//Retrieving coin value of given number of cards
			if(numCard >= field.getCardType().getCoin()[n]) {
				return n;
			}
		}
		return 0;
	}
}
