/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package monopoly.ws.events;

/**
 *
 * @author ShiloMangam
 */
public class InternalEvent {
    private String eventType;
    
    public InternalEvent(String _type){
        this.eventType = _type;
    }
    
    public String getType(){
        return this.eventType;
    }
    
}
