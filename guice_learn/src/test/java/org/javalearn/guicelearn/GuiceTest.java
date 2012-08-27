package org.javalearn.guicelearn;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.javalearn.guicelearn.service.TestService;

/**
 * Created with IntelliJ IDEA.
 * User: gongwenwei
 * Date: 12-8-25
 * Time: 下午2:44
 * To change this template use File | Settings | File Templates.
 */
public class GuiceTest {
       public static void main(String[] args){

           Injector injector = Guice.createInjector(new TestModule());

           /*
           * Now that we've got the injector, we can build objects.
           */
           TestService testService = injector.getInstance(TestService.class);
           testService.say();
       }
}
