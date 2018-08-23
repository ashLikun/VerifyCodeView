package com.ashlikun.verifycodeview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import java.util.ArrayList;

/**
 * @author　　: 李坤
 * 创建时间: 2018/8/23 10:22
 * 邮箱　　：496546144@qq.com
 * <p>
 * 功能介绍：滴滴形式的验证码输入框
 */
public class VerifyCodeView extends LinearLayout {
    public static final char CODE_DEFAULT = ' ';
    /**
     * 验证码个数
     */
    private int codeNumber = 6;
    private int textSize = 16;
    private int textColor = 0xff333333;
    private int inputType = 1;
    /**
     * 每个输入框是否要正方形,默认true
     */
    private boolean isSquare = true;
    /**
     * 间距宽度
     */
    private int spacingWidth = 5;
    /**
     * 获取焦点的背景
     */
    private Drawable focusDrawable;
    /**
     * 普通背景
     */
    private Drawable normalDrawable;

    /********************************************************************************************
     *                                           下面的变量，外部不可修改
     ********************************************************************************************/
    /**
     * 全部的输入框合集
     */
    private ArrayList<EditText> editTexts;
    /**
     * 验证码Text大小,计算获得
     */
    private int codeWidth;
    private int codeHeight;

    /**
     * 当前输入的验证码
     */
    private StringBuilder code = new StringBuilder();
    private OnCompleteListener listener;
    /**
     * 是否可以跳转到之前的view，内部标记
     */
    private boolean isSkipBefore = false;


    public void setListener(OnCompleteListener listener) {
        this.listener = listener;
    }

    public VerifyCodeView(@NonNull Context context) {
        this(context, null);
    }

