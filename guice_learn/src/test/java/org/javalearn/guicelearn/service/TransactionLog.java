package org.javalearn.guicelearn.service;

/**
 * Created with IntelliJ IDEA.
 * User: gongwenwei
 * Date: 12-8-25
 * Time: 下午3:07
 * To change this template use File | Settings | File Templates.
 */
public interface TransactionLog  {
    void setJdbcUrl(String url);
    void setThreadPoolSize(int size);
}
