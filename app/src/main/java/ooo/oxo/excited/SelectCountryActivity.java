package ooo.oxo.excited;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import ooo.oxo.excited.login.CountriesAdapter;
import ooo.oxo.excited.view.SideBar;

public class SelectCountryActivity extends AppCompatActivity implements SideBar.OnTouchingLetterChangedListener, CountriesAdapter.OnItemSelectedListener {
    private SideBar sideBar;
    private TextView floatingLetter;
    private RecyclerView recyclerView;

    private CountriesAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_country);

        sideBar = (SideBar) findViewById(R.id.side_bar);
        floatingLetter = (TextView) findViewById(R.id.floating_letter);
        recyclerView = (RecyclerView) findViewById(R.id.countries);

        sideBar.setOnTouchingLetterChangedListener(this);
        sideBar.setTextView(floatingLetter);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        adapter = new CountriesAdapter(this);
        recyclerView.setAdapter(adapter);

        adapter.setOnItemSelectedListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTouchingLetterChanged(String s) {
        int firstVisiblePosition = ((LinearLayoutManager) recyclerView.getLayoutManager())
                .findFirstVisibleItemPosition();
        int lastVisiblePosition = ((LinearLayoutManager) recyclerView.getLayoutManager())
                .findLastVisibleItemPosition();
        for (int i = 0; i < adapter.getItemCount(); i++)
            if (adapter.getItem(i).equals(s)) {
                if (i < firstVisiblePosition) {
                    recyclerView.smoothScrollToPosition(i);
                    return;
                } else {
                    for (; i < adapter.getItemCount(); i++)
                        if (!adapter.getIndex().get(adapter.getRealIndex(i)).equals(s)) {
                            if (lastVisiblePosition < i)
                                recyclerView.smoothScrollToPosition(i);
                            return;
                        }
                    recyclerView.smoothScrollToPosition(i - 1);
                }
            }
    }

    @Override
    public void onItemSelected(View itemView, View parent, int position) {
        Intent result = new Intent();
        result.putExtra("number", adapter.getNumbers().get(adapter.getRealIndex(position)));
        result.putExtra("country", adapter.getItem(position));
        setResult(0, result);
        finish();
    }
}
