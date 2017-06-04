package geniuslabs.iakinter;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class BookmarkActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmark);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Bookmark");
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
