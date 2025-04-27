package b02Propensi.siladu.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import b02Propensi.siladu.DTO.NewsMapper;
import b02Propensi.siladu.DTO.request.AddNewsRequestDTO;
import b02Propensi.siladu.DTO.request.EditNewsRequestDTO;
import b02Propensi.siladu.model.News;
import b02Propensi.siladu.service.NewsService;
import b02Propensi.siladu.user.model.UserModel;
import b02Propensi.siladu.user.service.UserService;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.security.Principal;

@Controller
public class NewsController {

    @Autowired
    private NewsMapper newsMapper;

    @Autowired
    private NewsService newsService;

    @Autowired
    private UserService userService;

    // menampilkan halaman kelola news dan semua news
    @GetMapping("news")
    public String newsPage(Model model, Principal principal) {
        UserModel user = userService.findByEmail(principal.getName());

        List<News> listNews = new ArrayList<>();
        listNews = newsService.getAllNews();

        // Create a Map to store Base64 encoded images temporarily
        Map<String, String> base64ImagesMap = new HashMap<>();

        // Convert byte array to Base64 for each event and store in the Map
        listNews.forEach(news -> {
            String base64Image = Base64.getEncoder().encodeToString(news.getGambarNews());
            base64ImagesMap.put(news.getIdNews(), base64Image);
        });

        model.addAttribute("base64ImagesMap", base64ImagesMap);
        model.addAttribute("newsList", listNews);

        model.addAttribute("userRole", user.getRole().name());
        model.addAttribute("user", user);
        return "news/news-page";
    }

    // formulir untuk menambah news
    @GetMapping("news/add")
    public String formAddNews(Model model, Principal principal) {
        var newsDTO = new AddNewsRequestDTO();
        model.addAttribute("newsDTO", newsDTO);

        UserModel user = userService.findByEmail(principal.getName());
        model.addAttribute("userRole", user.getRole().name());
        model.addAttribute("user", user);

        return "news/form2-add-news";
    }

    // menyimpan isian formulir ke database
    @PostMapping("news/add")
    public String addNews(@RequestParam("file") MultipartFile file,
            @ModelAttribute("newsDTO") AddNewsRequestDTO newsDTO, Model model, RedirectAttributes redirectAttributes) {

        try {

            // Validate file name
            String fileName = StringUtils.cleanPath(file.getOriginalFilename());
            if (fileName.contains("..")) {
                model.addAttribute("errorMessage", "File tidak valid.");
                return "news/form2-add-news";// You can return an appropriate response here
            }

            // Process the file and newsDTO as needed
            var news = newsMapper.addNewsRequestDTOToNews(newsDTO);

            // Convert MultipartFile to byte array
            byte[] imageData = file.getBytes();
            news.setGambarNews(imageData);
            newsService.generateIdNews(news);
            news.setTanggalDibuat(LocalDate.now());
            newsService.saveNews(news);

            model.addAttribute("idNews", news.getIdNews());
            model.addAttribute("judulNews", news.getJudul());
            // Update the model with the newly added news
            List<News> newsList = newsService.getAllNews(); // Retrieve the updated list of news
            model.addAttribute("newsList", newsList);

            // Redirect dengan pesan berhasil
            redirectAttributes.addAttribute("successMessage", "Berita berhasil ditambahkan!");
            return "redirect:/news";

        } catch (IOException e) {
            e.printStackTrace();
            // Redirect dengan pesan gagal
            redirectAttributes.addAttribute("errorMessage",
                    "Gagal menambahkan berita. Terjadi kesalahan: " + e.getMessage());
            return "redirect:/news";
        }

    }

