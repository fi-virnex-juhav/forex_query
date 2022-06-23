package com.virnex.juhav.CurrencyExchangeQuery.Controller;

/**
 * @author Juha Valimaki, new guy at virnex.fi June 19 2022, 2022-06-19
 *
 */

import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.Optional;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.virnex.juhav.CurrencyExchangeQuery.DAO.CurrencyExchangeDAO;
import com.virnex.juhav.CurrencyExchangeQuery.DTO.RequestDTO;
import com.virnex.juhav.CurrencyExchangeQuery.DTO.ResponseDTO;

// data source:
// https://apilayer.com/marketplace/description/exchangerates_data-api 

@RestController
public class Controller {

	private static final String EXPIRED_APIKEY = "HZOwa1dYHjD4MEEA0KNPsOiOEwqhQcvt";
	private static final int VALID_APIKEY_LENGTH = EXPIRED_APIKEY.length();

	private static final String UNDEFINED_APIKEY = "UNDEFINED";
	private static String apikey = UNDEFINED_APIKEY;

	private static final String GET_APIKEY_URL = "https://apilayer.com/marketplace/exchangerates_data-api";
	private static final String GET_APIKEY_ADVICE = "you can get an apikey from: " + GET_APIKEY_URL;

	private static final String SET_APIKEY_URL = "http://localhost:8080/set_apikey?apikey=YOUR_APIKEY_HERE";
	private static final String SET_APIKEY_ADVICE1 = "Please, copy following link to your browser address bar: " + SET_APIKEY_URL;
	private static final String SET_APIKEY_ADVICE2 = " and replace the text YOUR_APIKEY_HERE with your own apikey.";
	private static final String SET_APIKEY_ADVICE3 = " BUT: If you don't have an apikey yet, " + GET_APIKEY_ADVICE;
	private static final String SET_APIKEY_ADVICE_ALL = SET_APIKEY_ADVICE1 + SET_APIKEY_ADVICE2 + SET_APIKEY_ADVICE3;

	/* 
	   		.../set_apikey?apikey="???..."
	 */

	@GetMapping("/set_apikey")
	public String setApikey(
			@RequestParam(value="apikey", required=false) String newApikey) {

		System.out.println("INFO : Entered Controller @GetMapping endpoint for /set_apikey");

		if ( newApikey == null || newApikey.equals(UNDEFINED_APIKEY) ) {
			System.out.println("INFO: Advice given : " + SET_APIKEY_ADVICE_ALL );
			return SET_APIKEY_ADVICE_ALL;
		}

		newApikey.replaceAll("\"", "");
		newApikey.replaceAll("\'", "");

		if ( newApikey.length() == VALID_APIKEY_LENGTH ) {
			apikey = newApikey;
			return "apikey updated to: " + apikey;
		}
		if ( newApikey.length() < VALID_APIKEY_LENGTH ) {
			return "Too short apikey: length " + newApikey.length() + " while " + VALID_APIKEY_LENGTH + " expected";  
		}
		return "Too long apikey: length " + newApikey.length() + " while " + VALID_APIKEY_LENGTH + " expected";  
	}

	@GetMapping("/exchange_amount")
	public String fetchExchangeInformation(
			@RequestParam(value="amount", required=false) String fromAmountAsString,
			@RequestParam(value="from", required=false) String fromCurrency,
			@RequestParam(value="to", required=false) String toCurrency)
	// only an integer accepted by remote API server

					throws ParseException, URISyntaxException {

		System.out.println("INFO : Entered Controller @GetMapping endpoint for /exchange_amount");

		if ( apikey == UNDEFINED_APIKEY ) {
			System.out.println("--- ERROR : apikey has NOT been initialized.");
			return SET_APIKEY_ADVICE_ALL;
		}

		String exampleFormat = ".../exchange_amount?amount=10&from=USD&to=SEK is an example end of a valid http link here";

		System.out.println(exampleFormat);

		if ( fromAmountAsString == null || fromCurrency == null || toCurrency == null ) {
			return exampleFormat;
		}
		if ( fromAmountAsString.length() == 0 || fromCurrency.length() == 0 || toCurrency.length() == 0 ) {
			return exampleFormat;
		}

		int fromAmount;
		try {
			fromAmount = Integer.parseInt(fromAmountAsString);
		}
		catch( Exception e) {
			return "Please, give a whole number without decimals after \"amount=\" in the link (e.g. amount=10 instead of 10.25 or 10,25)";
		}

		RequestDTO requestDTO = new RequestDTO();
		requestDTO.setFromAmount( fromAmount );
		requestDTO.setFromCurrency( fromCurrency );
		requestDTO.setToCurrency( toCurrency );

		Optional<Object> response;

		try {
			response = CurrencyExchangeDAO.fetch(requestDTO, apikey);

			if (response.isPresent()) {
				if ( ((Object) response.get()).getClass() == com.virnex.juhav.CurrencyExchangeQuery.DTO.ResponseDTO.class ) {
					ResponseDTO responseDTO = (ResponseDTO) response.get();
					return responseDTO.toUserStringDate();
				}
				else if ( ((Object) response.get()).getClass() == String.class )
					return response.get().toString();
			}

		} catch (Exception e) {
			System.out.println("--- Exception in ExchangeRateDao.fetch : " + e);
		}
		return "--- Error getting data with amount=" + fromAmount + " from=" + fromCurrency + " to=" + toCurrency;
	}

	public Controller() {}

}

// POSTMAN:
// http://api.apilayer.com/exchangerates_data/convert?amount=107,from=USD&to=SEK&apikey=HZOwa1dYHjD4MEEA0KNPsOiOEwqhQcvt

/*
Endpoints:

GET exchange_amount
	// EUR, SEK and USD need to be supported
	Request:
		from
		to
		from_amount
	Response:
		from
		to
		to_amount
		exchange_rate

 */
