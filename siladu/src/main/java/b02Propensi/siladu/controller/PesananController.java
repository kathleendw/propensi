package b02Propensi.siladu.controller;

import java.io.IOException;
import java.security.Principal;

import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import java.util.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

import b02Propensi.siladu.DTO.PembayaranMapper;
import b02Propensi.siladu.DTO.PesananMapper;
import b02Propensi.siladu.DTO.request.CreatePaketLayananRequestDTO;
import b02Propensi.siladu.DTO.request.CreatePembayaranRequestDTO;
import b02Propensi.siladu.model.Pembayaran;
import b02Propensi.siladu.DTO.request.UpdatePembayaranRequestDTO;
import b02Propensi.siladu.model.Pesanan;
import b02Propensi.siladu.service.PembayaranService;
import b02Propensi.siladu.service.PesananService;
import b02Propensi.siladu.user.model.UserModel;
import b02Propensi.siladu.user.service.EmailSenderService;
import b02Propensi.siladu.user.service.UserService;
import jakarta.validation.Valid;

@Controller
public class PesananController {

    @Autowired
    UserService userService;

    @Autowired
    PesananService pesananService;

    @Autowired
    PembayaranMapper pembayaranMapper;

    @Autowired
    PesananMapper pesananMapper;

    @Autowired
    PembayaranService pembayaranService;

    @GetMapping("pesanan")
    public String pesanan(Model model, Principal principal, RedirectAttributes redirectAttributes) {

        UserModel user = userService.findByEmail(principal.getName());
  
     
        model.addAttribute("user", user);
        model.addAttribute("userRole", user.getRole().name());
        model.addAttribute("listPesanan", user.getListPesanan());
        model.addAttribute("listPembayaran", pembayaranService.getAllPembayaran());
        //System.out.println("LIST BAYAR: " + user.getListPembayaran());
        String successMessage = (String) redirectAttributes.getFlashAttributes().get("successMessage");
    if (successMessage != null) {
        model.addAttribute("successMessage", successMessage);
    }
         
        return "track-pesanan";
    }
    private String formatHarga(double harga) {
        if (harga < 1000) {
            return String.format("%.0f", harga);
        } else {
            return String.format("%,.3f", harga / 1000.0).replace(",", ".");
        }
    }
    @GetMapping("pesanan/{id}/belum-bayar/detail")
    public String detailBelumBayar(@PathVariable("id") UUID id,Model model, Principal principal) {
        
        UserModel user = userService.findByEmail(principal.getName());
        model.addAttribute("user", user);
        model.addAttribute("userRole", user.getRole().name());
        model.addAttribute("listPesanan", user.getListPesanan());
        var pesanan = pesananService.getPesananById(id);
        model.addAttribute("pesanan", pesanan);
        model.addAttribute("hargaPesanan", formatHarga(pesanan.getHargaPesanan()));
        return "detail-belum-bayar";
    }
    @GetMapping("pesanan/{id}/bayar")
    public String Pembayaran(@PathVariable("id") UUID id,Model model, Principal principal) {
        var pembayaranDTO = new CreatePembayaranRequestDTO();
    
        model.addAttribute("pembayaranDTO", pembayaranDTO);

        UserModel user = userService.findByEmail(principal.getName());
        model.addAttribute("user", user);
        model.addAttribute("userRole", user.getRole().name());
        model.addAttribute("id", id);
       

        return "form-pembayaran";
    }

