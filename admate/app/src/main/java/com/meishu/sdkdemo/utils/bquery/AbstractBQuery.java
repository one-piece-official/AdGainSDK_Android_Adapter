package com.meishu.sdkdemo.utils.bquery;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.Spanned;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.meishu.sdkdemo.utils.http.HttpGetBytesCallback;
import com.meishu.sdkdemo.utils.http.HttpResponse;
import com.meishu.sdkdemo.utils.http.HttpUtil;

import java.io.IOException;
import java.lang.reflect.Constructor;

public abstract class AbstractBQuery<T extends AbstractBQuery<T>>{
    private  Activity act;
    private Constructor<T> constructor;

    private View root;
    private Context context;

    protected View view;

    protected T create(View view){
        T result = null;
        Constructor<T> c = getConstructor();
        try {
            result = c.newInstance(view);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public Constructor<T> getConstructor(){
        if (constructor ==null){
            try {
                constructor = (Constructor<T>) getClass().getConstructor(View.class);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return constructor;
    }

    public AbstractBQuery(Activity act){
        this.act = act;
    }
    public AbstractBQuery(View root){
        this.root = root;
        this.view = root;
    }

    public AbstractBQuery(Activity act, View root){
        this.root = root;
        this.view = root;
        this.act = act;
    }

    public AbstractBQuery(Context context){
        this.context = context;
    }

    public View findView(int id){
        View result = null;
        if (root !=null){
            result = root.findViewById(id);
        }else if (act !=null){
            result = act.findViewById(id);
        }

        return result;
    }

    private View findView(String tag){

        //((ViewGroup)findViewById(android.R.id.content)).getChildAt(0)
        View result = null;
        if(root != null){
            result = root.findViewWithTag(tag);
        }else if(act != null){
            //result = act.findViewById(id);
            View top = ((ViewGroup) act.findViewById(android.R.id.content)).getChildAt(0);
            if(top != null){
                result = top.findViewWithTag(tag);
            }
        }
        return result;

    }


    private View findView(int... path){

        View result = findView(path[0]);

        for(int i = 1; i < path.length && result != null; i++){
            result = result.findViewById(path[i]);
        }

        return result;

    }


    public T find(int id){
        View view = findView(id);
        return create(view);
    }


    public View getView(){
        return view;
    }

    public T id(int id){

        return id(findView(id));
    }

    public T id(View view){
        this.view = view;
        return self();
    }

    protected  T self() {
        return (T)this; // 仍然需要类型转换，但已通过泛型参数限制
    }

    public T text(int resid){

        if(view instanceof TextView){
            TextView tv = (TextView) view;
            tv.setText(resid);
        }
        return self();
    }

    public T text(CharSequence text){

        if(view instanceof TextView){
            TextView tv = (TextView) view;
            tv.setText(text);
        }

        return self();
    }

    public T text(CharSequence text, boolean goneIfEmpty){

        if(goneIfEmpty && (text == null || text.length() == 0)){
            return gone();
        }else{
            return text(text);
        }
    }

    public T text(Spanned text){


        if(view instanceof TextView){
            TextView tv = (TextView) view;
            tv.setText(text);
        }
        return self();
    }

    public T textColor(int color){

        if(view instanceof TextView){
            TextView tv = (TextView) view;
            tv.setTextColor(color);
        }
        return self();
    }

    public T typeface(Typeface tf){

        if(view instanceof TextView){
            TextView tv = (TextView) view;
            tv.setTypeface(tf);
        }
        return self();
    }

    /**
     * Set the text size (in sp) of a TextView.
     *
     * @param size size
     * @return self
     */
    public T textSize(float size){

        if(view instanceof TextView){
            TextView tv = (TextView) view;
            tv.setTextSize(size);
        }
        return self();
    }

    /**
     * Set the adapter of an AdapterView.
     *
     * @param adapter adapter
     * @return self
     */

    @SuppressWarnings({"unchecked", "rawtypes" })
    public T adapter(Adapter adapter){

        if(view instanceof AdapterView){
            AdapterView av = (AdapterView) view;
            av.setAdapter(adapter);
        }

        return self();
    }

    /**
     * Set the adapter of an ExpandableListView.
     *
     * @param adapter adapter
     * @return self
     */
    public T adapter(ExpandableListAdapter adapter){

        if(view instanceof ExpandableListView){
            ExpandableListView av = (ExpandableListView) view;
            av.setAdapter(adapter);
        }

        return self();
    }

    /**
     * Set the image of an ImageView.
     *
     * @param resid the resource id
     * @return self
     *
     * @see
     */
    public T image(int resid){

        if(view instanceof ImageView){
            ImageView iv = (ImageView) view;
//            iv.setTag(AQuery.TAG_URL, null);
            if(resid == 0){
                iv.setImageBitmap(null);
            }else{
                iv.setImageResource(resid);
            }
        }

        return self();
    }

    /**
     * Set the image of an ImageView.
     *
     * @param drawable the drawable
     * @return self
     *
     * @see
     *
     */
    public T image(Drawable drawable){

        if(view instanceof ImageView){
            ImageView iv = (ImageView) view;
//            iv.setTag(AQuery.TAG_URL, null);
            iv.setImageDrawable(drawable);
        }

        return self();
    }

    /**
     * Set the image of an ImageView.
     *
     * @param bm Bitmap
     * @return self
     *
     * @see
     */
    public T image(Bitmap bm){

        if(view instanceof ImageView){
            ImageView iv = (ImageView) view;
//            iv.setTag(AQuery.TAG_URL, null);
            iv.setImageBitmap(bm);
        }

        return self();
    }

    public T image(String url) {
        return image(url, false);
    }

    public T image(String url, boolean blur) {
        if (view instanceof ImageView){
            try {
                HttpUtil.asyncGetImage(url, (ImageView) view);
            } catch (Throwable e) {

            }
        }
        return self();
    }

    /**
     * Set view visibility to View.GONE.
     *
     * @return self
     */
    public T gone(){
		/*
		if(view != null && view.getVisibility() != View.GONE){
			view.setVisibility(View.GONE);
		}

		return self();
		*/
        return visibility(View.GONE);
    }

    /**
     * Set view visibility to View.INVISIBLE.
     *
     * @return self
     */
    public T invisible(){

		/*
		if(view != null && view.getVisibility() != View.INVISIBLE){
			view.setVisibility(View.INVISIBLE);
		}

		return self();
		*/
        return visibility(View.INVISIBLE);
    }

    /**
     * Set view visibility to View.VISIBLE.
     *
     * @return self
     */
    public T visible(){

		/*
		if(view != null && view.getVisibility() != View.VISIBLE){
			view.setVisibility(View.VISIBLE);
		}

		return self();
		*/
        return visibility(View.VISIBLE);
    }

    /**
     * Set view visibility, such as View.VISIBLE.
     *
     * @return self
     */
    public T visibility(int visibility){

        if(view != null && view.getVisibility() != visibility){
            view.setVisibility(visibility);
        }

        return self();
    }

    public boolean isVisibility() {
        if (view != null) {
            return view.getVisibility() == View.VISIBLE;
        }
        return false;
    }

    /**
     * Set view background.
     *
     * @param id the id
     * @return self
     */
    public T background(int id){

        if(view != null){

            if(id != 0){
                view.setBackgroundResource(id);
            }else{
                view.setBackgroundDrawable(null);
            }

        }

        return self();
    }

    /**
     * Set view background color.
     *
     * @param color
     * @return self
     */
    public T backgroundColor(int color){

        if(view != null){
            view.setBackgroundColor(color);
        }

        return self();
    }

    /**
     * Notify a ListView that the data of it's adapter is changed.
     *
     * @return self
     */
    public T dataChanged(){

        if(view instanceof AdapterView){

            AdapterView<?> av = (AdapterView<?>) view;
            Adapter a = av.getAdapter();

            if(a instanceof BaseAdapter){
                BaseAdapter ba = (BaseAdapter) a;
                ba.notifyDataSetChanged();
            }

        }


        return self();
    }

    /**
     * Checks if the current view exist.
     *
     * @return true, if is exist
     */
    public boolean isExist(){
        return view != null;
    }

    /**
     * Gets the tag of the view.
     *
     * @return tag
     */
    public Object getTag(){
        Object result = null;
        if(view != null){
            result = view.getTag();
        }
        return result;
    }

    /**
     * Gets the tag of the view.
     * @param id the id
     *
     * @return tag
     */
    public Object getTag(int id){
        Object result = null;
        if(view != null){
            result = view.getTag(id);
        }
        return result;
    }

    /**
     * Gets the current view as an image view.
     *
     * @return ImageView
     */
    public ImageView getImageView(){
        return (ImageView) view;
    }

    /**
     * Gets the current view as an Gallery.
     *
     * @return Gallery
     */
    public Gallery getGallery(){
        return (Gallery) view;
    }



    /**
     * Gets the current view as a text view.
     *
     * @return TextView
     */
    public TextView getTextView(){
        return (TextView) view;
    }

    /**
     * Gets the current view as an edit text.
     *
     * @return EditText
     */
    public EditText getEditText(){
        return (EditText) view;
    }

    /**
     * Gets the current view as an progress bar.
     *
     * @return ProgressBar
     */
    public ProgressBar getProgressBar(){
        return (ProgressBar) view;
    }

    /**
     * Gets the current view as seek bar.
     *
     * @return SeekBar
     */

    public SeekBar getSeekBar(){
        return (SeekBar) view;
    }

    /**
     * Gets the current view as a button.
     *
     * @return Button
     */
    public Button getButton(){
        return (Button) view;
    }

    /**
     * Gets the current view as a checkbox.
     *
     * @return CheckBox
     */
    public CheckBox getCheckBox(){
        return (CheckBox) view;
    }

    /**
     * Gets the current view as a listview.
     *
     * @return ListView
     */
    public ListView getListView(){
        return (ListView) view;
    }

    /**
     * Gets the current view as a ExpandableListView.
     *
     * @return ExpandableListView
     */
    public ExpandableListView getExpandableListView(){
        return (ExpandableListView) view;
    }

    /**
     * Gets the current view as a webview.
     *
     * @return WebView
     */
    public WebView getWebView(){
        return (WebView) view;
    }

    /**
     * Gets the current view as a spinner.
     *
     * @return Spinner
     */
    public Spinner getSpinner(){
        return (Spinner) view;
    }

    /**
     * Gets the editable.
     *
     * @return the editable
     */
    public Editable getEditable(){

        Editable result = null;

        if(view instanceof EditText){
            result = ((EditText) view).getEditableText();
        }

        return result;
    }

    /**
     * Gets the text of a TextView.
     *
     * @return the text
     */
    public CharSequence getText(){

        CharSequence result = null;

        if(view instanceof TextView){
            result = ((TextView) view).getText();
        }

        return result;
    }

    /**
     * Gets the selected item if current view is an adapter view.
     *
     * @return selected
     */
    public Object getSelectedItem(){

        Object result = null;

        if(view instanceof AdapterView<?>){
            result = ((AdapterView<?>) view).getSelectedItem();
        }

        return result;

    }


    /**
     * Gets the selected item position if current view is an adapter view.
     *
     * Returns AdapterView.INVALID_POSITION if not valid.
     *
     * @return selected position
     */
    public int getSelectedItemPosition(){

        int result = AdapterView.INVALID_POSITION;

        if(view instanceof AdapterView<?>){
            result = ((AdapterView<?>) view).getSelectedItemPosition();
        }

        return result;

    }


    private static final Class<?>[] ON_CLICK_SIG = {View.class};




    /**
     * Register a callback method for when the view is clicked.
     *
     * @param listener The callback method.
     * @return self
     */
    public T clicked(View.OnClickListener listener){

        if(view != null){
            view.setOnClickListener(listener);
        }

        return self();
    }




    /**
     * Register a callback method for when the view is long clicked.
     *
     * @param listener The callback method.
     * @return self
     */
    public T longClicked(View.OnLongClickListener listener){

        if(view != null){
            view.setOnLongClickListener(listener);
        }

        return self();
    }

    /**
     * Clear a view. Applies to ImageView, WebView, and TextView.
     *
     * @return self
     */
    public T clear(){

        if(view != null){

            if(view instanceof ImageView){
                ImageView iv = ((ImageView) view);
                iv.setImageBitmap(null);
            }else if(view instanceof WebView){
                WebView wv = ((WebView) view);
                wv.stopLoading();
                wv.clearView();
            }else if(view instanceof TextView){
                TextView tv = ((TextView) view);
                tv.setText("");
            }


        }

        return self();
    }

    /**
     * Return the context of activity or view.
     *
     * @return Context
     */

    public Context getContext(){
        if(act != null){
            return act;
        }
        if(root != null){
            return root.getContext();
        }
        return context;
    }


    /**
     * Trigger click event
     *
     * <br>
     * contributed by: neocoin
     *
     * @return self
     *
     * @see View#performClick()
     */
    public T click(){
        if(view != null){
            view.performClick();
        }
        return self();
    }

    /**
     * Trigger long click event
     *
     * <br>
     * contributed by: neocoin
     *
     * @return self
     *
     * @see View#performClick()
     */
    public T longClick(){
        if(view != null){
            view.performLongClick();
        }
        return self();
    }
}
