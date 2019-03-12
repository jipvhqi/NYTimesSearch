package example.oath.com.nytimessearch.models;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.util.List;

@Parcel
public class Article {

    @Parcel
    public static class Headline {
        @SerializedName("content_kicker")
        String contentKicker;
        String kicker;
        String main;
        String name;
        @SerializedName("print_headline")
        String printHeadline;
        String seo;
        String sub;

        // empty constructor needed by the Parceler library
        public Headline() {
        }
    }

    @Parcel
    public static class Multimedia {
        String caption;
        String credit;
        @SerializedName("crop_name")
        String cropName;
        Integer height;
        Integer width;
        Integer rank;
        String subtype;
        String type;
        String url;

        // empty constructor needed by the Parceler library
        public Multimedia() {
        }
    }

    // fields must be package private
    @SerializedName("web_url")
    String webURL;
    Headline headline;
    List<Multimedia> multimedia;
    String snippet;
    @SerializedName("news_desk")
    String newsDesk;

    // empty constructor needed by the Parceler library
    public Article() {
    }

    public String getWebURL() {
        return webURL;
    }

    public String getHeadline() {
        return headline.main;
    }

    public List<Multimedia> getMultimedia() {
        return multimedia;
    }

    public String getSnippet() {
        return snippet;
    }

    public String getNewsDesk() {
        return newsDesk;
    }

    public String getThumbNail() {
        if (multimedia.size() > 0) {
            return "http://www.nytimes.com/".concat(multimedia.get(0).url);
        } else {
            return "";
        }
    }

    public static Article parseJSON(String response) {
        Gson gson = new GsonBuilder().create();
        Article articleResponse = gson.fromJson(response, Article.class);
        return articleResponse;
    }
}
