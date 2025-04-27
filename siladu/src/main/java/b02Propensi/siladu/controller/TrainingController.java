package b02Propensi.siladu.controller;

import b02Propensi.siladu.DTO.TrainingMapper;
import b02Propensi.siladu.DTO.PesananMapper;
import b02Propensi.siladu.DTO.request.CreateTrainingRequestDTO;
import b02Propensi.siladu.DTO.request.UpdateTrainingRequestDTO;
import b02Propensi.siladu.DTO.response.ReadTrainingResponseDTO;
import b02Propensi.siladu.DTO.request.CreatePesananRequestDTO;
import b02Propensi.siladu.model.Training;
import b02Propensi.siladu.model.Pesanan;
import b02Propensi.siladu.model.Pembayaran;
import b02Propensi.siladu.service.TrainingService;
import b02Propensi.siladu.service.PembayaranService;
import b02Propensi.siladu.service.PesananService;
import jakarta.validation.Valid;

import org.springframework.ui.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import java.util.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import b02Propensi.siladu.user.model.UserModel;
import b02Propensi.siladu.user.service.UserService;
import java.security.Principal;

@Controller
public class TrainingController {
    @Autowired
    private TrainingService trainingService;
    @Autowired
    private TrainingMapper trainingMapper;
    @Autowired
    private UserService userService;
    @Autowired
    private PesananMapper pesananMapper;
    @Autowired
    private PesananService pesananService;
    @Autowired
    private PembayaranService pembayaranService;

    @GetMapping("training")
    public String daftarTraining(Model model, @RequestParam(name = "search", required = false) String search, Principal principal) {
        List<Training> listTraining;
        if (search != null && !search.isEmpty()) {
            listTraining = trainingService.searchTraining(search);
        } else {
            listTraining = trainingService.getAllTraining();
        }
        model.addAttribute("listTraining", listTraining);

        UserModel user = userService.findByEmail(principal.getName());
        model.addAttribute("userRole", user.getRole().name());
        model.addAttribute("user", user);
        return "viewall-training";
    }
    private String formatHarga(double harga) {
        if (harga < 1000) {
            return String.format("%.0f", harga);
        } else {
            return String.format("%,.3f", harga / 1000.0).replace(",", ".");
        }
    }
    @GetMapping("training/{idTraining}")
    public String detailTraining(@PathVariable("idTraining") UUID idTraining, Model model, Principal principal, RedirectAttributes redirectAttributes) {
        var training = trainingService.getTrainingById(idTraining);
        ReadTrainingResponseDTO readTrainingResponseDTO = trainingMapper.trainingToReadTrainingResponseDTO(training);
        model.addAttribute("training", readTrainingResponseDTO);
        model.addAttribute("hargaDiskon", formatHarga(training.getHargaTraining()*80/100));
        model.addAttribute("harga", formatHarga(training.getHargaTraining()));
        String errorMessage = (String) redirectAttributes.getFlashAttributes().get("errorMessage");
        UserModel user = userService.findByEmail(principal.getName());
        model.addAttribute("user", user);
        model.addAttribute("userRole", user.getRole().name());
        if (errorMessage != null) {
            model.addAttribute("errorMessage", errorMessage);
        }
        if(trainingService.unlockedTraining(user, idTraining)){
            return "view-training-unlocked";
        }
        return "view-training";
    }

    @GetMapping("training/tambah")
    public String formAddTraining(Model model, Principal principal) {
        var trainingDTO = new CreateTrainingRequestDTO();
        model.addAttribute("trainingDTO", trainingDTO);

        UserModel user = userService.findByEmail(principal.getName());
        model.addAttribute("userRole", user.getRole().name());
        model.addAttribute("user", user);
        return "form-create-training";
    }

    @PostMapping("training/tambah")
    public String addTraining(@Valid @ModelAttribute CreateTrainingRequestDTO trainingDTO, BindingResult bindingResult,
            Model model, RedirectAttributes redirectAttributes) {
        var training = trainingMapper.createTrainingRequestDTOtoTraining(trainingDTO);
        training.setRatingTraining(0);
        trainingService.saveTraining(training);
        redirectAttributes.addAttribute("successMessage", " Training dengan ID " + training.getIdTraining() + " berhasil ditambahkan.");
        var listTraining = trainingService.getAllTraining(); 
        model.addAttribute("listTraining", listTraining);
        return "redirect:/training";
    }

