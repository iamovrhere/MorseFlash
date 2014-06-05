package com.ovrhere.android.morseflash.ui;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ScrollView;

import com.ovrhere.android.morseflash.R;
import com.ovrhere.android.morseflash.morsecode.dictionaries.InternationalMorseCode;
import com.ovrhere.android.morseflash.morsecode.transcriber.MorseTranscriber;
import com.ovrhere.android.morseflash.morsecode.transcriber.MorseTranscriberHeadlessFragment;
import com.ovrhere.android.morseflash.ui.fragments.ScreenFlashFragment;
import com.ovrhere.android.morseflash.ui.fragments.ScreenFlashFragment.OnFragmentInteraction;
import com.ovrhere.android.morseflash.ui.prefs.PreferenceUtils;
import com.ovrhere.android.morseflash.utils.CameraFlashUtil;

/**
 * The main activity for the application. This is the primary entry point
 * of the app.
 * @author Jason J.
 * @version 0.3.0-20140605
 */
public class MainActivity extends ActionBarActivity implements 
	ScreenFlashFragment.OnFragmentInteraction,
	MorseTranscriber.OnSignalListener,
	MorseTranscriber.OnTranscriptionListener {
	/** The tag for logs. */
	final static private String CLASS_NAME = MainActivity.class.getSimpleName();
	/** The tag for morse transcriber headless fragment. */
	final static private String MORSE_TRANSCRIBER_TAG = 
			MorseTranscriberHeadlessFragment.class.getName();
	
	/** Bundle key: The current fragment tag. String. */
	final static private String KEY_CURRENT_FRAG_TAG = 
			CLASS_NAME + ".KEY_CURRENT_FRAG_TAG";
	/** Bundle key: Whether the screen is fullscreen. Boolean. */
	final static private String KEY_IS_FULLSCREEN = 
			CLASS_NAME + ".KEY_IS_FULLSCREEN";		
	/** Bundle key: The message for the input screen. String. */
	final static private String KEY_INPUT_MESSAGE = 
			CLASS_NAME + ".KEY_INPUT_MESSAGE";	
	/////////////////////////////////////////////////////////////////////////////////////////////////
	/// end constants
	////////////////////////////////////////////////////////////////////////////////////////////////
	
	/** The morse transcriber as set in the headless fragment. */
	private MorseTranscriber morseTranscriber  = null;
	
	/** The reference to the flasher, if attached. */
	private ScreenFlashFragment flashFrag = null;
	/** The reference to the main fragment, if attached. */
	private MainFragment mainFrag = null;
	/** The reference to the camera flash util to flash light. 
	 * Should be destroyed in onPause. */
	private CameraFlashUtil maincameraFlashUtil = null;
	
	/** The current fragment tag. Top level is {@link MainFragment#TAG}. 
	 * Default empty string.*/
	private String currentFragmentTag = "";
	/** Whether the screen is currently fullscreen. */
	private boolean isFullscreen = false;
	/** The input message for the main fragment. */
	private String inputMessage = "";
	/** If the message is being sent by flash light. */
	private boolean isMessageByFlashLight = false;
	
	/////////////////////////////////////////////////////////////////////////////////////////////////
	/// End members
	////////////////////////////////////////////////////////////////////////////////////////////////
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString(KEY_CURRENT_FRAG_TAG, currentFragmentTag);
		outState.putBoolean(KEY_IS_FULLSCREEN, isFullscreen);
		outState.putString(KEY_INPUT_MESSAGE, inputMessage);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_main);
		FragmentManager manager = getSupportFragmentManager();
		
		MorseTranscriberHeadlessFragment mtFrag = (MorseTranscriberHeadlessFragment)
				manager.findFragmentByTag(MORSE_TRANSCRIBER_TAG);
		if (mtFrag == null ){
			mtFrag = new MorseTranscriberHeadlessFragment();
			morseTranscriber = new MorseTranscriber(new InternationalMorseCode(), this);
			manager.beginTransaction()
					.add(mtFrag, MORSE_TRANSCRIBER_TAG)
					.commit();
			mtFrag.setMorseTranscriber(morseTranscriber);
		} else {
			morseTranscriber = mtFrag.getMorseTranscriber();
			morseTranscriber.setOnSignalListener(this);
		}
		morseTranscriber.setOnMorseListener(this);
		
		if (savedInstanceState == null) {
			setFragToDefault();
		} else {
			currentFragmentTag = savedInstanceState.getString(KEY_CURRENT_FRAG_TAG);
			//if stored state is fullscreen, maintain it.
			if (savedInstanceState.getBoolean(KEY_IS_FULLSCREEN)){
				setFullscreen(true);
			}
			if (savedInstanceState.getString(KEY_INPUT_MESSAGE) != null){
				inputMessage = savedInstanceState.getString(KEY_INPUT_MESSAGE);
			}
		}
	}	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (morseTranscriber != null){
			//clean up activity references
			morseTranscriber.setOnSignalListener(null);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	/////////////////////////////////////////////////////////////////////////////////////////////////
	/// End creation
	////////////////////////////////////////////////////////////////////////////////////////////////
	
	@Override
	protected void onPause() {
		super.onPause();
		maincameraFlashUtil.close();
	}	
	
	@Override
	public void onAttachFragment(Fragment fragment) {
		super.onAttachFragment(fragment);
		if (fragment instanceof ScreenFlashFragment){
			flashFrag = (ScreenFlashFragment) fragment;
			flashFrag.flashBackground(false);
		} else if (fragment instanceof MainFragment){
			mainFrag = ((MainFragment) fragment);
		}
	}
	
	@Override
	public void onBackPressed() {
		if (currentFragmentTag.equals(MainFragment.TAG)){
			super.onBackPressed();
		} else if (currentFragmentTag.equals(ScreenFlashFragment.class.getName())){
			onCancelButton();
		} else {
			//we currently have one depth, if we had more we'd keep a trail
			setFragToDefault();
		}
	}
	
	
	/////////////////////////////////////////////////////////////////////////////////////////////////
	/// Inner fragment
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The fragment for main. Activity must implement
	 * {@link OnFragmentInteraction}.
	 * 
	 *  @version 0.2.1-20140605
	 *  @author Jason J.
	 */
	public static class MainFragment extends Fragment 
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
		public void onSaveInstanceState(Bundle outState) {
			super.onSaveInstanceState(outState);
			outState.putString(KEY_MESSAGE_INPUT_CONTENT, et_messageInput.getText().toString());
			outState.putBoolean(KEY_SHOW_ADVANCED_TOGGLE, cb_advancedSettings.isChecked());
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
				cb_advancedSettings.setChecked(
							savedInstanceState.getBoolean(KEY_SHOW_ADVANCED_TOGGLE)
						);
			}
			return rootView;
		}
		
		@Override
		public void onDestroyView() {
			super.onDestroyView();
			//proper clean up.
			cameraFlashUtil.close();
			cameraFlashUtil = null;
		}
		
		/** Gets the current valid util object. Be sure to call close()
		 *  before destroying it.		 */
		public CameraFlashUtil getCameraFlashUtil() {
			return cameraFlashUtil;
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
			
			cameraFlashUtil = new CameraFlashUtil(
					(SurfaceView) 
						rootView.findViewById(R.id.com_ovrhere_morseflash_frag_main_surfaceview)
					);
			
			((MainActivity)getActivity())._onUpdateCameraFlashUtil(cameraFlashUtil);
			initCameraFlashCheckbox(rootView, cameraFlashUtil);
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
			
			if (cameraFlashUtil.isFlashAvailable()){
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
		/** Toggles visbility of advanced settings container based on bool passed.
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
			isSendingMessage = sending;
		}
		
		/////////////////////////////////////////////////////////////////////////////////////////////////
		/// Listeners
		////////////////////////////////////////////////////////////////////////////////////////////////
		
		@Override
		public void onClick(View v) {
			switch (v.getId()){
			case R.id.com_ovrhere_morseflash_frag_main_button_send:
				MainActivity a = ((MainActivity) getActivity());
				if (isSendingMessage){
					a._onCancelButton();
					setSendingMessage(false);
				} else {
					//prevent double taps
					b_sendMessage.setEnabled(false);
					if (cb_useCamFlash.isChecked()){						
						//re-enable.
						b_sendMessage.setEnabled(true);
						setSendingMessage(true);
					}
					//send message. can be abstract _onSendButton if necessary.
					a._onSendButton(
							et_messageInput.getText().toString(),
							cb_loopMessage.isChecked(),
							cb_useCamFlash.isChecked()
						);
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
				SharedPreferences.Editor editor = prefs.edit();
				editor.putBoolean(
						getResources().getString(
								R.string.com_ovrhere_morseflash_pref_KEY_LOOP_MESSAGE), 
						buttonView.isChecked());
				editor.commit();
				break;
			case R.id.com_ovrhere_morseflash_frag_main_toggle_advanced:
				showAdvancedSettings(buttonView.isChecked());
				break;				
			}
		}
		
		/////////////////////////////////////////////////////////////////////////////////////////////////
		/// Internal interfaces 
		////////////////////////////////////////////////////////////////////////////////////////////////
		
		//can be abstract _onSendButton if necessary.
		/* The interface for fragment interactions. 
		public interface OnFragmentInteraction {
			public void onSendButton(String message);
		}*/
		 
	}
	
	/////////////////////////////////////////////////////////////////////////////////////////////////
	/// Helper functions 
	////////////////////////////////////////////////////////////////////////////////////////////////
	/** Switches the fragment back to the default. */
	private void setFragToDefault(){
		setFragmentAsContent(
				MainFragment.newInstance(inputMessage, false),
				MainFragment.class.getName()
				);
	}
	
	/** Adds/Replaces the content fragment and sets the currentFragmentTag
	 * appropriately.
	 * @param fragment The fragment to add.
	 * @param tag The tag to use for findByTag.
	 */
	private void setFragmentAsContent(Fragment fragment, String tag){
		if (currentFragmentTag.isEmpty()){
			getSupportFragmentManager().beginTransaction()
				.add( R.id.container, fragment, tag)
				.commit();			
		} else {
			getSupportFragmentManager().beginTransaction()
			.replace( R.id.container, fragment, tag)
			.commit();
		}
		currentFragmentTag = tag;
	}
	
	/** Updates the camera flash util as soon as the fragment recreates it
	 * @param cameraFlashUtil
	 */
	private void _onUpdateCameraFlashUtil(CameraFlashUtil cameraFlashUtil){
		maincameraFlashUtil = cameraFlashUtil;
	}
	
	/** The action to take when the fragment has cancelled. */
	private void _onCancelButton(){
		isMessageByFlashLight = false;
		try {
			if (maincameraFlashUtil != null){
				maincameraFlashUtil.flashLed(false); 
			}
		} catch (IllegalStateException e){}
		endMessage();
	}
		
	/** The action to perform when the send button is sent. 
	 * @param message The raw message to pass on.
	 * @param loop Whether or not to loop the message.
	 * @param useFlashlight <code>true</code> to use the camera light, 
	 * <code>false</code> to use screen. 
	 */
	private void _onSendButton(String message, boolean loop, 
			boolean useFlashlight) {
		isMessageByFlashLight = useFlashlight;
		
		morseTranscriber.setMessage(message);
		morseTranscriber.setLoop(loop);
		inputMessage = message;
		
		if (!useFlashlight){
			startFlashFrag(true);
		} else {
			morseTranscriber.start();
		}
	}
	
	
	
	/** Starts or unsets the flashing fragment based upon boolean. 
	 * @param add <code>true</code> adds it, <code>false</code> removes it
	 * and resets to default.
	 */
	private void startFlashFrag(boolean add){
		if (add){
			//swap in screen flash fragment.
			setFragmentAsContent( 
					ScreenFlashFragment.newInstance(),
					ScreenFlashFragment.class.getName()
					);
			setFullscreen(true);
		} else {
			//swap back in main.
			setFragToDefault();
			flashFrag = null;
			setFullscreen(false);
		}		
	}
	/**
	 * Sets the activity to be fullscreen, removing the action bar and status
	 * bar. Also sets isFullscreen to the value passed.
	 * @param fullscreen <code>true</code> to set fullscreen,
	 * <code>false</code> to return to normal screen.
	 */
	private void setFullscreen(boolean fullscreen){
		if (fullscreen){
			getWindow().setFlags(
					WindowManager.LayoutParams.FLAG_FULLSCREEN, 
					WindowManager.LayoutParams.FLAG_FULLSCREEN);
			getWindow().clearFlags(
					WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
			getSupportActionBar().hide();			
		} else {
			getWindow().setFlags(
					WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN, 
					WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
			getWindow().clearFlags(
					WindowManager.LayoutParams.FLAG_FULLSCREEN);
			getSupportActionBar().show();
		}
		//ensure redraw
		findViewById(R.id.container).requestLayout();
		isFullscreen = fullscreen;
	}
	/**
	 * Prompts for the signal action (taking into account UI threading).
	 * @param state <code>true</code> for the on state, <code>false</code>
	 * for off state.
	 */
	private void signalAction(final boolean state){
		if (isMessageByFlashLight){
			try {
				if (maincameraFlashUtil != null){
					maincameraFlashUtil.flashLed(false);
				}
			} catch (IllegalStateException e){}
			
		} else if (flashFrag != null){
			MainActivity.this.runOnUiThread(new Runnable(){
			    public void run(){
			    	flashFrag.flashBackground(state);
			    }
			});
		}		
	}
	/** Ends the message and returns to starting state. */
	private void endMessage(){
		morseTranscriber.cancel();
		if (!isMessageByFlashLight){
			MainActivity.this.runOnUiThread(new Runnable(){
			    public void run(){
			    	startFlashFrag(false);
		    }});
		} else {
			if (mainFrag != null){
				MainActivity.this.runOnUiThread(new Runnable(){
				    public void run(){
				    	mainFrag.setMessageComplete(true);
				    }
			    });
			}
		}
	}
	
	/////////////////////////////////////////////////////////////////////////////////////////////////
	/// Implemented listeners
	////////////////////////////////////////////////////////////////////////////////////////////////
	
	@Override
	public void onCancelButton() {
		Log.d(CLASS_NAME, "onCancelButton");
		endMessage();
	}
	
	@Override
	public void onFragmentViewLoaded() {
		morseTranscriber.start();
	}
	
	@Override
	public void onSignalStart() {
		signalAction(true);
		Log.d(CLASS_NAME, "onSignalStart");
	}
	
	@Override
	public void onSignalEnd() {
		signalAction(false);
		Log.d(CLASS_NAME, "onSignalEnd");
	}
	
	@Override
	public void onMorseParsed() {
		// TODO Auto-generated method stub		
	}
	
	@Override
	public void onMorseCompleted() {
		Log.d(CLASS_NAME, "message complete");
		endMessage();		
	}

}
