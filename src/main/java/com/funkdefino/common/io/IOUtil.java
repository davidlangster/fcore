package com.funkdefino.common.io;

import com.funkdefino.common.util.xml.XmlDocument;
import com.funkdefino.common.util.xml.XmlException;

import org.springframework.core.io.Resource;
import java.io.*;

/**
 * <p/>
 * <code>$Id: $</code>
 * @author Differitas (David M. Lang)
 * @version $Revision: $
 */
public final class IOUtil {

  //** -------------------------------------------------------------- Operations
  
  /**
   * This closes a resource.
   * @param c the closeable resource.
   */
  public final static void close(Closeable c) {
    if(c != null) {
       try  {c.close();}
       catch(IOException excp) {
         excp.printStackTrace();
       }
    }
  }

  /**
   * This loads a document from Spring resources.
   * @param resource the resource.
   * @throws IOException on error.
   * @throws XmlException on error.
   * @return the document.
   */
  public static XmlDocument loadResource(Resource resource)
                throws IOException, XmlException {

    InputStream is = null;
    XmlDocument doc;
    try {
      is = resource.getInputStream( );
      doc = new XmlDocument(is,false);
    }
    finally {
      close(is);
    }

    return doc;

  } // loadResource()

} // class IOUtil
