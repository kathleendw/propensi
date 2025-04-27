package b02Propensi.siladu.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.validation.Valid;

import java.io.IOException;
import com.fasterxml.jackson.databind.ObjectMapper;

import b02Propensi.siladu.DTO.request.CreateEventRequestDTO;
import b02Propensi.siladu.DTO.request.CreatePesananRequestDTO;
import b02Propensi.siladu.DTO.request.UpdateEventRequestDTO;
import b02Propensi.siladu.service.PesananService;
import b02Propensi.siladu.service.EventService;
import b02Propensi.siladu.DTO.EventMapper;
import b02Propensi.siladu.DTO.PesananMapper;
import java.util.*;

import b02Propensi.siladu.model.Event;

import java.security.Principal;

import b02Propensi.siladu.user.model.UserModel;
import b02Propensi.siladu.user.service.UserService;

@Controller
public class EventController {
    @Autowired
    private EventMapper eventMapper;
    @Autowired
    private EventService eventService;
    @Autowired
    private UserService userService;
    @Autowired
    private PesananMapper pesananMapper;
    @Autowired
    private PesananService pesananService;
    
    @GetMapping("/event/tambah")
    public String formAddEvent(Model model, Principal principal){
        var eventDTO = new CreateEventRequestDTO();
        model.addAttribute("eventDTO", eventDTO);

        UserModel user = userService.findByEmail(principal.getName());
        model.addAttribute("userRole", user.getRole().name());
        model.addAttribute("user", user);
        return "form-create-event";
    }

    @PostMapping("/event/tambah")
    public String addEvent(@RequestParam("file") MultipartFile file,
                            @ModelAttribute("eventDTO") CreateEventRequestDTO eventDTO,
                            BindingResult bindingResult,
                            RedirectAttributes redirectAttributes,
                            Model model){
                              
        try{
            String contentType = file.getContentType(); 
            // Validate file name
            String fileName = StringUtils.cleanPath(file.getOriginalFilename());
            if (fileName.contains("..")) {
                System.out.println("Not a valid file");
                return "form-create-event"; // You can return an appropriate response here
            }

            
            if( !contentType.equals("image/jpeg") && !contentType.equals("image/png") ) {
                model.addAttribute("errorMessage", "Invalid file type. Only JPG, PNG, and JPEG files are allowed.");
                return "form-create-event"; // You can return an appropriate response here
            }

             // Process the file and eventDTO as needed
            var event = eventMapper.createEventRequestDTOToEvent(eventDTO);

              // Convert MultipartFile to byte array
            byte[] imageData = file.getBytes();
            event.setGambarEvent(imageData);
            eventService.generateIdEvent(event);
            eventService.saveEvent(event);
            redirectAttributes.addAttribute("successMessage", " Event Berhasil Ditambahkan");

            model.addAttribute("idEvent", event.getIdEvent());
            model.addAttribute("namaEvent", event.getNamaEvent());
         
            return "redirect:/event/mengelola";
            
        } catch (IOException e){
            e.printStackTrace();
            redirectAttributes.addAttribute("errorMessage",
                    "Gagal menambahkan event. Terjadi kesalahan: " + e.getMessage());
            return "redirect:/event/mengelola";
        }
        
    }

    @GetMapping("/event/mengelola")
    public String listAllEvent(Model model, Principal principal){
        List<Event> listEvent = eventService.getAllEvent();


        // Create a Map to store Base64 encoded images temporarily
        Map<String, String> base64ImagesMap = new HashMap<>();

        // Convert byte array to Base64 for each event and store in the Map
        listEvent.forEach(event -> {
            String base64Image = Base64.getEncoder().encodeToString(event.getGambarEvent());
            base64ImagesMap.put(event.getIdEvent(), base64Image); // Assuming idEvent is unique and can be used as a key
        });

        model.addAttribute("base64ImagesMap", base64ImagesMap);
        model.addAttribute("listEvent", listEvent);

        UserModel user = userService.findByEmail(principal.getName());
        model.addAttribute("userRole", user.getRole().name());
        model.addAttribute("user", user);
        return "viewall-event";
    }

    @GetMapping("/event/{idEvent}")
    public String detailEvent(@PathVariable("idEvent") String  idEvent, Model model, Principal principal) {
        var event = eventService.getEventByIdEvent(idEvent);

        // Convert byte array to Base64 encoded string
        String image = Base64.getEncoder().encodeToString(event.getGambarEvent());
        UserModel user = userService.findByEmail(principal.getName());

        boolean isRegistered = eventService.isRegistered(user, idEvent);

        model.addAttribute("isRegistered", isRegistered);
        model.addAttribute("image", image);
        model.addAttribute("event", event);
        model.addAttribute("hargaDiskon", event.getHargaEvent()*80/100);
        model.addAttribute("userRole", user.getRole().name());
        model.addAttribute("user", user);
        return "view-event";
    }

    @GetMapping("/event/update/{idEvent}")
    public String formUpdateEvent(@PathVariable("idEvent") String idEvent, Model model, Principal principal){
        var event = eventService.getEventByIdEvent(idEvent);
        var eventDTO = eventMapper.eventToUpdateEventRequestDTO(event);
        eventDTO.setIdEvent(event.getIdEvent());

        model.addAttribute("eventDTO", eventDTO);
        model.addAttribute("event", event);

        UserModel user = userService.findByEmail(principal.getName());
        model.addAttribute("userRole", user.getRole().name());
        model.addAttribute("user", user);
        return "form-update-event";
    }

