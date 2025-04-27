package b02Propensi.siladu.service;

import b02Propensi.siladu.model.Layanan;
import b02Propensi.siladu.model.PaketLayanan;
import b02Propensi.siladu.model.Pesanan;
import b02Propensi.siladu.user.model.UserModel;

import java.util.*;


public interface PaketLayananService {
    boolean checkPesananBelumBayar(UserModel user, UUID pesanan);
    boolean checkPesananStatusPembayaran(UserModel user, UUID pesanan);
    boolean checkPesananDiproses(UserModel user, UUID pesanan);
    void savePaketLayanan(PaketLayanan paketLayanan);
    PaketLayanan updatePaketLayanan(PaketLayanan paketLayananFromDto);
    PaketLayanan getPaketLayananById(UUID id);
    List<PaketLayanan> getAllPaketLayanan();
    List<PaketLayanan> getAllPaketLayananByLayanan(Layanan layanan);
    void deletePaketLayanan(PaketLayanan paketLayanan);
    boolean isNamaPaketExist(String namaPaket, String namaLayanan);
    List<PaketLayanan> searchPaketLayanan(String namaPaket, Layanan layanan) ;
    public Map<String, Double> getRatingLayanan();
    public Map<String, Integer> getLayananPeserta();
    public Map<String, Integer> getLayananPesertaFilter(String layanan);
    public Map<String, Double> getRatingLayananFilter(String layanan);
    Long[] getPendapatanPerBulan(int Tahun);
    public Set<UserModel> getAllUserPesanPaket();
}
