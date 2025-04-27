package b02Propensi.siladu.controller;

import b02Propensi.siladu.DTO.PaketLayananMapper;
import b02Propensi.siladu.DTO.request.CreatePaketLayananRequestDTO;
import b02Propensi.siladu.DTO.request.UpdatePaketLayananRequestDTO;
import b02Propensi.siladu.model.Layanan;
import b02Propensi.siladu.model.PaketLayanan;
import b02Propensi.siladu.service.LayananService;
import b02Propensi.siladu.service.PaketLayananService;
import jakarta.validation.Valid;

import org.springframework.ui.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import java.util.*;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

import b02Propensi.siladu.user.model.UserModel;
import b02Propensi.siladu.user.service.UserService;

@Controller
public class LayananController {
    @Autowired
    private LayananService layananService;
    @Autowired
    private UserService userService;

    @GetMapping("layanan/{id}")
    public String layanan(@PathVariable("id") Long id,Model model, Principal principal) {
        var layanan = layananService.getLayananByIdLayanan(id);
        model.addAttribute("namaLayanan", layanan.getNamaLayanan());
        model.addAttribute("idLayanan", layanan.getIdLayanan());
        model.addAttribute("listPaketLayanan", layanan.getListPaketLayanan());

        UserModel user = userService.findByEmail(principal.getName());
        model.addAttribute("userRole", user.getRole().name());
        model.addAttribute("user", user);
        return "viewall-paket-layanan";
    }
}
