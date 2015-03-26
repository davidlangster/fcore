package com.funkdefino.common.util.xml;

import org.jdom2.*;

/**
 * <p/>
 * <code>$Id: $</code>
 * @author Differitas (David M. Lang)
 * @version $Revision: $
 */
final class XmlFactory extends DefaultJDOMFactory {

    //** ------------------------------------------------------------------ Data

    private Namespace namespace = null;

    //** ---------------------------------------------------------- Construction

    /**
     * Constructor.
     * @param defaultNS the default namespace.
     */
    public XmlFactory(String defaultNS) {
        if(defaultNS != null) {
            namespace = Namespace.getNamespace("", defaultNS);
        }
    }

    //** ------------------------------------------------------------ Operations

    /**
     * Creates an element in the default namespace.
     * @param  name the element name.
     * @return the element.
     */
//    @Override
//    public Element element(String name) {
//        return namespace != null ? new Element(name, namespace) : new Element(name);
//    }

} // class XmlFactory
