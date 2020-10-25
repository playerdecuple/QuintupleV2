package com.DecupleProject.API.Game.Steam;

public enum SearchMode {

    RELEVANCE("_ASC"), RELEASEDATE("Released_DESC"), NAME("Name_ASC"),
    LOWESTPRITCE("Price_ASC"), HIGHESTPRICE("Price_DESC"), REVIEWS("Reviews_DESC");

    private final String sortBy;

    SearchMode(String sortBy) {
        this.sortBy = sortBy;
    }

    String getSortBy() {
        return sortBy;
    }
}
