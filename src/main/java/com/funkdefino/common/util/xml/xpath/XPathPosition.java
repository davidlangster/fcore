package com.funkdefino.common.util.xml.xpath;
import  com.funkdefino.common.util.xml.XmlException;
import  java.util.Iterator;

/**
 * <p/>
 * <code>$Id: $</code>
 * Iterates incrementally through an array of positional values, as directed by
 * an external evaluating interface. eg. for an array with two context postions,
 * iteration starts at [1][1], and proceeds through [1][2], [1][3] etc. to
 * [2][1], [2][2], [2][3] etc, as dictated by the evaluation outcome.
 * @author Differitas (David M. Lang)
 * @version $Revision: $
 */
final class XPathPosition<T> implements Iterator<T> {

    //** ------------------------------------------------------------- Constants

    public final static String NTH = "[n]";

    //** ------------------------------------------------------------------ Data

    private int[] values;          // Storage for 'm' context positional values
    private int   lsidx;           // The least-significant values[] index (m-1)
    private int   index;           // The current values[] index (lsidx => 0)
    private T     next;            // The iterable object
    private IEvaluation<T> eval;   // An external evaluation interface

    //** ---------------------------------------------------------- Construction

    /**
     * Constructor.
     * @param  expr an XPath meta-data expression
     * @param  eval an external evaluation interface.
     * @throws XmlException on error.
     */
    public XPathPosition(String expr, IEvaluation<T> eval) throws XmlException {

        // Calculate the number of segments in the incoming meta-data
        // expression, separated by the token '[n]'. Subtract 1 from
        // this to give the number of context positional values.
        int segments = expr.split("\\s*\\[n]\\s*").length;
        if(expr.endsWith(NTH)) ++segments;
        if(segments < 2) {
            throw new XmlException("Invalid metatadta");
        }

        // Allocate an array to hold all positional values
        values = new int[segments - 1];

        // Initialise indices
        lsidx = index = values.length - 1;

        // Initialise the array
        for(int i = 0; i < values.length; i++) {
            values[i] = 1;
        }

        // Initialise remaining variables
        this.eval = eval;

    }   // Position()

    //** --------------------------------------- Operations (Iterator interface)

    /**
     * Returns true if the iteration has more elements.
     * @return true or false.
     */
    public boolean hasNext() {
        boolean hasNext  = false;
        boolean complete = false;

        while(!complete) {
            if((next = eval.next(values, index, index != lsidx)) != null) {
                values[index = lsidx] += 1;
                hasNext  = true;
                complete = true;
            }
            else {
                for(int i = 0; i < values.length; i++) {
                    if(i >= index) {
                        values[i] = 1;
                    }
                }
                if(!(complete = --index < 0)) {
                    values[index] += 1;
                }
           }
        }

        return hasNext;

    }   // hasNext()

    /**
     * Returns the next element in the iteration.
     * @return the next element.
     */

    public T next() {
        return next;
    }

    /**
     * Removes the last element returned by the iterator (no-op).
     */
    public void remove()
    {
    }

    //** -------------------------------------------------------- Implementation

    /**
     * Dumps the postional values.
     * @param values an array of positional values.
     */
    private void dump(int[] values) {
        StringBuffer sb = new StringBuffer();
        for(int value : values)
            sb.append(String.format("[%d] ", value));
        System.out.println(sb.toString());
    }

    //** ------------------------------------------------------ Nested interface

    public static interface IEvaluation<T> {
        public T next(int[] values, int index, boolean incr);
    }

} // class XPathPosition
