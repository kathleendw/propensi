package b02Propensi.siladu.controller;

import org.springframework.stereotype.Controller;

import b02Propensi.siladu.DTO.request.CreatePembayaranRequestDTO;
import b02Propensi.siladu.DTO.request.SendEmailRequestDTO;
import b02Propensi.siladu.model.Layanan;
import b02Propensi.siladu.model.PaketLayanan;
import b02Propensi.siladu.model.Pembayaran;
import b02Propensi.siladu.model.Pesanan;
import b02Propensi.siladu.service.EventService;
import b02Propensi.siladu.service.LayananService;
import b02Propensi.siladu.service.PaketLayananService;
import b02Propensi.siladu.service.PesananService;
import b02Propensi.siladu.service.TrainingService;
import b02Propensi.siladu.user.dto.UserRegistrationDTO;
import b02Propensi.siladu.user.model.Role;
import b02Propensi.siladu.user.model.UserModel;
import b02Propensi.siladu.user.service.EmailSenderService;
import b02Propensi.siladu.user.service.UserService;
import b02Propensi.siladu.service.PembayaranService;
import jakarta.validation.Valid;

import org.springframework.ui.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import java.util.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.time.ZoneId;
@Controller
public class DashboardController {
    @Autowired
    PaketLayananService paketLayananService;

    @Autowired
    PesananService pesananService;
    @Autowired
    UserService userService;
    @Autowired
    PembayaranService pembayaranService;
    @Autowired
    LayananService layananService;
    @Autowired
    TrainingService trainingService;
    @Autowired
    EventService eventService;

    @Autowired
    private EmailSenderService emailSenderService;

