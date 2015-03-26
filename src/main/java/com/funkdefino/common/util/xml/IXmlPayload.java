package com.funkdefino.common.util.xml;
import  java.io.Serializable;

/**
 * <p/>
 * <code>$Id: $</code>
 * @author Differitas (David M. Lang)
 * @version $Revision: $
 */
public interface IXmlPayload extends Cloneable, Serializable {
    public Object clone() throws CloneNotSupportedException;
}
