package com.ovrhere.android.morseflash.ui.fragments;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.ovrhere.android.morseflash.R;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass. Use the
 * {@link ScreenFlashFragment#newInstance} factory method to create an instance
 * of this fragment.
 * 
 * Used to show a fully white or black screen.
 * Parent Activity must implement {@link OnFragmentInteraction}.
 * 
 * @version 0.1.0-20140522
 * @author Jason J.
 */
public class ScreenFlashFragment extends Fragment implements OnClickListener{
	
	/** The colour to set for flash on. */
	final static private int COLOUR_FLASH_ON = Color.WHITE;
	/** The colour to set for flash off. */
	final static private int COLOUR_FLASH_OFF = Color.BLACK;
	/** Key for storing and resuming screen state. */
	final static private String KEY_SCREEN_STATE = 
			ScreenFlashFragment.class.getSimpleName() +".KEY_SCREEN_STATE";
	
	/////////////////////////////////////////////////////////////////////////////////////////////////
	/// End constants here
	////////////////////////////////////////////////////////////////////////////////////////////////
	
	/** The background to flash on/off. **/
	private View flashingBackground = null;
	/** The cancel button for closing this screen. */
	private Button cancelButton = null;
	/** The listener of fragment interaction. */
	private OnFragmentInteraction fragmentInteractionListener = null;
	/** Notes whether the screen is on or off. Default is off. */
	private boolean screenOn = false;
	
	/////////////////////////////////////////////////////////////////////////////////////////////////
	/// End members here
	////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Use this factory method to create a new instance of this fragment using
	 * the provided parameters.
	 * @return A new instance of fragment ScreenFlashFragment.
	 */
	public static ScreenFlashFragment newInstance() {
		ScreenFlashFragment fragment = new ScreenFlashFragment();
		Bundle args = new Bundle();
		fragment.setArguments(args);
		return fragment;
	}

	public ScreenFlashFragment() {
		// Required empty public constructor
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {		
		super.onSaveInstanceState(outState);
		outState.putBoolean(KEY_SCREEN_STATE, screenOn);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {			
		}
		if (savedInstanceState != null){
			this.screenOn = savedInstanceState.getBoolean(KEY_SCREEN_STATE);
		}
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (activity instanceof OnFragmentInteraction ){
			this.fragmentInteractionListener = (OnFragmentInteraction) activity;
		} else {
			throw new ClassCastException(
					"Activity must implement \"OnFragmentInteraction\"");
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View v = inflater.inflate(R.layout.fragment_screen_flash, container,
				false);
		flashingBackground = (View) 
				v.findViewById(R.id.com_ovrhere_morseflash_frag_flashscreen_background);
		cancelButton = (Button) 
				v.findViewById(R.id.com_ovrhere_morseflash_frag_flashscreen_button_cancelMessage);
		cancelButton.setOnClickListener(this);		
		//ensure to return to previous state after rotation
		flashBackground(screenOn); 
		return v;
	}
	/** Sets the background to be either on or off.
	 * @param on if <code>true</code> set flash on (white), 
	 * otherwise turns background off.
	 */
	public void flashBackground(boolean on){
		if (flashingBackground == null) return;
		flashingBackground.setBackgroundColor(on ? COLOUR_FLASH_ON :COLOUR_FLASH_OFF);
		flashingBackground.invalidate();
		screenOn = on;
	}
	
	/////////////////////////////////////////////////////////////////////////////////////////////////
	/// Internal Listener Interfaces
	////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Interface required by Activity to implement.
	 * @author Jason J.
	 * @version 0.1.0-20140515
	 */
	static public interface OnFragmentInteraction {
		/** Called when the cancel button is pressed.
		 * @param frag The fragment it is being called from. */
		public void onCancelButton(ScreenFlashFragment frag);
	}
	
	/////////////////////////////////////////////////////////////////////////////////////////////////
	/// Listeners implemented
	////////////////////////////////////////////////////////////////////////////////////////////////
	
	@Override
	public void onClick(View v) {
		switch (v.getId()){
			case R.id.com_ovrhere_morseflash_frag_flashscreen_button_cancelMessage:
				fragmentInteractionListener.onCancelButton(this);
				break;			
		}
	}
}
