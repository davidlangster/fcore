package com.funkdefino.common.thread;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * <p/>
 * <code>$Id: $</code>
 * @author Differitas (David M. Lang)
 * @version $Revision: $
 */
public final class WorkerThreadUnitTest {

  //** ------------------------------------------------------------------ Nested

  private static IWorkerThread<String> workerThread;

  //** ------------------------------------------------------------------ Nested

  @Before
  public void init() throws Exception {
   workerThread = new WorkerThread<String>(1,4,0,-1,"test");
  }

  @Test
  public void test() throws Exception {
    String s = workerThread.execute(new WUTest());
    System.out.println("Result : " + s);
    Thread.sleep(4000);
  }

  @After
  public void exit() {
    workerThread.close();
  }

  //** ------------------------------------------------------------------ Nested

  private final static class WUTest implements IWorkUnit<String> {
    public String onExecute()  {
      try  {Thread.sleep(2000);}
      catch(InterruptedException excp){excp.printStackTrace();}
      return "Executed";
    }
    public String onThreadPoolExhausted() {return "Exhausted";}
    public void onExpiry(String obj, long timestamp) {
      System.out.println("onExpiry()");
    }
  }

} // class WorkerThreadUnitTest
