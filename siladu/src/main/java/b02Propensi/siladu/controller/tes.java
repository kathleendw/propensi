// @PostMapping("paketlayanan/tambah")
// public String addPaketLayanan(@Valid @ModelAttribute CreatePaketLayananRequestDTO paketLayananDTO, BindingResult bindingResult,
//         Model model, RedirectAttributes redirectAttributes) {
        
//     var paketLayanan = paketLayananMapper.createPaketLayananRequestDTOtoPaketLayanan(paketLayananDTO);
//     if (paketLayananService.isNamaPaketExist(paketLayananDTO.getNamaPaket(), paketLayananDTO.getLayanan().getNamaLayanan())) {
//         redirectAttributes.addAttribute("errorMessage", " Nama Paket Layanan Sudah Pernah Ditambahkan");
//         return "redirect:/layanan/" +paketLayanan.getLayanan().getIdLayanan();
//     }
//     paketLayanan.setKategoriLayanan(paketLayanan.getLayanan().getKategoriLayanan());
//     paketLayanan.setRating(0);
//     paketLayananService.savePaketLayanan(paketLayanan);
//     redirectAttributes.addAttribute("successMessage", " Paket Layanan Berhasil Ditambahkan");


//     return "redirect:/layanan/" +paketLayanan.getLayanan().getIdLayanan();
// }