package com.funkdefino.common.pool;

import com.funkdefino.common.util.event.IEventManager;
import com.funkdefino.common.util.event.Severity;
import com.funkdefino.common.util.xml.XmlValidate;
import com.funkdefino.common.util.xml.XmlElement;
import com.funkdefino.common.util.UtilException;
import com.funkdefino.common.util.ICConstants;
import com.funkdefino.common.util.reflect.Loader;

import java.util.*;

/**
 * <p/>
 * <code>$Id: $</code>
 * @author Differitas (David M. Lang)
 * @version $Revision: $
 */
public class ObjectPool<T> implements IObjectPool<T> {

  //** --------------------------------------------------------------- Constants

  private final static String s_sElmntFactory  = "Factory";
  private final static String s_sAttrbInitial  = "initialSize";
  private final static String s_sAttrbMaximum  = "maximumSize";
  private final static String s_sInvalidSizes  = "Invalid size parameter(s)";
  private final static String s_sLoadedFactory = "Loaded factory '%s'";
  private final static String s_sNoFactory     = "Unable to load object factory";
  private final static String s_sOnDemand      = "Created an object on demand";
  private final static String s_sCreated       = "Created %s object(s)";
  private final static String s_sClosed        = "Closed - removed %s object(s)";
  private final static String s_sNotAvailable  = "No free object available";

  //** -------------------------------------------------------------------- Data

  private int m_nInitial;
  private int m_nMaximum;
  private IObjectFactory<T> m_factory;
  private List<Entry<T>> m_lsEntry;
  private IEventManager m_eventMgr;

  //** ------------------------------------------------------------ Construction

  /**
   * Ctor.
   * @param nInitial the initial pool size.
   * @param nMaximum the maximum pool size.
   * @param factory an object factory.
   * @param eventMgr an event manager implementation (or null).
   * @throws UtilException on error.
   */
  public ObjectPool(int nInitial, int nMaximum, IObjectFactory<T> factory,
                    IEventManager eventMgr) throws UtilException {
    try  {initialise(nInitial, nMaximum, factory, eventMgr);}
    catch(UtilException excp) {
      close();
      throw excp;
    }
  }

  /**
   * Ctor.
   * @param eConfig a configuration element.
   * @param eventMgr an event manager implementation (or null).
   * @throws UtilException on error.
   */
  public ObjectPool(XmlElement eConfig, IEventManager eventMgr) throws UtilException {
    try  {initialise(eConfig, eventMgr);}
    catch(UtilException excp) {
      close();
      throw excp;
    }
  }

  //** -------------------------------------- Operations (IObjectPool interface)

  /**
   * This checks out the next free object.
   * @return the object (or null if none available).
   * @throws UtilException on error.
   */
  public synchronized T checkout() throws UtilException {
    
    T tObj = null;
    for(Entry<T> entry : m_lsEntry) {
      if(!entry.isInUse()) {
        tObj = entry.getObject();
        entry.setInUse(true);
        break;
      }
    }

    if(tObj == null && m_lsEntry.size() < m_nMaximum) {
       if((tObj = m_factory.create()) != null) {
         m_lsEntry.add(new Entry<T>(tObj, true));
         log(s_sOnDemand, Severity.INFO);
       }
    }

    if(tObj == null) {
       log(s_sNotAvailable, Severity.ERROR);
    }

    return tObj;

  } // checkout()

  /**
   * This releases an object back into the pool.
   * @param tObj the object.
   */
  public synchronized void release(T tObj) {
    Iterator<Entry<T>> ii = m_lsEntry.iterator();
    while(ii.hasNext()) {
      Entry entry = ii.next();
      if(tObj == entry.getObject()) {
        entry.setInUse(false);
        break;
      }
    }
  }

  /**
   * This removes an object from the pool.
   * @param tObj the object.
   */
  public synchronized void remove(T tObj) {
    Iterator<Entry<T>> ii = m_lsEntry.iterator();
    while(ii.hasNext()) {
      if(tObj == ii.next().getObject()) {
        remove(tObj,ii);
        break;
      }
    }
  }

  /**
   * An iterator.
   * @param cb a callback.
   */
  public synchronized void iterate(IOPCallback<T> cb) {
    Iterator<Entry<T>> ii = m_lsEntry.iterator();
    boolean bComplete = false;
    while(ii.hasNext() && !bComplete) {
      Entry<T> entry = ii.next();
      bComplete = cb.action(entry.getObject(), entry.isInUse(), ii, !ii.hasNext());
    }
  }

