package com.mycompany;

import com.mycompany.entity.User;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;
import com.vaadin.data.Property;
import com.vaadin.data.util.BeanItem;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.*;


/**
 * Created by ziqew on 12/5/13.
 */
public class UserListView extends VerticalLayout implements View {
    private Table userTable;

    private TextField searchField;

    private Button newButton;
    private Button deleteButton;
    private Button editButton;

    private JPAContainer<User> users;
    private String textFilter;


    public UserListView(){
        users = JPAContainerFactory.make(User.class,
                MyVaadinUI.PERSISTENCE_UNIT);
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {

        userTable = new Table(null, users);
        userTable.setSelectable(true);
        userTable.setImmediate(true);
        userTable.addListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                setModificationsEnabled(event.getProperty().getValue() != null);
            }

            private void setModificationsEnabled(boolean b) {
                deleteButton.setEnabled(b);
                editButton.setEnabled(b);
            }
        });

        userTable.setSizeFull();
        // personTable.setSelectable(true);
        userTable.addListener(new ItemClickEvent.ItemClickListener() {
            @Override
            public void itemClick(ItemClickEvent event) {
                if (event.isDoubleClick()) {
                    userTable.select(event.getItemId());
                }
            }
        });

        userTable.setVisibleColumns(new Object[] { "id", "name", "passwordDigest",
                "createdAt", "updatedAt"});

        HorizontalLayout toolbar = new HorizontalLayout();
        newButton = new Button("Add");
        newButton.addClickListener(new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                final BeanItem<User> newPersonItem = new BeanItem<User>(
                        new User());
                UserEditor personEditor = new UserEditor(newPersonItem);
                personEditor.addListener(new UserEditor.EditorSavedListener() {
                    @Override
                    public void editorSaved(UserEditor.EditorSavedEvent event) {
                        users.addEntity(newPersonItem.getBean());
                    }
                });
                UI.getCurrent().addWindow(personEditor);
            }
        });

        deleteButton = new Button("Delete");
        deleteButton.addClickListener(new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                users.removeItem(userTable.getValue());
            }
        });
        deleteButton.setEnabled(false);

        editButton = new Button("Edit");
        editButton.addClickListener(new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                UI.getCurrent().addWindow(
                        new UserEditor(userTable.getItem(userTable
                                .getValue())));
            }
        });
        editButton.setEnabled(false);

        searchField = new TextField();
        searchField.setInputPrompt("Search by name");
        searchField.addTextChangeListener(new FieldEvents.TextChangeListener() {

            @Override
            public void textChange(FieldEvents.TextChangeEvent event) {
                textFilter = event.getText();
                updateFilters();
            }
        });

        toolbar.addComponent(newButton);
        toolbar.addComponent(deleteButton);
        toolbar.addComponent(editButton);
        toolbar.addComponent(searchField);
        toolbar.setWidth("100%");
        toolbar.setExpandRatio(searchField, 1);
        toolbar.setComponentAlignment(searchField, Alignment.TOP_RIGHT);
        this.addComponent(toolbar);
        this.addComponent(userTable);
        this.setExpandRatio(userTable, 1);
        this.setSizeFull();
    }


    private void updateFilters() {
//        persons.setApplyFiltersImmediately(false);
//        persons.removeAllContainerFilters();
//        if (departmentFilter != null) {
//            // two level hierarchy at max in our demo
//            if (departmentFilter.getParent() == null) {
//                persons.addContainerFilter(new Equal("department.parent",
//                        departmentFilter));
//            } else {
//                persons.addContainerFilter(new Equal("department",
//                        departmentFilter));
//            }
//        }
//        if (textFilter != null && !textFilter.equals("")) {
//            Or or = new Or(new Like("firstName", textFilter + "%", false),
//                    new Like("lastName", textFilter + "%", false));
//            persons.addContainerFilter(or);
//        }
//        persons.applyFilters();
    }
}
