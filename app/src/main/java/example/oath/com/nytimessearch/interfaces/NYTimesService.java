package example.oath.com.nytimessearch.interfaces;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface NYTimesService {
    @GET("/svc/search/v2/articlesearch.json")
    Call<ResponseBody> searchArticles(@Query("api-key") String apiKey,
                                      @Query("q") String keyWord,
                                      @Query("begin_date") String beginDate,
                                      @Query("sort") String sortBy,
                                      @Query("fq") String filterQuery,
                                      @Query("page") Integer page);
}