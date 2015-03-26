package com.funkdefino.common.util.event;

import com.funkdefino.common.util.reflect.Loader;
import com.funkdefino.common.util.xml.XmlElement;
import com.funkdefino.common.util.xml.XmlValidate;
import com.funkdefino.common.util.*;

import java.util.*;

/**
 * <p/>
 * <code>$Id: $</code>
 * An event target aggregating an event handler and filter.
 * @author Differitas (David M. Lang)
 * @version $Revision: $
 */
final class EventTarget {

  //** --------------------------------------------------- Static initialisation

  private static Map<String, Severity> s_mpSeverity = new HashMap<String, Severity>();
  static {
    s_mpSeverity.put(Severity.DEBUG.getText(),    Severity.DEBUG   );
    s_mpSeverity.put(Severity.INFO.getText(),     Severity.INFO    );
    s_mpSeverity.put(Severity.WARNING.getText(),  Severity.WARNING );
    s_mpSeverity.put(Severity.ERROR.getText(),    Severity.ERROR   );
    s_mpSeverity.put(Severity.CRITICAL.getText(), Severity.CRITICAL);
    s_mpSeverity.put(Severity.SECURITY.getText(), Severity.SECURITY);
  }

  //** --------------------------------------------------------------- Constants

  private final static String s_sHandlerFail     = "Unable to load handler";
  private final static String s_sFilterFail      = "Unable to load filter";
  private final static String s_sConverterFail   = "Unable to load converter";
  private final static String s_sUnknownSeverity = "Unknown severity '%s'";
  private final static String s_sElmntHandler    = "Handler";
  private final static String s_sElmntFilter     = "Filter";
  private final static String s_sElmntConverter  = "Converter";
  private final static String s_sAttrbSeverity   = "severity";

  //** -------------------------------------------------------------------- Data

  private String          m_sID        = "";
  private IEventHandler   m_handler    = null;
  private IEventFilter    m_filter     = null;
  private IEventConverter m_converter  = null;
  private Severity        m_severity   = null;
  private XmlElement      m_eHandler   = null;
  private XmlElement      m_eFilter    = null;
  private XmlElement      m_eConverter = null;

  //** ------------------------------------------------------------ Construction

  /**
   * Ctor.
   * @param eConfig a configuration element.
   * @throws UtilException thrown on error.
   */
  public EventTarget(XmlElement eConfig) throws UtilException {
    refresh(eConfig);
  }

  //** -------------------------------------------------------------- Operations

  public String          getID       ()  {return m_sID;      }
  public IEventHandler   getHandler  ()  {return m_handler;  }
  public IEventFilter    getFilter   ()  {return m_filter;   }
  public IEventConverter getConverter()  {return m_converter;}
  public Severity        getSeverity ()  {return m_severity; }

  /**
   * This performs a (re)load.
   * @param eConfig a configuration element.
   * @throws UtilException thrown on error.
   */
  public void refresh(XmlElement eConfig) throws UtilException
  {
    // Validate
    m_sID = XmlValidate.getAttribute(eConfig,ICConstants.ATTR_ID);
    String sSeverity = XmlValidate.getAttribute(eConfig,s_sAttrbSeverity);
    m_severity = getSeverity(sSeverity.toLowerCase());

    // (Re)load the handler
    XmlElement eHandler = XmlValidate.getElement(eConfig,s_sElmntHandler);
    if(m_eHandler == null || m_eHandler.hashCode() != eHandler.hashCode()) {
      if(m_handler != null ) m_handler.close();
      String sActive = XmlValidate.getAttribute(eHandler, ICConstants.ATTR_ACTIVE, ICConstants.TRUE);
      String sClass  = XmlValidate.getAttribute(eHandler, ICConstants.ATTR_CLASS);
      if(sActive.equals(ICConstants.TRUE)) {
         m_handler = (IEventHandler)Loader.getTarget(eHandler);
         if(m_handler == null) throw new UtilException(s_sHandlerFail);
         m_eHandler = eHandler;
      }
      else {
         m_eHandler = null;
         m_handler = null;
      }
    }

    // (Re)load the optional filter
    XmlElement eFilter = eConfig.getChild(s_sElmntFilter);
    if(m_handler != null && eFilter != null) {
       if(m_eFilter == null || m_eFilter.hashCode() != eFilter.hashCode()) {
          String sActive = XmlValidate.getAttribute(eFilter, ICConstants.ATTR_ACTIVE, ICConstants.TRUE);
          String sClass  = XmlValidate.getAttribute(eFilter, ICConstants.ATTR_CLASS);
          if(sActive.equals(ICConstants.TRUE)) {
             m_filter = (IEventFilter)Loader.getTarget(eFilter);
             if(m_filter == null) throw new UtilException(s_sFilterFail);
             m_eFilter = eFilter;
          }
          else {
             m_eFilter = null;
             m_filter = null;
          }
       }
    }

    // (Re)load the optional converter
    XmlElement eConverter = eConfig.getChild(s_sElmntConverter);
    if(m_handler != null && eConverter != null) {
       if(m_eConverter == null || m_eConverter.hashCode() != eConverter.hashCode()) {
          String sActive = XmlValidate.getAttribute(eConverter, ICConstants.ATTR_ACTIVE, ICConstants.TRUE);
          String sClass  = XmlValidate.getAttribute(eConverter, ICConstants.ATTR_CLASS);
          if(sActive.equals(ICConstants.TRUE)) {
             m_converter = (IEventConverter)Loader.getTarget(eConverter);
             if(m_converter == null) throw new UtilException(s_sConverterFail);
             m_eConverter = eConverter;
          }
          else {
             m_eConverter = null;
             m_converter = null;
          }
       }
    }

  } // refresh()

  /**
   * This releases all resources 
   */
  public void close(){
    if(m_handler != null) m_handler.close();
  }

  /**
   * A utility method that extracts an identifier from the incoming element.
   * @param eTarget the element.
   * @return the identifier.
   * @throws UtilException thrown on error.
   */
  public static String getID(XmlElement eTarget) throws UtilException {
    return XmlValidate.getAttribute(eTarget, ICConstants.ATTR_ID);
  }

  //** ---------------------------------------------------------- Implementation

  /**
   * This returns the enum equivalent of a severity string.
   * @param sSeverity the severity string.
   * @return the enum equivalent.
   * @throws UtilException thrown if severity is unknown.
   */
  private Severity getSeverity(String sSeverity) throws UtilException {
    Severity severity = s_mpSeverity.get(sSeverity);
    if(severity == null)
       throw new UtilException(String.format(s_sUnknownSeverity, sSeverity));
    return severity;

  } // getSeverity

} // class EventTarget
