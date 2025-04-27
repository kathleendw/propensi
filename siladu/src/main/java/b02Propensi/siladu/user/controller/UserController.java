package b02Propensi.siladu.user.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import b02Propensi.siladu.DTO.PesananMapper;
import b02Propensi.siladu.DTO.request.CreatePesananRequestDTO;
import b02Propensi.siladu.model.Pesanan;
import b02Propensi.siladu.service.PesananService;
import b02Propensi.siladu.user.authentication.PasswordConfig;
import b02Propensi.siladu.user.dto.DeleteAccountDTO;
import b02Propensi.siladu.user.dto.UpdatePasswordDTO;
import b02Propensi.siladu.user.dto.UpdateProfileDTO;
import b02Propensi.siladu.user.dto.UserRegistrationDTO;
import b02Propensi.siladu.user.model.UserModel;
import b02Propensi.siladu.user.service.EmailSenderService;
import b02Propensi.siladu.user.service.UserService;
import jakarta.validation.Valid;

import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.Date;

@Controller
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private EmailSenderService emailSenderService;

    @Autowired
    private PesananMapper pesananMapper;
    @Autowired
    private PesananService pesananService;

    @GetMapping("/registration/{role}")
    public String showRegistrationForm(@PathVariable String role, Model model){
        var userRegistrationDTO = new UserRegistrationDTO();

        model.addAttribute("userRegistrationDTO", userRegistrationDTO);
        model.addAttribute("role", role);
        System.out.println("tes "+role);
        return "registration";
    }

    @PostMapping("/registration")
    public String registerUser(@Valid @ModelAttribute UserRegistrationDTO userRegistrationDTO, BindingResult bindingResult){
        UserModel existing = userService.findByEmail(userRegistrationDTO.getEmail());
        if (existing != null) {
            return "redirect:/registration/"+userRegistrationDTO.getRole()+"?emailError";
        }

        if (userRegistrationDTO.getPassword().equals(userRegistrationDTO.getConfirmPassword())){

            if (userService.validatePassword(userRegistrationDTO.getPassword())){
                UserModel user = userService.save(userRegistrationDTO);
                if (userRegistrationDTO.getRole().equals("premium")){
                    proceedPremiumRegistration(user);
                    sendEmailPaymentNotification(user);
                }
                sendEmailConfirmation(user);
            } else {
                return "redirect:/registration/"+userRegistrationDTO.getRole()+"?passFailed";
            }
        } else {
            return "redirect:/registration/"+userRegistrationDTO.getRole()+"?failed";
        }
        
        return "redirect:/login";

    }

    @GetMapping("/profile-member")
    public String profileMember(Model model, Principal principal){
        UserModel user = userService.findByEmail(principal.getName());
        model.addAttribute("user", user);
        String role = user.getRole().name();
        model.addAttribute("userRole", role);

        return "profile-member-2";
    }

    @GetMapping("/update-member-premium")
    public String updatePremium(Model model, Principal principal){
        UserModel user = userService.findByEmail(principal.getName());

        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String formattedDate = sdf.format(date);


        //userService.updatePremium(user);
        model.addAttribute("userRole", user.getRole().name());
        model.addAttribute("user", user);
        model.addAttribute("tanggal", formattedDate);

        return "update-member-premium";
    }

    @GetMapping("/delete-account")
    public String confirmDeleteAccount(Model model, Principal principal){

        var deleteAccountDTO = new DeleteAccountDTO();

        UserModel user = userService.findByEmail(principal.getName());

        model.addAttribute("deleteAccountDTO", deleteAccountDTO);
        model.addAttribute("user", user);
        model.addAttribute("userRole", user.getRole().name());
        model.addAttribute("user", user);


        return "delete-account";
    }

    @PostMapping("/delete-account")
    public String deleteAccount(Model model, Principal principal, @Valid @ModelAttribute DeleteAccountDTO deleteAccountDTO){
        UserModel user = userService.findByEmail(principal.getName());

        if(PasswordConfig.checkPassword(deleteAccountDTO.getPassword(), user.getPassword())){
            userService.deleteAccount(user);
            return "redirect:/";
        } else{
            return "redirect:/delete-account?failed";
        }

        
    }

    @PostMapping("/update-profile-member")
    public String update(Model model, Principal principal, @Valid @ModelAttribute UpdateProfileDTO updateProfileDTO){
        UserModel user = userService.findByEmail(principal.getName());
        UserModel existing = userService.findByEmail(updateProfileDTO.getEmail());
        if (existing != null & existing != user) {
            return "redirect:/update-profile-member?emailError";
        }else {
            userService.updateProfile(user, updateProfileDTO);
        }
        
        return "redirect:/profile-member?profileSuccess";
    }

    @GetMapping("/update-profile-member")
    public String showUpdateProfileForm(Model model, Principal principal){
        var updateProfileDTO = new UpdateProfileDTO();

        UserModel user = userService.findByEmail(principal.getName());

        updateProfileDTO.setEmail(user.getEmail());
        updateProfileDTO.setName(user.getName());
        updateProfileDTO.setTelephone(user.getTelephone());
        updateProfileDTO.setUmkm(user.getUmkm());

        model.addAttribute("user", user);
        model.addAttribute("userRole", user.getRole().name());
        model.addAttribute("updateProfileDTO", updateProfileDTO);

        return "update-profile-member";
    }

    @GetMapping("/update-password")
    public String showUpdatePasswordForm(Model model, Principal principal){
        var updatePasswordDTO = new UpdatePasswordDTO();

        UserModel user = userService.findByEmail(principal.getName());

        model.addAttribute("user", user);
        model.addAttribute("userRole", user.getRole().name());
        model.addAttribute("updatePasswordDTO", updatePasswordDTO);

        return "update-password";
    }

    @PostMapping("/update-password")
    public String updatePassword(Model model, Principal principal, @Valid @ModelAttribute UpdatePasswordDTO updatePasswordDTO){
        UserModel user = userService.findByEmail(principal.getName());

        if (!PasswordConfig.checkPassword(updatePasswordDTO.getCurrentPassword(), user.getPassword())){
            return "redirect:/update-password?wrongPassword";
        }
        if (updatePasswordDTO.getPassword().equals(updatePasswordDTO.getConfirmPassword())){

            if (userService.validatePassword(updatePasswordDTO.getPassword())){
                userService.updatePassword(user, updatePasswordDTO);
            } else {
                return "redirect:/update-password?passFailed";
            }
            
        } else {
            return "redirect:/update-password?failed";
        }
        
        return "redirect:/profile-member?passSuccess";

    }

    public void sendEmailConfirmation(UserModel user){
        String body = """
                Hi, %s!

                Anda telah berhasil terdaftar menjadi member Yayasan Komunitas Tangerang Berdaya.

                Selanjutnya, silakan lengkapi nama usaha dan nomor telepon Anda pada bagian profile.

                Salam,
                Yayasan Komunitas Tangerang Berdaya
                """;
        emailSenderService.sendSimpleEmail(user.getEmail(), "Registrasi Member Berhasil", String.format(body, user.getName()));
    }

    @GetMapping("pesan/member-premium")
    public String formPesanan(Model model, Principal principal) {
        var pesananDTO = new CreatePesananRequestDTO();
        model.addAttribute("pesananDTO", pesananDTO);

        UserModel user = userService.findByEmail(principal.getName());
        model.addAttribute("user", user);
        model.addAttribute("userRole", user.getRole().name());
        return "redirect:/profile-member";
    }

    @PostMapping("pesan/member-premium")
    public String pesanMemberPremium(Principal principal, @Valid @ModelAttribute CreatePesananRequestDTO pesananDTO, BindingResult bindingResult,
            Model model, RedirectAttributes redirectAttributes) {
        UserModel user = userService.findByEmail(principal.getName());
        model.addAttribute("user", user);
        model.addAttribute("userRole", user.getRole().name());

        var pesanan = pesananMapper.createPesananRequestDTOtoPesanan(pesananDTO);
        pesanan.setStatusPesanan("BELUM_BAYAR");
        pesanan.setUser(user);
        Date currentTime = new Date();
        pesanan.setWaktuPesanan(currentTime);
        pesanan.setJenisPesanan("PREMIUM");
        pesanan.setHargaPesanan(Long.valueOf(100000));
        pesananService.savePesanan(pesanan);
     
        redirectAttributes.addFlashAttribute("successMessage", "Mohon selesaikan pembayaran member premium dalam waktu 24 jam.");
        return "redirect:/pesanan";
    }

    public void proceedPremiumRegistration(UserModel user){
        Pesanan pesanan = new Pesanan();

        pesanan.setStatusPesanan("BELUM_BAYAR");
        pesanan.setUser(user);
        Date currentTime = new Date();
        pesanan.setWaktuPesanan(currentTime);
        pesanan.setJenisPesanan("PREMIUM");
        pesanan.setHargaPesanan(Long.valueOf(100000));
        pesananService.savePesanan(pesanan);

        //send email
    }

    public void sendEmailPaymentNotification(UserModel user){
        String body = """
                Halo, Bapak/Ibu %s

                Silakan lakukan pembayaran untuk aktivasi member premium dalam 24 jam. Pembayaran dapat diakses melalui menu Pesanan.

                Salam,
                Yayasan Komunitas Tangerang Berdaya
                """;
        emailSenderService.sendSimpleEmail(user.getEmail(), "Registrasi Member Berhasil", String.format(body, user.getName()));
    }


}