      @PostMapping("pesanan/bayar")
    public String addPembayaran(@Valid @ModelAttribute CreatePembayaranRequestDTO pembayaranDTO, BindingResult bindingResult,
   @RequestParam("file") MultipartFile file, @RequestParam("id") UUID id,   Model model, RedirectAttributes redirectAttributes
   , Principal principal) {
    try {
        UserModel user = userService.findByEmail(principal.getName());
        var userRole = user.getRole().name();
       
        var pesanan= pesananService.getPesananById(id);
        var pembayaran = pembayaranMapper.createPembayaranRequestDTOtoPembayaran(pembayaranDTO);
        Date currentTime = new Date();
        if(pembayaran.getNomorTelepon().equals("")){
            pembayaran.setNomorTelepon("-");
        }
        pesanan.setStatusPesanan("DIPROSES");
        pesanan.setIdPesanan(id);
        String filename = StringUtils.cleanPath(file.getOriginalFilename());
        if(filename.contains("..")){
            System.out.println("Not a valid file");
            return "failure";
        }
        byte[] imageData = file.getBytes();
        pembayaran.setBuktiPembayaran(imageData);
        pembayaran.setJenisKegiatan(pesanan.getJenisPesanan());
        pembayaran.setStatusPembayaran("PERLU KONFIRMASI");
        pembayaran.setIdPesanan(pesanan.getIdPesanan());
        if(pesanan.getJenisPesanan().equals("PREMIUM")){
            pembayaran.setTotalBayar(100000);
        }
        else{
            pembayaran.setTotalBayar(pesanan.getHargaPesanan());
        }
       
        pembayaran.setWaktuPembayaran(currentTime);
        
       
        pembayaranService.savePembayaran(pembayaran);
        pesanan.setIdPembayaran(pembayaran.getId());
        pesananService.updateStatusPesanan(pesanan);
        System.out.print("hello" + pembayaran.getId());
        model.addAttribute("user", user);
        model.addAttribute("userRole", userRole);
        redirectAttributes.addAttribute("successMessage", " Bukti Pembayaran Berhasil Diunggah");
        redirectAttributes.addAttribute("activeSection", "Diproses");

        return "redirect:/pesanan";
     }
     catch (IOException e) {
        e.printStackTrace();
        // Redirect dengan pesan gagal
        redirectAttributes.addAttribute("errorMessage",
                "Gagal mengunggah bukti. Terjadi kesalahan: " + e.getMessage());
        return "redirect:/pesanan";
    }
     

    }
    @GetMapping("pesanan/{id}/diproses/detail")
    public String detailDiproses(@PathVariable("id") UUID id,Model model, Principal principal) {
        
        UserModel user = userService.findByEmail(principal.getName());
        var pesanan =  pesananService.getPesananById(id);
        model.addAttribute("user", user);
        model.addAttribute("userRole", user.getRole().name());
        model.addAttribute("pesanan", pesanan);
        model.addAttribute("hargaPesanan", formatHarga(pesanan.getHargaPesanan()));
        model.addAttribute("pembayaran", pembayaranService.getPembayaranById(pesanan.getIdPembayaran()));

        return "detail-diproses";
    }

    @GetMapping("pesanan/{id}/status-pembayaran/detail")
    public String detailStatusPembayaran(@PathVariable("id") UUID id, Model model, Principal principal) {
        UserModel user = userService.findByEmail(principal.getName());
        var pesanan =  pesananService.getPesananById(id);
        model.addAttribute("user", user);
        model.addAttribute("userRole", user.getRole().name());
        model.addAttribute("pesanan", pesanan);
        model.addAttribute("hargaPesanan", formatHarga(pesanan.getHargaPesanan()));
        model.addAttribute("pembayaran", pembayaranService.getPembayaranById(pesanan.getIdPembayaran()));

        return "detail-status-pembayaran";
    }


    @Scheduled(fixedRate = 3600000)
    public void scheduledTask() {
        checkStatusPesanan();
    }
    public void checkStatusPesanan() {
        // Ambil semua pesanan yang belum dibayar dan waktu pesanannya sudah lebih dari 24 jam
        List<Pesanan> pesananList = pesananService.findPesananBelumBayarLewat24Jam();
        
        // Ubah status pesanan yang sudah lewat dari 24 jam menjadi "DIBATALKAN"
        for (Pesanan pesanan : pesananList) {
            pesananService.deletePesanan(pesanan);
        }
    }

        // public void checkStatusPembayaran() {
    //     List<Pesanan> pesananList = pesananService.findMenungguPembayaranKembaliLewat24Jam();
    //     for (Pesanan pesanan : pesananList) {
    //         for (Pembayaran pembayaran : pembayaranService.getAllPembayaran()) {
    //             if (pesanan.getIdPembayaran().equals(pembayaran.getId())) {
    //                 pembayaran.setStatusPembayaran("GAGAL");
    //             }
    //         }
    //     }
    // }

    @GetMapping("pesanan/{idPesanan}/bayar/{idPembayaran}")
    public String formUpdatePembayaran(@PathVariable("idPesanan") UUID idPesanan, 
    @PathVariable("idPembayaran") UUID idPembayaran,
    Model model, Principal principal) {
        var pembayaran = pembayaranService.getPembayaranById(idPembayaran);
        var pembayaranDTO = pembayaranMapper.pembayaranToUpdatePembayaranRequestDTO(pembayaran);
        System.out.print("halo" +pembayaranDTO.getNomorTelepon());
        model.addAttribute("idPesanan", idPesanan);
        model.addAttribute("pembayaranDTO", pembayaranDTO);

        UserModel user = userService.findByEmail(principal.getName());
        model.addAttribute("userRole", user.getRole().name());
        model.addAttribute("user", user);
        return "form-update-pembayaran";
    }

