package com.faceunity.pta_art.utils.keyboard;

import android.app.Activity;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.PopupWindow;

import com.faceunity.pta_art.R;

/**
 * The keyboard height provider, this class uses a PopupWindow
 * to calculate the window height when the floating keyboard is opened and closed.
 */
public class KeyboardHeightProvider extends PopupWindow {

    /**
     * The keyboard height observer
     */
    private KeyboardHeightObserver observer;

    /**
     * The view that is used to calculate the keyboard height
     */
    private View popupView;

    /**
     * The parent view
     */
    private View parentView;

    /**
     * The root activity that uses this KeyboardHeightProvider
     */
    private Activity activity;

    private int mNegativeHiddenHeight;

    /**
     * Construct a new KeyboardHeightProvider
     *
     * @param activity The parent activity
     */
    public KeyboardHeightProvider(Activity activity) {
        super(activity);
        this.activity = activity;

        LayoutInflater inflator = (LayoutInflater) activity.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        this.popupView = inflator.inflate(R.layout.keyboard_popup_window, null, false);
        setContentView(popupView);

        setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE | WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);

        parentView = activity.findViewById(android.R.id.content);

        setWidth(0);
        setHeight(WindowManager.LayoutParams.MATCH_PARENT);

        popupView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                if (popupView != null) {
                    handleOnGlobalLayout();
                }
            }
        });
    }

    /**
     * Start the KeyboardHeightProvider, this must be called after the onResume of the Activity.
     * PopupWindows are not allowed to be registered before the onResume has finished
     * of the Activity.
     */
    public void start() {
        if (!isShowing() && parentView.getWindowToken() != null) {
            setBackgroundDrawable(new ColorDrawable(0));
            showAtLocation(parentView, Gravity.NO_GRAVITY, 0, 0);
        }
    }

    /**
     * Close the keyboard height provider,
     * this provider will not be used anymore.
     */
    public void close() {
        this.observer = null;
        dismiss();
    }

    /**
     * Set the keyboard height observer to this provider. The
     * observer will be notified when the keyboard height has changed.
     * For example when the keyboard is opened or closed.
     *
     * @param observer The observer to be added to this provider.
     */
    public void setKeyboardHeightObserver(KeyboardHeightObserver observer) {
        this.observer = observer;
    }

    /**
     * Get the screen orientation
     *
     * @return the screen orientation
     */
    private int getScreenOrientation() {
        return activity.getResources().getConfiguration().orientation;
    }

    /**
     * Popup window itself is as big as the window of the Activity.
     * The keyboard can then be calculated by extracting the popup view bottom
     * from the activity window height.
     */
    private void handleOnGlobalLayout() {

        Point screenSize = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(screenSize);

        Rect rect = new Rect();
        popupView.getWindowVisibleDisplayFrame(rect);

        // REMIND, you may like to change this using the fullscreen size of the phone
        // and also using the status bar and navigation bar heights of the phone to calculate
        // the keyboard height. But this worked fine on a Nexus.
        int orientation = getScreenOrientation();
        int keyboardHeight = screenSize.y - rect.bottom;
        boolean negative = keyboardHeight < 0;
        // 解决高度为负数的情况
        if (negative) {
            mNegativeHiddenHeight = -keyboardHeight;
            keyboardHeight = 0;
        }

        if (keyboardHeight == 0) {
            notifyKeyboardHeightChanged(0, orientation);
        } else {
            notifyKeyboardHeightChanged(keyboardHeight + mNegativeHiddenHeight, orientation);
        }
    }

    /**
     *
     */
    private void notifyKeyboardHeightChanged(int height, int orientation) {
        if (observer != null) {
            observer.onKeyboardHeightChanged(height, orientation);
        }
    }
}