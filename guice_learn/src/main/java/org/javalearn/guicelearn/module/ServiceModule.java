package org.javalearn.guicelearn.module;

import com.google.inject.Binder;
import com.google.inject.Module;
import org.javalearn.guicelearn.service.AccountService;
import org.javalearn.guicelearn.service.AccountServiceImpl;

/**
 * Created with IntelliJ IDEA.
 * User: gongwenwei
 * Date: 12-8-25
 * Time: 上午10:20
 * To change this template use File | Settings | File Templates.
 */
public class ServiceModule implements Module {
    public void configure(Binder binder) {
        binder.bind(AccountService.class).to(AccountServiceImpl.class);
    }
}
