package com.funkdefino.common.util;

/**
 * <p/>
 * <code>$Id: $</code>
 *
 * @author Differitas (David M. Lang)
 * @version $Revision: $
 */
public interface ICRetCodes {

  public final static int SUCCESS                 = 200;
  public final static int NOT_SUPPORTED           = 500;
  public final static int UNDEFINED               = 501;
  public final static int SECURITY_ERROR          = 502;
  public final static int AUTHORISATION_ERROR     = 503;
  public final static int VALIDATION_ERROR        = 504;
  public final static int COMMAND_ERROR           = 505;
  public final static int NO_ENTRIES              = 506;
  public final static int OUT_OF_RESOURCES        = 507;
  public final static int REMOTE_ERROR            = 508;
  public final static int FAIL                    = 512;
  public final static int TIMEOUT                 = 513;
  
} // interface ICRetCodes
