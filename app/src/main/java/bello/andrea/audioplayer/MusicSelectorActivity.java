package bello.andrea.audioplayer;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class MusicSelectorActivity extends Activity {

    MusicAdapter musicAdapter;

    public  void requestWrapper() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                readMusicsItems();
            } else {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            }
        }
        else {
            readMusicsItems();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
            readMusicsItems();
        }
    }

    private void readMusicsItems() {
        Uri uri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor cur = getContentResolver().query(
                uri,
                null,
                MediaStore.Audio.Media.IS_MUSIC + " = 1",
                null,
                null
        );

        int artistColumn = cur.getColumnIndex(MediaStore.Audio.Media.ARTIST);
        int titleColumn = cur.getColumnIndex(MediaStore.Audio.Media.TITLE);
        int durationColumn = cur.getColumnIndex(MediaStore.Audio.Media.DURATION);
        int idColumn = cur.getColumnIndex(MediaStore.Audio.Media._ID);
        while (cur.moveToNext()) {
            musicAdapter.addMusicItem(
                    new MusicItem(
                            cur.getString(titleColumn),
                            cur.getString(artistColumn),
                            cur.getLong(durationColumn),
                            cur.getLong(idColumn)
                    )
            );
        }

        cur.close();

        musicAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_selector);

        musicAdapter = new MusicAdapter();
        ListView listView = (ListView)findViewById(R.id.listView);
        listView.setAdapter(musicAdapter);

        requestWrapper();
    }

    private class MusicAdapter extends BaseAdapter{

        ArrayList<MusicItem> musicItems;

        public MusicAdapter() {
            musicItems = new ArrayList<>();
        }

        public void addMusicItem(MusicItem musicItem){
            musicItems.add(musicItem);
        }

        @Override
        public int getCount() {
            return musicItems.size();
        }

        @Override
        public Object getItem(int position) {
            return musicItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null){
                convertView = getLayoutInflater().inflate(R.layout.music_item_layout, null);
            }
            final MusicItem musicItem = musicItems.get(position);
            ((TextView)convertView.findViewById(R.id.author)).setText(musicItem.getAuthor());
            ((TextView)convertView.findViewById(R.id.title)).setText(musicItem.getTitle());
            ((TextView)convertView.findViewById(R.id.duration)).setText(""+musicItem.getDuration());
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MusicSelectorActivity.this, MainActivity.class);
                    intent.putExtra(getString(R.string.intent_key_uri), musicItem.getURI());
                    startActivity(intent);
                }
            });
            return convertView;
        }
    }
}
