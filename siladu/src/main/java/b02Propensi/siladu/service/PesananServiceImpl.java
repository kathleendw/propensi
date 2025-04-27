package b02Propensi.siladu.service;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import b02Propensi.siladu.model.PaketLayanan;
import b02Propensi.siladu.model.Pesanan;
import b02Propensi.siladu.model.Pembayaran;
import b02Propensi.siladu.user.model.UserModel;
import b02Propensi.siladu.repository.PaketLayananDb;
import b02Propensi.siladu.repository.PesananDb;
import b02Propensi.siladu.service.PembayaranService;
import java.util.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;


@Service
public class PesananServiceImpl implements PesananService{
    @Autowired
    PesananDb pesananDb; 
    @Autowired
    PembayaranService pembayaranService;

    public void savePesanan(Pesanan pesanan){
        pesananDb.save(pesanan);
    }

    @Override
    public  Pesanan getPesananById(UUID id){
        for (Pesanan pesanan : getAllPesanan()) {
            if (pesanan.getIdPesanan().equals(id)) {
                return pesanan;
            }
        }
        return null;
    }
    @Override
    public List<Pesanan> getAllPesanan() {
        return pesananDb.findAll();
    
    }
    @Override
    public List<Pesanan> getAllPesananPaketLayanan() {
        return pesananDb.findByJenisPesanan("LAYANAN");
    }
    @Override
    public List<Pesanan> getAllPesananTraining() {
        return pesananDb.findByJenisPesanan("TRAINING");
    }
    @Override
    public List<Pesanan> getAllPesananEvent() {
        return pesananDb.findByJenisPesanan("EVENT");
    }
    @Override
    public Pesanan updateStatusPesanan(Pesanan pesananFromDto){
        Pesanan pesanan = getPesananById(pesananFromDto.getIdPesanan());
        if (pesanan != null) {
            pesanan.setIdPesanan(pesananFromDto.getIdPesanan());
            pesanan.setStatusPesanan(pesananFromDto.getStatusPesanan());
            pesanan.setIdPembayaran(pesananFromDto.getIdPembayaran());
            pesananDb.save(pesanan);
        }
        return pesanan;
    }
    @Override
    public List<Pesanan> findPesananBelumBayarLewat24Jam() {
      LocalDateTime now = LocalDateTime.now();
      List<Pesanan> list = new ArrayList<>();
        for (Pesanan pesanan : getAllPesanan()) {
            if (pesanan.getStatusPesanan().equals("BELUM_BAYAR")) {
                LocalDateTime waktuPesanan = pesanan.getWaktuPesanan().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
                long hoursDifference = ChronoUnit.HOURS.between(waktuPesanan, now);
                System.out.print(hoursDifference);
                if (hoursDifference >= 24) {
                    list.add(pesanan);
                }
            }
        }
        return list;
    }
    // @Override
    // public List<Pesanan> findMenungguPembayaranKembaliLewat24Jam() {
    //     LocalDateTime now = LocalDateTime.now();
    //     List<Pesanan> list = new ArrayList<>();
    //     for (Pesanan pesanan : getAllPesanan()) {
    //         if (pesanan.getStatusPesanan().equals("STATUS_PEMBAYARAN")) {
    //             for (Pembayaran pembayaran : pembayaranService.getAllPembayaran()) {
    //                 if (pesanan.getIdPembayaran().equals(pembayaran.getId()) && pembayaran.getStatusPembayaran().equals("MENUNGGU_PEMBAYARAN_KEMBALI")) {
    //                     LocalDateTime waktuKonfirmasi = pesanan.getWaktuKonfirmasi().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    //                     long hoursDifference = ChronoUnit.HOURS.between(waktuKonfirmasi, now);
    //                     if (hoursDifference > 24) {
    //                         list.add(pesanan);
    //                     }
    //                 }
    //             }
    //         }
    //     }
    //     return list;
    // }
    @Override
    public  void deletePesanan(Pesanan pesanan) {
        pesananDb.delete(pesanan);
    }
    

    
}
