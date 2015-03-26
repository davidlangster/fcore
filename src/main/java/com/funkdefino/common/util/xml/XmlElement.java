package com.funkdefino.common.util.xml;

import org.jdom2.output.XMLOutputter;
import org.jdom2.output.Format;
import org.jdom2.*;

import java.util.*;
import java.io.*;

/**
 * <p/>
 * <code>$Id: $</code>
 * Encapsulates a JDOM element.
 * @author Differitas (David M. Lang)
 * @version $Revision: $
 */
public final class XmlElement implements IXmlPayload {

    //** ------------------------------------------------- Static initialisation

    public final static long serialVersionUID = 8764218676147432279L;

    static {
        // Configure the underlying Xerces2 parser to cache grammars
        System.setProperty("org.apache.xerces.xni.parser.XMLParserConfiguration",
                           "org.apache.xerces.parsers.XMLGrammarCachingConfiguration");
    }

    //** ------------------------------------------------------------------ Data

    private Element element  = null;               // The underlying JDOM object
    private String  encoding = IXmlConstants.ENCODING;        // Output encoding

    //** ---------------------------------------------------------- Construction

    /**
     * Constructs an empty element.
     * @param name the element name.
     */
    public XmlElement(String name) {
        this(name, "");
    }

    /**
     * Constructor.
     * @param name    the element name.
     * @param content the content.
     */
    public XmlElement(String name, String content) {
        element = new Element(name);
        element.setText(content);
    }

    /**
     * Constructor.
     * @param name      the element name.
     * @param content   the content.
     * @param defaultNS a default namespace
     */
    public XmlElement(String name, String content, String defaultNS) {
        this(name, content);
        element.setNamespace(Namespace.getNamespace("", defaultNS));
    }


    /**
     * Copy constructor.
     * @param rhs the object to copy (performs a deep clone).
     */
    public XmlElement(XmlElement rhs) {
        element  = (Element)rhs.element.clone();
        encoding = rhs.encoding;
    }

    /**
     * Copy constructor.
     * @param rhs the JDOM object to copy (performs a shallow clone).
     */
    public XmlElement(Element rhs) {
        element = rhs;
    }

    //** ------------------------------------------------ Operations (overrides)

