package b02Propensi.siladu.service;

import java.util.List;

import b02Propensi.siladu.model.News;

public interface NewsService {
    
    void saveNews(News news);

    List<News> getAllNews();

    News getNewsById(String idNews);

    void generateIdNews(News news);

    void deleteNews(News news);

    News editNews(News newsFromDto);
}
