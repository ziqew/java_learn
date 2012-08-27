package org.javalearn.guicelearn.service;

import java.sql.Connection;

/**
 * Created with IntelliJ IDEA.
 * User: gongwenwei
 * Date: 12-8-25
 * Time: 下午3:07
 * To change this template use File | Settings | File Templates.
 */
public class DatabaseTransactionLog implements TransactionLog {

    private  Connection connection;

    private String jdbcUrl;
    private int threadPoolSize;


    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public String getJdbcUrl() {
        return jdbcUrl;
    }

    public void setJdbcUrl(String jdbcUrl) {
        this.jdbcUrl = jdbcUrl;
    }

    public int getThreadPoolSize() {
        return threadPoolSize;
    }

    public void setThreadPoolSize(int threadPoolSize) {
        this.threadPoolSize = threadPoolSize;
    }
}