    /**
     * Performs a deep clone.
     * @return the cloned object.
     */
    @Override
    public Object clone() {
        XmlElement clone;
        try {
            clone = (XmlElement)super.clone();
            clone.element  = (Element)element.clone();
            clone.encoding = new String(encoding);
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
                ret  = this.element.equals(((XmlElement)rhs).element);
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
        return element.hashCode();
    }

    /**
     * Returns a string representation of the object.
     * @return the string.
     */
    @Override
    public String toString() {
        return element.toString();
    }

    //** ---------------------------------------- Operations (element modifiers)

    /**
     * Sets the element name.
     * @param name the element name.
     * @return self-reference.
     */
    public XmlElement setName(String name) {
        element.setName(name);
        return this;
    }

    /**
     * Sets the element content.
     * @param content the element content.
     * @return self-reference.
     */
    public XmlElement setContent(String content) {
        element.setText(content);
        return this;
    }

    /**
     * Adds a new child element.
     * @param name    the element name.
     * @param content the content.
     * @return self-reference.
     */
    public XmlElement addChild(String name, String content) {
        element.addContent(new Element(name, content));
        return this;
    }

    /**
     * Adds a child element. If the incoming element is either a root element,
     * or has a parent (i.e. it's already part of an XML tree), JDOM throws an
     * IllegalAddException. However, such  exceptions are caught, and the element
     * is cloned before trying again.
     * @param child the child element to add.
     * @return self-reference.
     */
    public XmlElement addChild(XmlElement child)
    {
        try   {element.addContent(child.toElement());}
        catch (IllegalAddException excp) {
            element.addContent((Element)child.toElement().clone());
        }

        return this;

    } // addChild()

    /**
     * Adds a (detached) child element at the given index.
     * @param index the index.
     * @param child the child element to add.
     * @return self-reference.
     * @throws XmlException if the index is out of range.
     */
    public XmlElement addChild(int index, XmlElement child) throws XmlException {
        List ls = element.getChildren();
        try  {ls.add(--index, child.element.detach());}
        catch(IndexOutOfBoundsException excp) {
            throw new XmlException(excp.getMessage());
        }

        return this;

    }   // addChild()

    /**
     * Removes a child element.
     * @param name the element name.
     * @return true if successful; otherwise false.
     */
    public boolean removeChild(String name) {
        return element.removeChild(name, element.getNamespace());
    }

    /**
     * Removes a child element.
     * @param child the element.
     * @return true if successful; otherwise false.
     */
    public boolean removeChild(XmlElement child) {
        return element.removeContent(child.toElement());
    }

    /**
     * Removes a child element with the specified name. If multiple elements
     * exist with that name, all matching children are removed.
     * @param name the element name.
     * @return true if successful; otherwise false.
     */
    public boolean removeChildren(String name) {
        return element.removeChildren(name);
    }

    /**
     * Sets the element's namespace
     * @param prefix the namespace prefix.
     * @param uri the namespace URI.
     */
    public void setNamespace(String prefix, String uri ){
        element.setNamespace(Namespace.getNamespace(prefix, uri));
    }

    //** -------------------------------------- Operations (attribute modifiers)

    /**
     * Adds an attribute.
     * @param name  the attribute name.
     * @param value the attribute value.
     * @return self-reference.
     */
    public XmlElement addAttribute(String name, String value) {
        element.setAttribute(name, value);
        return this;
    }

    /**
     * Adds an attribute.
     * @param attrb the attribute.
     * @return self reference.
     */
    public XmlElement addAttribute(XmlAttribute attrb) {
        return this.addAttribute(attrb.getName(), attrb.getValue());
    }

    /**
     * Removes an attribute.
     * @param name the attribute name.
     */
    public void removeAttribute(String name) {
        element.removeAttribute(name);
    }

    /**
     * Adds a comment.
     * @param comment the comment.
     * @return self-reference.
     */
    public XmlElement addComment(String comment) {
        element.addContent(new Comment(comment));
        return this;
    }

    /**
     * Adds CDATA text.
     * @param text the text.
     * @return self-reference.
     */
    public XmlElement addCDATA(String text){
        element.addContent(new CDATA(text));
        return this;
    }

    //** ---------------------------------------- Operations (element accessors)

    /**
     * Returns the element name.
     * @return the name.
     */
    public String getName() {
        return element.getName();
    }

    /**
     * Returns the element's content.
     * @return the content.
     */
    public String getContent(){
        return element.getText();
    }

    /**
     * Returns the element's parent.
     * @return the parent (or null).
     */
    public XmlElement getParent() {
        Object parent = element.getParent();
        return parent != null && parent instanceof Element
               ? new XmlElement((Element)parent) : null;
    }

    /**
     * Determines if the element has any children.
     * @return true if the element has children; otherwise false.
     */
    public boolean hasChildren() {
        return element.getChildren().size() > 0;
    }

    /**
     * Returns a child element.
     * @param name the child element name.
     * @return the child element (or null if not found).
    */
    public XmlElement getChild (String name){
        Element child = element.getChild(name, element.getNamespace());
        return child != null ? new XmlElement(child) : null;
    }

    /**
     * Returns a child element's content.
     * @param name the child element name.
     * @return the content (or "" if not found).
     */
    public String getChildContent(String name) {
        XmlElement child = getChild(name);
        return child != null ? child.getContent() : "";
    }

    /**
     * Returns a typesafe list of child element(s).
     * @return the list.
     */
    public List<XmlElement> getChildren()
    {
        List list = element.getChildren();
        ArrayList<XmlElement> children = new ArrayList<XmlElement>(list.size());
        for(Object child : list) children.add(new XmlElement((Element)child));
        return children;
    }

    /**
     * Returns the element's namespace prefix
     * @return the prefix.
     */
    public String getNamespacePrefix() {
        return element.getNamespacePrefix();
    }

    /**
     * Returns the element's namespace URI
     * @return the prefix URI.
     */
    public String getNamespaceURI() {
        return element.getNamespaceURI();
    }

    //** -------------------------------------- Operations (attribute accessors)

    /**
     * Determines if an attribute exists.
     * @param name the attribute name.
     * @return true if it exists; otherwise false.
     */
    public boolean isAttribute(String name) {
        return element.getAttribute(name) != null;
    }

    /**
     * Returns an attribute value.
     * @param name the attribute name.
     * @return the value.
     */
    public String getAttribute(String name) {
        Attribute attribute = element.getAttribute(name);
        return attribute != null ? attribute.getValue() : "";
    }

    /**
     * Retuns a typesafe list of attributes.
     * @return the list
     */
    public List<XmlAttribute> getAttributes()
    {
        List list = element.getAttributes();
        List<XmlAttribute> attributes = new ArrayList<XmlAttribute>(list.size());
        for(Object obj : list) {
            Attribute attrb = (Attribute)obj;
            attributes.add(new XmlAttribute(attrb.getName(), attrb.getValue()));
        }

        return attributes;

    } // getAttributes()

    //** ----------------------------------- Operations (namespace declarations)

    /**
     * Adds a namespace declaration to the element. i.e. not the namespace
     * of the element itself.
     * @param prefix the namespace prefix.
     * @param uri the namespace URI.
     */
    public void addAdditionalNamespace(String prefix, String uri ){
        element.addNamespaceDeclaration(Namespace.getNamespace(prefix, uri));
    }

    /**
     * Returns all namespaces associated with this element, both the namespace
     * of the element itself, plus all additional namespace declarations.
     * @return a map of namespace URIs keyed on prefix.
     */
    public Map<String, String> getNamespaces() {
        Map<String, String> map = new HashMap<String, String>();
        map.put(element.getNamespacePrefix(), element.getNamespaceURI());
        for(Object o : element.getAdditionalNamespaces()) {
            Namespace ns = (Namespace)o;
            map.put(ns.getPrefix(), ns.getURI());
        }

        return map;

    }   // getNamespaces()

    //** --------------------------------------------------- Operations (output)

    /**
     * Sets the output encoding.
     * @param encoding the encoding.
     */
    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    /**
     * Outputs the element to the given stream.
     * @param ostream the stream
     * @throws IOException on error.
     */
    public void output(OutputStream ostream) throws IOException {
        getOutputter().output(element, ostream);
    }

    /**
     * Returns a textual representation of the element.
     * @return the representation
     */
    public String output() {
        return element != null ? getOutputter().outputString(element) : "";
    }

    /**
     * Returns the length of the encoded string
     * @return the length
     * @throws UnsupportedEncodingException on error.
     */
    public int getEncodedLength() throws UnsupportedEncodingException {
        return (element != null ? getOutputter().outputString(element).getBytes(encoding).length : 0);
    }
    
    /**
     * Dumps the element to STDOUT.
     */
    public void dump()
    {
        try {
            System.out.println("" );
            this.output(System.out);
            System.out.println("" );
        }
        catch(IOException excp) {
            excp.printStackTrace();
        }
    }

    //** ------------------------------------------------------------ Operations

    /**
     * Returns the underlying JDOM element.
     * @return the element.
     */
    public Element toElement() {
        return element;
    }

    //** -------------------------------------------------------- Implementation

    /**
     * Returns a JDOM XMLOutputter.
     * @return the outputter.
     */
    private XMLOutputter getOutputter()  {
        Format fmt = Format.getPrettyFormat();
        fmt.setEncoding(encoding);
        return new XMLOutputter(fmt);
    }

} // class XmlElement
