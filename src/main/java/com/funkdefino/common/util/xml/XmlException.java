package com.funkdefino.common.util.xml;
import  com.funkdefino.common.util.UtilException;

/**
 * <p/>
 * <code>$Id: $</code>
 * An exception class for catching and forwarding JDOM exceptions.
 * @author Differitas (David M. Lang)
 * @version $Revision: $
 */
public class XmlException extends UtilException
{
    /**
     * Constructor.
     * @param message an exception message
     */
    public XmlException(String message) {
        super(message);
    }

} // class XmlException
