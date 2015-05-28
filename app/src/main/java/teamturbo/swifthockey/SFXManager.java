package teamturbo.swifthockey;

import android.media.AudioManager;
import android.media.SoundPool;
import android.view.View;

import java.util.Random;

import insertcreativecompanynamehere.swifthockey.R;

/**
 * Created by Filip on 2014-12-05.
 */
public class SFXManager {

    protected static SoundPool sp;
    protected static int sound_verynice;
    protected static int sound_nevergetthis;
    protected static int sound_bounces[];

    protected static Random rand = new Random();

    public static void initSounds(View v) {

        cleanupSounds();

        sp = new SoundPool(24, AudioManager.STREAM_MUSIC, 0);
        sound_verynice = sp.load(v.getContext(), R.raw.verynice, 1);
        sound_nevergetthis = sp.load(v.getContext(), R.raw.nevergetthis, 1);
        sound_bounces = new int[]{
                sp.load(v.getContext(), R.raw.bounce_01, 1),
                sp.load(v.getContext(), R.raw.bounce_02, 1),
                sp.load(v.getContext(), R.raw.bounce_03, 1),
                sp.load(v.getContext(), R.raw.bounce_04, 1)
        };
    }

    public static void sfx_bounce(Ball b) {
        float volumex = Math.abs(b.speed_x) / Ball.MAX_SPEED.x;
        float volumey = Math.abs(b.speed_y) / Ball.MAX_SPEED.y;
        float volume_final = Math.max(volumex, volumey);

        if (volume_final > 0.3)
            sp.play(sound_bounces[rand.nextInt(sound_bounces.length)],
                    volume_final, volume_final,
                    0, 0, 1);
    }

    public static void sfx_verynice() {
        sp.play(sound_verynice, 1, 1, 0, 0, 1);
    }

    public static void sfx_nevergetthis() {
        sp.play(sound_nevergetthis, 1, 1, 0, 0, 1);
    }

    public static void cleanupSounds() {
        if (sp != null) sp.release();
    }
}
