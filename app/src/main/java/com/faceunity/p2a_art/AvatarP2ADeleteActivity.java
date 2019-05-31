package com.faceunity.p2a_art;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.faceunity.p2a_art.entity.AvatarP2A;
import com.faceunity.p2a_art.entity.DBHelper;
import com.faceunity.p2a_art.ui.NormalDialog;
import com.faceunity.p2a_art.utils.FileUtil;
import com.faceunity.p2a_art.utils.FullScreenUtils;

import java.util.Arrays;
import java.util.List;

public class AvatarP2ADeleteActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String TAG = AvatarP2ADeleteActivity.class.getSimpleName();

    private DBHelper mDBHelper;
    private List<AvatarP2A> mAvatarP2As;
    private RecyclerView mRecyclerView;
    private AvatarP2ARecyclerAdapter mAvatarP2ARecyclerAdapter;
    private ImageButton mBackBtn;
    private Button mDeleteBtn;
    private TextView mDeleteMidTextView;
    private boolean[] isDeleteList;

    public static void start(Activity context) {
        Intent intent = new Intent(context, AvatarP2ADeleteActivity.class);
        context.startActivity(intent);
        context.overridePendingTransition(R.anim.slide_in_bottom, android.R.anim.fade_out);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_avatar_p2a_delete);
        FullScreenUtils.fullScreen(this);
        mDBHelper = new DBHelper(this);
        mAvatarP2As = mDBHelper.getAllHistoryItems();
        isDeleteList = new boolean[mAvatarP2As.size()];
        Arrays.fill(isDeleteList, false);

        mRecyclerView = findViewById(R.id.avatar_p2a_delete_recycler);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 4, GridLayoutManager.VERTICAL, false));
        mRecyclerView.setAdapter(mAvatarP2ARecyclerAdapter = new AvatarP2ARecyclerAdapter());
        ((SimpleItemAnimator) mRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);

        mBackBtn = findViewById(R.id.avatar_p2a_delete_back);
        mBackBtn.setOnClickListener(this);

        mDeleteBtn = findViewById(R.id.avatar_p2a_delete_bottom_delete);
        mDeleteBtn.setOnClickListener(this);

        mDeleteMidTextView = (TextView) findViewById(R.id.avatar_p2a_delete_mid);
        updateView();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.avatar_p2a_delete_back:
                onBackPressed();
                break;
            case R.id.avatar_p2a_delete_bottom_delete:
                if (getCheckedCount() > 0) {
                    NormalDialog normalDialog = new NormalDialog();
                    normalDialog.setMessageStr("确认删除所选模型？");
                    normalDialog.setNegativeStr("取消");
                    normalDialog.setPositiveStr("确认");
                    normalDialog.show(getSupportFragmentManager(), NormalDialog.TAG);
                    normalDialog.setOnClickListener(new NormalDialog.OnSimpleClickListener() {
                        @Override
                        public void onPositiveListener() {
                            for (int i = mAvatarP2As.size() - 1; i >= 0; i--) {
                                AvatarP2A avatarP2A = mAvatarP2As.get(i);
                                if (isDeleteList[i]) {
                                    mAvatarP2As.remove(i);
                                    mAvatarP2ARecyclerAdapter.notifyItemRemoved(i);
                                    mDBHelper.deleteHistoryByDir(avatarP2A.getBundleDir());
                                    FileUtil.deleteDirAndFile(avatarP2A.getBundleDir());
                                }
                            }
                            isDeleteList = new boolean[mAvatarP2As.size()];
                            Arrays.fill(isDeleteList, false);
                            updateView();
                        }
                    });
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(AvatarP2ADeleteActivity.this, MainActivity.class));
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, R.anim.slide_out_bottom);
    }

    private int getCheckedCount() {
        int count = 0;
        for (boolean b : isDeleteList) {
            if (b) {
                count++;
            }
        }
        return count;
    }

    private void updateView() {
        int count = getCheckedCount();
        mDeleteBtn.setText(count == 0 ? "删除" : String.format("删除(%d)", count));
        mDeleteBtn.setTextColor(getResources().getColor(count == 0 ? R.color.color999999 : R.color.color3E99F4));
        mDeleteMidTextView.setVisibility(mAvatarP2As.isEmpty() ? View.VISIBLE : View.GONE);
    }

    class AvatarP2ARecyclerAdapter extends RecyclerView.Adapter<AvatarP2ARecyclerAdapter.HomeRecyclerHolder> {

        @Override
        public AvatarP2ARecyclerAdapter.HomeRecyclerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new AvatarP2ARecyclerAdapter.HomeRecyclerHolder(LayoutInflater.from(AvatarP2ADeleteActivity.this).inflate(R.layout.layout_delete_bottom_item, parent, false));
        }

        @Override
        public void onBindViewHolder(final AvatarP2ARecyclerAdapter.HomeRecyclerHolder holder, int pos) {
            int position = holder.getLayoutPosition();
            final AvatarP2A avatarP2A = mAvatarP2As.get(position);
            holder.img.setBackgroundResource(isDeleteList[position] ? R.drawable.main_item_select : 0);
            holder.img.setImageBitmap(BitmapFactory.decodeFile(avatarP2A.getOriginPhotoThumbNail()));
            holder.img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = holder.getLayoutPosition();
                    isDeleteList[position] = !isDeleteList[position];
                    notifyItemChanged(position);
                    updateView();
                }
            });
        }

        @Override
        public int getItemCount() {
            return mAvatarP2As.size();
        }

        class HomeRecyclerHolder extends RecyclerView.ViewHolder {

            ImageView img;

            public HomeRecyclerHolder(View itemView) {
                super(itemView);
                img = itemView.findViewById(R.id.bottom_item_img);
            }
        }
    }
}
