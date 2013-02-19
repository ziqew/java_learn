package org.zkoss.zksandbox.zkfiddle;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventQueue;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Textbox;

/**
 * Created with IntelliJ IDEA.
 * User: payegishemingway
 * Date: 2/19/13
 * Time: 10:07 AM
 * To change this template use File | Settings | File Templates.
 */
public class Include1Composer extends GenericForwardComposer {

    private Textbox msg;

    /*
     * create "connection" EventQueue  , all the "connection" EventQueue could talke to each other.
     *
         *   (You could change the naming of "connection" to anything you like. ;) )
         *
     * Note: we use desktop level EventQueue , only EventQueue in the same desktop will get the published event.
     *
         *      That means if there are different session or different desktop , it won't have any side effect to others.
                But if you are using "application scope" EventQueue , you have to take care of taht.
         *
     *
     * More reference.
     * http://books.zkoss.org/wiki/ZK_Developer's_Reference/Event_Handling/Event_Queues
     */
    EventQueue qe = EventQueues.lookup(MyConsts.EVENTQUEUE_CONNECTION, true);

    public void onClick$btn(Event e) {

        //publish event to include1 by connection EventQueue

        //Note here the third argument is event data , you could get the event from reciever.
        //if you need to pass multiple objects , you could consider to pass a List/Map.

        qe.publish(new Event(MyConsts.EVENT_MESSAGE_1,null,msg.getValue()));
    }
}