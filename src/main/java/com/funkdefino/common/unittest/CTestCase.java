package com.funkdefino.common.unittest;

import com.funkdefino.common.util.reflect.Loader;
import com.funkdefino.common.util.xml.*;
import com.funkdefino.common.util.*;

import  junit.framework.*;
import  java.io.File;
import  java.util.*;

/**
 * <p/>
 * <code>$Id: $</code>
 * A Junit wrapper
 * @author Differitas (David M. Lang)
 * @version $Revision: $
 */
public abstract class CTestCase extends TestCase {

  //** --------------------------------------------------- Static initialisation

  private static TestSuite s_suite  = null;
  private static boolean   s_bInit  = false;
  private static int       s_nCount = 0;

  //** --------------------------------------------------------------- Constants

  private final static String s_sElmntSuites = "Suites";
  private final static String s_sUnknownID   = "Unknown suite ID '%s'";

  //** ------------------------------------------------------------ Construction

  /**
   * Default ctor.
   */
  protected CTestCase()
  {
  }

  /**
   *
   * Constructor.
   * @param sMethod a test method name.
   */
  protected CTestCase(String sMethod) {
    super(sMethod);
  }

  //** ------------------------------------- Operations (overridable / abstract)

  protected void load() throws Exception {}

  //** ------------------------------------------------------ Operations (JUnit)

  /**
   * This method creates all fixture resources.
   * @throws Exception on error.
   */
  protected void setUp() throws Exception
  {
    // Perform suite-wide initialisation
    if(!s_bInit)  {
       s_nCount = 0;
       s_bInit = true;
       load();
    }

  } // setUp()

  /**
   * This method releases all fixture resources.
   * @throws Exception on error.
   */
  protected void tearDown() throws Exception {
    if(s_suite == null || s_suite.testCount() == ++s_nCount) {
       s_bInit = false;
    }
  }

  //** ---------------------------------------------------- Operations (Utility)

  /**
   * This method creates a test suite.
   * @param  clzTest the subclass class.
   * @param  sXmlConfig a configuration filename (or null).
   * @param  sID a suite ID (or null).
   * @return the test suite.
   */
  public static TestSuite suite(Class clzTest, String sXmlConfig, String sID)
  {

    TestSuite suite = null;
    Thread.currentThread().setContextClassLoader(clzTest.getClassLoader());
    Exception excp = null;
    s_bInit = false;

    try {
      XmlElement eConfig;
      if((eConfig = getConfig(clzTest, sXmlConfig)) != null) {
        suite = initTestSuite(eConfig, getSuiteID(sID));
      }
    }
    catch(Exception _excp) {_excp.printStackTrace();}
    finally {
      if(suite == null) {
         suite  = excp == null ? new TestSuite(clzTest) : new TestSuite();
      }
    }

    return (s_suite = suite);

  } // createTestSuite()

  /**
   * This allows a test to be repeatedly invoked on a number of threads.
   * @param  obj the parent object.
   * @param  sMethod the name of the method for repeat testing.
   * @param  nThreads the number of threads.
   * @param  nRepeats the number of test repeats.
   * @throws AssertionFailedError on error.
   */
  public static void stress(Object obj, String sMethod, int nThreads,
                            int nRepeats) throws AssertionFailedError
  {
    stress(obj, sMethod, nThreads, nRepeats, null);
  }

  /**
   * This allows a test to be repeatedly invoked on a number of threads.
   * @param  obj the parent object.
   * @param  sMethod the name of the method for repeat testing.
   * @param  nThreads the number of threads.
   * @param  nRepeats the number of test repeats.
   * @param  callback a runnable callback (or null).
   * @throws AssertionFailedError on error.
   * @return test duration.
   */
  public static long stress(Object obj, String sMethod, int nThreads,
                            int nRepeats, ICTCCallback callback)
                            throws AssertionFailedError
  {
    Object lock = new Object();
    CTCThreadPool pool = new CTCThreadPool(lock, callback);
    long lTimestamp = System.currentTimeMillis();

    try {
      synchronized(lock) {
        for(int i = 0; i < nThreads; i++)
          pool.createThread(obj, sMethod, nRepeats).start();
        lock.wait();
      }
    }
    catch(InterruptedException excp) {
      Thread.currentThread().interrupted();
      throw new AssertionFailedError();
    }

    return System.currentTimeMillis() - lTimestamp;

  } // stress()

  //** ---------------------------------------------------------- Implementation

  /**
   * This method returns a configuration element (if any).
   * @param  clzTest the subclass class.
   * @param  _sXmlConfig a configuration filename.
   * @throws Exception on error.
   * @return the element (or null).
   */
  private static XmlElement getConfig(Class clzTest, String _sXmlConfig) throws Exception
  {

    XmlElement eConfig = null;
    // Check for a system property override
    String  sXmlConfig = System.getProperty("xcore.unittest.config");
    if(sXmlConfig == null || sXmlConfig.equals("")) sXmlConfig = _sXmlConfig;
    if(sXmlConfig != null) {
       try {
         XmlDocument config = new XmlDocument(new File(sXmlConfig), false);
         eConfig = config.getRootElement();
       }
       catch(Exception excp) {
         XmlDocument config = XmlDocument.fromResource(clzTest, sXmlConfig);
         eConfig = config.getRootElement();
       }
    }

    return eConfig;

  } // getConfig()

  /**
   * This method returns a suite identifier.
   * @param  _sID a suite ID.
   * @return the suite ID (or null to test all suites).
   */
  private static String getSuiteID(String _sID)
  {
    // Check for a system property override
    String sID = System.getProperty("xcore.unittest.suite");
    if(sID == null || sID.equals("")) sID = _sID;
    if(sID != null && sID.toLowerCase().equals("all")) sID = null;
    return sID;

  } // getSuiteID()

  /**
   * This method creates and initialises a test suite with configured methods.
   * @param  eConfig the configuration element.
   * @param  _sID the suite ID (or null to add all configured suites).
   * @return the test suite.
   * @throws UtilException on error.
   */
  private static TestSuite initTestSuite(XmlElement eConfig, String _sID)
                           throws UtilException
  {

    TestSuite suite = new TestSuite();
    XmlElement eSuites = XmlValidate.getElement(eConfig, s_sElmntSuites);
    Iterator ii = eSuites.getChildren().iterator();
    while(ii.hasNext()) {
      XmlElement eSuite = (XmlElement)ii.next();
      String sID = XmlValidate.getAttribute(eSuite, ICConstants.ATTR_ID);
      String sClass = XmlValidate.getAttribute(eSuite, ICConstants.ATTR_CLASS);
      if(_sID == null || _sID.equals(sID)) {
         Iterator jj = eSuite.getChildren().iterator();
         while(jj.hasNext()) {
            String sMethod = ((XmlElement)jj.next()).getContent();
            Loader.ArgumentList argList = new Loader.ArgumentList(String.class, sMethod);
            TestCase tc  = (TestCase) Loader.getTarget(sClass, argList);
            if(tc != null) suite.addTest(tc);
         }
      }
    }

    if(suite.countTestCases() == 0 && _sID != null) {
      String sExcp = String.format(s_sUnknownID, _sID);
      throw new UtilException(sExcp);
    }

    return suite;

  } // initTestSuite()

} // class CTestCase
