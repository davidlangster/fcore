package com.funkdefino.common.message;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jms.listener.SessionAwareMessageListener;
import javax.jms.*;

/**
 * <p/>
 * <code>$Id: $</code>
 * @author Differitas (David M. Lang)
 * @version $Revision: $
 */
public final class Consumer implements SessionAwareMessageListener {

  /**
   * Application entry point.
   * @param args [Spring configuration file][context path][schema file]
   * @throws Exception on error.
   */
  public static void main(String[] args) throws Exception {
    ApplicationContext ctx = new ClassPathXmlApplicationContext(args[0]);
    Consumer consumer = ctx.getBean(Consumer.class);
  }

  //** -------------------------------------------------------------- Operations

  public void onMessage(Message message, Session session) throws JMSException {
    if(message instanceof TextMessage) {
      try  {System.out.println(((TextMessage) message).getText());}
      catch(JMSException excp) {
        excp.printStackTrace();
        session.rollback();
      }
    }
  }

  //** ------------------------------------------------------------------ Nested

  public final static class DefaultListener implements SessionAwareMessageListener {
    public void onMessage(Message message, Session session) throws JMSException {
      System.out.println("onMessage()");
      if(message instanceof TextMessage) {
        try  {System.out.println(((TextMessage) message).getText());}
        catch(JMSException excp) {
          excp.printStackTrace();
          session.rollback();
        }
      }
    }
  }

}
