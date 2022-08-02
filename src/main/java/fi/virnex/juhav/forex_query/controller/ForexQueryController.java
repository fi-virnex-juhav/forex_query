package fi.virnex.juhav.forex_query.controller;

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

import fi.virnex.juhav.forex_query.dao.ForexDAO;
import fi.virnex.juhav.forex_query.dto.ForexRequestDTO;
import fi.virnex.juhav.forex_query.dto.ForexResponseDTO;

// data source:
// https://apilayer.com/marketplace/description/exchangerates_data-api 

@RestController
public class ForexQueryController {

	private static String apikey = System.getenv("apikey");

	private static final String EXPIRED_APIKEY = "HZOwa1dYHjD4MEEA0KNPsOiOEwqhQcvt";
	private static final int VALID_APIKEY_LENGTH = EXPIRED_APIKEY.length();

	private static final String DUMMY_APIKEY = "DUMMY";

	private static final String GET_APIKEY_URL = "https://apilayer.com/marketplace/exchangerates_data-api" + System.lineSeparator();
	private static final String GET_APIKEY_ADVICE = "you can get an apikey from: " + GET_APIKEY_URL + System.lineSeparator();

	// private static final String SET_APIKEY_URL = "http://localhost:8080/set_apikey?apikey=xxx" + System.lineSeparator();

	private static final String SET_APIKEY_ADVICE =
			  "For start-up from terminal take the command inside square-brackets:" + System.lineSeparator()
			+ "[apikey=xxx docker-compose up]" + System.lineSeparator()
			+ "Replace xxx with your apikey value." + System.lineSeparator();

	private static final String SET_APIKEY_ADVICE_ALL = 
			GET_APIKEY_ADVICE + 
			SET_APIKEY_ADVICE + System.lineSeparator()
			+ "Sent no query to the external API - avoiding unnecessary load on it." + System.lineSeparator();

	// http://localhost:8080/exchange_amount?amount=1&from=USD&to=SEK

	@GetMapping("/exchange_amount")
	public String fetchExchangeInformation(
			@RequestParam(value="amount", required=false) String fromAmountAsString,
			@RequestParam(value="from", required=false) String fromCurrency,
			@RequestParam(value="to", required=false) String toCurrency)
					throws ParseException, URISyntaxException {
	
		// only whole number amount accepted by remote API server			

		System.out.println("INFO: Entered ForexQueryController @GetMapping endpoint for /exchange_amount");

		String exampleFormat = "http://localhost:8080/exchange_amount?amount=10&from=USD&to=SEK is an example end of a valid http link";

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
			return "Please, give a whole number without decimals for amount - e.g. amount=10 instead of 10.25 or 10,25";
		}

		ForexRequestDTO forexRequestDTO = new ForexRequestDTO();
		forexRequestDTO.setFromAmount( fromAmount );
		forexRequestDTO.setFromCurrency( fromCurrency );
		forexRequestDTO.setToCurrency( toCurrency );

		System.out.println();
		System.out.println("composed forex data request: " + forexRequestDTO.toJson());
		System.out.println();

		if (   (apikey == null) 
				|| (apikey.length() < 1 )
				|| (apikey.equals(DUMMY_APIKEY) ) ) {
			System.out.println("--- ERROR : apikey needs a valid value.");
			System.out.println("--- ERROR : now apikey=" + apikey);
			System.out.println(SET_APIKEY_ADVICE_ALL);
			System.out.println();
			System.out.println("following request composed but NOT sent due to invalid apikey=" + apikey + " :");
			System.out.println("unsent request: " + forexRequestDTO.toJson());
			System.out.println();

			return "<html>" 
			+ "request NOT sent due to invalid apikey=" + apikey + "<br>"
			+ "unsent request: " + forexRequestDTO.toJson() + "<br>"
			+ SET_APIKEY_ADVICE_ALL.replaceAll(System.lineSeparator(), "<br>") 
			+ "</html>";
		}

		Optional<Object> response;

		try {
			response = ForexDAO.fetch(forexRequestDTO, apikey);

			if (response.isPresent()) {
				if ( ((Object) response.get()).getClass() == fi.virnex.juhav.forex_query.dto.ForexResponseDTO.class ) {
					ForexResponseDTO forexResponseDTO = (ForexResponseDTO) response.get();
					return forexResponseDTO.toJson();
				}
				else if ( ((Object) response.get()).getClass() == String.class )
					return response.get().toString();
			}

		} catch (Exception e) {
			System.out.println("--- Exception in ExchangeRateDao.fetch : " + e);
		}
		return "--- Error getting data with amount=" + fromAmount + " from=" + fromCurrency + " to=" + toCurrency;
	}


/*
	//		.../set_apikey?apikey="???..."

	// I removed the possibility to set apikey from browser - for clarity.
	// Instead apikey is only set from terminal with below command at start-up:
	// apikey=xxx docker-compose up
	// where xxx is to be replaced by a valid apikey

	// http://localhost:8080/set_apikey?apikey=vGLeIBxS5cqpW6nbrEEIIbzWGmmCAbEF

	@GetMapping("/set_apikey")
	public String setApikey(
			@RequestParam(value="apikey", required=false) String newApikey) {

		System.out.println("INFO : Entered ForexQueryController @GetMapping endpoint for /set_apikey");

		if ( newApikey == null || newApikey.equals(DUMMY_APIKEY) ) {
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
*/

	public ForexQueryController() {}

}

// POSTMAN: the apikey value below is invalid - related user account closed:
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
