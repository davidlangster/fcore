package com.funkdefino.common.util.xml;

import com.funkdefino.common.message.ISerializable;
import org.jaxen.dom.XPath;
import org.jdom2.input.SAXBuilder;
import org.jdom2.input.DOMBuilder;
import org.jdom2.input.sax.XMLReaderJDOMFactory;
import org.jdom2.input.sax.XMLReaderSAX2Factory;
import org.jdom2.output.XMLOutputter;
import org.jdom2.output.Format;
import org.jdom2.*;

import org.jdom2.xpath.jaxen.JDOMXPath;
import org.xml.sax.EntityResolver;
import org.jaxen.*;

import java.net.URL;
import java.util.*;
import java.io.*;


/**
 * <p/>
 * <code>$Id: $</code>
 * Encapsulates a JDOM document.
 * @author Differitas (David M. Lang)
 * @version $Revision: $
 */
public final class XmlDocument implements ISerializable, Cloneable {

    //** ------------------------------------------------- Static initialisation

    public  final static long   serialVersionUID = -8644688210575346817L;
    private final static String DEFAULT_PREFIX   = "_funkdefino_";
    private final static String s_sNoResource    = "Unable to load resource file '%s'";

    static {
        // Configure the underlying Xerces2 parser to cache grammars
        System.setProperty("org.apache.xerces.xni.parser.XMLParserConfiguration",
                           "org.apache.xerces.parsers.XMLGrammarCachingConfiguration");
    }

    //** ------------------------------------------------------------------ Data

    private Document document = null;              // The underlying JDOM object
    private String   encoding = IXmlConstants.ENCODING;       // Output encoding

    //** ---------------------------------------------------------- Construction

    /**
     * Constructs a document with a single root element.
     * @param root the name of the root element.
     */
    public XmlDocument(String root) {
        document = new Document(new Element(root));
    }

    /**
     * Constructs a document with the specified element as root. If the element
     * is already part of an XML tree, JDOM throws an IllegalAddException.
     * However, such exceptions are caught, and the element is cloned
     * before trying again.
     * @param element the element to add as root.
     */
    public XmlDocument(XmlElement element) {
        this(element, null);
    }

    /**
     * Constructs a document with the specified element as root. If the element
     * is already part of an XML tree, JDOM throws an IllegalAddException.
     * However, such exceptions are caught, and the element is cloned
     * before trying again.
     * @param element the element to add as root.
     * @param docType for DTD validation.
     */
    public XmlDocument(XmlElement element, XmlDocType docType)
    {
        Element root = element.toElement();
        try  {document = new Document(root);}
        catch(IllegalAddException excp)  {
            document = new Document((Element)root.clone());
        }

        if(docType != null) {
            document.setDocType(docType);
        }

    } // XmlDocument()

    /**
     * Constructs a document using a source input stream.
     * @param istream the stream.
     * @param bValidate true to perform DTD validation.
     * @throws XmlException on error.
     */
    public XmlDocument(InputStream istream, boolean bValidate) throws XmlException {
        this(istream, null, bValidate);
    }

    /**
     * Constructs a document using a source input stream.
     * @param  istream the stream.
     * @param  defaultNS a default namespace.
     * @param  bValidate true to perform DTD validation.
     * @throws XmlException on error.
     */
    public XmlDocument(InputStream istream, String defaultNS,
                       boolean bValidate) throws XmlException
    {
        try  {document = buildDocument(istream, defaultNS, bValidate);}
        catch(Exception excp) {
            throw new XmlException(excp.getMessage());
        }
    }

    /**
     * Constructs a document using a source reader.
     * @param reader the reader.
     * @param bValidate true to perform DTD validation.
     * @throws XmlException on error.
     */
    public XmlDocument(Reader reader, boolean bValidate) throws XmlException
    {
        try  {document = buildDocument(reader, bValidate);}
        catch(Exception excp) {
            throw new XmlException(excp.getMessage());
        }
    }

    /**
     * Constructs a document from file.
     * @param file a file descriptor.
     * @param bValidate true to perform DTD validation.
     * @throws XmlException on error.
     */
    public XmlDocument(File file, boolean bValidate) throws XmlException
    {
        try  {document = buildDocument(new FileInputStream(file), null, bValidate);}
        catch(Exception excp) {
            throw new XmlException(excp.getMessage());
        }
    }

    /**
     * Constructs a document from the supplied URL.
     * @param url the URL.
     * @param bValidate true to perform DTD validation.
     * @throws XmlException on error.
     */
    public XmlDocument(URL url, boolean bValidate) throws XmlException {
        try  {document = buildDocument(url, bValidate);}
        catch(Exception excp) {
            throw new XmlException(excp.getMessage());
        }
    }

