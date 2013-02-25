package org.zkoss.zksandbox.zkfiddle;

import org.zkoss.zk.ui.*;
import org.zkoss.zk.ui.event.*;
import org.zkoss.zk.ui.util.*;
import org.zkoss.zk.ui.ext.*;
import org.zkoss.zk.au.*;
import org.zkoss.zk.au.out.*;
import org.zkoss.zul.*;
import org.zkoss.zul.impl.*;

import java.util.*;

public class Validation1Composer extends GenericForwardComposer{

    private Component form;
    private Label label;

    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
    }

    public void onClick$btn(Event e){
        check(form);

        // This line is only executed is form is valid
        label.setValue("Validation OK!");
    }

    private void check(Component component) {
        checkIsValid(component);

        List<Component> children = component.getChildren();
        for (Component each: children) {
            check(each);
        }
    }

    private void checkIsValid(Component component) {
        if (component instanceof InputElement) {
            if (!((InputElement) component).isValid()) {
                // Force show errorMessage
                ((InputElement) component).getText();
            }
        }
    }

}
