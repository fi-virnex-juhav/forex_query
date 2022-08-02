package fi.virnex.juhav.forex_query.dao;

/**
 * @author Juha Valimaki, new guy at virnex.fi 2022-06-19, June 19 2022
 *
 */

/**
 * @author Juha Valimaki, Virnex 2022-06-19
 *
 */
import java.io.IOException;

import java.net.URI;
import java.net.URISyntaxException;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.TimeZone;

// import org.apache.coyote.Request;
import org.springframework.boot.json.JsonParser;
import org.springframework.boot.json.JsonParserFactory;
import org.springframework.web.client.RestTemplate;

import fi.virnex.juhav.forex_query.dto.ForexRequestDTO;
import fi.virnex.juhav.forex_query.dto.ForexResponseDTO;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

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

import java.net.http.HttpRequest.Builder;
import java.net.http.HttpResponse;
import java.net.URL;


// only static methods and final constants on class level here

public class ForexDAO {

	
	// example old apikey = "vGLeIBxS5cqpW6nbrEEIIbzWGmmCAbEF";
	
	private static final String EXCEPTION_HEAD_JSON = "{Exception:";
	private static final String EXCEPTION_TAIL_JSON = "}";
	
	private static final String requestUrlTemplate = "http://api.apilayer.com/exchangerates_data/convert?amount=<fromAmount>&from=<fromCurrency>&to=<toCurrency>";

	private static final String getUrlString(ForexRequestDTO forexRequestDTO, String apikey) {

		String url = requestUrlTemplate
				.replace("<fromAmount>",  Integer.toString( forexRequestDTO.getFromAmount()))
				.replace("<fromCurrency>", forexRequestDTO.getFromCurrency() )
				.replace("<toCurrency>", forexRequestDTO.getToCurrency());
		return url;
	}
	
	/* 
	 * The response from external forex API does contains only date without time.
	 * The response contains anyway timestamp.
	 * The timestamp is converted to date and time, stored as String "yyyy-mm-dd hh:mm:ss".
	 *
	 */
	
	// handling of date & time methods begin
	
	private static final String timestampToDateTimeString(int timestamp) {
		return getDateTimeAsString(getDateTimeFromTimestamp(timestamp));
	}
	
	private static final LocalDateTime getDateTimeFromTimestamp(int timestamp) {
		if (timestamp == 0)
			return null;
		return LocalDateTime.ofInstant(Instant.ofEpochSecond(timestamp), 
				TimeZone.getDefault().toZoneId());
	}

	private static final String getDateTimeAsString(LocalDateTime localDateTime) {
		return 		localDateTime.getYear() 
				+ "-" + ifLessThan10thenPrefix0(localDateTime.getMonthValue())
				+ "-" + ifLessThan10thenPrefix0(localDateTime.getDayOfMonth())
				+ " " + ifLessThan10thenPrefix0(localDateTime.getHour())
				+ ":" + ifLessThan10thenPrefix0(localDateTime.getMinute())
				+ ":" + ifLessThan10thenPrefix0(localDateTime.getSecond());
	}

	// convert e.g. "7" to "07" for month/dayOfMonth/hour/min/seconds
	private static final String ifLessThan10thenPrefix0(int h) {	
		if ( h < 10 ) {
			return "0" + h;		
		}
		return "" + h;
	}
	
	// handling of date & time methods end
	
	public static String getCurrencyExchangeJson(int amount, String from, String to, String apikey ) {
		OkHttpClient client = new OkHttpClient().newBuilder().build();

		String urlTemplate = "https://api.apilayer.com/exchangerates_data/convert?amount=<amount>&from=<from>&to=<to>";
		String urlString = urlTemplate
				.replace("<amount>", Integer.toString(amount))
				.replace("<from>", from )
				.replace("<to>", to );

		Request request = new Request.Builder()
				.url(urlString)
				.addHeader("apikey", apikey )
				//.addHeader("apikey", "vGLeIBxS5cqpW6nbrEEIIbzWGmmCAbEF")
				.method("GET", null)
				.build();

		Response response;
		try {
			response = client.newCall(request).execute();
			String responseBody = response.body().string();
			System.out.println( "+++ responseBody : " + responseBody );
			
			return responseBody;
			
		} catch (Exception e) {				// (IOException e) {
			System.out.println("--- Exception in ForexDAO.getCurrencyExchangeJson() : " + e.toString());
			e.printStackTrace();
			return EXCEPTION_HEAD_JSON + e.toString() + EXCEPTION_TAIL_JSON; 
		}
	}

