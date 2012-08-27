package org.javalearn.guicelearn.service;

import com.google.inject.ImplementedBy;

/**
 * Created with IntelliJ IDEA.
 * User: gongwenwei
 * Date: 12-8-25
 * Time: 上午11:16
 * To change this template use File | Settings | File Templates.
 */
@ImplementedBy(TestServiceImpl.class)
public interface TestService {
    public void say();
}
