package com.funkdefino.common.pool;

import com.funkdefino.common.util.event.IEventManager;
import com.funkdefino.common.util.reflect.Loader;
import com.funkdefino.common.util.xml.XmlValidate;
import com.funkdefino.common.util.xml.XmlElement;
import com.funkdefino.common.util.ICConstants;

/**
 * <p/>
 * <code>$Id: $</code>
 * @author Differitas (David M. Lang)
 * @version $Revision: $
 */
public final class ObjectPoolFactory {

  //** ------------------------------------------------------------ Construction

  /**
   * Ctor (private)
   */
  private ObjectPoolFactory()
  {
  }

  //** -------------------------------------------------------------- Operations

  /**
   * This creates an object pool.
   * @param eConfig a configuration element.
   * @param eventMgr an event manager implementation (or null).
   * @return the pool.
   * @throws Exception on error.
   */
  public static <T> IObjectPool<T> create(XmlElement eConfig, IEventManager eventMgr) throws Exception {
    Loader.ArgumentList argList = new Loader.ArgumentList();
    argList.add(XmlElement.class, eConfig);
    argList.add(IEventManager.class, eventMgr);
    String sClz = XmlValidate.getAttribute(eConfig, ICConstants.ATTR_CLASS);
    return (IObjectPool<T>)Loader.getTarget(sClz, argList);
  }

} // class ObjectPoolFactory 