    /**
     * Copy constructor.
     * @param rhs the object to copy (performs a deep clone).
     */
    public XmlDocument(XmlDocument rhs) {
        document = (Document)rhs.document.clone();
        encoding = new String(rhs.encoding);
    }

    /**
     * Copy constructor.
     * @param rhs the W3C document to copy.
     */
    public XmlDocument(org.w3c.dom.Document rhs) {
        DOMBuilder builder = new DOMBuilder();
        builder.build(rhs);
    }

    /**
     * Copy constructor (private).
     * @param rhs the JDOM object to copy (performs a shallow clone).
     */
    private XmlDocument(Document rhs) {
        this.document = rhs;
    }

    //** ------------------------------------------------ Operations (overrides)

    /**
     * Performs a deep clone.
     * @return the cloned object.
     * @throws CloneNotSupportedException on error.
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        XmlDocument clone = (XmlDocument)super.clone();
        clone.document  = (Document)document.clone();
        clone.encoding = new String(encoding);
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
                ret  = this.document.equals(((XmlDocument)rhs).document);
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
        return document.hashCode();
    }

    /**
     * Returns a string representation of the object.
     * @return the string.
     */
    @Override
    public String toString() {
        return document.toString();
    }

    //** ---------------------------------------------------- Operations (XPath)

//    /**
//     * Returns a list of nodes selected by the incoming XPath expression.
//     * @param sXPathExpr the XPath expression.
//     * @return the list.
//     */
//    public List selectNodes(String sXPathExpr) {
//        return selectNodes(sXPathExpr, null);
//    }

    /**
     * Returns a list of nodes selected by the incoming XPath expression.
     * @param  sXPathExpr the XPath expression.
     * @param  mpNS namespace bindings (or null).
     * @return the list.
     */
//    public List selectNodes(String sXPathExpr, Map<String,String> mpNS)
//    {
//
//        List list;
//        try {
//            XPath xpath = createXPath(sXPathExpr,mpNS);
//            Iterator ii = xpath.selectNodes(document).iterator();
//            list = new ArrayList();
//            while(ii.hasNext())  {
//                Object o = ii.next();
//                if(o instanceof Element) list.add(new XmlElement((Element)o));
//                else if(o instanceof Attribute) list.add(new XmlAttribute((Attribute)o));
//            }
//        }
//        catch(JaxenException excp) {
//            list = null;
//        }
//
//        return list;
//
//    } // selectNodes()

    //** ------------------------------------------------------------ Operations

    /**
     * Returns the root element.
     * @return the element.
     */
    public XmlElement getRootElement() {
        Element element = document.getRootElement();
        return element != null ? new XmlElement(element) : null;
    }

//    /**
//     * Adds a processing instruction.
//     * @param pi the instruction.
//     */
//    public void addProcessingInstruction(XmlPI pi) {
//        document.addContent(pi);
//    }

    //** ----------------------------------------------- Operations (validation)

    /**
     * Checks that the incoming document is conformant to the associated DTD.
     * @param  source the document.
     * @throws XmlException if invalid.
     */
    public static void validate(XmlDocument source) throws XmlException
    {
        if(source.document.getDocType() != null) {
            String output = source.getOutputter().outputString(source.document);
            try  {buildDocument(new StringReader(output), true);}
            catch(Exception excp) {
                throw new XmlException(excp.getMessage());
            }
        }
    }

    /**
     * Checks that the incoming document is conformant to the supplied schema.
     * @param  source the document.
     * @param  schema the schema URI.
     * @param  bTargetNS set 'true' if the document instance has a target namespace.
     * @throws XmlException if invalid.
     */
    public static void validate(XmlDocument source, String schema,
                                boolean bTargetNS) throws XmlException
    {
        try {
            // Set up the Xerces2 parser - configure via properties
            SAXBuilder builder = new SAXBuilder("org.apache.xerces.parsers.SAXParser", true);
            builder.setFeature ("http://apache.org/xml/features/validation/schema", true);
            String prop = bTargetNS
                        ? "http://apache.org/xml/properties/schema/external-schemaLocation"
                        : "http://apache.org/xml/properties/schema/external-noNamespaceSchemaLocation";
            builder.setProperty(prop, schema);
            // Validate
            builder.build(new StringReader(source.output()));
      }
      catch(Exception excp) {
            throw new XmlException(excp.getMessage());
      }
    }

