package b02Propensi.siladu.service;

import java.util.*;
import b02Propensi.siladu.model.Pesanan;
import b02Propensi.siladu.user.model.UserModel;
import b02Propensi.siladu.model.Training;

public interface TrainingService {
    void saveTraining(Training training);
    List<Training> getAllTraining();
    Training getTrainingById(UUID idTraining);
    Training updateTraining(Training trainingFromDto);
    void deleteTraining(Training training);
    List<Training> searchTraining(String judulTraining);
    boolean checkPesananBelumBayar(UserModel user, UUID pesanan);
    boolean checkPesananStatusPembayaran(UserModel user, UUID pesanan);
    boolean checkPesananDiproses(UserModel user, UUID pesanan);
    boolean unlockedTraining(UserModel user, UUID idTraining);
    Map<String, Double> getRatingTraining();
    Map<String, Integer> getTrainingPeserta();
    Long[] getPendapatanPerBulan(int tahun);
    Set<UserModel> getAllUserPesanTraining();
}
