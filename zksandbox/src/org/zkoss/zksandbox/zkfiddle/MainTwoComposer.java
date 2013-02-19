package org.zkoss.zksandbox.zkfiddle;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Button;
import org.zkoss.zul.Include;
import org.zkoss.zul.Label;

public class MainTwoComposer extends GenericForwardComposer {
    Button main_two_btn;

    public void doAfterCompose (Component comp) throws Exception  {
        super.doAfterCompose(comp);
        if (page.hasFellow("footer"))
            ((Label)page.getFellow("footer").getFellow("divFooter").getFellow("footer_lb")).setValue("currently at main_one");
    }

    public void onClick$main_two_btn () {
        ((Include)page.getFellow("main")).setSrc("main_one.zul");
    }
}
