package babymonitor.example.com.babymonitor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import babymonitor.example.com.babymonitor.fragments.GraphFragment;
import babymonitor.example.com.babymonitor.fragments.MainFragment;
import babymonitor.example.com.babymonitor.fragments.NavigationDrawerFragment;
import babymonitor.example.com.babymonitor.fragments.SettingsFragment;
import babymonitor.example.com.babymonitor.services.TemperatureMonitorService;


public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    private MainActivity.TemperatureReceiver receiver;

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    private MainFragment mainFragment;
    private GraphFragment graphFragment;
    private SettingsFragment settingsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // set up app layout and UI structure
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
        mTitle = getTitle();

        // set up temperature broadcast receiver
        this.receiver = new TemperatureReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(TemperatureMonitorService.ON_RECEIVE_DATA);
        registerReceiver(receiver, intentFilter);

        // start temperature monitoring service
        Intent i = new Intent(getApplicationContext(), TemperatureMonitorService.class);
        getApplicationContext().startService(i);

    }

    private void initFragments() {
        mainFragment = MainFragment.newInstance();
        graphFragment = GraphFragment.newInstance();
        settingsFragment = SettingsFragment.newInstance();
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        initFragments();
        FragmentManager fragmentManager = getSupportFragmentManager();

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        switch (position) {
            case 0:
                transaction.replace(R.id.container, mainFragment);
                break;
            case 1:
                transaction.replace(R.id.container, graphFragment);
                break;
            case 2:
                transaction.replace(R.id.container, settingsFragment);
                break;
        }
        transaction.commit();
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setTitle(mTitle);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void updateTemperature(long temperature) {
        // main fragment doesn't necessarily have to be visible for temperature widget to be updated with new value
        if (mainFragment != null) {
            mainFragment.setTemperature(temperature);
        }
    }

    /**
     * Open the web interface in a browser.
     * https://stackoverflow.com/questions/3004515/android-sending-an-intent-to-browser-to-open-specific-url
     * @param item
     */
    public void openBrowser(MenuItem item) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(MainFragment.url));
        this.startActivity(i);
    }

    class TemperatureReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            long temperature = intent.getLongExtra("temperature", 0L);
//            Toast.makeText(context, "Latest Temperature: " + temperature, Toast.LENGTH_LONG).show();
            System.out.println("Current Temperature: " + temperature);
            MainActivity.this.updateTemperature(temperature);
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(this.receiver);
    }
}
