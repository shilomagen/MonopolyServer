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
    private static int eventID=0;
    
    public EventManager(){
        this.eventModel = new EventModel();
    }
    
    public List<Event> getEventListFromEventID(int id){
        return this.eventModel.getEventFromEventID(id);
    }

    public void addEvent(EventType eventType) {
        Event eventToAdd = new Event();
        eventToAdd.setType(eventType);
        eventToAdd.setId(eventID);
        incrementEventID();
        switch (eventType){
            case GAME_START:
                this.eventModel.addEvent(eventToAdd);
                break;
            default:
                break;
        }
    }

    private void incrementEventID() {
        eventID++;
    }
}
