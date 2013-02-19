package org.zkoss.zksandbox.zkfiddle;

/**
 * Created with IntelliJ IDEA.
 * User: payegishemingway
 * Date: 2/19/13
 * Time: 9:34 AM
 * To change this template use File | Settings | File Templates.
 */
import org.zkoss.zk.ui.*;
import org.zkoss.zk.ui.event.*;
import org.zkoss.zk.ui.util.*;
import org.zkoss.zk.ui.ext.*;
import org.zkoss.zk.au.*;
import org.zkoss.zk.au.out.*;
import org.zkoss.zul.*;

public class TestComposer extends GenericForwardComposer{

    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);

    }

    public void onClick$btn(Event e) throws InterruptedException{
        Messagebox.show("Hi btn");
    }
}

