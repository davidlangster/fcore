package com.funkdefino.common.io;

import com.funkdefino.common.util.UtilException;
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


  /**
   * This reads a sequence of bytes from resources.
   * @param clz the calling class.
   * @param resource the resource.
   * @throws IOException on error.
   * @throws UtilException on error.
   * @return the array.
   */
    public static byte[] loadResource(Class clz, String resource)
                         throws IOException, UtilException {

        ByteArrayOutputStream baos = null;
        InputStream is = null;
        byte[] arr;

        try {

            ClassLoader classLoader = clz.getClassLoader();
            if((is = classLoader.getResourceAsStream(resource)) == null) {
                String sExcp = String.format("Unable to load resource file '%s'", resource);
                throw new UtilException(sExcp);
            }

            baos = new ByteArrayOutputStream();
            byte[] data = new byte[256];
            int read;

            while((read = is.read(data, 0, data.length)) != -1) {
              baos.write(data, 0, read);
            }

            baos.flush();
            arr = baos.toByteArray();

        }
        finally {
            close(baos);
            close(is);
        }

        return arr;

    } // loadResource()

} // class IOUtil
