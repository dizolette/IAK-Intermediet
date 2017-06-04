package geniuslabs.iakinter;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import geniuslabs.iakinter.config.Adapter;
import geniuslabs.iakinter.config.Server;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    final Context context = this;
    private Menu menu ;
    private String json_result;
    private String link = Server.link + "latest.php?title=";
    private ListView list_movie;
    private GridView grid_movie;
    Adapter simpleAdapter;

    private EditText edit_search;
    private ProgressBar progress_bar;
    private TextView text_notif;

    public static String video_id, episode_id, streaming, title, summary, eps, android_id,cover;

    ArrayList<HashMap<String, String>> animes = new ArrayList<HashMap<String, String>>();
    boolean grid = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        video_id = ""; episode_id = ""; streaming = ""; title = ""; eps = ""; summary = ""; cover = "";
        android_id = Settings.Secure.getString(this.getApplicationContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);
        Log.d("Log android_id", android_id);

        grid_movie      = (GridView) findViewById(R.id.grid_movie);
        list_movie      = (ListView) findViewById(R.id.list_movie);
        progress_bar    = (ProgressBar) findViewById(R.id.progress_bar);
        text_notif      = (TextView) findViewById(R.id.text_notif);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Log.d("Log link", link);
        AccessService();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_baru, menu);
        this.menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.List) {
            if (grid == true){
                progress_bar.setVisibility(View.VISIBLE);
                list_movie.setVisibility(View.VISIBLE);
                grid_movie.setVisibility(View.GONE);
                menu.getItem(0).setIcon(getResources().getDrawable(R.drawable.grid));
                grid = false;

                animes.clear(); list_movie.setAdapter(null);
                Log.d("Log link", link);
                AccessService();
            }else{
                progress_bar.setVisibility(View.VISIBLE);
                list_movie.setVisibility(View.GONE);
                grid_movie.setVisibility(View.VISIBLE);
                menu.getItem(0).setIcon(getResources().getDrawable(R.drawable.list));
                grid = true;

                animes.clear(); list_movie.setAdapter(null);
                Log.d("Log link", link);
                AccessService();
            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //script web service
    private class JsonReadTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(params[0]);
            try {
                HttpResponse response = httpclient.execute(httppost);
                json_result = inputStreamToString(
                        response.getEntity().getContent()).toString();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
        protected void onPostExecute(String result) {
            JsonResponses();
        }
    }

    private StringBuilder inputStreamToString(InputStream is) {
        String rLine = "";
        StringBuilder answer = new StringBuilder();
        BufferedReader rd = new BufferedReader(new InputStreamReader(is));
        try {
            while ((rLine = rd.readLine()) != null) {
                answer.append(rLine);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return answer;
    }

    public void AccessService() {
        JsonReadTask task = new JsonReadTask();
        task.execute(new String[]{link});
    }

    public void JsonResponses() {
        animes.clear(); list_movie.setAdapter(null); grid_movie.setAdapter(null);
        progress_bar.setVisibility(View.VISIBLE);
        text_notif.setVisibility(View.GONE);
        try {
            JSONObject jsonResponse = new JSONObject(json_result);
            JSONArray jsonMainNode = jsonResponse.optJSONArray("result");

            int i;
            for (i = 0; i < jsonMainNode.length(); i++) {
                progress_bar.setVisibility(View.GONE);

                JSONObject jsonChildNode    = jsonMainNode.getJSONObject(i);
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("cover",     jsonChildNode.optString("image"));
                map.put("video_id",     jsonChildNode.optString("video_id"));
                map.put("title",     jsonChildNode.optString("title"));
                map.put("summary",
                        jsonChildNode.optString("summary") + "\n\n" +
                                "Genres : " + jsonChildNode.optString("genres")
                );
                map.put("genres",     jsonChildNode.optString("genres"));
                map.put("image",     jsonChildNode.optString("image"));
                map.put("view",
                        jsonChildNode.optString("eps") + " Eps " +
                                jsonChildNode.optString("view") + " views"
                );
                map.put("created",    jsonChildNode.optString("created"));

                animes.add(map);
            }

            if (i == 0){
                progress_bar.setVisibility(View.GONE);
                text_notif.setVisibility(View.VISIBLE);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(grid == true){

            simpleAdapter = new Adapter(this, animes, R.layout.adapter_grid, new String[] { "cover", "video_id", "title", "summary", "genres", "image", "view", "created"},
                    new int[] {R.id.text_cover, R.id.text_video_id, R.id.text_title, R.id.text_summary, R.id.text_genres, R.id.img_image,
                            R.id.text_views, R.id.text_created});

            grid_movie.setAdapter(simpleAdapter);
            grid_movie.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                    interstitialAd.show();
                    cover       = ((TextView) view.findViewById(R.id.text_cover)).getText().toString();
                    video_id    = ((TextView) view.findViewById(R.id.text_video_id)).getText().toString();
                    title       = ((TextView) view.findViewById(R.id.text_title)).getText().toString();
                    summary     = ((TextView) view.findViewById(R.id.text_summary)).getText().toString();
                    eps         = ((TextView) view.findViewById(R.id.text_views)).getText().toString();
                    startActivity(new Intent(MainActivity.this, DetailActivity.class));
                }
            });

        }else{

            simpleAdapter = new Adapter(this, animes, R.layout.adapter_list,
                    new String[] { "cover", "video_id", "title", "summary", "genres", "image", "view", "created"},
                    new int[] {R.id.text_cover, R.id.text_video_id, R.id.text_title, R.id.text_summary, R.id.text_genres, R.id.img_image,
                            R.id.text_views, R.id.text_created});

            list_movie.setAdapter(simpleAdapter);
            list_movie.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                    interstitialAd.show();
                    cover       = ((TextView) view.findViewById(R.id.text_cover)).getText().toString();
                    video_id    = ((TextView) view.findViewById(R.id.text_video_id)).getText().toString();
                    title       = ((TextView) view.findViewById(R.id.text_title)).getText().toString();
                    summary     = ((TextView) view.findViewById(R.id.text_summary)).getText().toString();
                    eps         = ((TextView) view.findViewById(R.id.text_views)).getText().toString();
                    startActivity(new Intent(MainActivity.this, DetailActivity.class));
                }
            });
        }

    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_genres) {
            startActivity(new Intent(MainActivity.this,GenreActivity.class));
        } else if (id == R.id.nav_latest) {
            getSupportActionBar().setTitle("Latest");
        } else if (id == R.id.nav_bookmark) {
            startActivity(new Intent(context,BookmarkActivity.class));
        } else if (id == R.id.nav_new) {
            getSupportActionBar().setTitle("New");

        } else if (id == R.id.nav_refresh) {
            Toast.makeText(context, "Refresh", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_ongoing) {
            getSupportActionBar().setTitle("On Going");
        } else if (id == R.id.nav_bug) {
            startActivity(new Intent(MainActivity.this,ReportActivity.class));
        } else if (id == R.id.nav_about) { getSupportActionBar().setTitle("About");
            startActivity(new Intent(MainActivity.this,AboutActivity.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
