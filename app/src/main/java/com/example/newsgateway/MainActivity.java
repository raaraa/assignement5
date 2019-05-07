package com.example.newsgateway;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.PersistableBundle;
import android.support.annotation.ColorInt;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    static final String REQUEST_ARTICLES = "REQUEST_ARTICLES";
    static final String RESPONSE_ARTICLES = "RESPONSE_ARTICLES";

    private NewsReceiver newsReceiver;

    private ArrayList<NewsSource> source_list = new ArrayList<>();

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private ListView mDrawerList;
    private List<Fragment> fragments;
    private MyPageAdapter pageAdapter;
    private ViewPager pager;
    private ArrayAdapter<NewsSource> sourceArrayAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = new Intent(MainActivity.this, ServiceNews.class);
        startService(intent);

        newsReceiver = new NewsReceiver();

        registerReceiver(newsReceiver, new IntentFilter(RESPONSE_ARTICLES));
        registerReceiver(newsReceiver, new IntentFilter(REQUEST_ARTICLES));

        mDrawerLayout = findViewById(R.id.drawer_layout);
        mDrawerList = findViewById(R.id.left_drawer);
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
        );

        sourceArrayAdapter = new ArrayAdapter<>(this, R.layout.drawer_list_item, source_list);
        mDrawerList.setAdapter(sourceArrayAdapter);
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectSource(position);
            }
        });
        mDrawerToggle.syncState();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        AsynchSource asynchSource = new AsynchSource(this);
        asynchSource.execute("");

        fragments = new ArrayList<>();
        pageAdapter = new MyPageAdapter(getSupportFragmentManager());
        pager = findViewById(R.id.viewpager);
        pager.setAdapter(pageAdapter);

        if (savedInstanceState != null){
            fragments = (List<Fragment>) savedInstanceState.getSerializable("fragments");
            setTitle(savedInstanceState.getString("title"));
            pageAdapter.notifyDataSetChanged();
            for (int i = 0; i< pageAdapter.getCount(); i++) pageAdapter.notifyChangeInPosition(i);
        }
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState,  PersistableBundle persistentState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        //TODO: setup enums for categories to colors
        Deque<Integer> stack = new ArrayDeque<>();

        stack.add(Color.BLACK);
        stack.add(Color.rgb(247, 179, 86)); //gold
        stack.add(Color.rgb(181, 182, 224)); //light blue
        stack.add(Color.rgb(63, 125, 77)); //green
        stack.add(Color.rgb(203, 30, 43)); //red
        stack.add(Color.rgb(100, 145, 144)); //cyan
        stack.add(Color.rgb(162, 127, 160)); //purpple
        stack.add(Color.rgb(212, 65, 214)); //magenta

        for(int i = 0; i < menu.size(); i++) {
            MenuItem item = menu.getItem(i);
            SpannableString spanString = new SpannableString(menu.getItem(i).getTitle().toString());
            spanString.setSpan(new ForegroundColorSpan(stack.pop()), 0, spanString.length(), 0); //fix the color to white
            item.setTitle(spanString);
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(newsReceiver);
        Intent intent = new Intent(MainActivity.this, ServiceNews.class);
        stopService(intent);
        super.onDestroy();
    }

    private void selectSource(int position) {
        setTitle(source_list.get(position).getName());

        Intent requestIntent = new Intent();
        requestIntent.setAction(MainActivity.REQUEST_ARTICLES);
        requestIntent.putExtra("source", source_list.get(position).getId());
        sendBroadcast(requestIntent);
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) return true;

        AsynchSource asynchSource = new AsynchSource(this);

        if (item.toString().equals("all")) {
            asynchSource.execute("");
        }
        else {
            asynchSource.execute(item.toString());
        }
        return true;
    }

    public void addSource(NewsSource newsSource){
        source_list.add(newsSource);
        sourceArrayAdapter.notifyDataSetChanged();
    }

    public void Clearout(){
        source_list.clear();
    }




    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable("fragments", (Serializable) fragments);
        outState.putString("title",getTitle().toString());
        super.onSaveInstanceState(outState);
    }


    public class NewsReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()){
                case RESPONSE_ARTICLES:
                    ArrayList<Article> all_articles = (ArrayList<Article>) intent.getSerializableExtra("articles");
                    fragments.clear();
                    for (int i = 0; i<10; i++){
                        fragments.add(FragementArticle.newInstance(all_articles.get(i+1)));
                        pageAdapter.notifyChangeInPosition(i);
                    }
                    pageAdapter.notifyDataSetChanged();
                    pager.setCurrentItem(0);
            }
        }
    }





    private class MyPageAdapter extends FragmentPagerAdapter {
        private long id = 0;
        public MyPageAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Override
        public long getItemId(int position) {
            return id+position;
        }

        public void notifyChangeInPosition(int idx) {
            id += getCount() + idx;
        }
    }
}
