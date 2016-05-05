package in.codehex.shareipo;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import in.codehex.shareipo.db.DatabaseHandler;
import in.codehex.shareipo.model.FileItem;

public class UnShareFileActivity extends AppCompatActivity {

    Toolbar toolbar;
    Button btnUnShare;
    RecyclerView recyclerView;
    DatabaseHandler databaseHandler;
    List<FileItem> fileItemList;
    List<Integer> integerList;
    FileAdapter adapter;
    String user;
    boolean isUserFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_un_share_file);

        initObjects();
        prepareObjects();
    }

    /**
     * initialize the objects
     */
    private void initObjects() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        btnUnShare = (Button) findViewById(R.id.un_share);
        recyclerView = (RecyclerView) findViewById(R.id.un_share_file_list);

        databaseHandler = new DatabaseHandler(this);
        fileItemList = new ArrayList<>();
        integerList = new ArrayList<>();
        adapter = new FileAdapter(this, fileItemList);
    }

    /**
     * implement and manipulate the objects
     */
    private void prepareObjects() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        if (getIntent().hasExtra("user")) {
            isUserFile = true;
            user = getIntent().getStringExtra("user");
        }

        if (isUserFile)
            fileItemList.addAll(databaseHandler.getShareUserFileList(user));
        else
            fileItemList.addAll(databaseHandler.getShareFileList());
        adapter.notifyDataSetChanged();

        btnUnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < fileItemList.size(); i++) {
                    if (fileItemList.get(i).isSelected())
                        integerList.add(fileItemList.get(i).getId());
                }
                databaseHandler.removeShareFiles(integerList);
            }
        });
    }

    private class FileAdapter extends RecyclerView.Adapter<FileAdapter.FileViewHolder> {

        Context context;
        List<FileItem> fileItemList;

        public FileAdapter(Context context, List<FileItem> fileItemList) {
            this.context = context;
            this.fileItemList = fileItemList;
        }

        @Override
        public FileViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_un_share_file, parent, false);
            return new FileViewHolder(view);
        }

        @Override
        public void onBindViewHolder(FileViewHolder holder, int position) {
            final FileItem fileItem = fileItemList.get(position);

            File file = new File(fileItem.getFile());
            holder.file.setText(file.getName());
            holder.select.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked)
                        fileItem.setSelected(true);
                    else fileItem.setSelected(false);
                    adapter.notifyDataSetChanged();
                }
            });
        }

        @Override
        public int getItemCount() {
            return fileItemList.size();
        }

        protected class FileViewHolder extends RecyclerView.ViewHolder {

            private TextView file;
            private CheckBox select;

            public FileViewHolder(View view) {
                super(view);
                file = (TextView) view.findViewById(R.id.file);
                select = (CheckBox) view.findViewById(R.id.select);
            }
        }
    }
}
