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
package de.forsthaus.h2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.BatchUpdateException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;

/**
 * This <b>database filler</b> is called on application startup by spring. <br>
 * The declaration of this bean is in the <br>
 * file: src/main/resources/customize-applicationContext.xml <br>
 * file: src/main/resources/createSampleData.sql <br>
 * This class creates and fills the db with the needed tables, sequences and
 * sample data records.
 * 
 * @author bbruhns
 * @author Stephan Gerth
 */
public class My_H2_SampleDataFiller implements InitializingBean {

	private DataSource dataSource;

	@Override
	public void afterPropertiesSet() throws Exception {
		final Logger logger = Logger.getLogger(getClass());
		final Map<Integer, String> allSql = new HashMap<Integer, String>();
		final Connection conn = this.dataSource.getConnection();
		try {
			// reads the sql-file from the classpath
			final InputStream inputStream = getClass().getResourceAsStream("/createSampleData.sql");
			try {

				final Statement stat = conn.createStatement();

				final BufferedReader in = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
				String str;
				StringBuilder sb = new StringBuilder();
				int count = 0;
				while ((str = in.readLine()) != null) {
					sb.append(str);
					// make a linefeed at each readed line
					if (StringUtils.endsWith(str.trim(), ";")) {
						final String sql = sb.toString();
						stat.addBatch(sql);
						sb = new StringBuilder();
						allSql.put(Integer.valueOf(count++), sql);
					} else {
						sb.append("\n");
					}
				}

				final int[] ar = stat.executeBatch();
				final int i = ar.length;

				logger.info("Create DemoData");
				logger.info("count batch updates : " + i);

			} finally {
				try {
					inputStream.close();
				} catch (final IOException e) {
					logger.warn("", e);
				}
			}
		} catch (final BatchUpdateException e) {
			final BatchUpdateException be = e;
			final int[] updateCounts = be.getUpdateCounts();
			if (updateCounts != null) {
				for (int i = 0; i < updateCounts.length; i++) {
					final int j = updateCounts[i];
					if (j < 0) {
						logger.error("SQL errorcode: " + j + " -> in SQL\n" + allSql.get(Integer.valueOf(i)));
					}
				}
			}
			throw e;
		} finally {
			try {
				conn.close();
			} catch (final SQLException e) {
				logger.warn("", e);
			}
		}
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public DataSource getDataSource() {
		return this.dataSource;
	}

	// injected by spring
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}
}
