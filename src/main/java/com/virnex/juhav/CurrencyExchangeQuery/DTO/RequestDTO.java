package com.virnex.juhav.CurrencyExchangeQuery.DTO;

/**
 * @author Juha Valimaki, Virnex.fi 2022-06-19
 *
 */

public class RequestDTO {

	/*
	Request:
		amount:100,
		from:"USD",
		to:"SEK"
	 */

	private int		fromAmount;		// amount of from currency
	private String	fromCurrency;	// id of from currency: EUR, SEK, USD, ...
	private String	toCurrency;		// id of to currency: EUR, SEK, USD, ...
	
	public RequestDTO() {}

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
		return "RequestDTO [fromAmount=" + fromAmount + ", fromCurrency=" + fromCurrency + ", toCurrency=" + toCurrency
				+ "]";
	}
	
}
