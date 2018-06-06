package com.nowfloats.Analytics_Screen.Graph.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Abhi on 11/8/2016.
 */

public class BotVisitResponse {

    @SerializedName("BotVisits")
    @Expose
    private BotVisits botVisits = new BotVisits();

    @SerializedName("TotalCount")
    @Expose
    private Long totalCount;

    public BotVisits getBotVisits() {
        return botVisits;
    }

    public void setBotVisits(BotVisits botVisits) {
        this.botVisits = botVisits;
    }

    public Long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Long totalCount) {
        this.totalCount = totalCount;
    }

    public class BotVisits {

        @SerializedName("GoogleBot")
        @Expose
        private Long googleBot;

        @SerializedName("BingBot")
        @Expose
        private Long bingBot;

        @SerializedName("YandexBot")
        @Expose
        private Long yandexBot;

        @SerializedName("SlurpBot")
        @Expose
        private Long slurpBot;

        @SerializedName("DuckDuckBot")
        @Expose
        private Long duckDuckBot;

        @SerializedName("Facebook")
        @Expose
        private Long facebook;

        @SerializedName("YahooBot")
        @Expose
        private Long yahooBot;

        @SerializedName("BaiduSpider")
        @Expose
        private Long baiduSpider;

        @SerializedName("Others")
        @Expose
        private Long others;

        public Long getGoogleBot() {
            return googleBot;
        }

        public void setGoogleBot(Long googleBot) {
            this.googleBot = googleBot;
        }

        public Long getBingBot() {
            return bingBot;
        }

        public void setBingBot(Long bingBot) {
            this.bingBot = bingBot;
        }

        public Long getYandexBot() {
            return yandexBot;
        }

        public void setYandexBot(Long yandexBot) {
            this.yandexBot = yandexBot;
        }

        public Long getSlurpBot() {
            return slurpBot;
        }

        public void setSlurpBot(Long slurpBot) {
            this.slurpBot = slurpBot;
        }

        public Long getDuckDuckBot() {
            return duckDuckBot;
        }

        public void setDuckDuckBot(Long duckDuckBot) {
            this.duckDuckBot = duckDuckBot;
        }

        public Long getFacebook() {
            return facebook;
        }

        public void setFacebook(Long facebook) {
            this.facebook = facebook;
        }

        public Long getYahooBot() {
            return yahooBot;
        }

        public void setYahooBot(Long yahooBot) {
            this.yahooBot = yahooBot;
        }

        public Long getBaiduSpider() {
            return baiduSpider;
        }

        public void setBaiduSpider(Long baiduSpider) {
            this.baiduSpider = baiduSpider;
        }

        public Long getOthers() {
            return others;
        }

        public void setOthers(Long others) {
            this.others = others;
        }
    }
}
