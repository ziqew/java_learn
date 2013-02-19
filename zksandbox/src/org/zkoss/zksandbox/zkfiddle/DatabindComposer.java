package org.zkoss.zksandbox.zkfiddle;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: payegishemingway
 * Date: 2/19/13
 * Time: 9:45 AM
 * To change this template use File | Settings | File Templates.
 */
public class DatabindComposer extends GenericForwardComposer {

    private List<Person> persons;

    private Listbox theList;


    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);

        List<Person> personsList = initPersonsList();
        self.setAttribute("persons", personsList);
    }

    public void onClick$createJohnny(Event event){
        ListModelList model = (ListModelList) theList.getModel();
        model.add(new Person("Johnny", new Date()));
    }

    public void onSelect$theList(Event event) {
        updateWithJava();
        updateWithJavascript();
    }

    private void updateWithJava() {
        Label selectedName = (Label) self.getFellow("selectedNameJ");
        Label selectedBirthdate = (Label) self.getFellow("selectedBirthdateJ");

        Person selectedPerson = getSelectedPerson();
        selectedName.setValue(selectedPerson.getName());
        selectedBirthdate.setValue(selectedPerson.getBirthdate().toString());
    }

    private void updateWithJavascript() {
        Person selectedPerson = getSelectedPerson();

        Clients.evalJavaScript("zk.Widget.$('$selectedName').setValue('"
                + selectedPerson.getName() + "')");
        Clients.evalJavaScript("zk.Widget.$('$selectedBirthdate').setValue('"
                + selectedPerson.getBirthdate().toString() + "')");
    }

    private Person getSelectedPerson() {
        ListModelList listModel = (ListModelList) theList.getModel();
        Person selectedPerson = (Person) listModel.getElementAt(theList
                .getSelectedIndex());
        return selectedPerson;
    }

    private List<Person> initPersonsList() {
        persons = new ArrayList<Person>();

        persons.add(new Person("John Doe", new Date()));
        persons.add(new Person("Martin Scorsese", new Date()));
        persons.add(new Person("Madonna", new Date()));
        return persons;
    }

}

