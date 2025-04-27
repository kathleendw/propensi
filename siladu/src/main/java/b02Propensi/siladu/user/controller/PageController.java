package b02Propensi.siladu.user.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import b02Propensi.siladu.model.News;
import b02Propensi.siladu.model.Pesanan;
import b02Propensi.siladu.service.EventService;
import b02Propensi.siladu.service.LayananService;
import b02Propensi.siladu.service.NewsService;
import b02Propensi.siladu.service.PaketLayananService;
import b02Propensi.siladu.service.PembayaranService;
import b02Propensi.siladu.service.PesananService;
import b02Propensi.siladu.service.TrainingService;
import b02Propensi.siladu.user.model.UserModel;
import b02Propensi.siladu.user.service.UserService;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.time.Year;
import org.springframework.beans.factory.annotation.Qualifier;

@Controller
public class PageController {

    @Qualifier("userServiceImpl")
    @Autowired
    private UserService userService;
    @Autowired
    LayananService layananService;
    @Autowired
    PaketLayananService paketLayananService;
    @Autowired
    TrainingService trainingService;
    @Autowired
    PesananService pesananService;
    @Autowired
    PembayaranService pembayaranService;
    @Autowired
    NewsService newsService;
    @Autowired
    EventService eventService;

    @GetMapping("/")
    public String homeGuest(Model model){
        var layananList = layananService.getAllLayanan();

        model.addAttribute("listLayanan", layananList);
        model.addAttribute("listPaketLayanan", paketLayananService.getAllPaketLayanan());
        model.addAttribute("userRole", "GUEST");

        // Menampilkan berita
        List<News> listNews = new ArrayList<>();
        listNews = newsService.getAllNews();

        // Create a Map to store Base64 encoded images temporarily
        Map<String,String> base64ImagesMap = new HashMap<>();

        // Convert byte array to Base64 for each event and store in the Map
        listNews.forEach(news -> {
            String base64Image = Base64.getEncoder().encodeToString(news.getGambarNews());
            base64ImagesMap.put(news.getIdNews(), base64Image);
        });

        model.addAttribute("base64ImagesMap", base64ImagesMap);
        model.addAttribute("newsList", listNews);


        return "home-guest";
    }

    @GetMapping("/home-member")
    public String homeMember(Model model, Principal principal){
        var layananList = layananService.getAllLayanan();
        model.addAttribute("listLayanan", layananList);
        var user = userService.findByEmail(principal.getName());
        model.addAttribute("userRole", user.getRole().name());
        model.addAttribute("user", user);

        // Menampilkan berita
        List<News> listNews = new ArrayList<>();
        listNews = newsService.getAllNews();

        // Create a Map to store Base64 encoded images temporarily
        Map<String,String> base64ImagesMap = new HashMap<>();

        // Convert byte array to Base64 for each event and store in the Map
        listNews.forEach(news -> {
            String base64Image = Base64.getEncoder().encodeToString(news.getGambarNews());
            base64ImagesMap.put(news.getIdNews(), base64Image);
        });

        model.addAttribute("base64ImagesMap", base64ImagesMap);
        model.addAttribute("newsList", listNews);

        //set last sign in and status keaktifan
        userService.setLastSignIn(user);

        return "home-member";
    }
    @GetMapping("/tentang-kami")
    public String tentangKami(Model model){
      
            model.addAttribute("userRole", "GUEST");
       // }else{
        //     var user = userService.findByEmail(principal.getName());
        //     model.addAttribute("email", user.getEmail());
        // model.addAttribute("nama", user.getName());
        // model.addAttribute("masuk", "true");
        // model.addAttribute("userRole", user.getRole().name());
        // model.addAttribute("user", user);
        // }
     
        return "tentang-kami";
    }
    public String formatPrice(double price) {
        if (price < 1000) {
            return String.format("%.0f", price);
        } else {
            return String.format("%,.3f", price / 1000.0).replace(",", ".");
        }
    }
    
    @GetMapping("/dashboard")
    public String dashboard(Model model,Principal principal){
        var user = userService.findByEmail(principal.getName());
        model.addAttribute("userRole", user.getRole().name());
        model.addAttribute("user", user);

        var jumlahLayanan = paketLayananService.getAllPaketLayanan().size();
        var jumlahPesanan = 0;
        var jumlahPenghasilanLayanan = 0;
        for(Pesanan pesanan : pesananService.getAllPesananPaketLayanan()){
            if(pesanan.getIdPembayaran()!=null && pembayaranService.getPembayaranById(pesanan.getIdPembayaran()).getStatusPembayaran().equals("SUKSES")){
                jumlahPesanan+=1;
                jumlahPenghasilanLayanan+=pesanan.getHargaPesanan();
             
            }
        }
        model.addAttribute("jumlahLayanan", jumlahLayanan);
        model.addAttribute("jumlahPesanan", jumlahPesanan);
        model.addAttribute("jumlahPembeli", paketLayananService.getAllUserPesanPaket().size());
        model.addAttribute("jumlahPenghasilanLayanan", formatPrice(jumlahPenghasilanLayanan));

        Map<String, Double> listTraining = trainingService.getRatingTraining();
        var jumlahTraining = trainingService.getAllTraining().size();
        var jumlahPenghasilanTraining = 0;
        for(Pesanan pesanan : pesananService.getAllPesananTraining()){
            if(pesanan.getIdPembayaran() != null && pembayaranService.getPembayaranById(pesanan.getIdPembayaran()).getStatusPembayaran().equals("SUKSES")){
                jumlahPenghasilanTraining += pesanan.getHargaPesanan();
            }
        }
        model.addAttribute("listTraining", listTraining);
        model.addAttribute("jumlahPeserta", trainingService.getAllUserPesanTraining().size());
        model.addAttribute("jumlahTraining", jumlahTraining);
        model.addAttribute("jumlahPenghasilanTraining", formatPrice(jumlahPenghasilanTraining));

        Long[] listLine;
        var currentYear = Year.now().getValue();
        listLine = eventService.getPendapatanPerBulan(currentYear);
        var jumlahPesananEvent = 0;
        var jumlahPenghasilanEvent = 0;
        for(Pesanan pesanan : pesananService.getAllPesananEvent()){
            if(pesanan.getIdPembayaran()!=null && pembayaranService.getPembayaranById(pesanan.getIdPembayaran()).getStatusPembayaran().equals("SUKSES")){
                jumlahPesananEvent+=1;
                jumlahPenghasilanEvent+=pesanan.getHargaPesanan();
             
            }
        }
        model.addAttribute("listLine", listLine);
        model.addAttribute("jumlahPesananEvent", jumlahPesananEvent);
        model.addAttribute("jumlahPenghasilanEvent", formatPrice(jumlahPenghasilanEvent));


        return "dashboard";
    }
    @GetMapping("/tentang-kami-member")
    public String tentangKami(Model model,  Principal principal){
      
        var user = userService.findByEmail(principal.getName());
        model.addAttribute("email", user.getEmail());
        model.addAttribute("nama", user.getName());
        model.addAttribute("masuk", "true");
        model.addAttribute("userRole", user.getRole().name());
        model.addAttribute("user", user);
     
        return "tentang-kami-member";
    }

    @GetMapping("/login")
    public String loginForm(){
        return "login";
    }

    @GetMapping("/pilih-member")
    public String pilihMember(Model model){

        return "pilih-member";
    }

    @GetMapping(value = "/logout")
    public ModelAndView logout(Principal principal) {
        UserModel user = userService.findByEmail(principal.getName());

        System.out.println("tes user email: " + user.getEmail());

        return new ModelAndView("redirect:/logout");
    }


}
