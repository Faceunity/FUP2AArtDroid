package com.faceunity.pta_art.ui;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.faceunity.pta_art.R;

/**
 * Created by jiangyongxing on 2020/4/13.
 * 描述：
 */
public class TextDriveComponent extends ConstraintLayout {

    private ImageView ivBack;
    private EditText inputText;

    public TextDriveComponent(Context context) {
        super(context);
    }

    public TextDriveComponent(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TextDriveComponent(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ivBack = findViewById(R.id.iv_re_back);
        inputText = findViewById(R.id.et_input_text);

        inputText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (EditorInfo.IME_ACTION_SEND == actionId) {
                    sendText(inputText.getText().toString().trim());
                    return true;
                }
                return false;
            }
        });
        ivBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (textSendListener != null) {
                    textSendListener.onBackClick(v);
                }
            }
        });
    }

    private void sendText(String content) {
        if (textSendListener != null && !TextUtils.isEmpty(content)) {
            textSendListener.onTextSend(content);
            inputText.setText("");
        }
    }

    public interface TextDriveListener {
        void onTextSend(String content);

        void onBackClick(View view);
    }

    private TextDriveListener textSendListener;


    public void setTextSendListener(TextDriveListener textSendListener) {
        this.textSendListener = textSendListener;
    }

    public EditText getInputText() {
        return inputText;
    }
}
