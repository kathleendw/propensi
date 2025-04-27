package b02Propensi.siladu.service;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import b02Propensi.siladu.model.Pembayaran;
import b02Propensi.siladu.model.Pesanan;
import b02Propensi.siladu.repository.PembayaranDb;

import java.util.*;

@Service
public class PembayaranServiceImpl implements PembayaranService {
     @Autowired
    PembayaranDb pembayaranDb;  

    public void savePembayaran(Pembayaran pembayaran){
        pembayaranDb.save(pembayaran);
    }
    @Override
    public  Pembayaran getPembayaranById(UUID id){
        for (Pembayaran pembayaran : getAllPembayaran()) {
            if (pembayaran.getId().equals(id)) {
                return pembayaran;
            }
        }
        return null;
    }
    @Override
    public List<Pembayaran> getAllPembayaran() {
        return pembayaranDb.findAll();
    
    }

    @Override
    public Pembayaran updatePembayaran(Pembayaran pembayaranFromDto) {
        Pembayaran pembayaran = getPembayaranById(pembayaranFromDto.getId());
        if (pembayaran != null){
            pembayaran.setNamaPemilik(pembayaranFromDto.getNamaPemilik());
            pembayaran.setMetodePembayaran(pembayaranFromDto.getMetodePembayaran());
            pembayaran.setNomorTelepon(pembayaranFromDto.getNomorTelepon());
            pembayaranDb.save(pembayaran);
        }
        return pembayaran;
    }

    @Override
    public void deletePembayaran(Pembayaran pembayaran) {
        pembayaranDb.delete(pembayaran);
    }

    
    @Override
    public Pembayaran confirmPembayaran(UUID id, String status) {
        Pembayaran pembayaran = getPembayaranById(id);

        if (status.equals("MENUNGGU PEMBAYARAN KEMBALI")){
            pembayaran.setStatusPembayaran("MENUNGGU PEMBAYARAN KEMBALI");
        } else if (status.equals("PERLU KONFIRMASI")){
            pembayaran.setStatusPembayaran("PERLU KONFIRMASI");
        } else if (status.equals("SUKSES")){
            pembayaran.setStatusPembayaran("SUKSES");
        } else if (status.equals("GAGAL")){
            pembayaran.setStatusPembayaran("GAGAL");
        }
        
        savePembayaran(pembayaran);

        return pembayaran;
    }

    @Override
    public long getTotalIncomeFromPremiumSuccessPayments() {
        Long totalIncome = pembayaranDb.sumTotalBayarForPremiumSuccess();
        System.out.println(totalIncome);
        return totalIncome != null ? totalIncome : 0; // Handling null in case there are no matching records
    }
}
