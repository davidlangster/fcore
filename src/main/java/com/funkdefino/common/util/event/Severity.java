package com.funkdefino.common.util.event;

import java.util.HashMap;
import java.util.Map;

/**
 * <p/>
 * <code>$Id: $</code>
 * @author Differitas (David M. Lang)
 * @version $Revision: $
 */
public enum Severity {

  //** ------------------------------------------------------------------ Values

  DEBUG("debug"),
  INFO("info"),
  WARNING("warning"),
  ERROR("error"),
  CRITICAL("critical"),
  SECURITY("security");

  //** --------------------------------------------------- Static initialisation

  //** -------------------------------------------------------------------------

  private  String text;
  Severity(String text) {
      this.text = text;
  }

  //** -------------------------------------------------------------- Operations

  public String getText () {return text;}
  

} // enum Severity
