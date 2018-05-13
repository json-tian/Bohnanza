public class TradingArea{
	
	private int tradeAreaNum;	//the trading area number
	private Card card;			//the card in the trading area
	
	//constructor
	public TradingArea(int tradeAreaNum){
		this.tradeAreaNum = tradeAreaNum;
	}
	
	//getters and setters for the trading area number
	public int getTradeAreaNum() {
		return tradeAreaNum;
	}

	public void setTradeAreaNum(int tradeAreaNum) {
		this.tradeAreaNum = tradeAreaNum;
	}

	//getters and setters for the card in the trading area
	public Card getCard(){
		return card;
	}

	//adds card into the trading area
	public void addCard(Card card) {
		this.card = card;
	}
	
	//removes card from the trading area
	public void removeCard(){
		this.card = null;
	}
	
	//checks if the trading area is empty
	public boolean isEmpty(){
		return (card == null) ? true : false;
	}
	
}
