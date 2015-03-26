package com.funkdefino.common.message;

import org.junit.After;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.ApplicationContext;
import org.junit.Before;
import org.junit.Test;


/**
 * <p/>
 * <code>$Id: $</code>
 * @author Differitas (David M. Lang)
 * @version $Revision: $
 */
public final class ProducerUnitTest {

  private static ApplicationContext appCtx;
  private static Producer producer;

  @Before
  public void init() {
    appCtx = new ClassPathXmlApplicationContext("Spring.JMS.xml");
    producer = appCtx.getBean(Producer.class);
  }

  @Test
  public void test01() throws Exception {
    int i = 0;
    while(true) {
      try {
        producer.writeMessage(String.format("Hello World [%s]", ++i));
        //Thread.sleep(100);
      }
      catch(Exception excp) {
        System.out.println(excp.getMessage());
      }
    }
  }

  @After
  public void exit() {
  }

  private synchronized void doWait() {
    System.out.println("Waiting ...");
    try  {this.wait();}
    catch(InterruptedException excp) {
      excp.printStackTrace();
    }
  }

} // class ProducerUnitTest
