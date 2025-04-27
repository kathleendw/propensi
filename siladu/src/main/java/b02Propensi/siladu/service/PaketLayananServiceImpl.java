package b02Propensi.siladu.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties.User;
import org.springframework.stereotype.Service;

import b02Propensi.siladu.model.Layanan;
import b02Propensi.siladu.model.PaketLayanan;
import b02Propensi.siladu.model.Pesanan;
import b02Propensi.siladu.repository.LayananDb;
import b02Propensi.siladu.repository.PaketLayananDb;
import b02Propensi.siladu.user.model.UserModel;
import b02Propensi.siladu.model.Pembayaran;
import b02Propensi.siladu.repository.LayananDb;
import b02Propensi.siladu.repository.PaketLayananDb;
import b02Propensi.siladu.user.model.UserModel;
import b02Propensi.siladu.service.PembayaranService;
import b02Propensi.siladu.service.PesananService;
import java.time.LocalDate;
import java.time.Year;
import java.time.ZoneId;
import java.util.*;

@Service
public class PaketLayananServiceImpl implements PaketLayananService {
    @Autowired
    PaketLayananDb paketLayananDb;
    @Autowired
    PembayaranService pembayaranService;
    @Autowired
    PesananService pesananService;

    public void savePaketLayanan(PaketLayanan paketLayanan){
        paketLayananDb.save(paketLayanan);
    }
    @Override
    public  PaketLayanan getPaketLayananById(UUID id){
        for (PaketLayanan paketLayanan : getAllPaketLayanan()) {
            if (paketLayanan.getId().equals(id)) {
                return paketLayanan;
            }
        }
        return null;
    }
    
    @Override
    public List<PaketLayanan> getAllPaketLayanan() {
        return paketLayananDb.findAll();
    
    }
    @Override
    public List<PaketLayanan> getAllPaketLayananByLayanan(Layanan layanan) {
        return paketLayananDb.findByLayanan(layanan);
    }