    /**
     * Checks that the incoming document is conformant to the resolved schema.
     * @param source the document.
     * @param sTargetNS the target namespace.
     * @param resolver  the resolver.
     * @throws XmlException if invalid.
     */
    public static void validate(XmlDocument source, String sTargetNS, EntityResolver resolver) throws XmlException
    {
      try {
          // Set up the Xerces2 parser - configure via properties
          SAXBuilder builder = new SAXBuilder("org.apache.xerces.parsers.SAXParser", true);
          builder.setFeature ("http://apache.org/xml/features/validation/schema", true);
          String sProp = "http://apache.org/xml/properties/schema/external-schemaLocation";
          StringBuilder sb = new StringBuilder();
          sb.append(sTargetNS).append(" ").append(sTargetNS);
          builder.setProperty(sProp, sb.toString());
          builder.setEntityResolver(resolver);
          // Validate
          builder.build(new StringReader(source.output()));
      }
      catch(Exception excp) {
          throw new XmlException(excp.getMessage());
      }
    }

    /**
     * Removes the incoming node.
     * @param  node the node.
     * @return true if successful; otherwise false.
     */
    public boolean remove(Object node)
    {
        boolean ret;
        if(node instanceof XmlAttribute) {
            XmlAttribute attrb = (XmlAttribute)node;
            XmlElement parent  = attrb.getParent( );
            parent.removeAttribute(attrb.getName());
            ret = true;
        }
        else {
            XmlElement parent = ((XmlElement)node).getParent();
            ret = parent.removeChild((XmlElement)node);
        }

        return ret;

    }   // remove()

    //** ----------------------------------------------- Operations (conversion)

    /**
     * Converts the document to an XMlStream.
     * @return the stream.
     * @throws IOException on error.
     */
    public XmlDocument.XmlStream toStream() throws IOException {
        return new XmlStream(this);
    }

    //** --------------------------------------------------- Operations (output)

    /**
     * Sets the output encoding.
     * @param encoding the encoding.
     */
    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    /**
     * Outputs the document to the given stream.
     * @param ostream the stream
     * @throws IOException on error.
     */
    public void output(OutputStream ostream) throws IOException {
        getOutputter().output(document, ostream);
    }

    /**
     * Returns a textual representation of the document.
     * @return the representation
     */
    public String output() {
        return getOutputter().outputString(document);
    }

    /**
     * Dumps the document to STDOUT.
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

    //** ----------------------------------- Operations (ISerializable interface)

    /**
     * Returns a serializable String equivalent.
     * @return the string.
    */
    public String serialize() {
        return output();
    }

    //** ------------------------------------------------------------ Operations

    /**
     * This loads a document from JAR resources.
     * @param clsObj the calling class.
     * @param sXMLConfig the document path.
     * @return the document.
     * @throws XmlException on error.
     */
    public static XmlDocument fromResource(Class clsObj, String sXMLConfig)
                              throws XmlException {
        return fromResource(clsObj, sXMLConfig, null);                             
    }

    /**
     * This loads a document from JAR resources.
     * @param clsObj the calling class.
     * @param sXMLConfig the document path.
     * @param sDefaultNS a default namespace (or null).
     * @return the document.
     * @throws XmlException on error.
     */
    public static XmlDocument fromResource(Class clsObj, String sXMLConfig,
                              String sDefaultNS) throws XmlException {
        InputStream is;
        ClassLoader classLoader = clsObj.getClassLoader();
        if((is = classLoader.getResourceAsStream(sXMLConfig)) == null) {
          String sExcp = String.format(s_sNoResource, sXMLConfig);
          throw new XmlException(sExcp);
        }

        return new XmlDocument(is, sDefaultNS, false);

    } // fromResource()

    //** -------------------------------------------------------- Implementation

    /**
     * Builds a document using a source input stream.
     * @param  istream the stream.
     * @param  defaultNS a default namespace.
     * @param  bValidate true to perform DTD validation.
     * @return the document.
     * @throws JDOMException on error.
     * @throws IOException on error.
     */
    private static Document buildDocument(InputStream istream, String defaultNS,
                            boolean bValidate) throws JDOMException, IOException
    {
        SAXBuilder builder = new SAXBuilder(new XMLReaderSAX2Factory(bValidate));
        return builder.build(istream);
    }

    /**
     * Builds a document using a source reader.
     * @param  reader the reader.
     * @param  bValidate true to perform DTD validation.
     * @return the document.
     * @throws JDOMException on error.
     * @throws IOException on error.
     */
    private static Document buildDocument(Reader reader, boolean bValidate)
                            throws JDOMException, IOException
    {
        SAXBuilder builder = new SAXBuilder(bValidate);
        return builder.build(reader);
    }

    /**
     * Builds a document from the supplied URL.
     * @param url the URL.
     * @param bValidate true to perform DTD validation.
     * @return the document.
     * @throws JDOMException on error.
     * @throws IOException on error.
     */
    private static Document buildDocument(URL url, boolean bValidate)
                   throws JDOMException, IOException
    {
        SAXBuilder builder = new SAXBuilder(bValidate);
        return builder.build(url);
    }

