
package ws.monopoly;

import javax.xml.ws.WebFault;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.2.6-1b01 
 * Generated source version: 2.2
 * 
 */
@WebFault(name = "InvalidParameters", targetNamespace = "http://monopoly.ws/")
public class InvalidParameters_Exception
    extends Exception
{

    /**
     * Java type that goes as soapenv:Fault detail element.
     * 
     */
    private InvalidParameters faultInfo;

    /**
     * 
     * @param faultInfo
     * @param message
     */
    public InvalidParameters_Exception(String message, InvalidParameters faultInfo) {
        super(message);
        this.faultInfo = faultInfo;
    }

    /**
     * 
     * @param faultInfo
     * @param cause
     * @param message
     */
    public InvalidParameters_Exception(String message, InvalidParameters faultInfo, Throwable cause) {
        super(message, cause);
        this.faultInfo = faultInfo;
    }

    /**
     * 
     * @return
     *     returns fault bean: ws.monopoly.InvalidParameters
     */
    public InvalidParameters getFaultInfo() {
        return faultInfo;
    }

}
