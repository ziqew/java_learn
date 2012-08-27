package org.javalearn.guicelearn.service;

import com.google.inject.Inject;
import com.google.inject.Provider;

import java.sql.Connection;

/**
 * Created with IntelliJ IDEA.
 * User: gongwenwei
 * Date: 12-8-25
 * Time: 下午3:14
 * To change this template use File | Settings | File Templates.
 */
public class DatabaseTransactionLogProvider implements Provider<TransactionLog> {

    private final Connection connection;

    @Inject
    public DatabaseTransactionLogProvider(Connection connection) {
        this.connection = connection;
    }

    public TransactionLog get() {
        DatabaseTransactionLog transactionLog = new DatabaseTransactionLog();
        transactionLog.setConnection(connection);
        return transactionLog;
    }
}
