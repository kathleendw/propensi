package b02Propensi.siladu.controller;

import b02Propensi.siladu.DTO.PaketLayananMapper;
import b02Propensi.siladu.DTO.PesananMapper;
import b02Propensi.siladu.DTO.request.CreatePaketLayananRequestDTO;
import b02Propensi.siladu.DTO.request.CreatePesananRequestDTO;
import b02Propensi.siladu.DTO.request.UpdatePaketLayananRequestDTO;
import b02Propensi.siladu.model.Layanan;
import b02Propensi.siladu.model.PaketLayanan;
import b02Propensi.siladu.repository.PaketLayananDb;
import b02Propensi.siladu.service.LayananService;
import b02Propensi.siladu.service.PaketLayananService;
import b02Propensi.siladu.service.PesananService;
import jakarta.validation.Valid;

import org.springframework.ui.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import java.util.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

import b02Propensi.siladu.user.model.UserModel;
import b02Propensi.siladu.user.service.UserService;


@Controller
public class PaketLayananController {
    @Autowired
    private LayananService layananService;
    @Autowired
    private PaketLayananMapper paketLayananMapper;
    @Autowired
    private PaketLayananService paketLayananService;
    @Autowired
    private UserService userService;
    @Autowired
    private PesananMapper pesananMapper;
    @Autowired
    private PesananService pesananService;

     @GetMapping("paketlayanan/tambah")
    public String formAddPaketLayanan(Model model, Principal principal) {
        var paketLayananDTO = new CreatePaketLayananRequestDTO();
        model.addAttribute("listNamaLayanan", layananService.generateHashMapNamaLayanan());
        model.addAttribute("paketLayananDTO", paketLayananDTO);

        UserModel user = userService.findByEmail(principal.getName());
        model.addAttribute("user", user);
        model.addAttribute("userRole", user.getRole().name());
        return "form-create-paket-layanan";
    }
     @PostMapping("paketlayanan/tambah")
    public String addPaketLayanan(@Valid @ModelAttribute CreatePaketLayananRequestDTO paketLayananDTO, BindingResult bindingResult,
            Model model, RedirectAttributes redirectAttributes) {
        
        var paketLayanan = paketLayananMapper.createPaketLayananRequestDTOtoPaketLayanan(paketLayananDTO);
       
        if (paketLayananService.isNamaPaketExist(paketLayananDTO.getNamaPaket(), paketLayananDTO.getLayanan().getNamaLayanan())) {
            redirectAttributes.addAttribute("errorMessage", " Nama Paket Layanan Sudah Pernah Ditambahkan");
            return "redirect:/layanan/" +paketLayanan.getLayanan().getIdLayanan();
        }
        
        paketLayanan.setKategoriLayanan(paketLayanan.getLayanan().getKategoriLayanan());
        paketLayanan.setRating(0);
        paketLayananService.savePaketLayanan(paketLayanan);
        redirectAttributes.addAttribute("successMessage", " Paket Layanan Berhasil Ditambahkan");


        return "redirect:/layanan/" +paketLayanan.getLayanan().getIdLayanan();
    }
    @GetMapping("paketlayanan/{id}/ubah")
    public String formUpdatePaketLayanan(@PathVariable("id") UUID id, Model model, Principal principal) {
        var paketLayanan = paketLayananService.getPaketLayananById(id);
       
        var paketLayananDTO = paketLayananMapper.paketLayananToUpdatePaketLayananRequestDTO(paketLayanan);
       
        model.addAttribute("listNamaLayanan", layananService.generateHashMapNamaLayanan());
       
        model.addAttribute("paketLayananDTO", paketLayananDTO);

        UserModel user = userService.findByEmail(principal.getName());
        model.addAttribute("user", user);
        model.addAttribute("userRole", user.getRole().name());
        
        return "form-update-paket-layanan";
    }

