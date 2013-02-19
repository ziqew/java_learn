package org.zkoss.zksandbox.zkfiddle;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Label;

/**
 * Created with IntelliJ IDEA.
 * User: payegishemingway
 * Date: 2/19/13
 * Time: 10:02 AM
 * To change this template use File | Settings | File Templates.
 */
public class HeaderComposer extends GenericForwardComposer {


    Label lblHeader;

    @Override
    public void doAfterCompose(Component comp) throws Exception {

        try {
            super.doAfterCompose(comp);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

		/*
		 * retrieve url parameters
		 */
        String[] parameter = (String[]) param.get("test");

        if (parameter != null)
            lblHeader.setValue( "Congratulations! Your parameters value is " + parameter[0] );
        else
            lblHeader.setValue( "No parameters found. URL should be something like http://yourserver/yoursite/main.zul?parameter=param-value" );
    }
}