package com.virnex.juhav.CurrencyExchangeQuery.DTO;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Juha Valimaki, Virnex 2022-06-19
 *
 */

/*
Example of a json response from the API on web:

{
"success": true,
"query": {
	"from": "EUR",
	"to": "USD",
	"amount": 1
},
"info": {
	"timestamp": 1652957583,
	"rate": 1.052443
},
"date": "2022-05-19",
"result": 1.052443
}
 */

public class ResponseDTO {
	
	SimpleDateFormat f = new SimpleDateFormat("yyyyMMdd");

    // Printing current date 
	    
	private static final SimpleDateFormat YYYYMMDD = new SimpleDateFormat("yyyyMMdd");
	private static final SimpleDateFormat YYYY_MM_DD = new SimpleDateFormat("yyyy-MM-dd");
	private static final SimpleDateFormat YYYYMMDD_HH = new SimpleDateFormat("yyyyMMdd HH");
	private static final SimpleDateFormat YYYYMMDD_HHMM = new SimpleDateFormat("yyyyMMdd HH:mm");
	private static final SimpleDateFormat YYYYMMDD_HHMMSS = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
	private static final SimpleDateFormat YYYY_MM_DD_HH = new SimpleDateFormat("yyyy-MM-dd HH");
	private static final SimpleDateFormat YYYY_MM_DD_HHMM = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	private static final SimpleDateFormat YYYY_MM_DD_HHMMSS = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	
	private Date	date;

	private int		fromAmount;		// amount of "from currency"	[1, 2, 3, ... ]
	private String	fromCurrency;	// id of "from currency":	["USD", "EUR", "SEK", ...]
	private double	toAmount;		// amount of "to currency"	e.g. 1.37
	private String	toCurrency;		// id of "to currency":		["EUR", "SEK", "USD", ...]
	private double	exchangeRate;	// amount of "to currency" for 1 unit of "from currency"

	public static final String toCurrencyString(double d) {
		String dAsString = Double.toString(d);
		int dotIndex = dAsString.indexOf(".");
		int length = dAsString.length();	
		if ( d >= 1.0 ) {
			return dAsString.substring(0,Math.min(dotIndex+3, length));
		}
		
		if ( dAsString.startsWith("0.") == false ) {
			return dAsString;
		}
		
		int index1 = dotIndex + 1;
		if ( index1 >= length ) {
			return dAsString;
		}

		while ( dAsString.charAt(index1) == '0' ) {
			if ( index1 + 1 < length) {
				index1++;
			}
			else {
				return dAsString;
			}
		}
		return dAsString.substring(0, Math.min(index1 + 3, length) ); 
	}
	
	public ResponseDTO() {}

	public Date getDate() {
		return date;
	}
	
	public String getDate(SimpleDateFormat simpleDateFormat) {
		return simpleDateFormat.format(date);
	}

	public void setDate(Date date) {
		this.date = date;
	}

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

	public double getToAmount() {
		return toAmount;
	}

	public void setToAmount(double toAmount) {
		this.toAmount = toAmount;
	}

	public String getToCurrency() {
		return toCurrency;
	}

	public void setToCurrency(String toCurrency) {
		this.toCurrency = toCurrency;
	}

	public double getExchangeRate() {
		return exchangeRate;
	}

	public void setExchangeRate(double exchangeRate) {
		this.exchangeRate = exchangeRate;
	}

	@Override
	public String toString() {
		return "ResponseDTO [date=" + date + ", fromAmount=" + fromAmount + ", fromCurrency=" + fromCurrency
				+ ", toAmount=" + toAmount + ", toCurrency=" + toCurrency + ", exchangeRate=" + exchangeRate + "]";
	}
	
	public String toUserStringDateTime() {
		return ""
			+ "date: " + getDate(YYYY_MM_DD_HHMM) + System.lineSeparator() 
			+ fromAmount + System.lineSeparator()
			+ fromCurrency + " to " + toCurrency + System.lineSeparator() 
			+ toCurrencyString(toAmount) + System.lineSeparator()
			+ "rate: " + toCurrencyString(exchangeRate) + System.lineSeparator();
	}
	
	public String toUserStringDate() {
		return ""
			+ "date: " + getDate(YYYY_MM_DD) + System.lineSeparator() 
			+ fromAmount + System.lineSeparator()
			+ fromCurrency + " to " + toCurrency + System.lineSeparator() 
			+ toCurrencyString(toAmount) + System.lineSeparator()
			+ "rate: " + toCurrencyString(exchangeRate) + System.lineSeparator();
	}
	
	public String toDailyJsonString() {
		return "{"
				  + "\"" + "date" + "\"" + ":" + "\"" + getDate(YYYY_MM_DD) + "\"" 
			+ "," + "\"" + "from_amount" + "\"" + ":" + fromAmount
			+ "," + "\"" + "from" + "\"" + ":" + "\"" + fromCurrency + "\""
 			+ "," + "\"" + "to_amount" + "\"" + ":" + toAmount
			+ "," + "\"" + "to" + "\"" + ":" + "\"" + toCurrency + "\""
			+ "," + "\"" + "rate" + "\"" + ":" + exchangeRate
			+ "}";
	}
	
	public String toShortDailyJsonString() {
		return "{"
				  + "\"" + "date" + "\"" + ":" + "\"" + getDate(YYYYMMDD) + "\"" 
			+ "," + "\"" + "from_amount" + "\"" + ":" + fromAmount
			+ "," + "\"" + "from" + "\"" + ":" + "\"" + fromCurrency + "\""
 			+ "," + "\"" + "to_amount" + "\"" + ":" + toAmount
			+ "," + "\"" + "to" + "\"" + ":" + "\"" + toCurrency + "\""
			+ "," + "\"" + "rate" + "\"" + ":" + exchangeRate
			+ "}";
	}
	
	public String toLogRateJsonString() {
		return "{"
				  + "\"" + "date" + "\"" + ":" + "\"" + getDate(YYYYMMDD_HHMM) + "\"" 
				  + "," + "\"" + "from" + "\"" + ":" + "\"" + fromCurrency + "\""
				  + "," + "\"" + "to" + "\"" + ":" + "\"" + toCurrency + "\""
				  + "," + "\"" + "rate" + "\"" + ":" + exchangeRate
			+ "}";
	}

	
	
	
}