    public String formatPrice(double price) {
        if (price < 1000) {
            return String.format("%.0f", price);
        } else {
            return String.format("%,.3f", price / 1000.0).replace(",", ".");
        }
    }
    @GetMapping("dashboard/layanan")
    public String chartPenerbit(Principal principal,Model model,@RequestParam(name = "tahun", required = false) String tahun, @RequestParam(name = "layanan", required = false) String layanan, @RequestParam(name = "layanan_rating", required = false) String layanan_rating) {
        UserModel user = userService.findByEmail(principal.getName());
        model.addAttribute("user", user);
        model.addAttribute("userRole", user.getRole().name());
        
        Long[] listLine;
       Map<String, Integer> listPie;
       Map<String, Double> list;
       var currentYear = Year.now().getValue();
        if (tahun != null && !tahun.isEmpty()) {
            listLine = paketLayananService.getPendapatanPerBulan(Integer.parseInt(tahun));
          
        } else {
            listLine = paketLayananService.getPendapatanPerBulan(currentYear);
        }
        if (layanan != null && !layanan.isEmpty()) {
            if(layanan.equals("All")){
                listPie = paketLayananService.getLayananPeserta();
            }else{
            listPie =paketLayananService.getLayananPesertaFilter(layanan);
            }
        } else {
            listPie = paketLayananService.getLayananPeserta();
        }
        if (layanan_rating != null && !layanan_rating.isEmpty()) {
            if(layanan_rating.equals("All")){
                list = paketLayananService.getRatingLayanan();
            }else{
            list =paketLayananService.getRatingLayananFilter(layanan_rating);
            }
        } else {
            list = paketLayananService.getRatingLayanan();
        }
        var jumlahLayanan = paketLayananService.getAllPaketLayanan().size();
        var jumlahPesanan = 0;
        var jumlahPenghasilan = 0;
        for(Pesanan pesanan : pesananService.getAllPesananPaketLayanan()){
            if(pesanan.getIdPembayaran()!=null && pembayaranService.getPembayaranById(pesanan.getIdPembayaran()).getStatusPembayaran().equals("SUKSES")){
                jumlahPesanan+=1;
                jumlahPenghasilan+=pesanan.getHargaPesanan();
             
            }
        }
        List<String> years = new ArrayList<>();
        for (int i = currentYear - 5; i <= currentYear; i++) {
            years.add(String.valueOf(i));
        }
        List<String> layananList = new ArrayList<>();
        
        for (Layanan layanann : layananService.getAllLayanan()){
            layananList.add(layanann.getNamaLayanan());
        }
        layananList.add("All");
        

        model.addAttribute("layananList", layananList);
        model.addAttribute("years", years);
        model.addAttribute("list", list);
        model.addAttribute("listPie", listPie);
        model.addAttribute("listLine", listLine);
        model.addAttribute("jumlahLayanan", jumlahLayanan);
        model.addAttribute("jumlahPesanan", jumlahPesanan);
        model.addAttribute("jumlahPeserta", paketLayananService.getAllUserPesanPaket().size());
        model.addAttribute("jumlahPenghasilan", formatPrice(jumlahPenghasilan));
        model.addAttribute("selectedYear", tahun);
        model.addAttribute("selectedLayanan", layanan);
        model.addAttribute("selectedLayananRating", layanan_rating);
        model.addAttribute("listPesanan", pesananService.getAllPesananPaketLayanan());
        List<Pembayaran> listPembayaran = new ArrayList<>();
        for(Pesanan pesanan : pesananService.getAllPesananPaketLayanan()){
            if(pesanan.getIdPembayaran() != null){
                listPembayaran.add(pembayaranService.getPembayaranById(pesanan.getIdPembayaran()));
            }else{
                listPembayaran.add(null);
            }
        }
        model.addAttribute("listPembayaran", listPembayaran);
        return "view-layanan-chart";
    }
    @GetMapping("dashboard/training")
    public String chartTraining(Principal principal,Model model,@RequestParam(name = "tahun", required = false) String tahun) {
        UserModel user = userService.findByEmail(principal.getName());
        model.addAttribute("user", user);
        model.addAttribute("userRole", user.getRole().name());
        
        Long[] listLine;
        Map<String, Integer> listPie;
        Map<String, Double> list;
        var currentYear = Year.now().getValue();
        if (tahun != null && !tahun.isEmpty()) {
            listLine = trainingService.getPendapatanPerBulan(Integer.parseInt(tahun));
        } else {
            listLine = trainingService.getPendapatanPerBulan(currentYear);
        }
        listPie = trainingService.getTrainingPeserta();
        list = trainingService.getRatingTraining();
        var jumlahTraining = trainingService.getAllTraining().size();
        var jumlahPesanan = 0;
        var jumlahPenghasilan = 0;
        for(Pesanan pesanan : pesananService.getAllPesananTraining()){
            if(pesanan.getIdPembayaran() != null && pembayaranService.getPembayaranById(pesanan.getIdPembayaran()).getStatusPembayaran().equals("SUKSES")){
                jumlahPesanan += 1;
                jumlahPenghasilan += pesanan.getHargaPesanan();
            }
        }
        List<String> years = new ArrayList<>();
        for (int i = currentYear - 5; i <= currentYear; i++) {
            years.add(String.valueOf(i));
        }    
        List<Pembayaran> listPembayaran = new ArrayList<>();
        for(Pesanan pesanan : pesananService.getAllPesananTraining()){
            if (pesanan.getIdPembayaran() != null) {
                listPembayaran.add(pembayaranService.getPembayaranById(pesanan.getIdPembayaran()));
            } else {
                listPembayaran.add(null);
            }
        }

        model.addAttribute("years", years);
        model.addAttribute("list", list);
        model.addAttribute("listPie", listPie);
        model.addAttribute("listLine", listLine);
        model.addAttribute("jumlahTraining", jumlahTraining);
        model.addAttribute("jumlahPesanan", jumlahPesanan);
        model.addAttribute("jumlahPeserta", trainingService.getAllUserPesanTraining().size());
        model.addAttribute("jumlahPenghasilan", formatPrice(jumlahPenghasilan));
        model.addAttribute("selectedYear", tahun);
        model.addAttribute("listPesanan", pesananService.getAllPesananTraining());
        model.addAttribute("listPembayaran", listPembayaran);
        return "view-training-chart";
    }
    @GetMapping("dashboard/event")
    public String chartdEvent(Principal principal,Model model,@RequestParam(name = "tahun", required = false) String tahun) {
        UserModel user = userService.findByEmail(principal.getName());
        model.addAttribute("user", user);
        model.addAttribute("userRole", user.getRole().name());
        
       Long[] listLine;
       Map<String, Integer> listPie;
       Map<String, Double> list;
       var currentYear = Year.now().getValue();
        if (tahun != null && !tahun.isEmpty()) {
            listLine = eventService.getPendapatanPerBulan(Integer.parseInt(tahun));
          
        } else {
            listLine = eventService.getPendapatanPerBulan(currentYear);
        }
        listPie = eventService.getEventPeserta();
        list = eventService.getRatingEvent();
        var jumlahEvent = eventService.getAllEvent().size();
        var jumlahPesanan = 0;
        var jumlahPenghasilan = 0;
        for(Pesanan pesanan : pesananService.getAllPesananEvent()){
            if(pesanan.getIdPembayaran()!=null && pembayaranService.getPembayaranById(pesanan.getIdPembayaran()).getStatusPembayaran().equals("SUKSES")){
                jumlahPesanan+=1;
                jumlahPenghasilan+=pesanan.getHargaPesanan();
             
            }
        }
        List<String> years = new ArrayList<>();
        for (int i = currentYear - 5; i <= currentYear; i++) {
            years.add(String.valueOf(i));
        }
    
        List<Pembayaran> listPembayaran = new ArrayList<>();
        for(Pesanan pesanan : pesananService.getAllPesananEvent()){
            if (pesanan.getIdPembayaran() != null) {
                listPembayaran.add(pembayaranService.getPembayaranById(pesanan.getIdPembayaran()));
            } else {
                listPembayaran.add(null);
            }
        }
        

        model.addAttribute("years", years);
        model.addAttribute("list", list);
        model.addAttribute("listPie", listPie);
        model.addAttribute("listLine", listLine);
        model.addAttribute("jumlahEvent", jumlahEvent);
        model.addAttribute("jumlahPesanan", jumlahPesanan);
        model.addAttribute("jumlahPeserta", eventService.getAllUserPesanEvent().size());
        model.addAttribute("jumlahPenghasilan", formatPrice(jumlahPenghasilan));
        model.addAttribute("selectedYear", tahun);
        model.addAttribute("listPesanan", pesananService.getAllPesananEvent());
        model.addAttribute("listPembayaran", listPembayaran);
        return "view-event-chart";
    }

