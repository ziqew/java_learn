package org.javalearn.guicelearn.web;

import com.google.inject.ImplementedBy;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: gongwenwei
 * Date: 12-8-25
 * Time: 上午10:04
 * To change this template use File | Settings | File Templates.
 */
@ImplementedBy(HelloServiceImpl.class)
public interface HelloService {
    void execute() throws IOException;
}
