package insertcreativecompanynamehere.swifthockey;

import android.os.Bundle;
import android.widget.TextView;


public class GameActivityAI extends GameActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_game_ai);

        scoreTop = (TextView) findViewById(R.id.scoreTopTxt);
        scoreBot = (TextView) findViewById(R.id.scoreBotTxt);
    }
}
