/**
 * Copyright 2010 the original author or authors.
 * 
 * This file is part of Zksample2. http://zksample2.sourceforge.net/
 *
 * Zksample2 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Zksample2 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Zksample2.  If not, see <http://www.gnu.org/licenses/gpl.html>.
 */
package de.forsthaus;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

/**
 * The IPLocator is a java wrapper for the hostip.info ip locator web service.
 * 
 * @author <a href="mailto:pillvin@iit.edu">Vinod Pillai</a>
 * @version $Revision: 1.0 $
 */
public class IpLocator {
	private static final String HOSTIP_LOOKUP_URL = "http://api.hostip.info/get_html.php?position=true&ip=";
	private static final String KEY_COUNTRY = "COUNTRY";
	private static final String KEY_CITY = "CITY";
	private static final String KEY_LATITUDE = "LATITUDE";
	private static final String KEY_LONGITUDE = "LONGITUDE";

	private String city = "";
	private String country = "";
	private float longitude = -1;
	private float latitude = -1;

	/**
	 * This is a singleton class therefore make the constructor private
	 */
	private IpLocator() {
	}

	/**
	 * Other than the getters & setters, this is the only method visible to the
	 * outside world
	 * 
	 * @param ip
	 *            The ip address to be located
	 * @return IPLocator instance
	 * @throws IOException
	 *             in case of any error/exception
	 */
	public static IpLocator locate(String ip) throws IOException {
		final String url = HOSTIP_LOOKUP_URL + ip;
		final URL u = new URL(url);
		final List<String> response = getContent(u);

		final Pattern splitterPattern = Pattern.compile(":");
		final IpLocator ipl = new IpLocator();

		for (final String token : response) {
			final String[] keyValue = splitterPattern.split(token);
			if (keyValue.length != 2) {
				continue;
			}

			final String key = StringUtils.upperCase(keyValue[0]);
			final String value = keyValue[1];
			if (KEY_COUNTRY.equals(key)) {
				ipl.setCountry(value);
			} else if (KEY_CITY.equals(key)) {
				ipl.setCity(value);
			} else if (KEY_LATITUDE.equals(key)) {
				ipl.setLatitude(stringToFloat(value));
			} else if (KEY_LONGITUDE.equals(key)) {
				ipl.setLongitude(stringToFloat(value));
			}
		}
		return ipl;
	}

	private static float stringToFloat(String fString) {
		try {
			return Float.parseFloat(fString);
		} catch (final NumberFormatException e) {
			return -1;
		}
	}

	/**
	 * Gets the content for a given url. This method makes a connection, gets
	 * the response from the url.
	 * 
	 * A RuntimeException is throws is the status code of the response is not
	 * 200.
	 * 
	 * @param url
	 *            The url to open.
	 * @return HTML response
	 * @throws IOException
	 */
	private static List<String> getContent(URL url) throws IOException {

		final HttpURLConnection http = (HttpURLConnection) url.openConnection();
		try {
			http.connect();

			final int code = http.getResponseCode();
			if (code != 200)
				throw new IOException("IP Locator failed to get the location. Http Status code : " + code + " [" + url
						+ "]");
			return getContent(http);
		} finally {
			http.disconnect();
		}
	}

	/**
	 * Gets the content for a given HttpURLConnection.
	 * 
	 * @param connection
	 *            Http URL Connection.
	 * @return HTML response
	 * @exception IOException
	 */

	private static List<String> getContent(HttpURLConnection connection) throws IOException {
		final InputStream in = connection.getInputStream();
		try {
			final List<String> result = new ArrayList<String>(5);
			final InputStreamReader isr = new InputStreamReader(in);
			final BufferedReader bufRead = new BufferedReader(isr);

			String aLine = null;
			while (null != (aLine = bufRead.readLine())) {
				result.add(aLine.trim());
			}

			bufRead.close();
			return result;
		} finally {
			try {
				in.close();
			} catch (final IOException e) {
			}
		}
	}

	/**
	 * @return the city
	 */
	public String getCity() {
		return this.city;
	}

	/**
	 * @param city
	 *            the city to set
	 */
	public void setCity(String city) {
		this.city = city;
	}

	/**
	 * @return the country
	 */
	public String getCountry() {
		return this.country;
	}

	/**
	 * @param country
	 *            the country to set
	 */
	public void setCountry(String country) {
		this.country = country;
	}

	/**
	 * @return the longitude
	 */
	public float getLongitude() {
		return this.longitude;
	}

	/**
	 * @param longitude
	 *            the longitude to set
	 */
	public void setLongitude(float longitude) {
		this.longitude = longitude;
	}

	/**
	 * @return the latitude
	 */
	public float getLatitude() {
		return this.latitude;
	}

	/**
	 * @param latitude
	 *            the latitude to set
	 */
	public void setLatitude(float latitude) {
		this.latitude = latitude;
	}

	/**
	 * For unit testing purposes only
	 * 
	 * @param args
	 */
	public static void main(String args[]) {
		System.err.println("Start");
		try {

			final IpLocator ipl = IpLocator.locate("12.215.42.19");
			System.out.println("City=" + ipl.getCity());
			System.out.println("Country=" + ipl.getCountry());
			System.out.println("CountryCode=" + ipl.getCountryCode());
			System.out.println("Latitude=" + ipl.getLatitude());
			System.out.println("Longitude=" + ipl.getLongitude());
		} catch (final Exception e) {
			System.err.println(Thread.currentThread().getName() + " FEHLER - " + e);
			e.printStackTrace();
		}
	}

	/**
	 * @return the country
	 */
	public String getCountryCode() {
		return StringUtils.defaultString(StringUtils.substringBetween(this.country, "(", ")"));
	}
}
