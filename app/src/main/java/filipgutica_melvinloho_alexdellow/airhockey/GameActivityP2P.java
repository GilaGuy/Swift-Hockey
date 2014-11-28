package filipgutica_melvinloho_alexdellow.airhockey;

import android.os.Bundle;
import android.widget.TextView;


public class GameActivityP2P extends GameActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_game_p2p);

        scoreTop = (TextView) findViewById(R.id.scoreTopTxt);
        scoreBot = (TextView) findViewById(R.id.scoreBotTxt);
    }
}
