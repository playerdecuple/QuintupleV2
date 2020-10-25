package com.DecupleProject.API.Game.Steam;

import com.DecupleProject.Core.Util.EasyEqual;

import java.util.ArrayList;

public class GameInfo {

    // This code was based on steam-api(made by 'ugurcany').
    // GITHUB PAGE URL : https://github.com/ugurcany/steam-api/

    private final EasyEqual e = new EasyEqual();
    private String id, title, price, discount, discountedPrice, reviewSummary, description, headerImageURL, releaseDate, addedOn, thumbnailURL, metaScore;
    private ArrayList<String> tags, platforms, screenshotURLs, details;

    protected GameInfo(String id, String title, String price, String discount, String discountedPrice,
                       String reviewSummary, ArrayList<String> platforms, String addedOn, String thumbnailURL, ArrayList<String> tags,
                       String description, String releaseDate, String metaScore) {

        this.id = id;
        this.title = title;
        this.price = e.eq(price, "") ? "?" : price;
        this.discount = e.eq(discount, "") ? "?" : discount;
        this.discountedPrice = e.eq(discountedPrice, "") ? "0%" : discountedPrice;
        this.reviewSummary = e.eq(reviewSummary, "") ? "?" : reviewSummary;
        this.platforms = platforms;
        this.addedOn = e.eq(addedOn, "") ? "?" : addedOn;
        this.thumbnailURL = e.eq(thumbnailURL, "") ? "?" : thumbnailURL;
        this.description = description;
        this.headerImageURL = "?";
        this.screenshotURLs = null;
        this.releaseDate = releaseDate;
        this.metaScore = metaScore;
        this.details = null;
        this.tags = tags;

    }

    public String getId() {
        return id;
    }

    protected void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    protected void setTitle(String title) {
        this.title = title;
    }

    public String getPrice() {
        return price;
    }

    protected void setPrice(String price) {
        this.price = price;
    }

    public String getDiscountedPrice() {
        return discountedPrice;
    }

    protected void setDiscountedPrice(String discountedPrice) {
        this.discountedPrice = discountedPrice;
    }

    public String getDiscount() {
        return discount;
    }

    protected void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getReviewSummary() {
        return reviewSummary;
    }

    protected void setReviewSummary(String reviewSummary) {
        this.reviewSummary = reviewSummary;
    }

    public ArrayList<String> getPlatforms() {
        return platforms;
    }

    protected void setPlatforms(ArrayList<String> platforms) {
        this.platforms = platforms;
    }

    public String getAddedOn() {
        return addedOn;
    }

    protected void setAddedOn(String addedOn) {
        this.addedOn = addedOn;
    }

    public String getDescription() {
        return description;
    }

    protected void setDescription(String description) {
        this.description = description;
    }

    public String getHeaderImageURL() {
        return headerImageURL;
    }

    protected void setHeaderImageURL(String headerImageURL) {
        this.headerImageURL = headerImageURL;
    }

    public ArrayList<String> getScreenshotURLs() {
        return screenshotURLs;
    }

    protected void setScreenshotURLs(ArrayList<String> screenshotURLs) {
        this.screenshotURLs = screenshotURLs;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getMetaScore() {
        return metaScore;
    }

    public void setMetaScore(String metaScore) {
        this.metaScore = metaScore;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
    }

    public ArrayList<String> getDetails() {
        return details;
    }

    public void setDetails(ArrayList<String> details) {
        this.details = details;
    }

    public String getThumbnailURL() {
        return thumbnailURL;
    }

    public void setThumbnailURL(String url) {
        this.thumbnailURL = url;
    }

}