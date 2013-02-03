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
package de.forsthaus.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang.StringUtils;

/**
 * Engine for creating random data from text files.<br>
 * 
 * 
 * @author bbruhns
 * @author sgerth
 */
public final class RandomDataEngine implements Serializable {

	private static final long serialVersionUID = 1L;

	final private Random RANDOM;
	final private String[] BLOB;
	final private String[] EMAIL;
	final private String[] HOMEPAGE;
	final private String[] MANFIRSTNAME;
	final private String[] LASTNAME;
	final private String[] CITY;
	final private String[] ZIP;
	final private String[] STREET;
	final private String[] PHONENUMBER;
	final private String[] FEMALEFIRSTNAME;

	public String getRandomBlob() {
		return this.BLOB[this.RANDOM.nextInt(this.BLOB.length)];
	}

	public String getRandomEmail() {
		return this.EMAIL[this.RANDOM.nextInt(this.EMAIL.length)];
	}

	public String getRandomHomepage() {
		return this.HOMEPAGE[this.RANDOM.nextInt(this.HOMEPAGE.length)];
	}

	public String getRandomManFirstname() {
		return this.MANFIRSTNAME[this.RANDOM.nextInt(this.MANFIRSTNAME.length)];
	}

	public String getRandomLastname() {
		return this.LASTNAME[this.RANDOM.nextInt(this.LASTNAME.length)];
	}

	public String getRandomCity() {
		return this.CITY[this.RANDOM.nextInt(this.CITY.length)];
	}

	public String getRandomZip() {
		return this.ZIP[this.RANDOM.nextInt(this.ZIP.length)];
	}

	public String getRandomStreet() {
		return this.STREET[this.RANDOM.nextInt(this.STREET.length)];
	}

	public String getRandomPhoneNumber() {
		return this.PHONENUMBER[this.RANDOM.nextInt(this.PHONENUMBER.length)];
	}

	public String getRandomFemaleFirstname() {
		return this.FEMALEFIRSTNAME[this.RANDOM.nextInt(this.FEMALEFIRSTNAME.length)];
	}

	private static String[] createStringArray(String name) {
		try {
			final InputStream inputStream = RandomDataEngine.class.getResourceAsStream("/example/" + name);
			try {
				final BufferedReader lr = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));

				final List<String> r = new ArrayList<String>();
				String str;
				while ((str = lr.readLine()) != null) {
					if (StringUtils.isBlank(str)) {
						continue;
					}
					r.add(str);
				}
				return r.toArray(new String[r.size()]);
			} finally {
				if (inputStream != null) {
					inputStream.close();
				}
			}
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}

	public RandomDataEngine() {
		this.RANDOM = new Random((long) Math.random() + System.currentTimeMillis());
		this.BLOB = createStringArray("blob.txt");
		this.EMAIL = createStringArray("email.txt");
		this.HOMEPAGE = createStringArray("homepage.txt");
		this.MANFIRSTNAME = createStringArray("manfirstname.txt");
		this.LASTNAME = createStringArray("lastname.txt");
		this.CITY = createStringArray("city.txt");
		this.ZIP = createStringArray("zip.txt");
		this.STREET = createStringArray("street.txt");
		this.PHONENUMBER = createStringArray("phonenumber.txt");
		this.FEMALEFIRSTNAME = createStringArray("femalefirstname.txt");
	}
}
