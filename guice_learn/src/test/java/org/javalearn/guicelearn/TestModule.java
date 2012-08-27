package org.javalearn.guicelearn;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.name.Names;
import org.javalearn.guicelearn.service.*;

/**
 * Created with IntelliJ IDEA.
 * User: gongwenwei
 * Date: 12-8-25
 * Time: 上午11:15
 * To change this template use File | Settings | File Templates.
 */
public class TestModule extends AbstractModule {
    @Override
    protected void configure() {
        //Linked Bindings
        // The @ImplementedBy annotation acts like a linked binding
        //bind(TestService.class).to(TestServiceImpl.class);
        //Binding Annotations
        bind(EchoProcessor.class).annotatedWith(Dog.class).to(DogEchoProcessor.class);
        //@Named
        bind(EchoProcessor.class).annotatedWith(Names.named("Cat")).to(CatEchoProcessor.class);

        bind(String.class)
                .annotatedWith(Names.named("JDBC_URL"))
                .toInstance("jdbc:mysql://localhost/pizza");
        //Instance Bindings
        bind(Integer.class)
                .annotatedWith(Names.named("login timeout seconds"))
                .toInstance(10);
        /*
        bind(TransactionLog.class)
                .toProvider(DatabaseTransactionLogProvider.class); */
    }

    /*
    @Provides
    TransactionLog provideTransactionLog() {
        DatabaseTransactionLog transactionLog = new DatabaseTransactionLog();
        transactionLog.setJdbcUrl("jdbc:mysql://localhost/pizza");
        transactionLog.setThreadPoolSize(30);
        return transactionLog;
    } */
}
