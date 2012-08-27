package org.javalearn.guicelearn.service;

import com.google.inject.Inject;
import com.google.inject.name.Named;

/**
 * Created with IntelliJ IDEA.
 * User: gongwenwei
 * Date: 12-8-25
 * Time: 上午11:16
 * To change this template use File | Settings | File Templates.
 */
public class TestServiceImpl implements TestService {
    @Inject
    @Dog
    private EchoProcessor dogEchoProcessor;

    @Inject
    @Named("Cat")
    private EchoProcessor catEchoProcessor;

    public EchoProcessor getCatEchoProcessor() {
        return catEchoProcessor;
    }

    public void setCatEchoProcessor(EchoProcessor catEchoProcessor) {
        this.catEchoProcessor = catEchoProcessor;
    }

    public EchoProcessor getDogEchoProcessor() {
        return dogEchoProcessor;
    }

    public void setDogEchoProcessor(EchoProcessor dogEchoProcessor) {
        this.dogEchoProcessor = dogEchoProcessor;
    }

    public void say(){
        dogEchoProcessor.echo();
        catEchoProcessor.echo();
    }
}
