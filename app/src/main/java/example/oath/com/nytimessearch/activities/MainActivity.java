package example.oath.com.nytimessearch.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Locale;

import example.oath.com.nytimessearch.R;
import example.oath.com.nytimessearch.adapters.ArticleRecyclerAdapter;
import example.oath.com.nytimessearch.interfaces.NYTimesService;
import example.oath.com.nytimessearch.models.Article;
import example.oath.com.nytimessearch.fragments.EditFilterDialogFragment;
import example.oath.com.nytimessearch.models.Filters;
import example.oath.com.nytimessearch.modules.EndlessRecyclerViewScrollListener;
import example.oath.com.nytimessearch.utils.NetworkChecker;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Callback;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity {

    String query;
    ArrayList<Article> articles;
    ArticleRecyclerAdapter adapter;
    RecyclerView rvResults;
    EndlessRecyclerViewScrollListener scrollListener;
    Filters filters;
    StaggeredGridLayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        setupViews();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_main, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) searchItem.getActionView();

        // Configure the search info and add any event listeners...
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                onArticleSearch(searchView);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        MenuItem filterItem = menu.findItem(R.id.action_filter);
        filterItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                showFilterDialog();
                return true;
            }
        });

        return true;
    }

    public void setupViews() {
        layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);

        query = null;
        filters = new Filters();
        articles = new ArrayList<>();
        adapter = new ArticleRecyclerAdapter(this, articles);

        rvResults = (RecyclerView) findViewById(R.id.rvResults);
        rvResults.setAdapter(adapter);
        rvResults.setLayoutManager(layoutManager);

        scrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                loadArticlesFromApi(page);
            }
        };
        // Adds the scroll listener to RecyclerView
        rvResults.addOnScrollListener(scrollListener);
        loadArticlesFromApi(0);
    }

    private void showFilterDialog() {
        FragmentManager fm = getSupportFragmentManager();
        EditFilterDialogFragment editFilterDialogFragment = EditFilterDialogFragment.newInstance("Set Filters");
        editFilterDialogFragment.show(fm, "fragment_filters");
    }

    public void onArticleSearch(SearchView searchView) {
        // 1. First, clear the array of data
        articles.clear();
        // 2. Notify the adapter of the update
        adapter.notifyDataSetChanged(); // or notifyItemRangeRemoved
        // 3. Reset endless scroll listener when performing a new search
        scrollListener.resetState();
        query = searchView.getQuery().toString();
        loadArticlesFromApi(0);
    }

    public void loadArticlesFromApi(int page) {
        final String BASE_URL = "https://api.nytimes.com/";
        final String apiKey = "wJAzKnqMIscOxTarlg382oNA3c8BgY69";
        String beginDateQueryString = null;
        String sort = null;
        String newsDeskQuery = null;

        OkHttpClient client = new OkHttpClient();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(BASE_URL)
                .client(client)
                .build();
        final NYTimesService apiService = retrofit.create(NYTimesService.class);

        if (filters.getBeginDate() != null) {
            beginDateQueryString = new StringBuilder().append(String.format(Locale.ENGLISH, "%04d", filters.getBeginDate().get(Calendar.YEAR)))
                    .append(String.format(Locale.ENGLISH,"%02d", filters.getBeginDate().get(Calendar.MONTH)))
                    .append(String.format(Locale.ENGLISH,"%02d", filters.getBeginDate().get(Calendar.DAY_OF_MONTH)))
                    .toString();
        }

        if (filters.getSortOrder() != null && !filters.getSortOrder().isEmpty()) {
            sort = filters.getSortOrder().toLowerCase();
        }

        if (filters.getNewsDesk() != null && filters.getNewsDesk().size() > 0) {
            newsDeskQuery = "news_desk:(";
            Iterator it = filters.getNewsDesk().iterator();

            while (it.hasNext()) {
                newsDeskQuery = newsDeskQuery.concat("\"").concat(it.next().toString()).concat("\" ");
            }

            newsDeskQuery = newsDeskQuery.substring(0, newsDeskQuery.length() - 1).concat(")");
        }

        if (NetworkChecker.isNetworkAvailable(this) && NetworkChecker.isOnline()) {
            Call<ResponseBody> call = apiService.searchArticles(apiKey, query, beginDateQueryString, sort, newsDeskQuery, page);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                    String articlesJSONString = null;
                    try {
                        String responseString = response.body().string();
                        JSONObject json = new JSONObject(responseString);
                        articlesJSONString = json.getJSONObject("response").getJSONArray("docs").toString();
                        Type articleListType = new TypeToken<ArrayList<Article>>() {}.getType();
                        ArrayList<Article> articlesResult = new Gson().fromJson(articlesJSONString, articleListType);
                        articles.addAll(articlesResult);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    } catch (JSONException ex) {
                        ex.printStackTrace();
                    } catch (NullPointerException ex) {
                        ex.printStackTrace();
                    }
                    adapter.notifyDataSetChanged();
                    Log.d("DEBUG", articles.toString());
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Log.e("ERROR", call.toString());
                }
            });
        }
    }

    public void setFilters(Filters newFilters) {
        filters = newFilters;
    }

    public Filters getFilters() {
        return filters;
    }
}
