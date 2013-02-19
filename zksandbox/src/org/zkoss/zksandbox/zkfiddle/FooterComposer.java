package org.zkoss.zksandbox.zkfiddle;

import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Button;
import org.zkoss.zul.Include;
import org.zkoss.zul.Label;
import org.zkoss.zul.Popup;

public class FooterComposer extends GenericForwardComposer {
    Button footer_btn;
    Label footer_lb;

    public void onClick$footer_btn() {
        ((Popup)page.getFellow("footer").getFellow("footer_popup")).open(footer_btn, "end_before");
        Include mainInclude = (Include)page.getFellow("main");
        if (mainInclude.hasFellow("main_one_popup"))
            ((Popup)mainInclude.getFellow("main_one_popup")).open(mainInclude, "end_before");
        else
            ((Popup)mainInclude.getFellow("main_two_popup")).open(mainInclude, "end_before");
    }
}

