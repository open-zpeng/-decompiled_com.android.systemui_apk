package com.badlogic.gdx.backends.android;

import android.media.MediaPlayer;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import java.io.IOException;
/* loaded from: classes21.dex */
public class AndroidMusic implements Music, MediaPlayer.OnCompletionListener {
    private final AndroidAudio audio;
    private MediaPlayer player;
    private boolean isPrepared = true;
    protected boolean wasPlaying = false;
    private float volume = 1.0f;
    protected Music.OnCompletionListener onCompletionListener = null;

    /* JADX INFO: Access modifiers changed from: package-private */
    public AndroidMusic(AndroidAudio audio, MediaPlayer player) {
        this.audio = audio;
        this.player = player;
        this.player.setOnCompletionListener(this);
    }

    @Override // com.badlogic.gdx.audio.Music, com.badlogic.gdx.utils.Disposable
    public void dispose() {
        MediaPlayer mediaPlayer = this.player;
        if (mediaPlayer == null) {
            return;
        }
        try {
            mediaPlayer.release();
            this.player = null;
            this.onCompletionListener = null;
            synchronized (this.audio.musics) {
                this.audio.musics.remove(this);
            }
        } catch (Throwable th) {
            try {
                Gdx.app.log("AndroidMusic", "error while disposing AndroidMusic instance, non-fatal");
                this.player = null;
                this.onCompletionListener = null;
                synchronized (this.audio.musics) {
                    this.audio.musics.remove(this);
                }
            } catch (Throwable th2) {
                this.player = null;
                this.onCompletionListener = null;
                synchronized (this.audio.musics) {
                    this.audio.musics.remove(this);
                    throw th2;
                }
            }
        }
    }

    @Override // com.badlogic.gdx.audio.Music
    public boolean isLooping() {
        MediaPlayer mediaPlayer = this.player;
        if (mediaPlayer == null) {
            return false;
        }
        try {
            return mediaPlayer.isLooping();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override // com.badlogic.gdx.audio.Music
    public boolean isPlaying() {
        MediaPlayer mediaPlayer = this.player;
        if (mediaPlayer == null) {
            return false;
        }
        try {
            return mediaPlayer.isPlaying();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override // com.badlogic.gdx.audio.Music
    public void pause() {
        MediaPlayer mediaPlayer = this.player;
        if (mediaPlayer == null) {
            return;
        }
        try {
            if (mediaPlayer.isPlaying()) {
                this.player.pause();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.wasPlaying = false;
    }

    @Override // com.badlogic.gdx.audio.Music
    public void play() {
        MediaPlayer mediaPlayer = this.player;
        if (mediaPlayer == null) {
            return;
        }
        try {
            if (mediaPlayer.isPlaying()) {
                return;
            }
            try {
                if (!this.isPrepared) {
                    this.player.prepare();
                    this.isPrepared = true;
                }
                this.player.start();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (IllegalStateException e2) {
                e2.printStackTrace();
            }
        } catch (Exception e3) {
            e3.printStackTrace();
        }
    }

    @Override // com.badlogic.gdx.audio.Music
    public void setLooping(boolean isLooping) {
        MediaPlayer mediaPlayer = this.player;
        if (mediaPlayer == null) {
            return;
        }
        mediaPlayer.setLooping(isLooping);
    }

    @Override // com.badlogic.gdx.audio.Music
    public void setVolume(float volume) {
        MediaPlayer mediaPlayer = this.player;
        if (mediaPlayer == null) {
            return;
        }
        mediaPlayer.setVolume(volume, volume);
        this.volume = volume;
    }

    @Override // com.badlogic.gdx.audio.Music
    public float getVolume() {
        return this.volume;
    }

    @Override // com.badlogic.gdx.audio.Music
    public void setPan(float pan, float volume) {
        if (this.player == null) {
            return;
        }
        float leftVolume = volume;
        float rightVolume = volume;
        if (pan < 0.0f) {
            rightVolume *= 1.0f - Math.abs(pan);
        } else if (pan > 0.0f) {
            leftVolume *= 1.0f - Math.abs(pan);
        }
        this.player.setVolume(leftVolume, rightVolume);
        this.volume = volume;
    }

    @Override // com.badlogic.gdx.audio.Music
    public void stop() {
        MediaPlayer mediaPlayer = this.player;
        if (mediaPlayer == null) {
            return;
        }
        if (this.isPrepared) {
            mediaPlayer.seekTo(0);
        }
        this.player.stop();
        this.isPrepared = false;
    }

    @Override // com.badlogic.gdx.audio.Music
    public void setPosition(float position) {
        MediaPlayer mediaPlayer = this.player;
        if (mediaPlayer == null) {
            return;
        }
        try {
            if (!this.isPrepared) {
                mediaPlayer.prepare();
                this.isPrepared = true;
            }
            this.player.seekTo((int) (1000.0f * position));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IllegalStateException e2) {
            e2.printStackTrace();
        }
    }

    @Override // com.badlogic.gdx.audio.Music
    public float getPosition() {
        MediaPlayer mediaPlayer = this.player;
        if (mediaPlayer == null) {
            return 0.0f;
        }
        return mediaPlayer.getCurrentPosition() / 1000.0f;
    }

    public float getDuration() {
        MediaPlayer mediaPlayer = this.player;
        if (mediaPlayer == null) {
            return 0.0f;
        }
        return mediaPlayer.getDuration() / 1000.0f;
    }

    @Override // com.badlogic.gdx.audio.Music
    public void setOnCompletionListener(Music.OnCompletionListener listener) {
        this.onCompletionListener = listener;
    }

    @Override // android.media.MediaPlayer.OnCompletionListener
    public void onCompletion(MediaPlayer mp) {
        if (this.onCompletionListener != null) {
            Gdx.app.postRunnable(new Runnable() { // from class: com.badlogic.gdx.backends.android.AndroidMusic.1
                @Override // java.lang.Runnable
                public void run() {
                    AndroidMusic.this.onCompletionListener.onCompletion(AndroidMusic.this);
                }
            });
        }
    }
}
