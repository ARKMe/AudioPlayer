package bello.andrea.audioplayer;

import android.content.ContentUris;
import android.net.Uri;

public class MusicItem {
    private String title;
    private String author;
    private long duration;
    private long id;

    public MusicItem(String title, String author, long duration, long id) {
        this.title = title;
        this.author = author;
        this.duration = duration;
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Uri getURI() {
        return ContentUris.withAppendedId(android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id);
    }
}