	//OLD UNUSED, MAYBE USEFUL LATER SOMEWHERE
	private static Optional<Object> parseServerError(String serverError) {

		Optional<Object> returnValueIfNull = Optional.ofNullable((Object) serverError);

		if (returnValueIfNull.isEmpty()) {
			String bug = "--- BUG1 outside of remote Exchange Rate API server: ForexDAO.parseServerError called with a null String";
			Optional<Object> optionalBug = Optional.of( (Object) bug);
			return optionalBug;
		}

		// my current API server side error:
		// ... "{"message":"You have exceeded your daily\/monthly API rate limit. Please review and upgrade your subscription plan at https:\/\/promptapi.com\/subscriptions to continue."}"

		String msg = serverError.replace("\"", "");
		msg = msg.replace(":", "");
		msg = msg.replace("{", "");
		msg = msg.replace("}", "");
		String expectedSubstring = "message";
		int index = msg.indexOf(expectedSubstring);
		if (index >= 0 ) {
			msg = msg.substring(index + expectedSubstring.length());
		}
		return Optional.of(msg);
	}

	private static ForexResponseDTO ForexResponseDTO = new ForexResponseDTO();


	// Beef of the burger {

	public static final Optional<Object> fetch( 
			ForexRequestDTO forexRequestDTO,
			String apikey) 
					throws ParseException
					, URISyntaxException
					, Exception {

		String json = null;
			
			json = getCurrencyExchangeJson( forexRequestDTO.getFromAmount(), forexRequestDTO.getFromCurrency(), forexRequestDTO.getToCurrency(), apikey);
			
			if ( json.startsWith(EXCEPTION_HEAD_JSON) ) {
				System.out.println("--- json : " + json );
				return Optional.of(json);
		 	}
			System.out.println("+++ json: " + json );
			
		// from here on any problem is in parsing of the json format	

		Optional<Object> response = Optional.ofNullable(null);

		try {
			JsonParser springParser = JsonParserFactory.getJsonParser();

			Map <String,Object> map = springParser.parseMap(json);	// !!!

			boolean success = (boolean) map.get("success");

			Map<String, Object> query = (Map<String, Object>) map.get("query");

			String fromCurrency = (String) query.get("from");
			String toCurrency   = (String) query.get("to");
			int fromAmount = (int) query.get("amount");

			Map<String, Object> info = (Map<String, Object>) map.get("info");

			int timestamp = (int) info.get("timestamp");
			
			String rateDate = timestampToDateTimeString(timestamp);

			double exchangeRate = (double) info.get("rate");

			/*
			// DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

			Date date = dateFormat.parse(dateString);  
			*/

			String onlyDateNoTime = (String) map.get("date");	// unused, timestamp used above 
			
			double toAmount = (double) map.get("result");	// to-amount

			ForexResponseDTO.setRateDate(rateDate);
			ForexResponseDTO.setFromAmount(fromAmount);
			ForexResponseDTO.setFromCurrency(fromCurrency);
			ForexResponseDTO.setToAmount(toAmount);
			ForexResponseDTO.setToCurrency(toCurrency);	
			ForexResponseDTO.setExchangeRate(exchangeRate);

			System.out.println();
			System.out.println("+++ response : " + ForexResponseDTO.toString());
			System.out.println();
			System.out.println("+++ response json : " + ForexResponseDTO.toJson() );
			System.out.println();
			
			response = Optional.of(ForexResponseDTO);
			
		} catch (Exception e) {
			String jsonParsingErrorMessage = "Exception while parsing json in ForexDAO.fetch() : " + e.toString();
			System.out.println("--- " + jsonParsingErrorMessage);
			response = Optional.of( EXCEPTION_HEAD_JSON + e.toString() + EXCEPTION_TAIL_JSON );
		}

		return response;
	}

	// Beef of the burger }

}