    @GetMapping("/dashboardpremium")
    public String getDashboardPremium(Principal principal, Model model,
            @RequestParam(name = "tahun", required = false) String tahun) {

        UserModel user = userService.findByEmail(principal.getName());
        model.addAttribute("user", user);
        model.addAttribute("userRole", user.getRole().name());

        // BAR CHART
        var currentYear = Year.now().getValue();
        List<String> years = new ArrayList<>();
        for (int i = currentYear - 2; i <= currentYear; i++) {
            years.add(String.valueOf(i));
        }
        model.addAttribute("years", years);

        System.out.println("TAHUNN 2024 MASUK " + tahun);
        List<Long> newPremiumMembersByMonth;
        if (tahun != null && !tahun.isEmpty()) {
            newPremiumMembersByMonth = userService.countNewPremiumMembersByMonth(Integer.parseInt(tahun));

        } else {
            newPremiumMembersByMonth = userService.countNewPremiumMembersByMonth(currentYear);
        }

        System.out.println(newPremiumMembersByMonth);
        model.addAttribute("newPremiumMembersByMonth", newPremiumMembersByMonth);
        model.addAttribute("selectedYear", tahun);

        // Hitung jumlah member basic dan member premium
        // Calculate the percentages
        double basicMemberPercentage = userService.calculateBasicMemberPercentage();
        double premiumMemberPercentage = userService.calculatePremiumMemberPercentage();

        // Format to one decimal place
        String formattedBasicPercentage = String.format("%.1f", basicMemberPercentage);
        String formattedPremiumPercentage = String.format("%.1f", premiumMemberPercentage);

        // Add formatted strings to the model
        model.addAttribute("basicMemberPercentage", formattedBasicPercentage);
        model.addAttribute("premiumMemberPercentage", formattedPremiumPercentage);

        // Adding total income from premium payments
        long totalPremiumIncome = pembayaranService.getTotalIncomeFromPremiumSuccessPayments();
        model.addAttribute("totalPremiumIncome", totalPremiumIncome);

        long totalPremiumMembers = userService.countTotalPremiumMembers();
        model.addAttribute("totalPremiumMembers", totalPremiumMembers);

        return "dashboard/premium";
    }

