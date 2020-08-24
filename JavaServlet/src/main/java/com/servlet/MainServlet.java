package com.servlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

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
    private List<NewsItem> newsItems;
    private boolean isCached = false;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.addHeader("Access-Control-Allow-Origin", "*");
        PrintWriter out = response.getWriter();

        //Check for cached data to return
        if(isCached)
        {
            out.print(convertToJSON(newsItems));
            out.flush();
        }
        else {

            newsItems = new ArrayList<NewsItem>();
            int sectionsLen = timeMagSections.length;
            //Go through each Time Section and add scraped data to list
            for (int i = 0; i < sectionsLen; i++) {
                newsItems.addAll(scrapeUrl(baseURL + timeMagSections[i], timeMagCategories[i]));
            }

            //Set flag for next request to use cached data
            isCached = true;
            out.print(convertToJSON(newsItems));
            out.flush();
        }
    }

    /* Converts NewsItem list to a JSON string */
    protected String convertToJSON(List<NewsItem> newsItems)
    {
        // Convert NewsItem list to a JSON string
        Gson gson = new GsonBuilder().disableHtmlEscaping().create();
        return gson.toJson(newsItems);
    }

    /*Uses url to connect and scrape key information*/
    protected List<NewsItem> scrapeUrl(String url, String category) {
        try {
            //Fetch the website and store it in Document object
            Document doc = Jsoup.connect(url).get();

            //Retrieve all articles from Document object
            Elements articles = doc.getElementsByTag("article");

            List newsItems = new ArrayList<NewsItem>();
            NewsItem newsItem = null;
            //Go through each article and scrape key data
            for (Element article: articles)
            {
                newsItem = new NewsItem();

                Elements articleTitleURL = article.select("h3");
                Elements articleExcerpt = article.getElementsByClass("summary margin-8-bottom desktop-only");
                Elements articleImgURL = article.getElementsByClass("media-img margin-16-bottom");

                newsItem.setCategory(category);
                newsItem.setTitle(articleTitleURL.select("a").text());
                newsItem.setPageUrl(baseURL + articleTitleURL.select("a").attr("href"));
                newsItem.setExcerpt(articleExcerpt.text());
                newsItem.setImgUrl(articleImgURL.select("div").attr("data-src"));

                newsItems.add(newsItem);
            }
            return newsItems;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }
}
