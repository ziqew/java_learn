package org.javalearn.guicelearn.service;

/**
 * Created with IntelliJ IDEA.
 * User: gongwenwei
 * Date: 12-8-25
 * Time: 下午2:55
 * To change this template use File | Settings | File Templates.
 */
public class CatEchoProcessor implements EchoProcessor {
    public void echo() {
        System.out.println("miao miao miao......");
    }
}