    @PostMapping("paketlayanan/ubah")
    public String updatePaketLayanan(@Valid @ModelAttribute UpdatePaketLayananRequestDTO paketLayananDTO, BindingResult bindingResult,
            Model model, RedirectAttributes redirectAttributes) {
                System.out.print("lalalla"+paketLayananDTO);
   
        var paketLayananFromDto = paketLayananMapper.updatePaketLayananRequestDTOToPaketLayanan(paketLayananDTO);
        var currentPaketLayanan=paketLayananService.getPaketLayananById(paketLayananDTO.getId());
        
        if(currentPaketLayanan.getNamaPaket().toLowerCase().equals(paketLayananDTO.getNamaPaket().toLowerCase())
        && currentPaketLayanan.getLayanan().getNamaLayanan().equals(paketLayananDTO.getLayanan().getNamaLayanan())){
            var paketLayanan =paketLayananService.updatePaketLayanan(paketLayananFromDto);
        
        redirectAttributes.addAttribute("successMessage", " Paket Layanan Berhasil Diubah");
        model.addAttribute("id", paketLayanan.getId());

        return "redirect:/layanan/" +paketLayanan.getLayanan().getIdLayanan();
        }
        if (paketLayananService.isNamaPaketExist(paketLayananDTO.getNamaPaket(), paketLayananDTO.getLayanan().getNamaLayanan())) {
            redirectAttributes.addAttribute("errorMessage", " Nama Paket Layanan Sudah Pernah Ditambahkan pada Layanan " + paketLayananDTO.getLayanan().getNamaLayanan());
            return "redirect:/layanan/" +currentPaketLayanan.getLayanan().getIdLayanan();
        }
        var paketLayanan =paketLayananService.updatePaketLayanan(paketLayananFromDto);
        
        redirectAttributes.addAttribute("successMessage", " Paket Layanan Berhasil Diubah");
        model.addAttribute("id", paketLayanan.getId());

        return "redirect:/layanan/" +paketLayanan.getLayanan().getIdLayanan();
    }
    @GetMapping("paketlayanan/{id}/delete")
    public String deletePaketLayanan(@PathVariable("id") UUID id, Model model,RedirectAttributes redirectAttributes) {
        var paketLayanan = paketLayananService.getPaketLayananById(id);
        var layanan = paketLayananService.getPaketLayananById(id).getLayanan();
        paketLayananService.deletePaketLayanan(paketLayanan);
        redirectAttributes.addAttribute("successMessage", " Paket Layanan Berhasil Dihapus");
        return "redirect:/layanan/" +layanan.getIdLayanan();
    }
    private String formatHarga(double harga) {
        if (harga < 1000) {
            return String.format("%.0f", harga);
        } else {
            return String.format("%,.3f", harga / 1000.0).replace(",", ".");
        }
    }
    @GetMapping("paketlayanan/{id}")
    public String detailBarang(@PathVariable("id") UUID id , Model model, Principal principal, RedirectAttributes redirectAttributes) {
      
        var paketLayanan = paketLayananService.getPaketLayananById(id);

        var paketLayananDto = paketLayananMapper.paketLayananToReadPaketLayananResponseDTO(paketLayanan);
        var manfaat = paketLayanan.getManfaat().replace("\n", ",");
        model.addAttribute("paketLayanan", paketLayananDto);
        model.addAttribute("listManfaat", Arrays.asList(manfaat.split(",")));
        model.addAttribute("hargaDiskon", formatHarga(paketLayananDto.getHargaPaket()*80/100));
        model.addAttribute("harga", formatHarga(paketLayananDto.getHargaPaket()));
        

        String errorMessage = (String) redirectAttributes.getFlashAttributes().get("errorMessage");
        UserModel user = userService.findByEmail(principal.getName());
        model.addAttribute("user", user);
        model.addAttribute("userRole", user.getRole().name());
        if (errorMessage != null) {
            model.addAttribute("errorMessage", errorMessage);
        }
        return "detail-paket-layanan";
    }

