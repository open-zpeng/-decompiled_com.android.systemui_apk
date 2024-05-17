package com.badlogic.gdx.backends.android;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import com.badlogic.gdx.Audio;
import com.badlogic.gdx.Files;
import com.badlogic.gdx.audio.AudioDevice;
import com.badlogic.gdx.audio.AudioRecorder;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.xiaopeng.speech.speechwidget.ListWidget;
import java.io.FileDescriptor;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
/* loaded from: classes21.dex */
public final class AndroidAudio implements Audio {
    private final AudioManager manager;
    protected final List<AndroidMusic> musics = new ArrayList();
    private final SoundPool soundPool;

    public AndroidAudio(Context context, AndroidApplicationConfiguration config) {
        if (!config.disableAudio) {
            this.soundPool = new SoundPool(config.maxSimultaneousSounds, 3, 100);
            this.manager = (AudioManager) context.getSystemService(ListWidget.EXTRA_TYPE_AUDIO);
            if (context instanceof Activity) {
                ((Activity) context).setVolumeControlStream(3);
                return;
            }
            return;
        }
        this.soundPool = null;
        this.manager = null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void pause() {
        if (this.soundPool == null) {
            return;
        }
        synchronized (this.musics) {
            for (AndroidMusic music : this.musics) {
                if (music.isPlaying()) {
                    music.pause();
                    music.wasPlaying = true;
                } else {
                    music.wasPlaying = false;
                }
            }
        }
        this.soundPool.autoPause();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void resume() {
        if (this.soundPool == null) {
            return;
        }
        synchronized (this.musics) {
            for (int i = 0; i < this.musics.size(); i++) {
                if (this.musics.get(i).wasPlaying) {
                    this.musics.get(i).play();
                }
            }
        }
        this.soundPool.autoResume();
    }

    @Override // com.badlogic.gdx.Audio
    public AudioDevice newAudioDevice(int samplingRate, boolean isMono) {
        if (this.soundPool == null) {
            throw new GdxRuntimeException("Android audio is not enabled by the application config.");
        }
        return new AndroidAudioDevice(samplingRate, isMono);
    }

    @Override // com.badlogic.gdx.Audio
    public Music newMusic(FileHandle file) {
        if (this.soundPool == null) {
            throw new GdxRuntimeException("Android audio is not enabled by the application config.");
        }
        AndroidFileHandle aHandle = (AndroidFileHandle) file;
        MediaPlayer mediaPlayer = new MediaPlayer();
        if (aHandle.type() == Files.FileType.Internal) {
            try {
                AssetFileDescriptor descriptor = aHandle.getAssetFileDescriptor();
                mediaPlayer.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength());
                descriptor.close();
                mediaPlayer.prepare();
                AndroidMusic music = new AndroidMusic(this, mediaPlayer);
                synchronized (this.musics) {
                    this.musics.add(music);
                }
                return music;
            } catch (Exception ex) {
                throw new GdxRuntimeException("Error loading audio file: " + file + "\nNote: Internal audio files must be placed in the assets directory.", ex);
            }
        }
        try {
            mediaPlayer.setDataSource(aHandle.file().getPath());
            mediaPlayer.prepare();
            AndroidMusic music2 = new AndroidMusic(this, mediaPlayer);
            synchronized (this.musics) {
                this.musics.add(music2);
            }
            return music2;
        } catch (Exception ex2) {
            throw new GdxRuntimeException("Error loading audio file: " + file, ex2);
        }
    }

    public Music newMusic(FileDescriptor fd) {
        if (this.soundPool == null) {
            throw new GdxRuntimeException("Android audio is not enabled by the application config.");
        }
        MediaPlayer mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(fd);
            mediaPlayer.prepare();
            AndroidMusic music = new AndroidMusic(this, mediaPlayer);
            synchronized (this.musics) {
                this.musics.add(music);
            }
            return music;
        } catch (Exception ex) {
            throw new GdxRuntimeException("Error loading audio from FileDescriptor", ex);
        }
    }

    @Override // com.badlogic.gdx.Audio
    public Sound newSound(FileHandle file) {
        if (this.soundPool == null) {
            throw new GdxRuntimeException("Android audio is not enabled by the application config.");
        }
        AndroidFileHandle aHandle = (AndroidFileHandle) file;
        if (aHandle.type() == Files.FileType.Internal) {
            try {
                AssetFileDescriptor descriptor = aHandle.getAssetFileDescriptor();
                AndroidSound sound = new AndroidSound(this.soundPool, this.manager, this.soundPool.load(descriptor, 1));
                descriptor.close();
                return sound;
            } catch (IOException ex) {
                throw new GdxRuntimeException("Error loading audio file: " + file + "\nNote: Internal audio files must be placed in the assets directory.", ex);
            }
        }
        try {
            return new AndroidSound(this.soundPool, this.manager, this.soundPool.load(aHandle.file().getPath(), 1));
        } catch (Exception ex2) {
            throw new GdxRuntimeException("Error loading audio file: " + file, ex2);
        }
    }

    @Override // com.badlogic.gdx.Audio
    public AudioRecorder newAudioRecorder(int samplingRate, boolean isMono) {
        if (this.soundPool == null) {
            throw new GdxRuntimeException("Android audio is not enabled by the application config.");
        }
        return new AndroidAudioRecorder(samplingRate, isMono);
    }

    public void dispose() {
        if (this.soundPool == null) {
            return;
        }
        synchronized (this.musics) {
            ArrayList<AndroidMusic> musicsCopy = new ArrayList<>(this.musics);
            Iterator<AndroidMusic> it = musicsCopy.iterator();
            while (it.hasNext()) {
                AndroidMusic music = it.next();
                music.dispose();
            }
        }
        this.soundPool.release();
    }
}
