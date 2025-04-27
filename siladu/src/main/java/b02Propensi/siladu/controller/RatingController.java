package b02Propensi.siladu.controller;
import java.security.Principal;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import b02Propensi.siladu.model.Event;
import b02Propensi.siladu.model.Layanan;
import b02Propensi.siladu.model.PaketLayanan;
import b02Propensi.siladu.model.Training;
import b02Propensi.siladu.service.EventService;
import b02Propensi.siladu.service.LayananService;
import b02Propensi.siladu.service.PaketLayananService;
import b02Propensi.siladu.service.PembayaranService;
import b02Propensi.siladu.service.PesananService;
import b02Propensi.siladu.service.TrainingService;
import b02Propensi.siladu.user.model.UserModel;
import b02Propensi.siladu.user.service.UserService;

@Controller
public class RatingController {
    @Autowired
    private UserService userService;
    @Autowired
    private PaketLayananService paketLayananService;
    @Autowired
    private EventService eventService;
    @Autowired
    private TrainingService trainingService;
    @Autowired
    PesananService pesananService;
    @Autowired
    PembayaranService pembayaranService;
    


    @GetMapping("/riwayatPesanan")
    public String riwayatPesanan(Model model, Principal principal){
        UserModel user = userService.findByEmail(principal.getName());
        
        model.addAttribute("userRole", user.getRole().name());
        model.addAttribute("listPesanan", user.getListPesanan());
        model.addAttribute("listPembayaran", pembayaranService.getAllPembayaran());
        model.addAttribute("user", user);

        return "riwayat-pesanan";
    }

   
    @PostMapping("/event/rate/{idPesanan}")
    public String rating(@PathVariable UUID idPesanan, @RequestParam(name = "rating") int rating, RedirectAttributes redirectAttributes) {
        
        var pesanan = pesananService.getPesananById(idPesanan);
        pesanan.setRating(rating);
        pesanan.setRated(true);
        if(pesanan.getJenisPesanan().equals("EVENT")){

            Event event = eventService.getEventByIdEvent(pesanan.getEvent().getIdEvent());

            if (event.getJumlahRating() > 0) {
                double totalRating =event.getRating() * event.getJumlahRating() + rating;
                double averageRating = totalRating / (event.getJumlahRating() + 1); 
                event.setJumlahRating(event.getJumlahRating() + 1);
                event.setRating(averageRating);
            } else {
                event.setRating((double) rating);
                event.setJumlahRating(1);
            }
            eventService.saveEvent(event);
            redirectAttributes.addAttribute("activeSection", "Event");

        }
        if(pesanan.getJenisPesanan().equals("TRAINING")){
            Training training = trainingService.getTrainingById(pesanan.getTraining().getIdTraining());

            if (training.getJumlahRating() > 0) {
                double totalRating = training.getRatingTraining() * training.getJumlahRating() + rating;
                double averageRating = totalRating / (training.getJumlahRating() + 1);
                training.setJumlahRating(training.getJumlahRating() + 1);
                training.setRatingTraining(averageRating);
            } else {
                training.setRatingTraining((double) rating);
                training.setJumlahRating(1);
            }
            trainingService.saveTraining(training);
            redirectAttributes.addAttribute("activeSection", "Training");
        }
        if(pesanan.getJenisPesanan().equals("LAYANAN")){
            PaketLayanan paketLayanan = paketLayananService.getPaketLayananById(pesanan.getPaketLayanan().getId());

            if (paketLayanan.getJumlahRating() > 0) {
                double totalRating = paketLayanan.getRating() * paketLayanan.getJumlahRating() + rating;
                double averageRating = totalRating / (paketLayanan.getJumlahRating() + 1); 
                paketLayanan.setJumlahRating(paketLayanan.getJumlahRating() + 1);
                paketLayanan.setRating(averageRating);
            } else {
                paketLayanan.setRating((double) rating);
                paketLayanan.setJumlahRating(1);
            }
            paketLayananService.savePaketLayanan(paketLayanan);
            redirectAttributes.addAttribute("activeSection", "Layanan");

        }
        pesananService.savePesanan(pesanan);

        
        return "redirect:/riwayatPesanan";
    }






    
}