    @GetMapping("training/{idTraining}/update")
    public String formUpdateTraining(@PathVariable("idTraining") UUID idTraining, Model model, Principal principal) {
        var training = trainingService.getTrainingById(idTraining);
        var trainingDTO = trainingMapper.trainingToUpdateTrainingRequestDTO(training);
        model.addAttribute("trainingDTO", trainingDTO);

        UserModel user = userService.findByEmail(principal.getName());
        model.addAttribute("userRole", user.getRole().name());
        model.addAttribute("user", user);
        return "form-update-training";
    }

    @PostMapping("training/update")
    public String updateTraining(@Valid @ModelAttribute UpdateTrainingRequestDTO trainingDTO, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        var trainingFromDTO = trainingMapper.updateTrainingRequestDTOtoTraining(trainingDTO);
        var training = trainingService.updateTraining(trainingFromDTO);
        redirectAttributes.addAttribute("successMessage", " Training dengan ID " + training.getIdTraining() + " berhasil diperbaharui.");
        return "redirect:/training";
    }

    @GetMapping("training/{idTraining}/delete")
    public String deleteTraining(@PathVariable("idTraining") UUID idTraining, Model model, RedirectAttributes redirectAttributes, Principal principal) {
        var training = trainingService.getTrainingById(idTraining);
        trainingService.deleteTraining(training);
        redirectAttributes.addAttribute("successMessage", " Training dengan ID " + training.getIdTraining() + " berhasil dihapus.");

        UserModel user = userService.findByEmail(principal.getName());
        model.addAttribute("userRole", user.getRole().name());
        model.addAttribute("user", user);
        return "redirect:/training";
    }

    @GetMapping("training/{idTraining}/daftar")
    public String formPesanan(@PathVariable("idTraining") UUID idTraining, Model model, Principal principal) {
        var pesananDTO = new CreatePesananRequestDTO();
        model.addAttribute("trainingDTO", pesananDTO);

        UserModel user = userService.findByEmail(principal.getName());
        model.addAttribute("user", user);
        model.addAttribute("userRole", user.getRole().name());
        return "redirect:/training/" + idTraining;
    }

    @PostMapping("training/{idTraining}/daftar")
    public String pesanTraining(@PathVariable("idTraining") UUID idTraining, Principal principal, @Valid @ModelAttribute CreatePesananRequestDTO pesananDTO, BindingResult bindingResult,
            Model model, RedirectAttributes redirectAttributes) {
                UserModel user = userService.findByEmail(principal.getName());
                var userRole = user.getRole().name();
               
        model.addAttribute("userRole",userRole);
        if(trainingService.checkPesananBelumBayar(user, idTraining)){
            redirectAttributes.addFlashAttribute("errorMessage", " Anda sudah pernah memesan training ini. Silahkan selesaikan pembayaran.");
            return "redirect:/training/" +idTraining;
        }else if(trainingService.checkPesananDiproses(user, idTraining)){
            redirectAttributes.addFlashAttribute("errorMessage", " Anda sudah pernah memesan training ini. Silahkan menunggu pembayaran dikonfirmasi.");
            return "redirect:/training/" +idTraining;
        }else if(trainingService.checkPesananStatusPembayaran(user, idTraining)){
            redirectAttributes.addFlashAttribute("errorMessage", " Anda sudah pernah memesan training ini.");
            return "redirect:/training/" +idTraining;
        }
        var pesanan = pesananMapper.createPesananRequestDTOtoPesanan(pesananDTO);
        pesanan.setStatusPesanan("BELUM_BAYAR");
        pesanan.setUser(user);
        Date currentTime = new Date();
        pesanan.setTraining(trainingService.getTrainingById(idTraining));
        pesanan.setWaktuPesanan(currentTime);
        pesanan.setJenisPesanan("TRAINING");
        if(userRole.equals("MEMBER_PREMIUM")){
            pesanan.setHargaPesanan(trainingService.getTrainingById(idTraining).getHargaTraining()*80/100);
        }else{
            pesanan.setHargaPesanan(trainingService.getTrainingById(idTraining).getHargaTraining());
        }
        pesananService.savePesanan(pesanan);
     
        redirectAttributes.addFlashAttribute("successMessage", " Pendaftaran berhasil dilakukan. Mohon selesaikan pembayaran dalam waktu 24 jam.");
        return "redirect:/pesanan";
    }
}