    /**
     * Returns a JDOM XMLOutputter.
     * @return the outputter.
     */
    private XMLOutputter getOutputter()  {
        Format fmt = Format.getPrettyFormat();
        fmt.setEncoding(encoding);
        return new XMLOutputter(fmt);
    }

    /**
     * Creates a Jaxen XPath object.
     * @param  sXPathExpr the XPath expression.
     * @param  mpNS       namespace bindings (or null).
     * @return the XPath object.
     * @throws JaxenException on error.
     */
//    private XPath createXPath(String sXPathExpr, Map<String,String> mpNS) throws JaxenException
//    {
//        SimpleNamespaceContext nsc = new SimpleNamespaceContext();
//        sXPathExpr = initNamespaceContext(sXPathExpr, nsc);
//        if(mpNS != null) {
//            for (Object o : mpNS.entrySet()) {
//                Map.Entry<String,String> entry = (Map.Entry)o;
//                String sPrefix = entry.getKey();
//                String sURI = entry.getValue( );
//                nsc.addNamespace(sPrefix, sURI);
//            }
//        }
//
//        XPath xpath = new JDOMXPath(sXPathExpr);
//        xpath.setNamespaceContext(nsc);
//        return xpath;
//
//    } // createXPath()

    /**
     * Initialises a namespace context with those namespaces (default and explicit)
     * specified on the root element. If a default namespace is present, the
     * expression is expanded, using a dummy prefix.
     * @param  sXPathExpr the XPath expression.
     * @param  nsc the namespace context.
     * @return the (modified) XPath expression.
     */
    private String initNamespaceContext(String sXPathExpr, SimpleNamespaceContext nsc) {

        Element root = document.getRootElement();
        List lsNS = new ArrayList();
        lsNS.add(root.getNamespace());
        lsNS.addAll(root.getAdditionalNamespaces());
        Iterator ii = lsNS.iterator();
        String sPrefix;

        while(ii.hasNext()) {
            Namespace ns = (Namespace)ii.next();
            if((sPrefix = ns.getPrefix()).equals("")) sPrefix = DEFAULT_PREFIX;
            nsc.addNamespace(sPrefix, ns.getURI());

            if(ns.getPrefix().equals("")) {
                StringTokenizer strtok = new StringTokenizer(sXPathExpr, "/\\");
                StringBuffer sb = new StringBuffer();
                while(strtok.hasMoreTokens()) {
                    String sToken = strtok.nextToken();
                    if(!expand(sToken)) sb.append(sToken);
                    else {
                        sb.append(DEFAULT_PREFIX).append(':').append(sToken);
                    }
                    if(strtok.hasMoreTokens()) {
                        sb.append('/');
                    }
                }
                sXPathExpr = sb.toString();
            }
        }

        return sXPathExpr;

    }   // initNamespaceContext()

    /**
     * This determines whether a path token should be expanded
     * with the default (dummy) prefix.
     * @param  sToken the token.
     * @return true to expand; otherwise false.
     */
    private boolean expand(String sToken) {
        boolean bExpand = false;
        int idx1, idx2;
        // Omit attributes
        if(!sToken.startsWith("@")) {
            // Expand if not already a namespace present.
            // Second test checks that ':' is part of an attribute predicate
            // e.g foo[@name="david@differitas.no"]
            if((idx1 = sToken.indexOf(":")) == -1) bExpand = true;
            else {
                if((idx2 = sToken.indexOf('@')) == -1) bExpand = false;
                else {
                   bExpand = idx1 > idx2;
                }
            }
        }

        return bExpand;

    }   // expand()

    //** ---------------------------------------------------------- Nested class
    /**
     * Converts a document to an input stream.
     */
    public final static class XmlStream implements Closeable  {
        private InputStream is;
        private int len;
        public  XmlStream(XmlDocument doc) throws IOException {
            ByteArrayOutputStream baos = null;
            try {
                doc.output((baos = new ByteArrayOutputStream( )));
                is = new ByteArrayInputStream(baos.toByteArray());
                len = baos.toByteArray().length;
            }
            finally {
                if(baos != null){
                    try  {baos.close();}
                    catch(IOException excp) {
                    }
                }
            }
        }
        public InputStream getInputStream()           {return is; }
        public int         getLength()                {return len;}
        public void        close() throws IOException {is.close();}

    } // class XmlStream

    //** ---------------------------------------------------------- Nested class

//    public final static class Parser {
//       public  static String getVersion() {
//         String sVersion;
//         try  {sVersion = org.apache.xerces.impl.Version.getVersion();}
//         catch(Exception excp) {
//           sVersion = "pre 2.0"; // org.apache.xerces.impl.Version.fVersion;
//         }
//         return sVersion;
//       }
//       private Parser() {}
//    }

} // class XmlDocument
