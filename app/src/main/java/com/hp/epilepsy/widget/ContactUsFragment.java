package com.hp.epilepsy.widget;

import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hp.epilepsy.R;

public class ContactUsFragment extends Fragment implements IStepScreen {


	public ContactUsFragment() {

	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.contact_us, container,
				false);
		setUpTextViews(rootView);
		return rootView;

	}
	private void setUpTextViews(View view) {
			TextView textView = (TextView) view.findViewById(R.id.cu_content);
		setTextViewHTML(textView, getActivity().getString(
				R.string.contact_us));
	}

	
	protected void makeLinkClickable(SpannableStringBuilder strBuilder, final URLSpan span)
	{
		try {
			int start = strBuilder.getSpanStart(span);
			int end = strBuilder.getSpanEnd(span);
			int flags = strBuilder.getSpanFlags(span);
			ClickableSpan clickable = new ClickableSpan() {
                  public void onClick(View view) {
                      // Do something with span.getURL() to handle the link click...
                      Log.i(getClass().getName(), span.getURL());
                      Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(span.getURL()));
                      startActivity(intent);
                  }
            };
			strBuilder.setSpan(clickable, start, end, flags);
			strBuilder.removeSpan(span);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void setTextViewHTML(TextView text, String html)
	{
		try {
			CharSequence sequence = Html.fromHtml(html);
			SpannableStringBuilder strBuilder = new SpannableStringBuilder(sequence);
			URLSpan[] urls = strBuilder.getSpans(0, sequence.length(), URLSpan.class);
			for(URLSpan span : urls) {
                makeLinkClickable(strBuilder, span);
            }
			text.setText(strBuilder);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}



}
