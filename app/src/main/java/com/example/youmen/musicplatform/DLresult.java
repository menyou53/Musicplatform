package com.example.youmen.musicplatform;


        import android.app.Activity;
        import android.app.DownloadManager;
        import android.content.Context;
        import android.net.Uri;
        import android.os.Bundle;
        import android.support.v7.app.AppCompatActivity;
        import android.view.View;
        import android.widget.AdapterView;
        import android.widget.ArrayAdapter;
        import android.widget.ListView;

        import java.util.ArrayList;


public class DLresult extends Activity {
    DownloadManager downloadManager;

    private ListView lv;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dl);

        ArrayList music1 =getIntent().getExtras().getStringArrayList("list");

        lv=(ListView)findViewById(R.id.ListViewDL);
        ArrayAdapter<String>adapter=new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                music1);
        lv.setAdapter(adapter);

        lv.setOnItemClickListener(listener);
    }
    private ListView.OnItemClickListener listener=new ListView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
            ArrayList dlurl1 =getIntent().getExtras().getStringArrayList("listdl");
            String str= (String) dlurl1.get(position);

            downloadManager = (DownloadManager)getSystemService(Context.DOWNLOAD_SERVICE);
            Uri uri = Uri.parse(str);
            DownloadManager.Request request = new DownloadManager.Request(uri);
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            Long reference = downloadManager.enqueue(request);
        }
    };

}