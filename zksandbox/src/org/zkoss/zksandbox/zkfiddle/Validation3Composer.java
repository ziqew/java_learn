package org.zkoss.zksandbox.zkfiddle;

import org.zkoss.zk.ui.*;
import org.zkoss.zk.ui.event.*;
import org.zkoss.zk.ui.util.*;
import org.zkoss.zk.ui.ext.*;
import org.zkoss.zk.au.*;
import org.zkoss.zk.au.out.*;
import org.zkoss.zul.*;

public class Validation3Composer extends GenericForwardComposer{
    private Textbox tb1, tb2;
    private Button btSave;

    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        addConstraints();

    }

    public void onClick$btSave(Event e){
        try {
            addConstraints();
        } catch (Exception ex) {
            removeConstraints();
            ex.printStackTrace();
        }
    }

    private void addConstraints() {
        tb1.setConstraint(new Validator());
        tb2.setConstraint(new Validator());
    }
    private void removeConstraints() {
        tb1.setConstraint("");
        tb2.setConstraint("");
    }
}
