package com.brunogois.clipescolatest.Utils;

import android.content.Context;
import android.content.res.Resources;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Patterns;
import android.util.TypedValue;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;

public class Utils {
    public static String getUniqueDeviceId(Context context) {
        String android_id = Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);

        return android_id;
    }

    public static int convertDpToPx(float dpValue, Context context) {
        try {
            Resources r = context.getResources();
            float px = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    dpValue,
                    r.getDisplayMetrics()
            );

            return (int) px;
        } catch (Exception ex) {
            ex.printStackTrace();
            return (int) dpValue;
        }
    }

    public static float getDeviceScreenWidth(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return displayMetrics.widthPixels / displayMetrics.density;
    }

    public static float getDeviceScreenHeight(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return displayMetrics.heightPixels / displayMetrics.density;
    }

    public static void showKeyboard(Context context) {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    public static void closeKeyboard(Context context) {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }

    public static void alterarVisibilidadeTextoListaVazia(TextView tv, Integer listaVerificar) {
        if (listaVerificar == 0) {
            if (tv.getVisibility() == View.INVISIBLE)
                tv.setVisibility(View.VISIBLE);
        } else {
            if (tv.getVisibility() == View.VISIBLE)
                tv.setVisibility(View.INVISIBLE);
        }
    }

    public static void setVisibilidadeGone(View tv) {
        if (tv.getVisibility() == View.VISIBLE) {
            tv.setVisibility(View.GONE);
        }
    }

    public static void setVisibilidadeVisible(View tv) {
        if (tv.getVisibility() == View.GONE) {
            tv.setVisibility(View.VISIBLE);
        }
    }

    public static boolean validarVazios(List<EditText> camposValidar) {
        boolean result = true;
        for (EditText et :
                camposValidar) {
            if (et.length() == 0) {
                et.setError("Campo Obrigatório");
                result = false;
            }
        }

        return result;
    }

    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }


}
