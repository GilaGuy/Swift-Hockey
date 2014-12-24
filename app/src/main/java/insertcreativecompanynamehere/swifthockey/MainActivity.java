package insertcreativecompanynamehere.swifthockey;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import insertcreativecompanynamehere.swifthockey.wificonn.WiFiServiceDiscoveryActivity;


public class MainActivity extends Activity {

    private MediaPlayer mp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mp = MediaPlayer.create(getApplicationContext(), R.raw.bgm_game_loop);
        mp.setLooping(true);
        mp.start();
    }

    public void onClickTwoPlayer(View v)
    {
        Intent intent = new Intent(this, GameActivity.class);

        startActivity(intent);
    }

    public void onClickMultiPlay(View v) {
        Intent intent = new Intent(this, WiFiServiceDiscoveryActivity.class);

        startActivity(intent);
    }

    public void onClickAI(View v) {
        Intent intent = new Intent(this, GameActivityAI.class);

        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopMusic();
    }

    @Override
    public void onPause() {
        super.onPause();
        stopMusic();
    }

    public void stopMusic() {
        if(mp != null && mp.isPlaying()) {
            mp.stop();
            mp.release();
            mp = null;
        }
    }
}