////////////////// below just notes related to studies //////////////////////////
/*
 OkHttpClient client = new OkHttpClient().newBuilder().build();

    Request request = new Request.Builder()
      .url("https://api.apilayer.com/exchangerates_data/convert?to=to&from=from&amount=amount")
      .addHeader("apikey", "vGLeIBxS5cqpW6nbrEEIIbzWGmmCAbEF")
      .method("GET", })
      .build();
    Response response = client.newCall(request).execute();
    System.out.println(response.body().string());

***************************

// Synchronous Get
// Response body as a String
public void get(String uri) throws Exception {
    HttpClient client = HttpClient.newHttpClient();
    HttpRequest request = HttpRequest.newBuilder()
          .uri(URI.create(uri))
          .build();

    HttpResponse<String> response =
          client.send(request, BodyHandlers.ofString());

    System.out.println(response.body());
}

+++
// The BodyHandler is invoked once the response status code and headers are available, but before the response body bytes are received. The BodyHandler is responsible for creating the BodySubscriber which is a reactive-stream subscriber that receives streams of data with non-blocking back pressure. The BodySubscriber is responsible for, possibly, converting the response body bytes into a higher-level Java type.

// The HttpResponse.BodyHandlers class provides a number of convenience static factory methods for creating a BodyHandler. A number of these accumulate the response bytes in memory until it is completely received, after which it is converted into the higher-level Java type, for example, ofString, and ofByteArray. Others stream the response data as it arrives; ofFile, ofByteArrayConsumer, and ofInputStream. Alternatively, a custom written subscriber implementation can be provided.

Response body as a File
public void get(String uri) throws Exception {
    HttpClient client = HttpClient.newHttpClient();
    HttpRequest request = HttpRequest.newBuilder()
          .uri(URI.create(uri))
          .build();

    HttpResponse<Path> response =
          client.send(request, BodyHandlers.ofFile(Paths.get("body.txt")));

    System.out.println("Response in file:" + response.body());
}
+++

Asynchronous Get
The asynchronous API returns immediately with a CompletableFuture that completes with the HttpResponse when it becomes available. CompletableFuture was added in Java 8 and supports composable asynchronous programming.

Response body as a String
public CompletableFuture<String> get(String uri) {
    HttpClient client = HttpClient.newHttpClient();
    HttpRequest request = HttpRequest.newBuilder()
          .uri(URI.create(uri))
          .build();

    return client.sendAsync(request, BodyHandlers.ofString())
          .thenApply(HttpResponse::body);
}
The CompletableFuture.thenApply(Function) method can be used to map the HttpResponse to its body type, status code, etc.

Response body as a File
public CompletableFuture<Path> get(String uri) {
    HttpClient client = HttpClient.newHttpClient();
    HttpRequest request = HttpRequest.newBuilder()
          .uri(URI.create(uri))
          .build();

    return client.sendAsync(request, BodyHandlers.ofFile(Paths.get("body.txt")))
          .thenApply(HttpResponse::body);
}
Post
A request body can be supplied by an HttpRequest.BodyPublisher.

public void post(String uri, String data) throws Exception {
    HttpClient client = HttpClient.newBuilder().build();
    HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(uri))
            .POST(BodyPublishers.ofString(data))
            .build();

    HttpResponse<?> response = client.send(request, BodyHandlers.discarding());
    System.out.println(response.statusCode());
}
The above example uses the ofString BodyPublisher to convert the given String into request body bytes.

The BodyPublisher is a reactive-stream publisher that publishes streams of request body on-demand. HttpRequest.Builder has a number of methods that allow setting a BodyPublisher; Builder::POST, Builder::PUT, and Builder::method. The HttpRequest.BodyPublishers class has a number of convenience static factory methods that create a BodyPublisher for common types of data; ofString, ofByteArray, ofFile.

The discarding BodyHandler can be used to receive and discard the response body when it is not of interest.

Concurrent Requests
It's easy to combine Java Streams and the CompletableFuture API to issue a number of requests and await their responses. The following example sends a GET request for each of the URIs in the list and stores all the responses as Strings.

public void getURIs(List<URI> uris) {
    HttpClient client = HttpClient.newHttpClient();
    List<HttpRequest> requests = uris.stream()
            .map(HttpRequest::newBuilder)
            .map(reqBuilder -> reqBuilder.build())
            .collect(toList());

    CompletableFuture.allOf(requests.stream()
            .map(request -> client.sendAsync(request, ofString()))
            .toArray(CompletableFuture<?>[]::new))
            .join();
}
Get JSON
In many cases the response body will be in some higher-level format. The convenience response body handlers can be used, along with a third-party library to convert the response body into that format.

The following example demonstrates how to use the Jackson library, in combination with BodyHandlers::ofString to convert a JSON response into a Map of String key/value pairs.

public CompletableFuture<Map<String,String>> JSONBodyAsMap(URI uri) {
    UncheckedObjectMapper objectMapper = new UncheckedObjectMapper();

    HttpRequest request = HttpRequest.newBuilder(uri)
          .header("Accept", "application/json")
          .build();

    return HttpClient.newHttpClient()
          .sendAsync(request, BodyHandlers.ofString())
          .thenApply(HttpResponse::body)
          .thenApply(objectMapper::readValue);
}

class UncheckedObjectMapper extends com.fasterxml.jackson.databind.ObjectMapper {
    // Parses the given JSON string into a Map.
    Map<String,String> readValue(String content) {
    try {
        return this.readValue(content, new TypeReference<>(){});
    } catch (IOException ioe) {
        throw new CompletionException(ioe);
    }
}
The above example uses ofString which accumulates the response body bytes in memory. Alternatively, a streaming subscriber, like ofInputStream could be used.

Post JSON
In many cases the request body will be in some higher-level format. The convenience request body handlers can be used, along with a third-party library to convert the request body into that format.

The following example demonstrates how to use the Jackson library, in combination with the BodyPublishers::ofString to convert a Map of String key/value pairs into JSON.

public CompletableFuture<Void> postJSON(URI uri,
                                        Map<String,String> map)
    throws IOException
{
    ObjectMapper objectMapper = new ObjectMapper();
    String requestBody = objectMapper
          .writerWithDefaultPrettyPrinter()
          .writeValueAsString(map);

    HttpRequest request = HttpRequest.newBuilder(uri)
          .header("Content-Type", "application/json")
          .POST(BodyPublishers.ofString(requestBody))
          .build();

    return HttpClient.newHttpClient()
          .sendAsync(request, BodyHandlers.ofString())
          .thenApply(HttpResponse::statusCode)
          .thenAccept(System.out::println);
}
Setting a Proxy
A ProxySelector can be configured on the HttpClient through the client's Builder::proxy method. The ProxySelector API returns a specific proxy for a given URI. In many cases a single static proxy is sufficient. The ProxySelector::of static factory method can be used to create such a selector.

Response body as a String with a specified proxy
public CompletableFuture<String> get(String uri) {
    HttpClient client = HttpClient.newBuilder()
          .proxy(ProxySelector.of(new InetSocketAddress("www-proxy.com", 8080)))
          .build();

    HttpRequest request = HttpRequest.newBuilder()
          .uri(URI.create(uri))
          .build();

    return client.sendAsync(request, BodyHandlers.ofString())
          .thenApply(HttpResponse::body);
}

*/

