package org.zkoss.zksandbox.zkfiddle;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.EventQueue;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Label;
/**
 * Created with IntelliJ IDEA.
 * User: payegishemingway
 * Date: 2/19/13
 * Time: 10:07 AM
 * To change this template use File | Settings | File Templates.
 */
public class Include2Composer extends GenericForwardComposer{
    private Label lbl;

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
    private EventQueue qe= EventQueues.lookup(MyConsts.EVENTQUEUE_CONNECTION,true);

    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);


        //subcribe event
        qe.subscribe(new EventListener() {
            public void onEvent(Event event) throws Exception {

                //If you have many event go through this event queue,
                // you have to handle the event name by yourself now.

                //All the event will publish to every subcriber.
                if(MyConsts.EVENT_MESSAGE_1.equals(event.getName())){
                    //Recieved from Include1
                    String message = (String) event.getData();
                    lbl.setValue(message);

                }
            }
        });
    }
}