package models;

import java.time.LocalDateTime;

public class NewsItem {
    private String title;
    private String excerpt;
    private String imgUrl;
    private String pageUrl;
    private String category;
    private LocalDateTime publishedDate;

    public NewsItem() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getExcerpt() {
        return excerpt;
    }

    public void setExcerpt(String excerpt) {
        this.excerpt = excerpt;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getPageUrl() {
        return pageUrl;
    }

    public void setPageUrl(String pageUrl) {
        this.pageUrl = pageUrl;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public LocalDateTime getPublishedDate() { return publishedDate; }

    public void setPublishedDate(LocalDateTime publishedDate) { this.publishedDate = publishedDate; }
}
