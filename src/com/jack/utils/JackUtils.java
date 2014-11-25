package com.jack.utils;

import java.io.IOException;
import java.io.InputStream;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class JackUtils {
	/**
     * ¥”Assets÷–∂¡»°Õº∆¨
     */
    public static Bitmap getbmFromAssetsFile(Resources res , String fileName)
    {
    	if(res==null) return null;
        Bitmap bm = null;
        AssetManager am = res.getAssets();
        try
        {
            InputStream is = am.open(fileName);
            bm = BitmapFactory.decodeStream(is);
            is.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return bm;

    }
    public static ProgressDialog showProgressDialog(Context context,String text){
		if(null!=context){
			ProgressDialog dialog = new ProgressDialog(context);
	        dialog.setMessage(text);
	        dialog.setIndeterminate(true);
	        dialog.setCancelable(false);
	        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
	        dialog.show();
			return dialog;
		}
		return null;
	}
}