    // melakukan delete pada berita berdasarkan id news
    @GetMapping(value = "news/delete/{idNews}")
    public String deleteNews(@PathVariable(value = "idNews") String idNews, Model model,
            RedirectAttributes redirectAttributes) {
        try {
            var news = newsService.getNewsById(idNews);
            newsService.deleteNews(news);
            // Redirect dengan pesan berhasil
            redirectAttributes.addAttribute("successMessage", "Berita berhasil dihapus!");
        } catch (Exception e) {
            e.printStackTrace();
            // Redirect dengan pesan gagal
            redirectAttributes.addAttribute("errorMessage",
                    "Gagal menghapus berita. Terjadi kesalahan: " + e.getMessage());
        }

        return "redirect:/news";
    }

    // menampilkan detail dari news
    @GetMapping(value = "news/{idNews}")
    public String detailNews(@PathVariable(value = "idNews") String idNews, Model model, Principal principal) {
        var news = newsService.getNewsById(idNews);
        var newsResponseDTO = newsMapper.newsToReadNewsResponseDTO(news);

        String image = Base64.getEncoder().encodeToString(news.getGambarNews());

        // Tambahkan atribut dengan format tanggal baru
        String formattedDate = news.getTanggalDibuat()
                .format(DateTimeFormatter.ofPattern("dd MMMM yyyy", new Locale("id")));
        model.addAttribute("formattedDate", formattedDate);

        model.addAttribute("idNews", newsResponseDTO.getIdNews());
        model.addAttribute("news", newsResponseDTO);
        model.addAttribute("image", image);

        UserModel user = userService.findByEmail(principal.getName());
        model.addAttribute("userRole", user.getRole().name());
        model.addAttribute("user", user);
        return "news/view-news";
    }

    // menampilkan form edit news
    @GetMapping(value = "news/edit/{idNews}")
    public String formEditNews(@PathVariable(value = "idNews") String idNews, Model model, Principal principal) {
        var news = newsService.getNewsById(idNews);
        var newsDTO = newsMapper.newsToEditNewsRequestDTO(news);
        model.addAttribute("newsDTO", newsDTO);

        UserModel user = userService.findByEmail(principal.getName());
        model.addAttribute("userRole", user.getRole().name());
        model.addAttribute("user", user);

        return "news/form2-edit-news";
    }

    @PostMapping("news/edit")
    public String editNews(@ModelAttribute("newsDTO") EditNewsRequestDTO newsDTO,
            @RequestParam("file") MultipartFile file, Model model, RedirectAttributes redirectAttributes) {

        try {

            // // Validate file size (100 KB limit)
            // if (file.getSize() > 100 * 1024) {
            // model.addAttribute("errorMessage", "Size file tidak boleh lebih dari 100
            // KB.");
            // return "news/form2-edit-news";
            // }

            var newsFromDTO = newsMapper.editNewsRequestDTOToNews(newsDTO);
            String idNews = newsFromDTO.getIdNews();

            if (file != null && !file.isEmpty()) {
                String fileName = StringUtils.cleanPath(file.getOriginalFilename());
                if (fileName.contains("..")) {
                    System.out.println("not a valid file");
                } else {
                    // Convert Multipartfile to byte array
                    byte[] imageData = file.getBytes();
                    newsFromDTO.setGambarNews(imageData);
                }

            } else {
                News existingNews = newsService.getNewsById(idNews);
                newsFromDTO.setGambarNews(existingNews.getGambarNews());

            }

            newsFromDTO.setIdNews(idNews);
            News updatedNews = newsService.editNews(newsFromDTO);

            model.addAttribute("idNews", updatedNews.getIdNews());
            model.addAttribute("judul", updatedNews.getJudul());
            // Redirect dengan pesan berhasil
            redirectAttributes.addAttribute("successMessage", "Berita berhasil diubah!");
            return "redirect:/news";

        } catch (IOException e) {
            e.printStackTrace();
            // Redirect dengan pesan gagal
            redirectAttributes.addAttribute("errorMessage",
                    "Gagal mengubah berita. Terjadi kesalahan: " + e.getMessage());
            return "redirect:/news";

        }
    }

}
