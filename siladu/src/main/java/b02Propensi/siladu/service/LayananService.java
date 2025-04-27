package b02Propensi.siladu.service;

import java.util.*;


import b02Propensi.siladu.model.Layanan;

public interface LayananService {
    HashMap<Long,String> generateHashMapNamaLayanan();
    Layanan getLayananByIdLayanan(Long idLayanan);
    List<Layanan> getAllLayanan();
 
    
}
