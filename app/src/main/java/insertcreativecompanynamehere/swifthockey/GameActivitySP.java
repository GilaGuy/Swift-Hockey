package insertcreativecompanynamehere.swifthockey;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.TextView;

public class GameActivitySP extends Activity
{
    public static final String TYPE = "GameActivityType";
    public static final int TYPE_2P = 0;
    public static final int TYPE_AI = 1;

    public static TextView scoreTop, scoreBot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_game);

        scoreTop = (TextView) findViewById(R.id.scoreTopTxt);
        scoreBot = (TextView) findViewById(R.id.scoreBotTxt);

        FrameLayout hockeyArenaContainer = ((FrameLayout)findViewById(R.id.hockeyArenaContainer));
        switch (getIntent().getIntExtra(GameActivitySP.TYPE, 0))
        {
            case TYPE_2P:
                hockeyArenaContainer.addView(new HockeyArenaSP2P(getApplicationContext()));
                break;
            case TYPE_AI:
                hockeyArenaContainer.addView(new HockeyArenaSPAI(getApplicationContext()));
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onDestroy();
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.game, menu);
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
}