    @GetMapping("layanan/{id}/paketlayanan")
    public String cariPaketLayanan(@PathVariable("id") long id, Model model, @RequestParam(name = "search", required = false) String search, Principal principal) {
        List<PaketLayanan> listPaketLayanan;
        if (search != null && !search.isEmpty()) {
            
            listPaketLayanan = paketLayananService.searchPaketLayanan(search, layananService.getLayananByIdLayanan(id));
        } else {
            listPaketLayanan = paketLayananService.getAllPaketLayananByLayanan(layananService.getLayananByIdLayanan(id));
        }
        model.addAttribute("listPaketLayanan", listPaketLayanan);
        model.addAttribute("namaLayanan", layananService.getLayananByIdLayanan(id).getNamaLayanan());
        model.addAttribute("idLayanan", layananService.getLayananByIdLayanan(id).getIdLayanan());

        UserModel user = userService.findByEmail(principal.getName());
        model.addAttribute("userRole", user.getRole().name());
        model.addAttribute("user", user);
        return "viewall-paket-layanan";
    }

    @GetMapping("paketlayanan/{id}/pesan")
    public String formPesanan(@PathVariable("id") UUID id,Model model, Principal principal) {
        var pesananDTO = new CreatePesananRequestDTO();
        model.addAttribute("paketLayananDTO", pesananDTO);

        UserModel user = userService.findByEmail(principal.getName());
        model.addAttribute("user", user);
        model.addAttribute("userRole", user.getRole().name());
        return "redirect:/paketlayanan/" +id;
    }

    @PostMapping("paketlayanan/{id}/pesan")
    public String pesanPaketLayanan(@PathVariable("id") UUID id, Principal principal, @Valid @ModelAttribute CreatePesananRequestDTO pesananDTO, BindingResult bindingResult,
            Model model, RedirectAttributes redirectAttributes) {
                UserModel user = userService.findByEmail(principal.getName());
                var userRole = user.getRole().name();
               
        model.addAttribute("userRole", userRole);
        if(paketLayananService.checkPesananBelumBayar(user, id)){
            redirectAttributes.addFlashAttribute("errorMessage", " Anda sudah pernah memesan layanan ini. Silahkan selesaikan pembayaran.");
            return "redirect:/paketlayanan/" +id;
        }else if(paketLayananService.checkPesananDiproses(user, id)){
            redirectAttributes.addFlashAttribute("errorMessage", " Anda sudah pernah memesan layanan ini. Silahkan menunggu pembayaran dikonfirmasi.");
            return "redirect:/paketlayanan/" +id;
        }else if(paketLayananService.checkPesananStatusPembayaran(user, id)){
            redirectAttributes.addFlashAttribute("errorMessage", " Anda sudah pernah memesan layanan ini.");
            return "redirect:/paketlayanan/" +id;
        }
        var pesanan = pesananMapper.createPesananRequestDTOtoPesanan(pesananDTO);
        pesanan.setStatusPesanan("BELUM_BAYAR");
        pesanan.setUser(user);
        Date currentTime = new Date();
        pesanan.setPaketLayanan(paketLayananService.getPaketLayananById(id));
        pesanan.setWaktuPesanan(currentTime);
        pesanan.setJenisPesanan("LAYANAN");
        if(userRole.equals("MEMBER_PREMIUM")){
            pesanan.setHargaPesanan(paketLayananService.getPaketLayananById(id).getHargaPaket()*80/100);
        }else{
            pesanan.setHargaPesanan(paketLayananService.getPaketLayananById(id).getHargaPaket());
        }
       
        pesananService.savePesanan(pesanan);
     
        redirectAttributes.addFlashAttribute("successMessage", " Pesanan berhasil dibuat. Mohon selesaikan pembayaran dalam waktu 24 jam.");
        return "redirect:/pesanan";
    }
}
