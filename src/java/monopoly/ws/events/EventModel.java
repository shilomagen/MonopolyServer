/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package monopoly.ws.events;

import java.util.ArrayList;
import java.util.List;
import ws.monopoly.Event;

/**
 *
 * @author ShiloMangam
 */
public class EventModel {
    private List<Event> eventList;
    
    public EventModel(){
        this.eventList = new ArrayList<>();
    }
    
    public List<Event> getEventList(){
        return this.eventList;
    }

    public List<Event> getEventFromEventID(int id) {
        int i =0;
        boolean found = false;
        //if id is 0, it means that this is the start game event
        if (id==0){
            return this.eventList;
        }
        for (i=0;i<this.eventList.size() && !found ; ++i){
            if (this.eventList.get(i).getId()==id){
                found = true;          
            }
        }   if (found && i != this.eventList.size()){            
            return this.eventList.subList(i, this.eventList.size()); 
        } else {
            return null;
        }
        
    }

    public void addEvent(Event newEvent) {
        this.eventList.add(newEvent);
    }
    
    
}
