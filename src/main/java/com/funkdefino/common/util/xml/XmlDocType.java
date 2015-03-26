package com.funkdefino.common.util.xml;

import java.io.Serializable;
import org.jdom2.DocType;

/**
 * <p/>
 * <code>$Id: $</code>
 * Encapsulates a JDOM document type (for DTD validation).
 * @author Differitas (David M. Lang)
 * @version $Revision: $
 */
public final class XmlDocType extends DocType implements Serializable {

    //** ------------------------------------------------------------- Constants

    static final long serialVersionUID = -3695967198072100395L;

    //** ---------------------------------------------------------- Construction

    /**
     * Constructor.
     * @param element name of the element being constrained.
     * @param publicID public ID of the referenced DTD.
     * @param systemID system ID of the referenced DTD.
     */
    public XmlDocType(String element, String publicID, String systemID) {
        super(element, publicID, systemID);
    }

} // class XmlDocType
