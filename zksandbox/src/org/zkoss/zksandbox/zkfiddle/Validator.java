package org.zkoss.zksandbox.zkfiddle;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zul.Bandbox;
import org.zkoss.zul.Constraint;
import org.zkoss.zul.Textbox;

public class Validator implements Constraint, java.io.Serializable {
    private static final long serialVersionUID = 1L;
    public Validator() {
        super();
    }
    @Override
    public void validate(Component comp, Object value) {
        String field = "";

        String enteredValue = String.valueOf(value);

        if (enteredValue.isEmpty()) {
            if (comp instanceof Textbox) {
                Textbox var = (Textbox) comp;
                field = var.getTooltip();
                var.setFocus(true);
            } else if (comp instanceof Bandbox) {
                Bandbox var = (Bandbox) comp;
                field = var.getTooltip();
                var.setFocus(true);
            }
            throw new WrongValueException(comp, String.format(Messages.MSG_CAMP_OBRIG, field));
            //throw new UnsupportedOperationException("O campo " + nmCampo + " é obrigatório.");
        }
    }
}
