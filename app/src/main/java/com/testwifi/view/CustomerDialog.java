package com.testwifi.view;


import android.app.Dialog;
import android.content.Context;
import android.text.TextWatcher;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.testwifi.R;

public class CustomerDialog {
    private Context context;
    private Dialog dialog;
    private LinearLayout lLayout_bg;
    private TextView txt_title;
    private TextView txt_msg;
    private EditText edit_msg;
    private Button btn_neg;
    private Button btn_pos;
    private ImageView img_line;
    private Display display;
    private boolean showTitle = false;
    private boolean showMsg = false;
    private boolean showEdit = false;
    private boolean showPosBtn = false;
    private boolean showNegBtn = false;
    private CustomDialogListener customDialogListener;

    
    public interface CustomDialogListener {
        void onConform(String str);
    }

    public CustomerDialog(Context context) {
        this.context = context;
        WindowManager windowManager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        display = windowManager.getDefaultDisplay();
    }

    public CustomerDialog(Context context, CustomDialogListener customListener) {
        this.context = context;
        WindowManager windowManager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        display = windowManager.getDefaultDisplay();
        customDialogListener = customListener;
    }

    public CustomerDialog builder() {
        // 获取Dialog布局
        View view = LayoutInflater.from(context).inflate(
                R.layout.view_alertdialog, null);

        // 获取自定义Dialog布局中的控件
        lLayout_bg = (LinearLayout) view.findViewById(R.id.lLayout_bg);
        txt_title = (TextView) view.findViewById(R.id.txt_title);
        txt_title.setVisibility(View.GONE);
        txt_msg = (TextView) view.findViewById(R.id.txt_msg);
        txt_msg.setVisibility(View.GONE);
        edit_msg = (EditText) view.findViewById(R.id.edit_msg);
        edit_msg.setVisibility(View.GONE);
        btn_neg = (Button) view.findViewById(R.id.btn_neg);
        btn_neg.setVisibility(View.GONE);
        btn_pos = (Button) view.findViewById(R.id.btn_pos);
        btn_pos.setVisibility(View.GONE);
        img_line = (ImageView) view.findViewById(R.id.img_line);
        img_line.setVisibility(View.GONE);

        // 定义Dialog布局和参数
        dialog = new Dialog(context, R.style.AlertDialogStyle);
        dialog.setContentView(view);

        // 调整dialog背景大小
        lLayout_bg.setLayoutParams(new FrameLayout.LayoutParams((int) (display
                .getWidth() * 0.85), LayoutParams.WRAP_CONTENT));

        return this;
    }

    public CustomerDialog setTitle(String title) {
        showTitle = true;
        if ("".equals(title)) {
            txt_title.setText("标题");
        } else {
            txt_title.setText(title);
        }
        return this;
    }

    public CustomerDialog setEditMode(boolean bEdit) {
        showEdit = bEdit;
        return this;
    }

    public CustomerDialog setEditText(String text) {
        showEdit = true;
        edit_msg.setText(text);
        if (text != null) {
            edit_msg.setSelection(text.length());
        }
        return this;
    }

    public CustomerDialog setMsg(String msg) {
        showMsg = true;
        if ("".equals(msg)) {
            txt_msg.setText("内容");
        } else {
            txt_msg.setText(msg);
        }
        return this;
    }

    public CustomerDialog setCancelable(boolean cancel) {
        dialog.setCancelable(cancel);
        return this;
    }

    public CustomerDialog setTextListener(TextWatcher textWatcher){
    	edit_msg.addTextChangedListener(textWatcher);
    	return this;
    }
    
    public EditText getEditText(){
    	return edit_msg;
    }
    
    public CustomerDialog setPositiveButton(String text,
            final OnClickListener listener) {
        showPosBtn = true;
        if ("".equals(text)) {
            btn_pos.setText(R.string.conform);
        } else {
            btn_pos.setText(text);
        }
        btn_pos.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener != null){
                    listener.onClick(v);    
                }
                dialog.dismiss();
                if (customDialogListener != null) {
                    customDialogListener.onConform(edit_msg.getText()
                            .toString());
                }
            }
        });
        return this;
    }

    public CustomerDialog setNegativeButton(String text,
            final OnClickListener listener) {
        showNegBtn = true;
        if ("".equals(text)) {
            btn_neg.setText(R.string.cancel);
        } else {
            btn_neg.setText(text);
        }
        btn_neg.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener != null){
                    listener.onClick(v);    
                }
                dialog.dismiss();
            }
        });
        return this;
    }

    private void setLayout() {
        if (!showTitle && !showMsg) {
            txt_title.setText("提示");
            txt_title.setVisibility(View.VISIBLE);
        }

        if (showTitle) {
            txt_title.setVisibility(View.VISIBLE);
        }
        if (showEdit) {
            edit_msg.setVisibility(View.VISIBLE);
        }
        if (showMsg && !showEdit) {
            txt_msg.setVisibility(View.VISIBLE);
        }

        if (!showPosBtn && !showNegBtn) {
            btn_pos.setText(R.string.conform);
            btn_pos.setVisibility(View.VISIBLE);
            btn_pos.setBackgroundResource(R.drawable.dialog_single_selector);
            btn_pos.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
        }

        if (showPosBtn && showNegBtn) {
            btn_pos.setVisibility(View.VISIBLE);
            btn_pos.setBackgroundResource(R.drawable.dialog_right_selector);
            btn_neg.setVisibility(View.VISIBLE);
            btn_neg.setBackgroundResource(R.drawable.dialog_left_selector);
            img_line.setVisibility(View.VISIBLE);
        }

        if (showPosBtn && !showNegBtn) {
            btn_pos.setVisibility(View.VISIBLE);
            btn_pos.setBackgroundResource(R.drawable.dialog_single_selector);
        }

        if (!showPosBtn && showNegBtn) {
            btn_neg.setVisibility(View.VISIBLE);
            btn_neg.setBackgroundResource(R.drawable.dialog_single_selector);
        }
    }
    

    public void show() {
        setLayout();
        dialog.show();
    }
    
    public void hide(){
    	dialog.hide();
    }
}
