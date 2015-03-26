package com.funkdefino.common.message;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import javax.jms.*;

/**
 * <p/>
 * <code>$Id: $</code>
 *
 * @author Differitas (David M. Lang)
 * @version $Revision: $
 */

public final class Producer implements IProducer {

  //** -------------------------------------------------------------------- Data

  private JmsTemplate m_template;

  //** ------------------------------------------------------- Spring properties

  public void setTemplate(JmsTemplate template) {m_template = template;}

  //** -------------------------------------------------------------- Operations

  /**
   * This writes a serializable payload to the destination.
   * @param payload the payload.
   */
  public void writeMessage(String payload) {
    Destination dest = m_template.getDefaultDestination( );
    m_template.convertAndSend(payload);
    // m_template.send(dest, new TextMessageCreator(payload));
  }

  //** ------------------------------------------------------------------ Nested

  private final static class TextMessageCreator implements MessageCreator {
    private String m_sMessage;
    public  TextMessageCreator(String sMessage) {m_sMessage = sMessage;}
    public Message createMessage(Session session) throws JMSException {
        TextMessage tmsg = session.createTextMessage(m_sMessage);
        // tmsg.setStringProperty("XX", "true"); // Selector
        return tmsg;
    }
  }

} // class Producer