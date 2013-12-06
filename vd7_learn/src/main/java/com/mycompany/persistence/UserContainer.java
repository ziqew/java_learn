package com.mycompany.persistence;


import com.mycompany.MyVaadinUI;
import com.mycompany.entity.User;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;
import com.vaadin.addon.jpacontainer.provider.CachingLocalEntityProvider;

/**
 * Created by ziqew on 12/5/13.
 */
public class UserContainer extends JPAContainer<User> {

    public UserContainer() {
        super(User.class);
        setEntityProvider(new CachingLocalEntityProvider<User>(
                User.class,
                JPAContainerFactory
                        .createEntityManagerForPersistenceUnit(MyVaadinUI.PERSISTENCE_UNIT)));
    }


}