    public VerifyCodeView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VerifyCodeView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        textSize = dip2px(textSize);
        spacingWidth = dip2px(spacingWidth);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.VerifyCodeView);
        inputType = a.getInt(R.styleable.VerifyCodeView_vcode_inputType, inputType);
        isSquare = a.getBoolean(R.styleable.VerifyCodeView_vcode_isSquare, isSquare);
        codeNumber = a.getInt(R.styleable.VerifyCodeView_vcode_codeNumber, codeNumber);
        textSize = (int) a.getDimension(R.styleable.VerifyCodeView_vcode_textSize, textSize);
        textColor = a.getColor(R.styleable.VerifyCodeView_vcode_textColor, textColor);
        spacingWidth = (int) a.getDimension(R.styleable.VerifyCodeView_vcode_spacingWidth, spacingWidth);
        focusDrawable = a.getDrawable(R.styleable.VerifyCodeView_vcode_focusDrawable);
        normalDrawable = a.getDrawable(R.styleable.VerifyCodeView_vcode_normalDrawable);
        a.recycle();
        setOrientation(HORIZONTAL);
        if (focusDrawable == null) {
            focusDrawable = getResources().getDrawable(R.drawable.verif_code_bg_focus);
        }
        if (normalDrawable == null) {
            normalDrawable = getResources().getDrawable(R.drawable.verif_code_bg_normal);
        }
        addEditView();
    }

    private void addEditView() {
        removeAllViews();
        editTexts = new ArrayList<>(codeNumber);
        for (int i = 0; i < codeNumber; i++) {
            code.append(CODE_DEFAULT);
            EditText editText = new EditText(getContext());
            editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(2)});
            if (inputType == 1) {
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
            } else if (inputType == 2) {
                editText.setInputType(InputType.TYPE_CLASS_TEXT);
            } else if (inputType == 3) {
                editText.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
            } else if (inputType == 4) {
                editText.setInputType(InputType.TYPE_CLASS_PHONE);
            }
            editText.setGravity(Gravity.CENTER);
            editText.setEms(1);
            editText.setTextColor(textColor);
            editText.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
            editText.setBackground(getBackGround());
            LayoutParams params = new LayoutParams(-1, -1);
            if (i != 0) {
                params.leftMargin = spacingWidth;
            }
            addView(editText, params);
            editText.addTextChangedListener(new MyTextWatcher(i, editText));
            editText.setOnKeyListener(new MyOnKeyListener(i, editText));
            editTexts.add(editText);

        }
    }

    protected Drawable getBackGround() {
        StateListDrawable bg = new StateListDrawable();
        bg.addState(new int[]{android.R.attr.state_focused, android.R.attr.state_enabled}, focusDrawable);
        bg.addState(new int[]{}, normalDrawable);
        return bg;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        codeWidth = (widthSize - getPaddingLeft() - getPaddingRight() - spacingWidth * (codeNumber - 1)) / (codeNumber);
        if (isSquare) {
            codeHeight = codeWidth;
        } else {
            //不是正方形，就强制设置高度,要不就指定高度
            if (heightMode == MeasureSpec.AT_MOST) {
                heightMode = MeasureSpec.EXACTLY;
                heightSize = dip2px(40);
            }
            codeHeight = heightSize;
        }
        focusDrawable.setBounds(0, 0, codeWidth, codeHeight);
        normalDrawable.setBounds(0, 0, codeWidth, codeHeight);
        //强制设置EditTextSize
        for (EditText editText : editTexts) {
            editText.getLayoutParams().width = codeWidth;
            editText.getLayoutParams().height = codeHeight;
        }
        super.onMeasure(MeasureSpec.makeMeasureSpec(widthSize, widthMode),
                MeasureSpec.makeMeasureSpec(codeHeight + getPaddingTop() + getPaddingBottom(), heightMode));
    }


    /**
     * 监听EditText按键按下
     */
    private class MyOnKeyListener implements OnKeyListener {
        int index;
        EditText editText;

        public MyOnKeyListener(int index, EditText editText) {
            this.index = index;
            this.editText = editText;
        }

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_DEL && isSkipBefore && editText != null && TextUtils.isEmpty(editText.getText())) {
                //跳转到之前的,并清空
                if (index > 0) {
                    editTexts.get(index - 1).requestFocus();
                    editTexts.get(index - 1).setText("");
                    return true;
                }
            }
            isSkipBefore = true;
            return false;
        }
    }

    private class MyTextWatcher implements TextWatcher {
        int index;
        EditText editText;
        /**
         * listener回调时候记录回调的验证码，后续一样的就不会掉了
         */
        private boolean isSkipListener = false;
        /**
         * 最终数据,保证只输入一位，而且如果重写输入，保证重写输入的正确性（当前值前后插入）
         */
        private char end;

        public MyTextWatcher(int index, EditText editText) {
            this.index = index;
            this.editText = editText;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s.length() > 0) {
                //输入了值
                end = s.charAt(start);
                code.setCharAt(index, end);
            } else {
                //去除了值
                code.setCharAt(index, CODE_DEFAULT);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (s.length() > 1) {
                //大于一个数据，就清空，重写添加新数据
                s.clear();
                s.append(end);
                // 因为在s.append(a)会再一次回调这些方法
                isSkipListener = true;
            }

            if (s.length() == 0) {
                isSkipBefore = false;
            }
            //防止多次回调，因为在s.append(a)会再一次回调这些方法
            if (!isSkipListener) {
                if (s.length() >= 1) {
                    //跳转到下一个
                    if (index < codeNumber - 1) {
                        editTexts.get(index + 1).requestFocus();
                    }
                }
                //判断是否完成
                if (code.indexOf(String.valueOf(CODE_DEFAULT)) == -1) {
                    //已经完成
                    if (listener != null) {
                        listener.onComplete(code.toString());
                    }
                }
            } else {
                isSkipListener = false;
            }
        }
    }

    public int dip2px(float dipValue) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    /**
     * 完成监听
     */
    public interface OnCompleteListener {
        /**
         * 当完成的时候
         *
         * @param code 验证码
         */
        void onComplete(String code);
    }
}
