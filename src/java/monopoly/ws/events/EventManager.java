/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package monopoly.ws.events;

import java.util.List;
import ws.monopoly.Event;
import ws.monopoly.InvalidParameters;
import ws.monopoly.InvalidParameters_Exception;

/**
 *
 * @author ShiloMangam
 */
public class EventManager {
    private EventModel eventModel;
    private static int eventID=0;
    
    public EventManager(){
        this.eventModel = new EventModel();
    }
    
    public List<Event> getEventListFromEventID(int id){
        return this.eventModel.getEventFromEventID(id);
    }

    public void addEvent(Event event) {
        event.setId(eventID);
        incrementEventID();
        this.eventModel.addEvent(event);
    }

    private void incrementEventID() {
        eventID++;
    }

    public Event getEventById(int arg1) throws InvalidParameters_Exception {
        for (Event event : this.eventModel.getEventList()){
            if (event.getId()==arg1){
                return event;
            }
        }
        throw new InvalidParameters_Exception("There is no event with ID: " + arg1, new InvalidParameters());
    }
}
