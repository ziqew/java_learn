package org.zkoss.zksandbox.zkfiddle;

import org.zkoss.zk.ui.*;
import org.zkoss.zk.ui.event.*;
import org.zkoss.zk.ui.util.*;
import org.zkoss.zk.ui.ext.*;
import org.zkoss.zk.au.*;
import org.zkoss.zk.au.out.*;
import org.zkoss.zul.*;
import java.util.ArrayList;

public class Validation4Composer extends GenericForwardComposer{

    private Textbox periodValue;
    private Textbox periodValue1;


    public void onClick$b(Event event) throws InterruptedException {

        ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
        if(periodValue.getValue().trim().equals("")){
            wve.add(new WrongValueException(periodValue, "Errore campo 1" ));
        }
        if(periodValue1.getValue().trim().equals("")){
            wve.add(new WrongValueException(periodValue1, "Errore campo 2" ));
        }

        if (wve.size() > 0) {
            throw new WrongValuesException(wve.toArray(new WrongValueException[wve.size()]));
        }
    }
}