    @GetMapping("/layout")
    public String getMethodName(Model model, @RequestParam(name = "tahun", required = false) String tahun) {
        // BAR CHART
        var currentYear = Year.now().getValue();
        List<String> years = new ArrayList<>();
        for (int i = currentYear - 2; i <= currentYear; i++) {
            years.add(String.valueOf(i));
        }
        model.addAttribute("years", years);

        System.out.println("TAHUNN 2024 MASUK " + tahun);
        List<Long> newPremiumMembersByMonth;
        if (tahun != null && !tahun.isEmpty()) {
            newPremiumMembersByMonth = userService.countNewPremiumMembersByMonth(Integer.parseInt(tahun));

        } else {
            newPremiumMembersByMonth = userService.countNewPremiumMembersByMonth(currentYear);
        }
        
        System.out.println(newPremiumMembersByMonth);
        model.addAttribute("newPremiumMembersByMonth", newPremiumMembersByMonth);

        return "dashboard/layout";
    }

    @GetMapping("/dashboard-keaktifan-member")
    public String dashboardKeaktifanMember(Principal principal, Model model){

        UserModel user = userService.findByEmail(principal.getName());

        List<Integer> monthlyChartData = userService.findActiveMemberByMonth();
        List<Integer> dailyChartData = userService.findActiveMemberByDay();

        LocalDate currentDate = LocalDate.now();
        int currentMonthValue = currentDate.getMonthValue();

        int totalMemberAktif = monthlyChartData.get(currentMonthValue-1);

        Long memberBasic = userService.findAllMember(Role.MEMBER_BASIC);
        Long memberPremium = userService.findAllMember(Role.MEMBER_PREMIUM);

        model.addAttribute("totalMemberBasic", memberBasic);
        model.addAttribute("totalMemberPremium", memberPremium);
        model.addAttribute("totalMemberAktif", totalMemberAktif);

        model.addAttribute("memberList", userService.findMembers());
        model.addAttribute("monthlyChartData", monthlyChartData);
        model.addAttribute("dailyChartData", dailyChartData);

        model.addAttribute("userRole", user.getRole().name());
        model.addAttribute("user", user);

        return "dashboard-aktif";
    }

    @GetMapping("/member/detail/{id}")
    public String memberDetail(@PathVariable("id") Long id, Model model, Principal principal) {
        
        UserModel user = userService.findByEmail(principal.getName());

        UserModel member = userService.findById(id);

        var sendEmailDTO = new SendEmailRequestDTO();
    
        model.addAttribute("sendEmailDTO", sendEmailDTO);

        model.addAttribute("userRole", user.getRole().name());
        model.addAttribute("user", user);
        model.addAttribute("name", member.getName());
        model.addAttribute("role", member.getRole().name());
        model.addAttribute("umkm", member.getUmkm());
        model.addAttribute("email", member.getEmail());
        model.addAttribute("telephone", member.getTelephone());

        return "member-detail";
    }

    @PostMapping("/send-email")
    public String sendEmail(@Valid @ModelAttribute SendEmailRequestDTO sendEmailDTO, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes
    , Principal principal){
        UserModel user = userService.findByEmail(principal.getName());
        var userRole = user.getRole().name();

        emailSenderService.sendSimpleEmail(sendEmailDTO.getEmail(), "Reminder Keaktifan Member", sendEmailDTO.getPesan());

        model.addAttribute("user", user);
        model.addAttribute("userRole", userRole);
        
        return "redirect:/dashboard-keaktifan-member";

    }
}
