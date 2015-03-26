package com.funkdefino.common.util.xml;

import org.jdom2.Attribute;

/**
 * <p/>
 * <code>$Id: $</code>
 * Encapsulates a JDOM attribute.
 * @author Differitas (David M. Lang)
 * @version $Revision: $
 */
public final class XmlAttribute implements IXmlPayload {

    //** ------------------------------------------------------------- Constants

    public  final static long serialVersionUID = 3970861635642339279L;

    //** ------------------------------------------------------------------ Data

    private Attribute attribute = null;            // The underlying JDOM object

    //** ---------------------------------------------------------- Construction

    /**
     * Constructs an empty attribute.
     * @param name the attribute name.
     */
    public XmlAttribute(String name) {
        this(name, "");
    }

    /**
     * Constructor.
     * @param name  the attribute name.
     * @param value the attribute value.
     */
    public XmlAttribute(String name, String value) {
        attribute = new Attribute(name, value);
    }

    /**
     * Copy constructor.
     * @param rhs the object to copy (performs a deep clone).
     */
    public XmlAttribute(XmlAttribute rhs) {
        attribute = (Attribute)rhs.attribute.clone();
    }

    /**
     * Copy constructor.
     * @param rhs the JDOM object to copy (performs a shallow clone).
     */
    public XmlAttribute(Attribute rhs) {
        attribute = rhs;
    }

    //** ------------------------------------------------ Operations (overrides)

    /**
     * Performs a deep clone.
     * @return the cloned object.
     */
    @Override
    public Object clone() {
        XmlAttribute clone;
        try {
            clone = (XmlAttribute)super.clone();
            clone.attribute = (Attribute)attribute.clone();
        }
        catch(CloneNotSupportedException excp) {
            return new InternalError(excp.getMessage());
        }

        return clone;

    }   // clone()

    /**
     * Tests for object equality.
     * @param   rhs the reference object with which to compare.
     * @return  true if this object is the same as the incoming
     *          object; otherwise false.
     */
    @Override
    public boolean equals(Object rhs)
    {
        boolean ret;
        if(!(ret  = (this == rhs))) {
            if((ret = (rhs != null && getClass() == rhs.getClass()))) {
                ret  = this.attribute.equals(((XmlAttribute)rhs).attribute);
            }
        }

        return ret;

    } // equals()

    /**
     * Returns a hash code value for the object.
     * @return the hash code.
     */
    @Override
    public int hashCode() {
        return attribute.hashCode();
    }

    /**
     * Returns a string representation of the object.
     * @return the string.
     */
    @Override
    public String toString() {
        return attribute.toString();
    }

    //** ------------------------------------------------------------ Operations

    /**
     * Returns the attribute name.
     * @return the name.
     */
    public String getName() {
        return attribute.getName();
    }

    /**
     * Returns the attribute value.
     * @return the value.
     */
    public String getValue() {
        return attribute.getValue();
    }

    /**
     * Sets the attribute name.
     * @param name the name.
     */
    public void setName(String name) {
        attribute.setName(name);
    }

    /**
     * Sets the attribute value.
     * @param value the value.
     */
    public void setValue(String value) {
        attribute.setValue(value);
    }

    /**
     * Returns the attribute's parent.
     * @return the parent element.
     */
    public XmlElement getParent() {
        return new XmlElement(attribute.getParent());
    }

} // class XmlAttribute
