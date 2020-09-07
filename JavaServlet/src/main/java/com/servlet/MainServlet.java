package com.servlet;

import javax.servlet.ServletException;
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
    private final String baseURL = "https://time.com";
    private final String[] timeMagSections = {"/section/tech", "/section/sports", "/section/business", "/section/science"};
    private final String[] timeMagCategories = {"Tech", "Sports", "Business", "Science"};
    private final DateTimeFormatter ISOFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
    private List<NewsItem> newsItems = new ArrayList<>();
    private Map<String, Integer> pathMap = new HashMap<>();
    private boolean isCached = false;


    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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
            //Scrape data from Time.com
            scrapeUrl();
            //Sort each newsItem by its corresponding articles published time
            newsItems.sort(Comparator.comparing(NewsItem::getPublishedDate));
            //Reverse list to have the most recent newsItem first
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

    /*Uses url to connect and scrape key information*/
    protected void scrapeUrl() {
        int sectionsLen = timeMagSections.length;
        for (int i = 0; i < sectionsLen; i++) {
            try {
                //Fetch the website and store it in Document object
                Document doc = Jsoup.connect(baseURL + timeMagSections[i]).get();

                //Retrieve all articles from Document object
                Elements articles = doc.getElementsByTag("article");

                NewsItem newsItem = null;

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
                        newsItems.get(newsItemPos).setCategory(currentCategory + "," + timeMagCategories[i]);
                    }
                    //Otherwise creating new newsItem
                    else {
                        newsItem.setCategory(timeMagCategories[i]);
                        newsItem.setTitle(articleTitleURL.select("a").text());
                        newsItem.setPageUrl(baseURL + articleTitleURL.select("a").attr("href"));
                        newsItem.setExcerpt(articleExcerpt.text());
                        newsItem.setImgUrl(articleImgURL.select("div").attr("data-src"));
                        newsItem.setPublishedDate(articlePublishedDate(newsItem.getPageUrl()));

                        newsItems.add(newsItem);
                        //Mapping current articles url path to its corresponding newsItem object to handle duplicates
                        pathMap.put(articleTitleURL.select("a").attr("href"), newsItems.size() - 1);
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /* Retrieves a specified article's published date and time */
    protected LocalDateTime articlePublishedDate(String url) {
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
