package teamturbo.swifthockey;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import insertcreativecompanynamehere.swifthockey.R;

public class MainActivity extends Activity {
    private MediaPlayer mp = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startMusic();
    }

    public void onClickGameMode(View v) {
        Intent intent;

        switch (v.getId()) {
            case R.id.btn_gamemode_twoPlayer:
                intent = new Intent(this, GameActivitySP.class);
                intent.putExtra(GameActivitySP.TYPE, GameActivitySP.TYPE_2P);
                break;
            case R.id.btn_gamemode_AI:
                intent = new Intent(this, GameActivitySP.class);
                intent.putExtra(GameActivitySP.TYPE, GameActivitySP.TYPE_AI);
                break;
            case R.id.btn_gamemode_multiPlayer:
                intent = new Intent(this, GameActivityMP.class);
                break;
            default:
                return;
        }

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
    public void onResume() {
        super.onResume();

        startMusic();
    }

    @Override
    public void onPause() {
        pauseMusic();

        super.onPause();
    }

    @Override
    public void onStop() {
        stopMusic();

        super.onStop();
    }

    public void startMusic() {
        if (mp == null) {
            mp = MediaPlayer.create(getApplicationContext(), R.raw.bgm_waiting_loop);
            mp.setLooping(true);
        }

        mp.start();
    }

    public void pauseMusic() {
        if (mp != null && mp.isPlaying()) {
            mp.pause();
        }
    }

    public void stopMusic() {
        if (mp != null && mp.isPlaying()) {
            mp.stop();
            mp.release();
            mp = null;
        }
    }
}
