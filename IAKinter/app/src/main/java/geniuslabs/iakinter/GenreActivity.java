package geniuslabs.iakinter;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class GenreActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_genre);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Genre");
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}

