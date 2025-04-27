package b02Propensi.siladu.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties.User;
import org.springframework.stereotype.Service;

import b02Propensi.siladu.model.Training;
import b02Propensi.siladu.repository.TrainingDb;
import b02Propensi.siladu.model.Pesanan;
import b02Propensi.siladu.model.Pembayaran;
import b02Propensi.siladu.user.model.UserModel;
import b02Propensi.siladu.service.PembayaranService;
import b02Propensi.siladu.service.PesananService;
import java.time.LocalDate;
import java.time.Year;
import java.time.ZoneId;
import java.util.*;

@Service
public class TrainingServiceImpl implements TrainingService {
    @Autowired
    TrainingDb trainingDb;
    @Autowired
    PembayaranService pembayaranService;
    @Autowired
    PesananService pesananService;

    @Override
    public void saveTraining(Training training) {
        trainingDb.save(training);
    }

    @Override
    public List<Training> getAllTraining() {
        return trainingDb.findAll(); 
    }

    @Override
    public Training getTrainingById(UUID idTraining) {
        for (Training training : getAllTraining()) {
            if (training.getIdTraining().equals(idTraining)) {
                return training;
            }
        }
        return null;
    }

    @Override
    public Training updateTraining(Training trainingFromDto) {
        Training training = getTrainingById(trainingFromDto.getIdTraining());
        if (training != null){
            training.setJudulTraining(trainingFromDto.getJudulTraining());
            training.setPengajarTraining(trainingFromDto.getPengajarTraining());
            training.setDeskripsiTraining(trainingFromDto.getDeskripsiTraining());
            training.setHargaTraining(trainingFromDto.getHargaTraining());
            training.setVideoTraining(trainingFromDto.getVideoTraining());
            trainingDb.save(training);
        }
        return training;
    }

    @Override
    public void deleteTraining(Training training){
        trainingDb.delete(training);
    }

    @Override
    public List<Training> searchTraining(String judulTraining) {
        return trainingDb.findByJudulTrainingContainingIgnoreCase(judulTraining);
    }

    @Override
    public boolean checkPesananBelumBayar(UserModel user, UUID idTraining){
        boolean check = false;
        for (Pesanan pesananUser: user.getListPesanan()) {
            if (pesananUser.getTraining() != null && pesananUser.getTraining().getIdTraining().equals(idTraining)) {
              if(pesananUser.getIdPembayaran() == null){
                return true;
              }
            }
        }
        return check;
    }
    @Override
    public boolean checkPesananDiproses(UserModel user, UUID idTraining){
        boolean check = false;
        for (Pesanan pesananUser: user.getListPesanan()) {
            if (pesananUser.getTraining() != null && pesananUser.getTraining().getIdTraining().equals(idTraining)) {
              Pembayaran pembayaran = pembayaranService.getPembayaranById(pesananUser.getIdPembayaran());
              if(pembayaran.getStatusPembayaran().equals("PERLU KONFIRMASI")){
                return true;
              }
            }
        }
        return check;
    }
    @Override
    public boolean checkPesananStatusPembayaran(UserModel user, UUID idTraining){
        boolean check = false;
        for (Pesanan pesananUser: user.getListPesanan()) {
            if (pesananUser.getTraining() != null && pesananUser.getTraining().getIdTraining().equals(idTraining)) {
              Pembayaran pembayaran = pembayaranService.getPembayaranById(pesananUser.getIdPembayaran());
              if(!pembayaran.getStatusPembayaran().equals("GAGAL")){
                return true;
              }
            }
        }
        return check;
    }
    @Override
    public boolean unlockedTraining(UserModel user, UUID idTraining){
        for (Pesanan pesananUser: user.getListPesanan()) {
            for (Pembayaran pembayaran: pembayaranService.getAllPembayaran()) {
                if (pesananUser.getTraining() != null && pesananUser.getTraining().getIdTraining().equals(idTraining) && pesananUser.getIdPembayaran() != null && pesananUser.getIdPembayaran().equals(pembayaran.getId()) && pembayaran.getStatusPembayaran().equals("SUKSES")) {
                    return true;
                }
            }
        }
        return false;
    }
    @Override
    public Set<UserModel> getAllUserPesanTraining() {
        Set<UserModel> uniqueUsers = new HashSet<>();
        for (Pesanan pesanan : pesananService.getAllPesanan()) {
            if (pesanan.getTraining() != null && pesanan.getIdPembayaran() != null && pembayaranService.getPembayaranById(pesanan.getIdPembayaran()).getStatusPembayaran().equals("SUKSES")) {
                uniqueUsers.add(pesanan.getUser());
            }
        }
        return uniqueUsers;
    }
    @Override 
    public Map<String, Double> getRatingTraining(){
        Map<String, Double> ratingTraining = new HashMap<>();
        for(Training training : getAllTraining()){
            String judulTraining = training.getJudulTraining();
            double rating = training.getRatingTraining();
            ratingTraining.put(judulTraining, rating); 
        }
        return ratingTraining;
    }
    @Override 
    public Map<String, Integer> getTrainingPeserta(){
        Map<String, Integer> trainingPeserta = new HashMap<>();
        for (Pesanan pesanan : pesananService.getAllPesananTraining()){
           if (pesanan.getIdPembayaran() != null && pembayaranService.getPembayaranById(pesanan.getIdPembayaran()).getStatusPembayaran().equals("SUKSES")){
                var jumlahPeserta = 0;
                var judulTraining = pesanan.getTraining().getJudulTraining();
                for (Pesanan pesanan2 : pesananService.getAllPesananTraining()){
                    if (pesanan2.getTraining().getJudulTraining().equals(judulTraining) && pesanan2.getIdPembayaran() != null && pembayaranService.getPembayaranById(pesanan2.getIdPembayaran()).getStatusPembayaran().equals("SUKSES")){
                        jumlahPeserta += 1;
                    }
                }
                trainingPeserta.put(judulTraining, jumlahPeserta);
            }
           
        }
        return trainingPeserta;
    }
    @Override
    public Long[] getPendapatanPerBulan(int tahun) {
        int currentYear = LocalDate.now().getYear();
        int currentMonth = LocalDate.now().getMonthValue();
        Long[] totalIncomePerMonth = new Long[12];
        Arrays.fill(totalIncomePerMonth, 0L);
    
        for (Pesanan pesanan : pesananService.getAllPesananTraining()) {
            if (pesanan.getIdPembayaran() != null && pembayaranService.getPembayaranById(pesanan.getIdPembayaran()).getStatusPembayaran().equals("SUKSES")) {
                int paymentYear = pembayaranService.getPembayaranById(pesanan.getIdPembayaran()).getWaktuPembayaran().toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate()
                        .getYear();
                int paymentMonth = pembayaranService.getPembayaranById(pesanan.getIdPembayaran()).getWaktuPembayaran().toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate()
                        .getMonthValue();
                if (tahun == paymentYear){
                    totalIncomePerMonth[paymentMonth - 1] += pesanan.getHargaPesanan();
                }
            }
            if (tahun == currentYear){
                for (int i = currentMonth; i < 12; i++) {
                    totalIncomePerMonth[i] = null;
                }
            }
        }
    
        return totalIncomePerMonth;
    }

}
