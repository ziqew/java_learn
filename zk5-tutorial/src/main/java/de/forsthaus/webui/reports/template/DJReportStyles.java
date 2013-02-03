package de.forsthaus.webui.reports.template;

import ar.com.fdvs.dj.domain.Style;
import ar.com.fdvs.dj.domain.constants.Border;
import ar.com.fdvs.dj.domain.constants.Font;
import ar.com.fdvs.dj.domain.constants.HorizontalAlign;

public class DJReportStyles {

	private Style columnStyleNumbers;
	private Style columnStyleText;
	private Style headerStyleNumbers;
	private Style headerStyleText;
	private Style subtitleStyle;
	private Style titleStyle;
	private Style footerStyle;

	public DJReportStyles() {

		createStyles();
	}

	private void createStyles() {

		/**
		 * Here we can additionally if needed get the styles out of the
		 * database. Means the customer can customize his styles for the
		 * reports.
		 */

		Style style;

		// Rows CONTENT for NUMBERS
		style = new Style();
		style.setFont(Font.VERDANA_SMALL);
		style.setHorizontalAlign(HorizontalAlign.RIGHT);
		setColumnStyleNumbers(style);

		// Rows CONTENT for TEXT
		style = new Style();
		style.setFont(Font.VERDANA_SMALL);
		style.setHorizontalAlign(HorizontalAlign.LEFT);
		setColumnStyleText(style);

		// HEADER for NUMBERS row content
		style = new Style();
		style.setFont(Font.VERDANA_MEDIUM_BOLD);
		style.setHorizontalAlign(HorizontalAlign.RIGHT);
		style.setBorderBottom(Border.PEN_1_POINT);
		setHeaderStyleNumbers(style);

		// HEADER for TEXT row content
		style = new Style();
		style.setFont(Font.VERDANA_MEDIUM_BOLD);
		style.setHorizontalAlign(HorizontalAlign.LEFT);
		style.setBorderBottom(Border.PEN_1_POINT);
		setHeaderStyleText(style);

		// SUBTITLE
		style = new Style();
		style.setHorizontalAlign(HorizontalAlign.LEFT);
		style.setFont(Font.VERDANA_MEDIUM_BOLD);
		setSubtitleStyle(style);

		// TITLE
		style = new Style();
		style.setHorizontalAlign(HorizontalAlign.CENTER);
		Font titleFont = Font.VERDANA_BIG_BOLD;
		titleFont.setUnderline(true);
		style.setFont(titleFont);
		setTitleStyle(style);

		// FOOTER
		style = new Style();
		style.setFont(Font.VERDANA_SMALL);
		style.getFont().setFontSize(8);
		style.setHorizontalAlign(HorizontalAlign.CENTER);
		style.setBorderTop(Border.PEN_1_POINT);
		setFooterStyle(style);

	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	private void setColumnStyleNumbers(Style columnStyleNumbers) {
		this.columnStyleNumbers = columnStyleNumbers;
	}

	public Style getColumnStyleNumbers() {
		return this.columnStyleNumbers;
	}

	private void setColumnStyleText(Style columnStyleText) {
		this.columnStyleText = columnStyleText;
	}

	public Style getColumnStyleText() {
		return columnStyleText;
	}

	private void setHeaderStyleNumbers(Style headerStyleNumbers) {
		this.headerStyleNumbers = headerStyleNumbers;
	}

	public Style getHeaderStyleNumbers() {
		return headerStyleNumbers;
	}

	private void setHeaderStyleText(Style headerStyleText) {
		this.headerStyleText = headerStyleText;
	}

	public Style getHeaderStyleText() {
		return headerStyleText;
	}

	private void setSubtitleStyle(Style subtitleStyle) {
		this.subtitleStyle = subtitleStyle;
	}

	public Style getSubtitleStyle() {
		return subtitleStyle;
	}

	private void setTitleStyle(Style titleStyle) {
		this.titleStyle = titleStyle;
	}

	public Style getTitleStyle() {
		return titleStyle;
	}

	private void setFooterStyle(Style footerStyle) {
		this.footerStyle = footerStyle;
	}

	public Style getFooterStyle() {
		return footerStyle;
	}
}