    @Override
    public PaketLayanan updatePaketLayanan(PaketLayanan paketLayananFromDto){
        PaketLayanan paketLayanan = getPaketLayananById(paketLayananFromDto.getId());
        if (paketLayanan != null) {
            paketLayanan.setHargaPaket(paketLayananFromDto.getHargaPaket());
            paketLayanan.setLayanan(paketLayananFromDto.getLayanan());
            paketLayanan.setNamaPaket(paketLayananFromDto.getNamaPaket());
            paketLayanan.setDeskripsi(paketLayananFromDto.getDeskripsi());
            paketLayanan.setManfaat(paketLayananFromDto.getManfaat());
            paketLayananDb.save(paketLayanan);
        }
        return paketLayanan;
    }
    @Override
    public  void deletePaketLayanan(PaketLayanan paketLayanan) {
        paketLayananDb.delete(paketLayanan);
    }
    @Override
    public boolean isNamaPaketExist(String namaPaket, String namaLayanan){
        return getAllPaketLayanan().stream().anyMatch(b -> b.getNamaPaket().toLowerCase().equals(namaPaket.toLowerCase()) && b.getLayanan().getNamaLayanan().equals(namaLayanan));
    }
    @Override
    public List<PaketLayanan> searchPaketLayanan(String namaPaket, Layanan layanan) {
        List<PaketLayanan> filter = new ArrayList<>();
        for (PaketLayanan paketLayanan :paketLayananDb.findByNamaPaketContainingIgnoreCase(namaPaket)){
            if(paketLayanan.getLayanan().equals(layanan)){
                filter.add(paketLayanan);
            }
        }
        return filter;
    }
    @Override
    public boolean checkPesananBelumBayar(UserModel user, UUID pesanan){
        boolean check = false;
        for (Pesanan pesananUser: user.getListPesanan()) {
            if(pesananUser.getPaketLayanan() != null && pesananUser.getPaketLayanan().getId().equals(pesanan)){
              if(pesananUser.getIdPembayaran() == null){
                return true;
              }
            }
        }
        return check;
    }
    @Override
    public boolean checkPesananDiproses(UserModel user, UUID pesanan){
        boolean check = false;
        for (Pesanan pesananUser: user.getListPesanan()) {
            if(pesananUser.getPaketLayanan() != null && pesananUser.getPaketLayanan().getId().equals(pesanan)){
              Pembayaran pembayaran = pembayaranService.getPembayaranById(pesananUser.getIdPembayaran());
              if(pembayaran.getStatusPembayaran().equals("PERLU KONFIRMASI")){
                return true;
              }
            }
        }
        return check;
    }
    @Override
    public boolean checkPesananStatusPembayaran(UserModel user, UUID pesanan){
        boolean check = false;
        for (Pesanan pesananUser: user.getListPesanan()) {
            if(pesananUser.getPaketLayanan() != null && pesananUser.getPaketLayanan().getId().equals(pesanan)){
              Pembayaran pembayaran = pembayaranService.getPembayaranById(pesananUser.getIdPembayaran());
              if(!pembayaran.getStatusPembayaran().equals("GAGAL")){
                return true;
              }
            }
        }
        return check;
    }
    @Override 
    public Map<String, Double> getRatingLayanan(){
        Map<String, Double> ratingLayanan = new HashMap<>();

        for(PaketLayanan paketLayanan : getAllPaketLayanan()){
           
                String namaPaketLayanan = paketLayanan.getNamaPaket();
                double rating = paketLayanan.getRating();
                ratingLayanan.put(namaPaketLayanan, rating);
           
        }
        return ratingLayanan;
    }
    @Override 
    public Map<String, Double> getRatingLayananFilter(String layanan){
        Map<String, Double> ratingLayanan = new HashMap<>();

        for(PaketLayanan paketLayanan : getAllPaketLayanan()){
                if(paketLayanan.getLayanan().getNamaLayanan().equals(layanan)){
                    String namaPaketLayanan = paketLayanan.getNamaPaket();
                    double rating = paketLayanan.getRating();
                    ratingLayanan.put(namaPaketLayanan, rating);
                }
           
        }
        return ratingLayanan;
    }
    @Override 
    public Map<String, Integer> getLayananPeserta(){
        Map<String, Integer> layananPeserta = new HashMap<>();

        for(Pesanan pesanan : pesananService.getAllPesananPaketLayanan()){
           if( pesanan.getIdPembayaran()!=null && pembayaranService.getPembayaranById(pesanan.getIdPembayaran()).getStatusPembayaran().equals("SUKSES"))
            {
                var jumlahPeserta = 0;
                var namaPaketLayanan = pesanan.getPaketLayanan().getNamaPaket();
                for(Pesanan pesanan2 : pesananService.getAllPesananPaketLayanan()){
                    if(pesanan2.getPaketLayanan().getNamaPaket().equals(namaPaketLayanan) && pesanan2.getIdPembayaran()!=null && pembayaranService.getPembayaranById(pesanan2.getIdPembayaran()).getStatusPembayaran().equals("SUKSES")){
                        jumlahPeserta +=1;
                    }
                }
                layananPeserta.put(namaPaketLayanan, jumlahPeserta);
            }
           
        }
        return layananPeserta;
    }
    @Override 
    public Map<String, Integer> getLayananPesertaFilter(String layanan){
        Map<String, Integer> layananPeserta = new HashMap<>();

        for(Pesanan pesanan : pesananService.getAllPesananPaketLayanan()){
           if(pesanan.getIdPembayaran()!=null && pembayaranService.getPembayaranById(pesanan.getIdPembayaran()).getStatusPembayaran().equals("SUKSES") && pesanan.getPaketLayanan().getLayanan().getNamaLayanan().equals(layanan))
            {
                var jumlahPeserta = 0;
                var namaPaketLayanan = pesanan.getPaketLayanan().getNamaPaket();
                for(Pesanan pesanan2 : pesananService.getAllPesananPaketLayanan()){
                    if( pesanan2.getPaketLayanan().getNamaPaket().equals(namaPaketLayanan) && pesanan2.getIdPembayaran()!=null && pembayaranService.getPembayaranById(pesanan2.getIdPembayaran()).getStatusPembayaran().equals("SUKSES")){
                        jumlahPeserta +=1;
                    }
                }
                layananPeserta.put(namaPaketLayanan, jumlahPeserta);
            }
           
        }
        return layananPeserta;
    }
    @Override
    public Long[] getPendapatanPerBulan(int targetYear) {
        int currentYear = LocalDate.now().getYear();
        int currentMonth = LocalDate.now().getMonthValue();
        Long[] totalIncomePerMonth = new Long[12];
        Arrays.fill(totalIncomePerMonth, 0L);
    
        for (Pesanan pesanan : pesananService.getAllPesananPaketLayanan()) {
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
    public Set<UserModel> getAllUserPesanPaket() {
        Set<UserModel> uniqueUsers = new HashSet<>();
        for (Pesanan pesanan : pesananService.getAllPesananPaketLayanan()) {  
            if(pesanan.getIdPembayaran()!=null && pembayaranService.getPembayaranById(pesanan.getIdPembayaran()).getStatusPembayaran().equals("SUKSES"))
                uniqueUsers.add(pesanan.getUser());
        }
        return uniqueUsers;
    }


}