/*
// Synchronous Get
// Response body as a String
public void get(String uri) throws Exception {
   
    HttpClient client = HttpClient.newHttpClient();
    
    HttpRequest request = HttpRequest.newBuilder()
          .uri(URI.create(uri))
          .build();

    HttpResponse<String> response =
          client.send(request, BodyHandlers.ofString());

    System.out.println(response.body());
}

+++

OkHttpClient client = new OkHttpClient().newBuilder().build();

    Request request = new Request.Builder()
      .url("https://api.apilayer.com/exchangerates_data/convert?to=to&from=from&amount=amount")
      .addHeader("apikey", "vGLeIBxS5cqpW6nbrEEIIbzWGmmCAbEF")
      .method("GET", })
      .build();
    Response response = client.newCall(request).execute();
    System.out.println(response.body().string());
 
 // GET
      HttpResponse response = HttpRequest
          .create(new URI("http://www.foo.com"))
              .headers("Foo", "foovalue", "Bar", "barvalue")
              .GET()
              .response();

      int statusCode = response.statusCode();
      String responseBody = response.body(asString());

      // POST
      response = HttpRequest
          .create(new URI("http://www.foo.com"))
          .body(fromString("param1=foo,param2=bar"))
          .POST()
          .response();
 +++++++
 HttpClient client = new HttpClient().newBuilder().build();
 // or
 HttpClient client = HttpClient.newHttpClient();
 
 Request request = new Request.Builder()
      .url("https://api.apilayer.com/exchangerates_data/convert?to=to&from=from&amount=amount")
      .addHeader("apikey", "vGLeIBxS5cqpW6nbrEEIIbzWGmmCAbEF")
      .method("GET", })
      .build();
    Response response = client.newCall(request).execute();
    System.out.println(response.body().string());
 
 
 url.toURI()
 url = uri.toURL();
 
 */

