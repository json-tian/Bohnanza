
public interface Decisions {

	// decide whether the card should be planted
	public boolean plantDecide(Card card);
	
	// pick a field in which the card will be planted
	public Field pickFieldToPlant(Card card);

	// decide whether to send a trade request
	public boolean sendTradeRequest(Card card, Player player);
		
	// pick the undesired card that you would like to trade
	public Card pickCardToTrade(Player player);

	// decide which player is the trading partner
	public int acceptTradeRequest(Card[][] tradeCards);
	
	//decide whether to trade or plant the following card
	public boolean plantDecideTA(Card card);

}


