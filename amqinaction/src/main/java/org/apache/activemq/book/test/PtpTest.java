package org.apache.activemq.book.test;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;


public class PtpTest {
	private static String brokerURL = "tcp://localhost:61616";
	
	public static void main(String[] args){
		
	}
	
	class ConsumerThread extends Thread{
	    private  ConnectionFactory factory;
	    private  Connection connection;
	    private  Session session;

	    
	    ConsumerThread(){
	    	super();
	    	factory = new ActiveMQConnectionFactory(brokerURL);
	    	try{
		    	connection = factory.createConnection();
		        connection.start();
		        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
	    	}catch(Exception ex){
	    		ex.printStackTrace();
	    	}
	    }
	    
	    
	    public void run() {
	    	try{
	    		Destination destination = session.createQueue("JOBS.queue");
	    		MessageConsumer messageConsumer = session.createConsumer(destination);
	    		messageConsumer.setMessageListener(new MyListener());
	    		while(true){
	    			Thread.sleep(1000);
	    		}
	    	}catch(Exception ex){
	    		ex.printStackTrace();
	    	}

	    }
	    
	    
		
	}
	
	class ProducerThread extends Thread{
		
	    private  ConnectionFactory factory;
	    private  Connection connection;
	    private  Session session;
	    private  MessageProducer producer;
	    protected  int count = 10;
	    
	    ProducerThread(){
	    	super();
	    	factory = new ActiveMQConnectionFactory(brokerURL);
	    	try{
		    	connection = factory.createConnection();
		        connection.start();
		        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		        producer = session.createProducer(null);
	    	}catch(Exception ex){
	    		ex.printStackTrace();
	    	}

	    	
	    	
	    }
	    
		public void run(){
			try{
				Destination destination = session.createQueue("JOBS.queue");
				for (int i = 0; i < count; i++) {
			        Message message = session.createObjectMessage(i);
			        System.out.println("Sending: id: " + ((ObjectMessage)message).getObject() + " on queue: " + destination);
			        producer.send(destination, message);
			        Thread.sleep(1000);
	            }
			}catch (Exception ex){
				ex.printStackTrace();
			}
			
		}
	}
	
	class MyListener implements MessageListener {


		
		public MyListener() {

		}

		public void onMessage(Message message) {
			try {
				//do something here
				System.out.println(((ObjectMessage)message).getObject());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
}