  /**
   * This dispose of an object during iteration.
   * @param tObj the object.
   * @param ii the iterator.
   */
  public synchronized void remove(T tObj, Iterator ii) {
    try  {m_factory.dispose(tObj); }
    catch(Exception excp){}
    ii.remove();
  }

  /**
   * This returns the pool size.
   * @return the size.
   */
  public synchronized int size() {
    return m_lsEntry.size();
  }

  /**
   * This releases all resources
   */
  public synchronized  void close() {
    if(m_lsEntry != null) {
      for(Entry<T> entry : m_lsEntry) {
        try  {m_factory.dispose(entry.getObject());}
        catch(Exception excp) {
          excp.printStackTrace();
        }
      }
      log(String.format(s_sClosed, m_lsEntry.size()), Severity.INFO);
      m_lsEntry.clear();
    }
  }
  
  //** ---------------------------------------------------- Operations (Utility)

  /**
   * Conditional logging.
   * @param sLog the log string.
   * @param severity a severity.
   */
  protected final void log(String sLog, Severity severity) {
    if(m_eventMgr != null) {
       m_eventMgr.post(this, sLog, severity);
    }
  }

  //** ---------------------------------------------------------- Implementation

  /**
   * This performs startup initialisation.
   * @param eConfig a configuration element.
   * @param eventMgr an event manager implementation.
   * @throws UtilException on error.
   */
  private void initialise(XmlElement eConfig, IEventManager eventMgr) throws UtilException {

    m_eventMgr = eventMgr;
    String sInitial = XmlValidate.getAttribute(eConfig, s_sAttrbInitial);
    String sMaximum = XmlValidate.getAttribute(eConfig, s_sAttrbMaximum);
    m_nInitial = Integer.parseInt(sInitial);
    m_nMaximum = Integer.parseInt(sMaximum);
    if(m_nInitial < 0 || m_nMaximum < 0 || m_nMaximum < m_nInitial) {
       throw new UtilException(s_sInvalidSizes);
    }

    XmlElement eFactory = XmlValidate.getElement(eConfig, s_sElmntFactory);
    m_factory = (IObjectFactory<T>) Loader.getTarget(eFactory );
    if(m_factory == null) throw new UtilException(s_sNoFactory);
    else {
      String sClass = XmlValidate.getAttribute(eFactory, ICConstants.ATTR_CLASS);
      log(String.format(s_sLoadedFactory, sClass), Severity.INFO);
    }
    
    m_lsEntry = new ArrayList<Entry<T>>(m_nMaximum);
    for(int i = 0; i < m_nInitial; i++) {
      m_lsEntry.add(new Entry<T>(m_factory.create(), false));
    }

    log(String.format(s_sCreated, m_nInitial), Severity.INFO);
    
  } // initialise()

  /**
   * This performs startup initialisation.
   * @param nInitial the initial pool size.
   * @param nMaximum the maximum pool size.
   * @param factory an object factory.
   * @param eventMgr an event manager implementation (or null).
   * @throws UtilException on error.
   */
  private void initialise(int nInitial, int nMaximum, IObjectFactory<T> factory,
                          IEventManager eventMgr) throws UtilException {

    m_nInitial = nInitial;
    m_nMaximum = nMaximum;
    m_factory  = factory;
    m_eventMgr = eventMgr;
    
    if(m_nInitial < 0 || m_nMaximum < 0 || m_nMaximum < m_nInitial) {
       throw new UtilException(s_sInvalidSizes);
    }

    m_lsEntry = new ArrayList<Entry<T>>(m_nMaximum);
    for(int i = 0; i < m_nInitial; i++) {
      m_lsEntry.add(new Entry<T>(m_factory.create(), false));
    }

    log(String.format(s_sCreated, m_nInitial), Severity.INFO);

  } // initialise()

  //** ------------------------------------------------------------------ Nested

  private final static class Entry<T> {
    private T m_tObj;
    private boolean m_bInUse;
    public  Entry(T tObj,boolean bInUse) {m_tObj = tObj;m_bInUse = bInUse;}
    public  T getObject() {return m_tObj;}
    public  void    setInUse(boolean bInUse) {m_bInUse = bInUse;}
    public  boolean isInUse() {return m_bInUse;}
  }

} // class ObjectPool
