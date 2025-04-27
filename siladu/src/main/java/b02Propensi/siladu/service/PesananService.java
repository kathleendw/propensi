package b02Propensi.siladu.service;

import b02Propensi.siladu.model.PaketLayanan;
import b02Propensi.siladu.model.Pesanan;
import b02Propensi.siladu.user.model.UserModel;
import java.util.*;

public interface PesananService {
    void savePesanan(Pesanan pesanan);
    public  Pesanan getPesananById(UUID id);
    List<Pesanan> getAllPesanan();
    Pesanan updateStatusPesanan(Pesanan pesanan);
    List<Pesanan> findPesananBelumBayarLewat24Jam();
    // List<Pesanan> findMenungguPembayaranKembaliLewat24Jam();
    void deletePesanan(Pesanan pesanan);
    List<Pesanan> getAllPesananPaketLayanan();
    List<Pesanan> getAllPesananTraining();
    List<Pesanan> getAllPesananEvent();
}
