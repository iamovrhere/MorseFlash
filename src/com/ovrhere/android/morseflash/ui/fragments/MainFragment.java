/*
 * Copyright 2014 Jason J.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.ovrhere.android.morseflash.ui.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ScrollView;

import com.ovrhere.android.morseflash.R;
import com.ovrhere.android.morseflash.ui.prefs.PreferenceUtils;
import com.ovrhere.android.morseflash.utils.CameraFlashUtil;

/**
 * The fragment for main. Activity must implement
 * {@link OnFragmentInteractionListener}.
 * 
 *  @version 0.5.0-20140719
 *  @author Jason J.
 */
public class MainFragment extends Fragment 
	implements OnClickListener, OnCheckedChangeListener {
	/** The class name. */
	final static private String CLASS_NAME = MainFragment.class.getSimpleName();
	/** The tag used in fragments. */
	final static public String TAG = MainFragment.class.getName();
	
	/** Bundle key: The message input content.String. */
	final static private String KEY_MESSAGE_INPUT_CONTENT = 
			CLASS_NAME + ".KEY_MESSAGE_INPUT_CONTENT";
	/** Bundle key: The bool to determine if we are currently sending a message. 
	 * Boolean. */
	final static private String KEY_SENDING_MESSAGE_CURRENTLY = 
			CLASS_NAME + ".KEY_SENDING_MESSAGE_CURRENTLY";
	/** Bundle key: The checkbox/toggle to show advanced settings. Boolean. */ 
	final static private String KEY_SHOW_ADVANCED_TOGGLE = 
			CLASS_NAME + ".KEY_SHOW_ADVANCED_TOGGLE";
	/////////////////////////////////////////////////////////////////////////////////////////////////
	/// end constants
	////////////////////////////////////////////////////////////////////////////////////////////////
	/** The camera flash util for checking and flashing the camera light. */
	private CameraFlashUtil cameraFlashUtil = null;
	/** Whether or not the send message button {@link #b_sendMessage} is
	 * Cancel or not. default is false.		 */
	private boolean isSendingMessage = false;
	
	/** Sends the message. */
	private Button b_sendMessage = null;
	/** The message input for main. */
	private EditText et_messageInput = null;		
	/** The checkbox to determine if to loop the message. */
	private CheckBox cb_loopMessage = null;
	/** The CompoundButton/toggle button to show and hide advanced options. */
	private CompoundButton cb_advancedSettings = null;
	/** The checkbox to determine whether to use the camera flash or not. */
	private CheckBox cb_useCamFlash  = null; 
	
	
	/** Container view holding advanced options. Used to toggle visibility. */
	private View v_advancedOptionsContainer = null;
	/** The root container of the fragment. */
	private ScrollView sv_scrollContainer = null;
	
	/** The handle for read access preferences. */
	private SharedPreferences prefs = null;
	/** The fragment interaction listner, typically the attached activity. */
	private OnFragmentInteractionListener mFragmentInteractionListener = null;
	
	public MainFragment() {}		
	
	static public MainFragment newInstance(String startingMessage, 
			boolean isSendingMessage){
		MainFragment fragment = new MainFragment();
		Bundle args = new Bundle();
		args.putString(KEY_MESSAGE_INPUT_CONTENT, startingMessage);
		args.putBoolean(KEY_SENDING_MESSAGE_CURRENTLY, isSendingMessage);
		
		fragment.setArguments(args);
		return fragment;
	}
	
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mFragmentInteractionListener = (OnFragmentInteractionListener) activity;
		} catch (ClassCastException e){
			String message = "Activity \""+ activity.getClass().getSimpleName()
					+"\" must implement OnFragmentInteractionListener";
			Log.e(CLASS_NAME, message);
			throw new ClassCastException(message);
		}
	}
	
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString(KEY_MESSAGE_INPUT_CONTENT, et_messageInput.getText().toString());
		outState.putBoolean(KEY_SHOW_ADVANCED_TOGGLE, cb_advancedSettings.isChecked());
		outState.putBoolean(KEY_SENDING_MESSAGE_CURRENTLY, isSendingMessage);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_main, container,
				false);
		initViews(rootView);
		
		//Assumes all views are initialised by tFragmenthis point.
		if (getArguments() != null) {
			Bundle args = getArguments();
			et_messageInput.setText(
					args.getString(KEY_MESSAGE_INPUT_CONTENT)
					);
			isSendingMessage = 
					args.getBoolean(KEY_SENDING_MESSAGE_CURRENTLY);
		}
		if (savedInstanceState != null){
			et_messageInput.setText(
						savedInstanceState.getString(KEY_MESSAGE_INPUT_CONTENT)
					);
			et_messageInput.clearFocus();
			cb_advancedSettings.setChecked(
						savedInstanceState.getBoolean(KEY_SHOW_ADVANCED_TOGGLE)
					);
			setSendingMessage(
					savedInstanceState.getBoolean(KEY_SENDING_MESSAGE_CURRENTLY)
					);			
		}
		
		if (isSendingMessage){
			mFragmentInteractionListener.onSendButton(
					et_messageInput.getText().toString()
					);
		}
		return rootView;
	}
	
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		//proper clean up.
		closeCameraFlashUtil();
	}

	
	@Override
	public void onPause() {
		super.onPause();
		closeCameraFlashUtil();
	}
	
	@Override
	public void onResume() {	
		super.onResume();
		closeCameraFlashUtil();
		initCameraFlashUtil(getView());
	}
	
	
	/** Informs the fragment if sending has completed. */
	public void setMessageComplete(boolean complete){
		setSendingMessage(!complete);
	}

	/////////////////////////////////////////////////////////////////////////////////////////////////
	/// Initialiser helper functions 
	////////////////////////////////////////////////////////////////////////////////////////////////
	/** Initialises the views, helps keep code tidy. 
	 * @param rootView The fragment root view being created.  
	 */
	private void initViews(View rootView) {
		sv_scrollContainer = (ScrollView) 
				rootView.findViewById(R.id.com_ovrhere_morseflash_frag_main_scrollContainer);	
		
		et_messageInput = (EditText) 
				rootView.findViewById(R.id.com_ovrhere_morseflash_frag_main_editext_textToMorse_input);
		b_sendMessage = (Button) 
				rootView.findViewById(R.id.com_ovrhere_morseflash_frag_main_button_send);
		b_sendMessage.setOnClickListener(this);
		
		prefs = PreferenceUtils.getPreferences(getActivity());
		
		initLoopCheckbox(rootView, prefs);
		initAdvancedContainerToggle(rootView);
		
		initCameraFlashUtil(rootView);		
		
		initCameraFlashCheckbox(rootView, cameraFlashUtil);
	}

	/** Initialises the camera flash utility using the surface view. */
	private void initCameraFlashUtil(final View rootView) {
		SurfaceView sv = (SurfaceView) 
				rootView.findViewById(R.id.com_ovrhere_morseflash_frag_main_surfaceview);
		if (sv != null){
			cameraFlashUtil = new CameraFlashUtil(sv);		
			mFragmentInteractionListener.onUpdateCameraFlashUtil(cameraFlashUtil);
		}
	}

	/** Initialises the loop message checkbox. 
	 * Assumes {@link #prefs} has been initialised. */
	private void initLoopCheckbox(View rootView, SharedPreferences prefs) {
		cb_loopMessage = (CheckBox) 
				rootView.findViewById(R.id.com_ovrhere_morseflash_frag_main_checkbox_loopMessage);
		cb_loopMessage.setOnCheckedChangeListener(this);
		//set checkbox according to preference
		cb_loopMessage.setChecked(
			prefs.getBoolean(
					getResources().getString(
							R.string.com_ovrhere_morseflash_pref_KEY_LOOP_MESSAGE),
							false)
			);
	}

	/** Initalises the advanced container and the toggle
	 * responsible for displaying/hiding it. */
	private void initAdvancedContainerToggle(View rootView) {
		cb_advancedSettings = (CompoundButton) 
				rootView.findViewById(R.id.com_ovrhere_morseflash_frag_main_toggle_advanced);
		cb_advancedSettings.setOnCheckedChangeListener(this);
		
		v_advancedOptionsContainer = 
				rootView.findViewById(R.id.com_ovrhere_morseflash_frag_main_layout_checkboxContainer);
	}
	
	/** Initialises the camera flash checkbox according to the 
	 * availability of the camera flash. */
	private void initCameraFlashCheckbox(View rootView, 
			CameraFlashUtil cameraFlashUtil) {
		cb_useCamFlash = (CheckBox)
				rootView.findViewById(R.id.com_ovrhere_morseflash_frag_main_checkbox_useCameraLight);
		cb_useCamFlash.setOnCheckedChangeListener(this);			
		cb_useCamFlash.setChecked(
				prefs.getBoolean(
						getResources().getString(
								R.string.com_ovrhere_morseflash_pref_KEY_USE_CAMERA_FLASH),
								false)
				);
		
		if (cameraFlashUtil != null && cameraFlashUtil.isFlashAvailable()){
			cb_useCamFlash.setEnabled(true);
			cb_useCamFlash.setText(
					getResources().getString(R.string.com_ovrhere_checkbox_camlight)
					);
		} else {
			cb_useCamFlash.setText(
					getResources().getString(R.string.com_ovrhere_checkbox_camFlashNotFound)
					);
			cb_useCamFlash.setEnabled(false);
			cb_useCamFlash.setChecked(false);  
		}
	}
	
	/////////////////////////////////////////////////////////////////////////////////////////////////
	/// Helper methods
	////////////////////////////////////////////////////////////////////////////////////////////////

	/** Closes the camera flash util and sets it to null. */
	private void closeCameraFlashUtil() {
		if (cameraFlashUtil != null){
			cameraFlashUtil.close();
			cameraFlashUtil = null;
		}
		mFragmentInteractionListener.onUpdateCameraFlashUtil(null);
	}
	
	/** Toggles visibility of advanced settings container based on bool passed.
	 * @param show <code>true</code> to show container, 
	 * <code>false</code> to hide.
	 */
	private void showAdvancedSettings(boolean show){
		if (v_advancedOptionsContainer == null) return;
		//TODO: return to same scrollview to original height.			
		if (show){
			v_advancedOptionsContainer.setVisibility(View.VISIBLE);
		} else {
			v_advancedOptionsContainer.setVisibility(View.GONE);
		}
		
	}
	/**
	 * Sets the #isSendingMessage bool. Additionally, 
	 * it:
	 * <ul><li> toggles send button's action and string to be send/cancel.
	 * Send if not sending currently, cancel if sending.</li>
	 * <li>disables the text field when sending, enables when inactive. </li>
	 * <li>disables the checkboxes when sending, enables when inactive. </li>
	 * <ul>
	 * @param sending <code>true</code> if currently sending, 
	 * <code>false</code> if not.
	 */
	private void setSendingMessage(boolean sending){
		int stringId = sending 	? R.string.com_ovrhere_button_cancel 
								: R.string.com_ovrhere_sendMessage;
		b_sendMessage.setText(getResources().getString(stringId));
		
		b_sendMessage.postInvalidate(); //redraw
		et_messageInput.setEnabled(!sending);
		cb_useCamFlash.setEnabled(!sending);
		cb_loopMessage.setEnabled(!sending);
		isSendingMessage = sending;
	}
	
	/** Sets boolean pref based upon a supplied key id and value. 
	 * @param boolKeyId The id to the bool pref key. 
	 * @param value The value to set the preference as.	 */
	private void setBoolPref(int boolKeyId, boolean value) {
		SharedPreferences.Editor editor = prefs.edit();
		editor.putBoolean(
				getResources().getString(boolKeyId), 
						value);
		editor.commit();
	}
	
	/** Hides the virtual keyboard from the screen. */
	private void hideKeyboard() {
		et_messageInput.clearFocus();
		InputMethodManager imm = 
				(InputMethodManager) getActivity().getSystemService(
						Context.INPUT_METHOD_SERVICE
						);
		imm.hideSoftInputFromWindow(et_messageInput.getWindowToken(), 0);
	}
	
	
	/////////////////////////////////////////////////////////////////////////////////////////////////
	/// Listeners
	////////////////////////////////////////////////////////////////////////////////////////////////
	
	@Override
	public void onClick(View v) {
		switch (v.getId()){
		case R.id.com_ovrhere_morseflash_frag_main_button_send:
			if (isSendingMessage){
				mFragmentInteractionListener.onCancelButton();
				setSendingMessage(false);
			} else {
				String msg = et_messageInput.getText().toString();
				if (msg.trim().isEmpty()){
					break;//if there is no message, nothing to send.
				}
				//prevent double taps
				b_sendMessage.setEnabled(false);
				if (cb_useCamFlash.isChecked()){						
					//re-enable.
					b_sendMessage.setEnabled(true);
					setSendingMessage(true);
				}
				mFragmentInteractionListener.onSendButton(msg);
				hideKeyboard();
			}
			
			break;
		}			
	}
	
	@Override
	public void onCheckedChanged(CompoundButton buttonView,
			boolean isChecked) {
		//is called on every change, even programmatically.
		
		switch	(buttonView.getId()){
		case R.id.com_ovrhere_morseflash_frag_main_checkbox_loopMessage:
			setBoolPref(R.string.com_ovrhere_morseflash_pref_KEY_LOOP_MESSAGE, 
						buttonView.isChecked());
			break;
		case R.id.com_ovrhere_morseflash_frag_main_checkbox_useCameraLight:
			setBoolPref(R.string.com_ovrhere_morseflash_pref_KEY_USE_CAMERA_FLASH, 
					buttonView.isChecked());
			break;
		case R.id.com_ovrhere_morseflash_frag_main_toggle_advanced:
			showAdvancedSettings(buttonView.isChecked());
			break;				
		}
	}	
	
	/////////////////////////////////////////////////////////////////////////////////////////////////
	/// Internal interfaces 
	////////////////////////////////////////////////////////////////////////////////////////////////
	
	/** The interface for fragment interactions. 
	 * Currently account for : sendButton click, cancelButton click, CameraFlashUtil updates.
	 * @version 0.2.0-20140606
	 * */ 
	public interface OnFragmentInteractionListener {
		/** Updates the camera flash util as soon as the fragment creates it.
		 * @param cameraFlashUtil The current valid camerFlashUtil of the 
		 * fragment or <code>null</code>.			 */
		public void onUpdateCameraFlashUtil(CameraFlashUtil cameraFlashUtil);
		/** The action to take when the fragment has cancelled an action. */
		public void onCancelButton();
		/** The action to perform when the send button is sent. 
		 * @param message The raw message to pass on.
		 * @param loop Whether or not to loop the message.
		 * @param useFlashlight <code>true</code> to use the camera light, 
		 * <code>false</code> to use screen. 
		 * @deprecated Use {@link #onSendButton(String)} instead. 
		 * Looping and camera flash should be accessed through the 
		 * {@link SharedPreferences} (via {@link PreferenceUtils}). 
		 */
		@Deprecated
		public void onSendButton(String message, boolean looped, boolean useCameraFlash);
		/** The action to perform when the send button is sent. 
		 * @param message The raw message to pass on. */
		public void onSendButton(String message);
	}
	 
}
