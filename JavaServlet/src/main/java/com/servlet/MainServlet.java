package com.servlet;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import models.NewsItem;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


@WebServlet("/MainServlet")
public class MainServlet extends HttpServlet {
    private final String[] TIME_SECTIONS = {"/section/tech", "/section/sports", "/section/business", "/section/science"};
    private final String[] TIME_CATEGORIES = {"Tech", "Sports", "Business", "Science"};
    private List<NewsItem> newsItems = new ArrayList<>();
    private boolean isCached = false;


    /* Retrieves an HTTP get request and responds with a JSON containing the NewsItem list's data */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        //Setting up response
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.addHeader("Access-Control-Allow-Origin", "*");
        PrintWriter out = response.getWriter();

        //Check if data is cached
        if(isCached) {
            out.print(convertToJSON(newsItems));
            out.flush();
        }
        //Otherwise scrape data
        else {
            //Scrape data from Time.com and build NewsItem list
            scrapeUrl();
            //Sort each NewsItem by its corresponding articles published time
            newsItems.sort(Comparator.comparing(NewsItem::getPublishedDate));
            //Reverse list to have the most recent NewsItem first
            Collections.reverse(newsItems);
            //Set flag for next request to use cached data
            isCached = true;
            out.print(convertToJSON(newsItems));
            out.flush();
        }
    }

    /* Converts NewsItem list to a JSON string */
    protected String convertToJSON(List<NewsItem> newsItems) {
        Gson gson = new GsonBuilder().disableHtmlEscaping().create();
        return gson.toJson(newsItems);
    }

    /* Scrapes article information and convert to NewsItem objects */
    protected void scrapeUrl() {
        String baseUrl = "https://time.com";
        int sectionsLen = TIME_SECTIONS.length;
        DateTimeFormatter ISOFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);

        for (int i = 0; i < sectionsLen; i++) {
            try {
                //Fetch the website and store it in Document object
                Document doc = Jsoup.connect(baseUrl + TIME_SECTIONS[i]).get();

                //Retrieve all articles from Document object
                Elements articles = doc.getElementsByTag("article");

                NewsItem newsItem = null;

                Map<String, Integer> pathMap = new HashMap<>();

                //Go through each article tag and scrape key data
                for (Element article : articles) {
                    newsItem = new NewsItem();

                    Elements articleTitleURL = article.select("h3");
                    Elements articleExcerpt = article.getElementsByClass("summary margin-8-bottom desktop-only");
                    Elements articleImgURL = article.getElementsByClass("media-img margin-16-bottom");

                    //Checking for duplicate article
                    if (pathMap.containsKey(articleTitleURL.select("a").attr("href"))) {
                        int newsItemPos = pathMap.get(articleTitleURL.select("a").attr("href"));
                        String currentCategory = newsItems.get(newsItemPos).getCategory();
                        newsItems.get(newsItemPos).setCategory(currentCategory + "," + TIME_CATEGORIES[i]);
                    }
                    //Otherwise creating new NewsItem
                    else {
                        newsItem.setCategory(TIME_CATEGORIES[i]);
                        newsItem.setTitle(articleTitleURL.select("a").text());
                        newsItem.setPageUrl(baseUrl + articleTitleURL.select("a").attr("href"));
                        newsItem.setExcerpt(articleExcerpt.text());
                        newsItem.setImgUrl(articleImgURL.select("div").attr("data-src"));
                        newsItem.setPublishedDate(articlePublishedDate(newsItem.getPageUrl(), ISOFormatter));

                        //Add new NewsItem to list
                        newsItems.add(newsItem);
                        //Mapping current articles url path to its corresponding NewsItem object to handle duplicates
                        pathMap.put(articleTitleURL.select("a").attr("href"), newsItems.size() - 1);
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /* Retrieves a specified article's published date and time */
    protected LocalDateTime articlePublishedDate(String url, DateTimeFormatter ISOFormatter) {
        try {
            //Fetch the website and store it in Document object
            Document doc = Jsoup.connect(url).get();

            Element articleDate = doc.getElementById("page-gtm-values");

            //Scrape date and time in ISO format and convert to LocalDateTime object
            return LocalDateTime.parse(articleDate.select("div").attr("data-content_published_date"), ISOFormatter);

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