    @PostMapping("/event/update/{idEvent}")
    public String updateEvent(@ModelAttribute("eventDTO") UpdateEventRequestDTO eventDTO, 
                                @PathVariable("idEvent") String idEvent, 
                                @RequestParam("file") MultipartFile file, 
                                RedirectAttributes redirectAttributes,
                                Model model){
            
            try{
                var eventFromDTO = eventMapper.createEventRequestDTOToEvent(eventDTO);
                if(file != null && !file.isEmpty()) {
                    String fileName = StringUtils.cleanPath(file.getOriginalFilename());
                    String contentType = file.getContentType();

                    if(fileName.contains("..")) {
                        System.out.println("not a valid file");
                    } else {
                         // Convert MultipartFile to byte array
                        byte[] imageData = file.getBytes();
                        eventFromDTO.setGambarEvent(imageData);
                        
                        if( !contentType.equals("image/jpeg") && !contentType.equals("image/png") ) {
                            redirectAttributes.addFlashAttribute("errorMessage", "Invalid file type. Only JPG, PNG, and JPEG files are allowed.");
                            return "redirect:/event/update/{idEvent}"; 
                        }
                    }

                } else {
                    Event existingEvent = eventService.getEventByIdEvent(idEvent);
                    eventFromDTO.setGambarEvent(existingEvent.getGambarEvent());
                    
                }
                   
                    eventFromDTO.setIdEvent(idEvent);
                    Event updatedEvent = eventService.updateEvent(eventFromDTO);
                    model.addAttribute("idEvent", updatedEvent.getIdEvent());
                    model.addAttribute("namaEvent", updatedEvent.getNamaEvent());
                    redirectAttributes.addAttribute("successMessage", " Event Berhasil Diperbaharui");
            
                } catch (IOException e) {
                    e.printStackTrace();
                    redirectAttributes.addAttribute("errorMessage", "Gagal menghapus event. Terjadi kesalahan: " + e.getMessage());

                }
          
        return "redirect:/event/mengelola";
    
    }

    @GetMapping("/event/delete/{idEvent}")
    public String deleteEvent(@PathVariable("idEvent") String idEvent, Model model, RedirectAttributes redirectAttributes) {
        try {
            // Panggil eventService untuk menghapus event berdasarkan idEvent
            eventService.deleteEventById(idEvent);

            // Set atribut yang diperlukan untuk ditampilkan di halaman success delete
            model.addAttribute("idEvent", idEvent);
            redirectAttributes.addAttribute("successMessage", " Event Berhasil Dihapus");
            return "redirect:/event/mengelola";
        } catch (Exception e) {
            e.printStackTrace();
            return "An error occurred: " + e.getMessage();
        }
    }

    @GetMapping("/event")
    public String listAllEventForMember(Model model, Principal principal) {
        UserModel user = userService.findByEmail(principal.getName());
        List<Event> listEvent = eventService.getAllEvent();

        // Create a Map to store Base64 encoded images temporarily
        Map<String, String> base64ImagesMap = new HashMap<>();

        // Convert byte array to Base64 for each event and store in the Map
        listEvent.forEach(event -> {
            String base64Image = Base64.getEncoder().encodeToString(event.getGambarEvent());
            base64ImagesMap.put(event.getIdEvent(), base64Image); // Assuming idEvent is unique and can be used as a key
        });

        model.addAttribute("base64ImagesMap", base64ImagesMap);
        model.addAttribute("listEvent", listEvent);

        
        model.addAttribute("userRole", user.getRole().name());
        model.addAttribute("user", user);
        return "viewall-event-member";
    }

    @GetMapping("/event/daftar/{idEvent}")
    public String daftarEvent(@PathVariable("idEvent") String idEvent, Model model, Principal principal) {
        var event = eventService.getEventByIdEvent(idEvent);
        String image = Base64.getEncoder().encodeToString(event.getGambarEvent());
        model.addAttribute("image", image);
        model.addAttribute("event", event);

        var pesananDTO = new CreatePesananRequestDTO();
        model.addAttribute("eventDTO", pesananDTO);

        UserModel user = userService.findByEmail(principal.getName());
        model.addAttribute("user", user);
        model.addAttribute("userRole", user.getRole().name());
        return "redirect:/event/" + idEvent;
    }

    @PostMapping("/event/daftar/{idEvent}")
    public String pesanEvent(@PathVariable("idEvent") String idEvent, Principal principal, @Valid @ModelAttribute CreatePesananRequestDTO pesananDTO, BindingResult bindingResult,
        Model model, RedirectAttributes redirectAttributes) {
        UserModel user = userService.findByEmail(principal.getName());
        var userRole = user.getRole().name();
        model.addAttribute("userRole", userRole);


        if(eventService.checkPesanan(user, idEvent)){
            redirectAttributes.addFlashAttribute("errorMessage", " Anda sudah pernah mendaftar pada event ini. Silahkan selesaikan pembayaran.");
            return "redirect:/event/" + idEvent;
        }
    
        var pesanan = pesananMapper.createPesananRequestDTOtoPesanan(pesananDTO);
        pesanan.setStatusPesanan("BELUM_BAYAR");
        pesanan.setUser(user);
        Date currentTime = new Date();
        pesanan.setEvent(eventService.getEventByIdEvent(idEvent));
        pesanan.setWaktuPesanan(currentTime);
        pesanan.setJenisPesanan("EVENT");
        if(userRole.equals("MEMBER_PREMIUM")){
            pesanan.setHargaPesanan(eventService.getEventByIdEvent(idEvent).getHargaEvent()*80/100);
        }else{
            pesanan.setHargaPesanan(eventService.getEventByIdEvent(idEvent).getHargaEvent());
        }

        pesananService.savePesanan(pesanan);
     
        redirectAttributes.addFlashAttribute("successMessage", " Pendaftaran berhasil dilakukan. Mohon selesaikan pembayaran dalam waktu 24 jam.");
        return "redirect:/pesanan";
    }

   

    
    







}
