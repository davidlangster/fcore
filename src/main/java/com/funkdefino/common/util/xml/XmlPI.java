package com.funkdefino.common.util.xml;

import org.jdom2.ProcessingInstruction;
import java.io.Serializable;
import java.util.Map;

/**
 * <p/>
 * <code>$Id: $</code>
 * Encapsulates a JDOM processing instruction (PI).
 * @author Differitas (David M. Lang)
 * @version $Revision: $
 */
public final class XmlPI extends ProcessingInstruction implements Serializable {

    static final long serialVersionUID = 2574082554673221091L;

    /**
     * Constructor.
     * @param target the target of the PI.
     * @param map data for the PI in name/value pairs
     */
    public XmlPI(String target, Map map) {
        super(target, map);
    }

} // class XmlPI
