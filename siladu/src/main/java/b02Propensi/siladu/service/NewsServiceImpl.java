package b02Propensi.siladu.service;

import java.util.List;

import javax.xml.catalog.Catalog;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import b02Propensi.siladu.model.News;
import b02Propensi.siladu.repository.NewsDb;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class NewsServiceImpl implements NewsService {
    private Long countNews;
    @Autowired
    NewsDb newsDb;

    @Override
    public void saveNews(News news) {
        newsDb.save(news);
    }

    @Override
    public List<News> getAllNews() {
        return newsDb.findAll();
    }

    @Override
    public News getNewsById(String idNews) {
        for (News news : getAllNews()) {
            if (news.getIdNews().equals(idNews)) {
                return news;
            }
        }
        return null;

    }

    /// Service untuk generate ID : NEWS___
    public void countData(){
    countNews = newsDb.count();
    }

    @Override
    public void generateIdNews(News news) {
        String idNews = generateUniqueNewsId();
        news.setIdNews(idNews);
    }

    private String generateUniqueNewsId() {
        countData();
        String idPrefix = "NEWS";
        String formattingNumCode;
        String idNews;

        do {
            formattingNumCode = String.format("%03d", countNews + 1);
            idNews = idPrefix + formattingNumCode;
            countNews++;
        } while (newsDb.existsById(idNews));

        return idNews;
    }

    @Override
    public void deleteNews(News news) {
        newsDb.delete(news);
    }

    @Override
    public News editNews(News newsFromDto) {
        News news = getNewsById(newsFromDto.getIdNews());

        if (news != null) {
            news.setJudul(newsFromDto.getJudul());
            news.setGambarNews(newsFromDto.getGambarNews());
            news.setDeskripsiNews(newsFromDto.getDeskripsiNews());
            newsDb.save(news);
        }
        return news;
    }

}
