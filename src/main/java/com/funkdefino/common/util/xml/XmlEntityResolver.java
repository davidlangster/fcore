package com.funkdefino.common.util.xml;

import com.funkdefino.common.util.event.IEventManager;
import com.funkdefino.common.util.event.Severity;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * <p/>
 * <code>$Id: $</code>
 * @author Differitas (David M. Lang)
 * @version $Revision: $
 */
public class XmlEntityResolver<T> implements EntityResolver {

  //** --------------------------------------------------------------- Constants

  protected final static String s_sNoResource     = "Unable to load resource '%s'";
  protected final static String s_sResolvedEntity = "Resolved entity '%s'";

  //** -------------------------------------------------------------------- Data

  private Map<String,T> m_mpResource = new HashMap<String,T>();
  private IEventManager m_eventMgr;

  //** ------------------------------------------------------------ Construction

  /**
   * Ctor
   * @param sSystemId the resource system identifier.
   * @param resource the resource.
   * @param eventMgr an event manager implementation (or null).
   */
  public XmlEntityResolver(String sSystemId, T resource, IEventManager eventMgr) {
    this(eventMgr); addResource(sSystemId, resource);
  }

  /**
   * Constructor (protected).
   * @param eventMgr an event manager implementation (or null).
   */
  protected XmlEntityResolver(IEventManager eventMgr) {
    m_eventMgr = eventMgr;
  }

  //** -------------------------------------------------------------- Operations

  /**
   * This adds a resource to the map.
   * @param sSystemID the resource system identifier.
   * @param resource the resource.
   */
  protected final void addResource(String sSystemID, T resource) {
    m_mpResource.put(sSystemID, resource);
  }

  /**
   * Default resource loading using class loader.
   * @param resource the resource.
   * @return an input stream.
   */
  protected InputStream loadResource(T resource) {

    InputStream is;
    ClassLoader classLoader = getClass().getClassLoader();
    if((is = classLoader.getResourceAsStream(resource.toString())) == null){
      log(String.format(s_sNoResource,resource),Severity.ERROR);
    }

    return is;

  } // loadResource()
  
  //** ----------------------------------- Operations (EntityResolver interface)

  /**
   * This resolves an entity from local resources, if available.
   * @param sPublicID the resource public identifier.
   * @param sSystemID the resource system identifier.
   * @return the resource (or null).
   */
  public final InputSource resolveEntity(String sPublicID, String sSystemID) {

    T resource;
    InputSource isrc = null;
    if((resource = m_mpResource.get(sSystemID)) != null) {
      InputStream is = loadResource(resource);
      if(is != null) {
         log(String.format(s_sResolvedEntity, sSystemID), Severity.INFO);
         isrc = new InputSource (is);
         isrc.setSystemId(sSystemID);
      }
    }

    return isrc;

  } // resolveEntity()

  //** ---------------------------------------------------- Operations (utility)

  /**
   * This logs a string.
   * @param sLog the string.
   * @param severity a severity.
   */
  protected final void log(String sLog, Severity severity) {
    if(m_eventMgr != null) m_eventMgr.post(this, sLog, severity);
    else {
      System.out.println(sLog);
    }
  }

} // class XmlEntityResolver
