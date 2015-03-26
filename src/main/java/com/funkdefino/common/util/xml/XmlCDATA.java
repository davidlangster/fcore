package com.funkdefino.common.util.xml;

import java.io.Serializable;
import org.jdom2.CDATA;

/**
 * <p/>
 * <code>$Id: $</code>
 * Encapsulates JDOM character data.
 * @author Differitas (David M. Lang)
 * @version $Revision: $
 */
public final class XmlCDATA implements Serializable, Cloneable {

    //** ------------------------------------------------------------- Constants

    public final static long serialVersionUID = 3970861635642339279L;

    //** ------------------------------------------------------------------ Data

    private CDATA cdata = null;                    // The underlying JDOM object

    //** ---------------------------------------------------------- Construction

    /**
     * Constructs an empty CDATA element.
     */
    public XmlCDATA() {
        this("");
    }

    /**
     * Constructor.
     * @param text character data.
     */
    public XmlCDATA(String text){
         cdata = new CDATA(text);
    }

    /**
     * Copy constructor.
     * @param rhs the object to copy (performs a deep clone).
     */
    public XmlCDATA(XmlCDATA rhs) {
        cdata = (CDATA)rhs.cdata.clone();
    }

    /**
     * Copy constructor.
     * @param rhs the JDOM object to copy (performs a shallow clone).
     */
    private XmlCDATA(CDATA rhs) {
        cdata = rhs;
    }

    //** ------------------------------------------------ Operations (overrides)

    /**
     * Performs a deep clone.
     * @return the cloned object.
     * @throws CloneNotSupportedException on error.
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        XmlCDATA clone = (XmlCDATA)super.clone();
        clone.cdata  = (CDATA)cdata.clone();
        return clone;
    }

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
                ret  = this.cdata.equals(((XmlCDATA)rhs).cdata);
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
        return cdata.hashCode();
    }

    /**
     * Returns a string representation of the object.
     * @return the string.
     */
    @Override
    public  String toString(){
        return cdata.toString();
    }

    //** ------------------------------------------------------------ Operations

    /**
     * Returns the character data.
     * @return the data.
     */
    public  String getText() {
        return cdata.getText();
    }

    /**
     * Sets the character data.
     * @param text the character data.
     */
    public void setText(String text) {
        cdata.setText(text);
    }

} // class XmlCDATA
