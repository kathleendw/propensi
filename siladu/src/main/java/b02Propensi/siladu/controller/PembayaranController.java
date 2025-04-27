package b02Propensi.siladu.controller;

import java.security.Principal;
import java.util.*;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import b02Propensi.siladu.DTO.request.ConfirmPembayaranRequestDTO;
import b02Propensi.siladu.model.Pembayaran;
import b02Propensi.siladu.model.Pesanan;
import b02Propensi.siladu.service.PembayaranService;
import b02Propensi.siladu.service.PesananService;
import b02Propensi.siladu.user.model.UserModel;
import b02Propensi.siladu.user.service.EmailSenderService;
import b02Propensi.siladu.user.service.UserService;
import jakarta.validation.Valid;


@Controller
public class PembayaranController {
    @Autowired
    private PembayaranService pembayaranService;
    
    @Autowired
    private UserService userService;

    @Autowired
    private PesananService pesananService;

    
    @Autowired
    EmailSenderService emailSenderService;

    @GetMapping("/ringkasan-pembayaran")
    public String getAllPembayaran(Model model, Principal principal) {
        UserModel user = userService.findByEmail(principal.getName());

        List<Pembayaran> listPembayaran = pembayaranService.getAllPembayaran();

        Map<UUID, String> base64ImagesMap = new HashMap<>();

        // Convert byte array to Base64 for each event and store in the Map
        listPembayaran.forEach(pembayaran -> {
            String base64Image = Base64.getEncoder().encodeToString(pembayaran.getBuktiPembayaran());
            base64ImagesMap.put(pembayaran.getId(), base64Image);
            System.out.println(pembayaran.getId());
        });

        model.addAttribute("base64ImagesMap", base64ImagesMap);
        model.addAttribute("pembayaranList", listPembayaran);

        model.addAttribute("userRole", user.getRole().name());
        model.addAttribute("user", user);
        return "ringkasan-pembayaran-2";
    }

    @PostMapping("/confirm-pembayaran-valid")
    public String confirmPembayaranValid(@ModelAttribute ConfirmPembayaranRequestDTO requestDTO, BindingResult bindingResult,
        Model model, RedirectAttributes redirectAttributes, Principal principal) {

        System.out.println("MASUK SINI NGGA");

        var pesanan = pesananService.getPesananById(requestDTO.getIdPesanan());
        var pembayaran= pembayaranService.getPembayaranById(pesanan.getIdPembayaran());

        pembayaran.setStatusPembayaran("SUKSES");
        pesanan.setStatusPesanan("STATUS_PEMBAYARAN");
    
        pembayaranService.savePembayaran(pembayaran);
        pesananService.savePesanan(pesanan);

        UserModel user = userService.findByEmail(principal.getName());

        if (pesanan.getJenisPesanan().equals("PREMIUM")){
            userService.updatePremium(pesanan.getUser());
            System.out.println("masuk update");
        }

        if(pesanan.getJenisPesanan().equals("EVENT")) {
            System.out.println("Anda Telah Terdaftar Ke Event ini");
            sendEmailEventDetail(pesanan.getUser(), pesanan);
        }
        
        sendEmailConfirmPayment(pesanan.getUser(), false, false);

        model.addAttribute("user", user);
        model.addAttribute("userRole", user.getRole().name());

        return "redirect:/ringkasan-pembayaran";
    }

    @PostMapping("/confirm-pembayaran-invalid")
    public String confirmPembayaranInvalid(@ModelAttribute ConfirmPembayaranRequestDTO requestDTO, BindingResult bindingResult,
        Model model, RedirectAttributes redirectAttributes, Principal principal) {

        var pesanan = pesananService.getPesananById(requestDTO.getIdPesanan());
        var pembayaran= pembayaranService.getPembayaranById(pesanan.getIdPembayaran());
        
        UserModel user = userService.findByEmail(principal.getName());

        if (pesanan.getWaktuKonfirmasi() == null){
            pembayaran.setStatusPembayaran("MENUNGGU_PEMBAYARAN_KEMBALI");
            pesanan.setStatusPesanan("STATUS_PEMBAYARAN");
            
            Date currentTime = new Date();
            pesanan.setWaktuKonfirmasi(currentTime);
        
            pembayaranService.savePembayaran(pembayaran);
            pesananService.savePesanan(pesanan);
    
            
            sendEmailConfirmPayment(pesanan.getUser(), true, false);
        } else {
            pembayaran.setStatusPembayaran("GAGAL");
            pesanan.setStatusPesanan("STATUS_PEMBAYARAN");
        
            pembayaranService.savePembayaran(pembayaran);
            pesananService.savePesanan(pesanan);
    
            sendEmailConfirmPayment(pesanan.getUser(), true, true);
        }


        model.addAttribute("user", user);
        model.addAttribute("userRole", user.getRole().name());

        return "redirect:/ringkasan-pembayaran";
    }

    //  send confirmation email to member for the payment
    public void sendEmailConfirmPayment(UserModel user, Boolean rejected, Boolean failed){
        String body = "";
        if (!rejected && !failed){
            body = """
                Halo, Bapak/Ibu %s!
    
                Pembayaran Anda telah berhasil dikonfirmasi dan diverifikasi.
    
                Selanjutnya, Anda dapat menikmati produk yang telah dibeli.
    

                Salam,
                Yayasan Komunitas Tangerang Berdaya
                """;
            emailSenderService.sendSimpleEmail(user.getEmail(), "Pembayaran Berhasil", String.format(body, user.getName()));
        } else if (rejected && !failed){
            body = """
                Halo, Bapak/Ibu %s
    
                Pembayaran Anda gagal dikonfirmasi dan diverifikasi.
    
                Silakan upload kembali bukti pembayaran yang valid dalam waktu 24 jam. Jika dalam 24 jam tidak ada pengunggahan bukti pembayaran yang balid, maka pesanan akan hangus.
    

                Salam,
                Yayasan Komunitas Tangerang Berdaya
                """;
            emailSenderService.sendSimpleEmail(user.getEmail(), "Pembayaran Tidak Valid", String.format(body, user.getName()));
        } else if (rejected && failed) {
            body = """
                Halo, Bapak/Ibu %s
    
                Pembayaran Anda sudah gagal dikonfirmasi dan diverifikasi sejumlah dua kali. Oleh karena itu, pesanan Anda hangus.
                
                
                Salam,
                Yayasan Komunitas Tangerang Berdaya
                """;
            emailSenderService.sendSimpleEmail(user.getEmail(), "Pembayaran Gagal", String.format(body, user.getName()));
        }
    
    }

    public void sendEmailEventDetail(UserModel user, Pesanan pesanan){
        String body = "";
        body = """
                Halo, Bapak/Ibu %s!
    
                Anda telah berhasil terdaftar ke event %s.
    
                Selanjutnya, Anda bisa masuk ke grup whatsapp ini untuk info selanjutnya.
    

                Salam,
                Yayasan Komunitas Tangerang Berdaya
                """;
        emailSenderService.sendSimpleEmail(user.getEmail(), "Informasi Event", String.format(body, user.getName(), pesanan.getEvent().getNamaEvent()));

    }


    
}
