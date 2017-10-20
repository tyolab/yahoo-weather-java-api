/**
 * 
 */
package com.github.fedy2.weather;

import com.github.fedy2.weather.binding.JsonParser;
import com.github.fedy2.weather.binding.Parser;
import com.github.fedy2.weather.data.Channel;
import com.github.fedy2.weather.data.unit.DegreeUnit;

import org.arbonaut.xml.bind.JAXBException;

import java.io.IOException;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URLEncoder;

import au.com.tyo.services.HttpPool;

import static com.github.fedy2.weather.YahooWeatherService.ResultFormat.JSON;

/**
 * Main access point for the Yahoo weather service.
 * original @author "Federico De Faveri defaveri@gmail.com"
 */
public class YahooWeatherService {

	private static final String WEATHER_SERVICE_BASE_URL = "https://query.yahooapis.com/v1/public/yql";

	private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;

	public enum ResultFormat {RSS, JSON};

	public interface LimitDeclaration {

		/**
		 * Limits results to first <code>count</code> {@link Channel}s.
		 * @param count the limit of the number of results.
		 * @return the results.
		 * @throws JAXBException if an error occurs parsing the response.
		 * @throws IOException if an error occurs communicating with the service.
		 */
		Object first(int count) throws Exception;
		
		/**
		 * Limits results to last <code>count</code> {@link Channel}s.
		 * @param count the limit of the number of results.
		 * @return the results.
		 * @throws JAXBException if an error occurs parsing the response.
		 * @throws IOException if an error occurs communicating with the service.
		 */
		Object last(int count) throws Exception;
		
		/**
		 * Returns all the results with no limits.
		 * @return the results.
		 * @throws JAXBException if an error occurs parsing the response.
		 * @throws IOException if an error occurs communicating with the service.
		 */
		Object all() throws Exception;
	}

	// private Logger logger = LoggerFactory.getLogger(YahooWeatherService.class);
	private Parser parser;
	// private Proxy proxy;
	private ResultFormat format;

	public YahooWeatherService(ResultFormat format)
	{
		this.format = format;

		if (format == JSON)
			this.parser = new JsonParser();
		// this.proxy = Proxy.NO_PROXY;
	}

//	public YahooWeatherService(Parser parser, Proxy proxy)
//	{
//		this.parser = parser;
//		// this.proxy = proxy;
//	}

	/**
	 * Gets the Weather RSS feed.
	 * @param woeid the location WOEID.
	 * @param unit the degrees units.
	 * @return the retrieved Channel.
	 * @throws JAXBException if an error occurs parsing the response.
	 * @throws IOException if an error occurs communicating with the service.
	 */
	public Object getForecast(String woeid, DegreeUnit unit) throws Exception
	{
		QueryBuilder query = new QueryBuilder();
		query.woeid(woeid).unit(unit);
		Object channels = execute(query.build());
		// if (channels.isEmpty()) throw new IllegalStateException("No results from the service.");
		return channels;
	}

	/**
	 * Gets the Weather RSS feed for the specified location.
	 * @param location the location to search.
	 * @param unit the degrees units.
	 * @return the limit declaration.
	 */
	public LimitDeclaration getForecastForLocation(String location, DegreeUnit unit)
	{
		final QueryBuilder query = new QueryBuilder();
		query.location(location).unit(unit);

		return new LimitDeclaration() {

			@Override
			public Object last(int count) throws Exception {
				query.last(count);
				return execute(query.build());
			}

			@Override
			public Object first(int count) throws Exception {
				query.first(count);
				return execute(query.build());
			}

			@Override
			public Object all() throws Exception {
				return execute(query.build());
			}
		};
	}

	/**
	 * Composes the URL with the specified query.
	 * @param query the query to submit.
	 * @return the composed URL.
	 */
	private String composeUrl(String query)
	{
		// logger.trace("query: {}", query);
		StringBuilder url = new StringBuilder(WEATHER_SERVICE_BASE_URL);
		try {
			url.append("?q=").append(URLEncoder.encode(query, "UTF-8"));
			if (format == JSON)
				url.append("&format=json");

		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("Url encoding failed", e);
		}
		return url.toString();
	}

	private Object execute(String query) throws Exception {
		String url = composeUrl(query);

		String result = retrieve(url);

		if (null != parser)
			return parser.parse(result);
		return result;
	}

	/**
	 * Open the connection to the service and retrieves the data.
	 * @param serviceUrl the service URL.
	 * @return the service response.
	 * @throws IOException if an error occurs during the connection.
	 */
	private String retrieve(String serviceUrl) throws Exception
	{
		return HttpPool.getConnection().get(serviceUrl);
	}

	/**
	 * Copy the input reader into the output writer.
	 * @param input the input reader.
	 * @param output the output writer.
	 * @return the number of char copied.
	 * @throws IOException if an error occurs during the copy.
	 */
	private static long copy(Reader input, Writer output) throws IOException {
		char[] buffer = new char[DEFAULT_BUFFER_SIZE];
		long count = 0;
		int n = 0;
		while (-1 != (n = input.read(buffer))) {
			output.write(buffer, 0, n);
			count += n;
		}
		return count;
	}
}
