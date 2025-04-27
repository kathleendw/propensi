package b02Propensi.siladu.service;

import org.glassfish.jaxb.core.annotation.OverrideAnnotationOf;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import b02Propensi.siladu.model.Event;
import b02Propensi.siladu.model.Pesanan;
import b02Propensi.siladu.model.Training;
import b02Propensi.siladu.model.Pembayaran;
import b02Propensi.siladu.user.model.UserModel;
import b02Propensi.siladu.service.PembayaranService;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.LocalDate;
import java.util.*;

import b02Propensi.siladu.repository.EventDb;

@Service
public class EventServiceImpl implements EventService {
    
    @Autowired
    EventDb eventDb;
    @Autowired
    PesananService pesananService;
    private Long countEvent;
    @Autowired
    PembayaranService pembayaranService;

    @Override
    public void saveEvent(Event event){
        eventDb.save(event);
    }

    @Override
    public List<Event> getAllEvent(){
        return eventDb.findAll();
    }    

    @Override
    public void generateIdEvent(Event event) {
        countData();
        countEvent++;
        String formattingNumCode = String.format("%03d", countEvent);
        String idEvent = "EVENT" + formattingNumCode;
        event.setIdEvent(idEvent);
    }

    @Override
    public Event getEventByIdEvent(String idEvent){
        return eventDb.findById(idEvent).orElse(null);
    }

    @Override
    public Event updateEvent(Event eventFromDTO){
        Event event = getEventByIdEvent(eventFromDTO.getIdEvent());
        if (event != null) {
        event.setIdEvent(eventFromDTO.getIdEvent());
        event.setNamaEvent(eventFromDTO.getNamaEvent());
        event.setGambarEvent(eventFromDTO.getGambarEvent());
        event.setNamaPembicara(eventFromDTO.getNamaPembicara());
        event.setHargaEvent(eventFromDTO.getHargaEvent());
        event.setLokasiEvent(eventFromDTO.getLokasiEvent());
        event.setJadwalEvent(eventFromDTO.getJadwalEvent());
        event.setDurasiEvent(eventFromDTO.getDurasiEvent());
        event.setDeskripsiEvent(eventFromDTO.getDeskripsiEvent());
        eventDb.save(event);
        }
        return event;
    }

  
    public void countData(){
        countEvent = eventDb.count();
    }

    public void deleteEventById(String idEvent){
        Event event = getEventByIdEvent(idEvent);

        if (event != null){
            eventDb.delete(event);
        } else {
            throw new IllegalArgumentException("Event dengan id " + idEvent + " tidak ditemukan");
        }

    }

    @Override
    public void CreatePesananEvent(String idEvent, UserModel user){
        Pesanan pesananEvent = new Pesanan();
        
        pesananEvent.setEvent(getEventByIdEvent(idEvent));
        pesananEvent.setJenisPesanan("EVENT");
        pesananEvent.setStatusPesanan("BELUM_BAYAR");
        pesananEvent.setUser(user);
        LocalDateTime localDateTime = LocalDateTime.now();
        Date date = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
        pesananEvent.setWaktuPesanan(date);
        pesananService.savePesanan(pesananEvent);

    }

    @Override
    public boolean checkPesanan(UserModel user, String idEvent){
        boolean check = false;
        for (Pesanan pesananUser: user.getListPesanan()) {
            if (pesananUser.getEvent() != null && pesananUser.getEvent().getIdEvent().equals(idEvent))  { //pesanan belom dibayar
                if (pesananUser.getStatusPesanan().equals("DIPROSES") || pesananUser.getStatusPesanan().equals("BELUM_BAYAR")) {
                    check = true;
                    break; 
                } else {
                    for (Pembayaran pembayaran : pembayaranService.getAllPembayaran()) {
                        if (pesananUser.getIdPembayaran().equals(pembayaran.getId()) && pembayaran.getStatusPembayaran().equals("GAGAL")) { //pesanan sudah dibayar tapi gagal
                            check = false;
                            return check; 
                        }
                    }
                }
            }
        }
        return check;
    }

    @Override
    public boolean isRegistered(UserModel user, String idEvent){
        for (Pesanan pesananUser: user.getListPesanan()) {
            for (Pembayaran pembayaran: pembayaranService.getAllPembayaran()) {
                if (pesananUser.getEvent()!= null && pesananUser.getEvent().getIdEvent().equals(idEvent) && pesananUser.getIdPembayaran() != null && pesananUser.getIdPembayaran().equals(pembayaran.getId()) && pembayaran.getStatusPembayaran().equals("SUKSES")) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public Long[] getPendapatanPerBulan(int targetYear) {
        int currentYear = LocalDate.now().getYear();
        int currentMonth = LocalDate.now().getMonthValue();
        Long[] totalIncomePerMonth = new Long[12];
        Arrays.fill(totalIncomePerMonth, 0L);
    
        for (Pesanan pesanan : pesananService.getAllPesananEvent()) {
            if (pesanan.getIdPembayaran() != null &&
                    pembayaranService.getPembayaranById(pesanan.getIdPembayaran()).getStatusPembayaran().equals("SUKSES")) {
                int paymentYear = pembayaranService.getPembayaranById(pesanan.getIdPembayaran()).getWaktuPembayaran().toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate()
                        .getYear();
                int paymentMonth = pembayaranService.getPembayaranById(pesanan.getIdPembayaran()).getWaktuPembayaran().toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate()
                        .getMonthValue();
    
           
            if (targetYear == paymentYear){
                    totalIncomePerMonth[paymentMonth - 1] += pesanan.getHargaPesanan();
                }
            
            }
            if(targetYear==currentYear){
                for (int i = currentMonth; i < 12; i++) {
                    totalIncomePerMonth[i] = null;
                }
            }
        }
    
        return totalIncomePerMonth;
    }

    @Override 
    public Map<String, Integer> getEventPeserta(){
        Map<String, Integer> EventPeserta = new HashMap<>();

        for(Pesanan pesanan : pesananService.getAllPesananEvent()){
           if( pesanan.getIdPembayaran()!=null && pembayaranService.getPembayaranById(pesanan.getIdPembayaran()).getStatusPembayaran().equals("SUKSES"))
            {
                var jumlahPeserta = 0;
                var namaEvent = pesanan.getEvent().getNamaEvent();
                for(Pesanan pesanan2 : pesananService.getAllPesananEvent()){
                    if(pesanan2.getEvent().getNamaEvent().equals(namaEvent)){
                        jumlahPeserta +=1;
                    }
                }
                EventPeserta.put(namaEvent, jumlahPeserta);
            }
           
        }
        return EventPeserta;
    }
    @Override
    public Set<UserModel> getAllUserPesanEvent() {
        Set<UserModel> uniqueUsers = new HashSet<>();
        for (Pesanan pesanan : pesananService.getAllPesanan()) {
            if (pesanan.getEvent() != null) {
                uniqueUsers.add(pesanan.getUser());
            }
        }
        return uniqueUsers;
    }
    @Override 
    public Map<String, Double> getRatingEvent(){
        Map<String, Double> ratingEvent = new HashMap<>();
        for(Event event : getAllEvent()){
            String judulEvent = event.getNamaEvent();
            double rating = event.getRating();
            ratingEvent.put(judulEvent, rating); 
        }
        return ratingEvent;
    }




}
