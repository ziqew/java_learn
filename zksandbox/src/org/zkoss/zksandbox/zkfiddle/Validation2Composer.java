package org.zkoss.zksandbox.zkfiddle;

import org.zkoss.zk.ui.*;
import org.zkoss.zk.ui.event.*;
import org.zkoss.zk.ui.util.*;
import org.zkoss.zk.ui.ext.*;
import org.zkoss.zul.*;

public class Validation2Composer extends GenericForwardComposer{
    protected Textbox one;
    protected Combobox two;
    protected Combobox three;
    protected Textbox four;
    protected Textbox five;
    protected Intbox six;
    protected Textbox seven;
    protected Textbox eight;
    protected Label errormsg;

    public void onClickSaveButton(Event event) {

        errormsg.setValue("");

        if(one.getValue().equals("")){
            errormsg.setValue("All Mandatory Fields are Required");
            one.setStyle("border-color: #EA101E");
        }

        if(two.getSelectedIndex() == -1){
            errormsg.setValue("All Mandatory Fields are Required");
            two.setStyle("border: 1px solid #EA101E;");
        }

        if(three.getSelectedIndex() == -1){
            errormsg.setValue("All Mandatory Fields are Required");
            three.setStyle("border: 1px solid #EA101E;");
        }

        if(four.getValue().equals("")){
            errormsg.setValue("All Mandatory Fields are Required");
            four.setStyle("border-color: #EA101E");
        }

        if(six.getValue() == null){
            errormsg.setValue("All Mandatory Fields are Required");
            six.setStyle("border-color: #EA101E");
        }

        if(seven.getValue().equals("")){
            errormsg.setValue("All Mandatory Fields are Required");
            seven.setStyle("border-color: #EA101E");
        }
    }
}