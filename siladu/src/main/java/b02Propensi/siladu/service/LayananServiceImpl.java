package b02Propensi.siladu.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import b02Propensi.siladu.model.Layanan;
import b02Propensi.siladu.repository.LayananDb;

import java.util.*;

@Service
public class LayananServiceImpl implements LayananService {
     @Autowired
    LayananDb layananDb;
    @Override
    public List<Layanan> getAllLayanan() {
        return layananDb.findAll();
    }
    @Override
    public HashMap<Long,String> generateHashMapNamaLayanan(){
        HashMap<Long,String> listNamaLayanan = new HashMap<>();
        for(Layanan layanan : getAllLayanan()){
            listNamaLayanan.put(layanan.getIdLayanan(),layanan.getNamaLayanan());
        }
      
        return listNamaLayanan;
    }

    @Override
    public Layanan getLayananByIdLayanan(Long idLayanan){
        return layananDb.findByIdLayanan(idLayanan);
    }
}
