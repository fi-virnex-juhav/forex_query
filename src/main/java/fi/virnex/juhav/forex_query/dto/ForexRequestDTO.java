package fi.virnex.juhav.forex_query.dto;

/**
 * @author Juha Valimaki, Virnex.fi 2022-06-19
 *
 */

public class ForexRequestDTO {

	/*
	Request:
		amount:100,
		from:"USD",
		to:"SEK"
	 */

	private int		fromAmount;		// amount of from currency
	private String	fromCurrency;	// id of from currency: EUR, SEK, USD, ...
	private String	toCurrency;		// id of to currency: EUR, SEK, USD, ...
	
	public ForexRequestDTO() {}

	public int getFromAmount() {
		return fromAmount;
	}

	public void setFromAmount(int fromAmount) {
		this.fromAmount = fromAmount;
	}

	public String getFromCurrency() {
		return fromCurrency;
	}

	public void setFromCurrency(String fromCurrency) {
		this.fromCurrency = fromCurrency;
	}

	public String getToCurrency() {
		return toCurrency;
	}

	public void setToCurrency(String toCurrency) {
		this.toCurrency = toCurrency;
	}

	@Override
	public String toString() {
		return "ForexRequestDTO [fromAmount=" + fromAmount + ", fromCurrency=" + fromCurrency + ", toCurrency=" + toCurrency
				+ "]";
	}
	
	public String toJson() {
		return "{ "
			+       "\"" + "from_amount" + "\"" + ": " + fromAmount
			+ "," + "\"" + "from" + "\"" + ": " + "\"" + fromCurrency + "\""
 			+ "," + "\"" + "to" + "\"" + ": " + "\"" + toCurrency + "\""
			+ " }";
	}
	
}
