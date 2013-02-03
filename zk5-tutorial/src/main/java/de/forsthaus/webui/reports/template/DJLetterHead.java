package de.forsthaus.webui.reports.template;

import java.awt.Color;
import java.util.Date;

import org.zkoss.spring.SpringUtil;
import org.zkoss.util.resource.Labels;

import ar.com.fdvs.dj.core.layout.HorizontalBandAlignment;
import ar.com.fdvs.dj.domain.AutoText;
import ar.com.fdvs.dj.domain.DynamicReport;
import ar.com.fdvs.dj.domain.ExpressionHelper;
import ar.com.fdvs.dj.domain.Style;
import ar.com.fdvs.dj.domain.builders.FastReportBuilder;
import ar.com.fdvs.dj.domain.builders.StyleBuilder;
import ar.com.fdvs.dj.domain.constants.Font;
import de.forsthaus.webui.util.ZksampleDateFormat;

/**
 * A letter head as a DynamicReportBuilder subreport (DynamicJasper framework).<br>
 *<br>
 * 
 * @author bbruhns
 * @author sge
 * 
 */
public class DJLetterHead extends DynamicReport {

	private static final long serialVersionUID = 1L;
	private DJReportStyles djReportStyles;

	public DJLetterHead() {
		super();

		createLetterHead();
	}

	private DynamicReport createLetterHead() {

		FastReportBuilder rb = new FastReportBuilder();

		// TEST
		Style atStyle = new StyleBuilder(true).setFont(Font.COMIC_SANS_SMALL).setTextColor(Color.red).build();
		/**
		 * Adding many autotexts in the same position (header/footer and
		 * aligment) makes them to be one on top of the other
		 */

		AutoText created = new AutoText(Labels.getLabel("common.Created") + ": " + ZksampleDateFormat.getDateTimeFormater().format(new Date()), AutoText.POSITION_HEADER, HorizontalBandAlignment.RIGHT);
		created.setWidth(new Integer(120));
		created.setStyle(atStyle);
		rb.addAutoText(created);

		AutoText autoText = new AutoText(AutoText.AUTOTEXT_PAGE_X_SLASH_Y, AutoText.POSITION_HEADER, HorizontalBandAlignment.RIGHT);
		autoText.setWidth(new Integer(20));
		autoText.setStyle(atStyle);
		rb.addAutoText(autoText);

		AutoText name1 = new AutoText("The Zksample2 Ltd.", AutoText.POSITION_HEADER, HorizontalBandAlignment.LEFT);
		name1.setPrintWhenExpression(ExpressionHelper.printInFirstPage());
		AutoText name2 = new AutoText("Software Consulting", AutoText.POSITION_HEADER, HorizontalBandAlignment.LEFT);
		name2.setPrintWhenExpression(ExpressionHelper.printInFirstPage());
		AutoText street = new AutoText("256, ZK Direct RIA Street ", AutoText.POSITION_HEADER, HorizontalBandAlignment.LEFT);
		street.setPrintWhenExpression(ExpressionHelper.printInFirstPage());
		AutoText city = new AutoText("ZKoss City", AutoText.POSITION_HEADER, HorizontalBandAlignment.LEFT);
		city.setPrintWhenExpression(ExpressionHelper.printInFirstPage());
		rb.addAutoText(name1).addAutoText(name2).addAutoText(street).addAutoText(city);
		// Footer
		AutoText footerText = new AutoText("Help to prevent the global warming by writing cool software.", AutoText.POSITION_FOOTER, HorizontalBandAlignment.CENTER);
		footerText.setStyle(getDjReportStyles().getFooterStyle());
		rb.addAutoText(footerText);

		DynamicReport dr = new DynamicReport();
		dr = rb.build();

		return dr;

	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setDjReportStyles(DJReportStyles djReportStyles) {
		this.djReportStyles = djReportStyles;
	}

	public DJReportStyles getDjReportStyles() {
		if (djReportStyles == null) {
			djReportStyles = (DJReportStyles) SpringUtil.getBean("djReportStyles");
		}
		return djReportStyles;
	}

}
