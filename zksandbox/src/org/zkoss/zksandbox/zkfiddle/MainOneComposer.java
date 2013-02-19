package org.zkoss.zksandbox.zkfiddle;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Button;
import org.zkoss.zul.Include;
import org.zkoss.zul.Label;

/**
 * Created with IntelliJ IDEA.
 * User: payegishemingway
 * Date: 2/19/13
 * Time: 10:13 AM
 * To change this template use File | Settings | File Templates.
 */
public class MainOneComposer extends GenericForwardComposer {
    Button main_one_btn;

    public void doAfterCompose (Component comp) throws Exception  {
        super.doAfterCompose(comp);
        if (page.hasFellow("footer"))
            ((Label)page.getFellow("footer").getFellow("divFooter").getFellow("footer_lb")).setValue("currently at main_two");
    }

    public void onClick$main_one_btn () {
        ((Include)page.getFellow("main")).setSrc("main_two.zul");
    }
}

