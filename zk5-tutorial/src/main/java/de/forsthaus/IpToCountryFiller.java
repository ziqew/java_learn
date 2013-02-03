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

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.supercsv.exception.SuperCSVReflectionException;
import org.supercsv.io.CsvBeanReader;
import org.supercsv.prefs.CsvPreference;
import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.Sessions;

import de.forsthaus.backend.model.IpToCountry;
import de.forsthaus.backend.service.IpToCountryService;

/**
 * Fills the ipToCountry table with data from a zipped csv files where we
 * downloaded from the web.
 * 
 * @author bbruhns
 * @author sgerth
 * 
 */
public final class IpToCountryFiller implements Serializable {

	private static final long serialVersionUID = 1L;

	private IpToCountryService ipToCountryService;

	public IpToCountryFiller() {
	}

	public String[] createStringArray(String name) {
		try {
			return new MyReader(name).result;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	final static class LineReader {
		public LineReader(InputStream inStream) {
			this.inStream = inStream;
		}

		byte[] inBuf = new byte[8192];

		char[] lineBuf = new char[1024];

		int inLimit = 0;

		int inOff = 0;

		InputStream inStream;

		int readLine() throws IOException {
			int len = 0;
			char c = 0;

			boolean skipWhiteSpace = true;
			boolean isNewLine = true;
			boolean appendedLineBegin = false;
			boolean precedingBackslash = false;
			boolean skipLF = false;

			while (true) {
				if (inOff >= inLimit) {
					inLimit = inStream.read(inBuf);
					inOff = 0;
					if (inLimit <= 0) {
						if (len == 0) {
							return -1;
						}
						return len;
					}
				}
				// The line below is equivalent to calling a
				// ISO8859-1 decoder.
				c = (char) (0xff & inBuf[inOff++]);
				if (skipLF) {
					skipLF = false;
					if (c == '\n') {
						continue;
					}
				}
				if (skipWhiteSpace) {
					if (c == ' ' || c == '\t' || c == '\f') {
						continue;
					}
					if (!appendedLineBegin && (c == '\r' || c == '\n')) {
						continue;
					}
					skipWhiteSpace = false;
					appendedLineBegin = false;
				}
				if (isNewLine) {
					isNewLine = false;
					if (c == '#' || c == '!') {
						continue;
					}
				}

				if (c != '\n' && c != '\r') {
					lineBuf[len++] = c;
					if (len == lineBuf.length) {
						int newLength = lineBuf.length * 2;
						if (newLength < 0) {
							newLength = Integer.MAX_VALUE;
						}
						char[] buf = new char[newLength];
						System.arraycopy(lineBuf, 0, buf, 0, lineBuf.length);
						lineBuf = buf;
					}
					// flip the preceding backslash flag
					if (c == '\\') {
						precedingBackslash = !precedingBackslash;
					} else {
						precedingBackslash = false;
					}
				} else {
					// reached EOL
					if (len == 0) {
						isNewLine = true;
						skipWhiteSpace = true;
						len = 0;
						continue;
					}
					if (inOff >= inLimit) {
						inLimit = inStream.read(inBuf);
						inOff = 0;
						if (inLimit <= 0) {
							return len;
						}
					}
					if (precedingBackslash) {
						len -= 1;
						// skip the leading whitespace characters in following
						// line
						skipWhiteSpace = true;
						appendedLineBegin = true;
						precedingBackslash = false;
						if (c == '\r') {
							skipLF = true;
						}
					} else {
						return len;
					}
				}
			}
		}
	}

	private static class MyReader {
		final String[] result;

		MyReader(String name) throws IOException {
			super();
			String path = Sessions.getCurrent().getWebApp().getRealPath("/res") + "/";
			File file = new File(path + name);
			LineReader lr = new LineReader(new BufferedInputStream(new FileInputStream(file)));

			int limit;

			List<String> r = new ArrayList<String>();

			while ((limit = lr.readLine()) >= 0) {
				String value = new String(lr.lineBuf, 0, limit);
				r.add(value);
			}

			result = r.toArray(new String[r.size()]);
		}
	}

	public List<IpToCountry> getTest() throws SuperCSVReflectionException, IOException {

		// save the object
		// getIpToCountryService().deleteAll();

		List<IpToCountry> list = new ArrayList<IpToCountry>();

		String path = Sessions.getCurrent().getWebApp().getRealPath("/res") + "/ip-to-country.csv";
		String[] stringNameMapping = { "ipcIpFrom", "ipcIpTo", "ipcCountryCode2", "ipcCountryCode3", "ipcCountryName" };

		BufferedReader br = new BufferedReader(new FileReader(path));
		// Loop through each line using a while construct with the readLine
		// method, and assign it to the String variable str you created earlier
		// in ur code.
		CsvBeanReader csvb = new CsvBeanReader(br, CsvPreference.STANDARD_PREFERENCE);

		String str;
		while ((str = br.readLine()) != null) {

			// IpToCountry ipToCountry =
			// getIpToCountryService().getNewIpToCountry();
			// ipToCountry = (IpToCountry) csvb.read(IpToCountry.class,
			// stringNameMapping);
			// list.add(ipToCountry);
			//
			// // save the object
			// getIpToCountryService().saveOrUpdate(ipToCountry);
		}

		for (int i = 0; i < list.size(); i++) {
			System.out.println((list.get(i)).getIpcIpFrom() + ", " + (list.get(i)).getIpcIpTo() + ", " + (list.get(i)).getIpcCountryName());
		}
		System.out.println(list.size());

		return list;
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setIpToCountryService(IpToCountryService ipToCountryService) {
		this.ipToCountryService = ipToCountryService;
	}

	public IpToCountryService getIpToCountryService() {

		if (ipToCountryService == null) {
			ipToCountryService = (IpToCountryService) SpringUtil.getBean("ipToCountryService");
		}
		return ipToCountryService;
	}

}
