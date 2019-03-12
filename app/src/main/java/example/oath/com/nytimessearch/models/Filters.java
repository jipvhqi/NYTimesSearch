package example.oath.com.nytimessearch.models;

import java.util.ArrayList;
import java.util.Calendar;

public class Filters {

    Calendar beginDate;
    String sortOrder;
    ArrayList<String> newsDesk;

    public Filters() {
        beginDate = null;
        sortOrder = null;
        newsDesk = new ArrayList<>();
    }

    public void setBeginDate(Calendar beginDate) {
        this.beginDate = beginDate;
    }

    public void setSortOrder(String sortOrder) {
        this.sortOrder = sortOrder;
    }

    public void addNewsDesk(String newsDesk) {
        this.newsDesk.add(newsDesk);
    }

    public Calendar getBeginDate() {
        return beginDate;
    }

    public String getSortOrder() {
        return sortOrder;
    }

    public ArrayList<String> getNewsDesk() {
        return newsDesk;
    }
}
