package org.javalearn.guicelearn.service;

/**
 * Created with IntelliJ IDEA.
 * User: gongwenwei
 * Date: 12-8-25
 * Time: 下午2:48
 * To change this template use File | Settings | File Templates.
 */
public class DogEchoProcessor implements  EchoProcessor{
    public void echo(){
        System.out.println("wang wang wang......");
    }
}