    @PostMapping("pesanan/{idPesanan}/bayar")
    public String updatePembayaran(@PathVariable("idPesanan") UUID idPesanan, @Valid @ModelAttribute UpdatePembayaranRequestDTO pembayaranDTO, BindingResult bindingResult,
    @RequestParam("file") MultipartFile file, Model model, RedirectAttributes redirectAttributes, Principal principal) {
        try {
            var pesanan = pesananService.getPesananById(idPesanan);
            var pembayaranFromDTO = pembayaranMapper.updatePembayaranRequestDTOtoPembayaran(pembayaranDTO);
            var pembayaran = pembayaranService.updatePembayaran(pembayaranFromDTO);

            Date currentTime = new Date();
            if(pembayaran.getNomorTelepon().equals("0")){
                pembayaran.setNomorTelepon("-");
            }
            pesanan.setStatusPesanan("DIPROSES");
            pesanan.setIdPesanan(idPesanan);
            if(file != null && !file.isEmpty()) {
                String fileName = StringUtils.cleanPath(file.getOriginalFilename());
                String contentType = file.getContentType();

                if(fileName.contains("..")) {
                    System.out.println("not a valid file");
                } else {
                     // Convert MultipartFile to byte array
                    byte[] imageData = file.getBytes();
                    pembayaran.setBuktiPembayaran(imageData);
                    
                    if( !contentType.equals("image/jpeg") && !contentType.equals("image/png") ) {
                        redirectAttributes.addFlashAttribute("errorMessage", "Invalid file type. Only JPG, PNG, and JPEG files are allowed.");
                        return "redirect:/event/update/{idEvent}"; 
                    }
                }
            }else{
                pembayaran.setBuktiPembayaran(pembayaranService.getPembayaranById(pesanan.getIdPembayaran()).getBuktiPembayaran());
            }

          
            pembayaran.setJenisKegiatan(pesanan.getJenisPesanan());
            pembayaran.setStatusPembayaran("PERLU KONFIRMASI");
            if (pesanan.getJenisPesanan().equals("LAYANAN")) {
                pembayaran.setTotalBayar(pesanan.getPaketLayanan().getHargaPaket());
            } 
            if (pesanan.getJenisPesanan().equals("TRAINING")) {
                pembayaran.setTotalBayar(pesanan.getTraining().getHargaTraining());
            } 
            pembayaran.setWaktuPembayaran(currentTime);
        
            pembayaranService.savePembayaran(pembayaran);
            pesanan.setIdPembayaran(pembayaran.getId());
            pesananService.updateStatusPesanan(pesanan);
            model.addAttribute("idPesanan", idPesanan);

            UserModel user = userService.findByEmail(principal.getName());
            model.addAttribute("user", user);
            model.addAttribute("userRole", user.getRole().name());
            redirectAttributes.addAttribute("successMessage", " Bukti pembayaran berhasil diunggah.");
            redirectAttributes.addAttribute("activeSection", "Diproses");

            return "redirect:/pesanan";
        }
        catch (IOException e) {
            e.printStackTrace();
            // Redirect dengan pesan gagal
            redirectAttributes.addAttribute("errorMessage",
                    "Gagal mengunggah bukti. Terjadi kesalahan: " + e.getMessage());
            return "redirect:/pesanan";
        }
    }

    @GetMapping("pesanan/{idPesanan}/hapus")
    public String deletePesanan(@PathVariable("idPesanan") String idPesanan, Model model) {
        UUID uuid = UUID.fromString(idPesanan);
        var pesanan = pesananService.getPesananById(uuid);
        pesananService.deletePesanan(pesanan);
        return "redirect:/pesanan";
    }

    @GetMapping("pembayaran/{id}/hapus")
    public String deletePembayaran(@PathVariable("id") String id, Model model) {
        UUID uuid = UUID.fromString(id);
        var pembayaran = pembayaranService.getPembayaranById(uuid);
        pembayaranService.deletePembayaran(pembayaran);
        return "redirect:/pesanan";
    }



}
