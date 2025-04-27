package b02Propensi.siladu.service;

import b02Propensi.siladu.model.Pembayaran;
import b02Propensi.siladu.model.Pesanan;

import java.util.*;
public interface PembayaranService {
      void savePembayaran(Pembayaran pembayaran);
      public  Pembayaran getPembayaranById(UUID id);
      List<Pembayaran> getAllPembayaran();
      Pembayaran confirmPembayaran(UUID id, String status);
      
    
      Pembayaran updatePembayaran(Pembayaran pembayaranFromDto);
      void deletePembayaran(Pembayaran pembayaran);

      long getTotalIncomeFromPremiumSuccessPayments();
}
