package com.funkdefino.common.util.xml.xpath;
import  com.funkdefino.common.util.xml.XmlDocument;
import  com.funkdefino.common.util.xml.XmlException;
import  java.util.*;

/**
 * <p/>
 * <code>$Id: $</code>
 * Iterates incrementally through an XPath meta-data expression, applying this
 * to the target document. e.g book[1]/chapter[1]/@name, book[1]/chapter[2]/@name
 * etc. An external callback is invoked for each successful match.
 * @author  Differitas (David M. Lang)
 * @version $Revision: $
 */
public final class XPathIterator implements Iterator<XPathIterator.Entry>,
                                            XPathPosition.IEvaluation<XPathIterator.Entry> {

    //** ------------------------------------------------------------------ Data

    private final String               expr;
    private final XmlDocument          doc;
    private final ICallback            callback;
    private final XPathPosition<Entry> position;

    //** ---------------------------------------------------------- Construction

    /**
     * Constructor.
     * @param  expr an XPath meta-data expression.
     * @param  doc the target document.
     * @param  callback an external callback invoked on each iteration.
     * @throws XmlException on error.
     */
    public XPathIterator(String expr, XmlDocument doc, ICallback callback)
           throws XmlException {
        position = new XPathPosition<Entry>((this.expr=expr), this);
        this.callback = callback;
        this.doc = doc;
    }

    //** --------------------------------------- Operations (Iterator interface)

    /**
     * Returns true if the iteration has more elements.
     * @return true or false.
     */
    public boolean hasNext() {
        return position.hasNext();
    }

    /**
     * Returns the next element in the iteration.
     * @return the next element.
     */
    public Entry next() {
        return position.next();
    }

    /**
     * Removes from the underlying collection the last element returned by the
     * iterator (no-op).
     */
    public void remove()
    {
    }

    //** ---------------------- Operations (XPathPosition.IEvaluation interface)

    /**
     * Invoked by XPathPosition to retrieve the next iteration element.
     * @param  values an array of positional values.
     * @param  index the current array index.
     * @param  incr true if the index value has been incremented.
     * @return the next element.
     */
    public Entry next(int[] values, int index, boolean incr) {

        String xpath = addCtxToXPath(expr, values);
        List nodes = null ;; //doc.selectNodes(xpath);
        nodes = nodes != null && nodes.size() == 1 ? nodes : null;
        if(nodes != null && callback != null) {
            callback.next(expr,index,incr);
        }

        return nodes != null ? new Entry(nodes.get(0), xpath) : null;

    }   // next()

    //** -------------------------------------------------------- Implementation

    /**
     * This adds a context position to the incoming XPath meta-data expression.
     * e.g. a node defined as /book/chapter[n] with a context position of 1 will
     * be formatted as /book/chapter[1].  i.e. the first 'text' child of
     * the 'book' parent.
     * @param  expr the meta-data expression.
     * @param  pos the position.
     * @return the formatted XPath expression.
     */
    private String addCtxToXPath(String expr, int[] pos)
    {
        for(int val : pos)  {
            int nIndex = expr.indexOf(XPathPosition.NTH);
            String s1  = expr.substring(0, nIndex+1);
            String s2  = expr.substring(nIndex+2, expr.length());
            expr = s1  + Integer.toString(val) + s2;
        }

        return expr;

    } // addCtxToXPath()

    //** ---------------------------------------------------------- Nested class

    public final static class Entry {
        private final Object node;
        private final String expr;
        public Entry(Object node, String expr) {
               this.node = node;
               this.expr = expr;
        }
        public Object getNode() {return node;}
        public String getExpr() {return expr;}

    }   // class Entry

    //** ------------------------------------------------------ Nested interface

    public static interface ICallback{
       public void next(String expr, int index, boolean incr);
    }

} // class XPathIterator
