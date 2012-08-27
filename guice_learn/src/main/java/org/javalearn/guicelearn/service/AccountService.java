package org.javalearn.guicelearn.service;

import com.google.inject.ImplementedBy;

/**
 * Created with IntelliJ IDEA.
 * User: gongwenwei
 * Date: 12-8-25
 * Time: 上午10:17
 * To change this template use File | Settings | File Templates.
 */
@ImplementedBy(AccountServiceImpl.class)
public interface AccountService {

    public String getName();
}
