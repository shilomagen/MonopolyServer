/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package monopoly.ws.events;

import java.util.List;
import ws.monopoly.Event;
import ws.monopoly.EventType;

/**
 *
 * @author ShiloMangam
 */
public class EventManager {
    private EventModel eventModel;
    
    public EventManager(){
        this.eventModel = new EventModel();
    }
    
    public List<Event> getEventListFromEventID(int id){
        return this.eventModel.getEventFromEventID(id);
    }

    public void addEvent(EventType eventType) {
        Event newEvent = new Event();
        newEvent.setType(eventType);
        this.eventModel.addEvent(newEvent);
    }
}
