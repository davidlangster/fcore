package com.funkdefino.common.util.xml;

/**
 * <p/>
 * <code>$Id: $</code>
 * Element validation.
 * @author Differitas (David M. Lang)
 * @version $Revision: $
 */
public final class XmlValidate {

    //** ------------------------------------------------------------- Constants

    private final static String MISSING_ELMNT = "The child element <%s> is missing from <%s>";
    private final static String BLANK_ELMNT   = "The child element <%s> on <%s> is blank";
    private final static String MISSING_ATTR  = "The attribute '%s' is missing from <%s>";

    //** ---------------------------------------------------------- Construction

    /**
     * Default constructor (private).
     */
    private XmlValidate() {
    }

    //** ------------------------------------------------------------ Operations

    /**
     * Validates the presence of a child element.
     * @param eParent the parent element.
     * @param sElmnt  the name of the required child element.
     * @return the child element (or null if parent is null).
     * @throws XmlException if the child element is missing.
     */
    public static XmlElement getElement(XmlElement eParent, String sElmnt)
                             throws XmlException
    {
        XmlElement element  = null;
        if(eParent != null) {
            if((element = eParent.getChild(sElmnt)) == null) {
                String sExcp = String.format(MISSING_ELMNT, sElmnt, eParent.getName());
                throw new XmlException(sExcp);
            }
        }

        return element;

    } // getElement()

    /**
     * Validates the presence of a child element.
     * @param eParent the parent element.
     * @param sElmnt  the name of the required child element.
     * @param eDefault a default
     * @return the child element (or null if parent is null, or element is missing).
     */
    public static XmlElement getElement(XmlElement eParent, String sElmnt, XmlElement eDefault)
    {
        XmlElement element;
        try  {element = getElement(eParent, sElmnt );}
        catch(XmlException excp) {element = eDefault;}
        return element;
    }

    /**
     * Validates that an element is neither missing, nor blank.
     * @param eParent the parent element.
     * @param sElmnt  the name of the required child element.
     * @return the element content (or "" if parent is null).
     * @throws XmlException if the child element is missing or blank.
     */
    public static String getContent(XmlElement eParent, String sElmnt)
                                    throws XmlException
    {
        String sContent = "";
        XmlElement element;

        if((element = getElement(eParent, sElmnt)) != null) {
            if((sContent = element.getContent()).equals("")) {
                String sExcp = String.format(BLANK_ELMNT, sElmnt, eParent.getName());
                throw new XmlException(sExcp);
            }
        }

        return sContent;

    } // getContent()

    /**
     * Validates that an element is neither missing, nor blank.
     * @param eParent  the parent element.
     * @param sElmnt   the name of the required child element.
     * @param sDefault the value to default to if validation fails.
     * @return the element content (or the default value).
     */
    public static String getContent(XmlElement eParent, String sElmnt, String sDefault)
    {
        String sContent;
        try  {sContent = getContent(eParent,sElmnt);}
        catch(XmlException excp) {sContent=sDefault;}
        return sContent;
    }

    /**
     * Validates the presence of an attribute.
     * @param eParent the parent element.
     * @param sAttr   the name of the required attribute.
     * @return the attribute (or "" if parent is null).
     * @throws XmlException if the attribute is missing.
     */
    public static String getAttribute(XmlElement eParent, String sAttr)
                         throws XmlException
    {
        String sVal = "";
        if(eParent != null) {
            if((sVal  = eParent.getAttribute(sAttr)).equals("")) {
                String sExcp = String.format(MISSING_ATTR, sAttr, eParent.getName());
                throw new XmlException(sExcp);
            }
        }

        return sVal;

    } // getAttribute()

    /**
     * Validates the presence of an attribute.
     * @param eParent the parent element.
     * @param sAttr   the name of the required attribute.
     * @param sDefault the value to default to if validation fails.
     * @return the attribute (or "" if parent is null).
     */
    public static String getAttribute(XmlElement eParent, String sAttr, String sDefault)
    {
        String sVal;
        try  {sVal = getAttribute(eParent, sAttr);}
        catch(XmlException excp) {sVal = sDefault;}
        return sVal;
    }

} // class XmlValidate
