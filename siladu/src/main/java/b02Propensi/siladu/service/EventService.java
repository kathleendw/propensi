package b02Propensi.siladu.service;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;
import b02Propensi.siladu.model.Event;
import b02Propensi.siladu.model.Pesanan;
import b02Propensi.siladu.user.model.UserModel;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.catalog.Catalog;


public interface EventService {
    void generateIdEvent(Event event);
    void saveEvent(Event event);
    List<Event> getAllEvent();
    Event getEventByIdEvent(String idEvent);
    Event updateEvent(Event eventFromDTO);
    void deleteEventById(String idEvent);
    void CreatePesananEvent(String idEvent, UserModel user);
    boolean checkPesanan(UserModel user, String idEvent);
    boolean isRegistered(UserModel user, String idEvent);
    Long[] getPendapatanPerBulan(int targetYear);
    public Map<String, Integer> getEventPeserta();
    public Map<String, Double> getRatingEvent(); 
    Set<UserModel> getAllUserPesanEvent();   
}
