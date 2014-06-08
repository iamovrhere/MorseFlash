package com.ovrhere.android.morseflash.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import com.ovrhere.android.morseflash.R;
import com.ovrhere.android.morseflash.morsecode.dictionaries.InternationalMorseCode;
import com.ovrhere.android.morseflash.morsecode.transcriber.MorseTranscriber;
import com.ovrhere.android.morseflash.morsecode.transcriber.MorseTranscriberHeadlessFragment;
import com.ovrhere.android.morseflash.ui.fragments.MainFragment;
import com.ovrhere.android.morseflash.ui.fragments.ScreenFlashFragment;
import com.ovrhere.android.morseflash.ui.prefs.PreferenceUtils;
import com.ovrhere.android.morseflash.utils.CameraFlashUtil;

/**
 * The main activity for the application. This is the primary entry point
 * of the app.
 * @author Jason J.
 * @version 0.5.0-20140606
 */
public class MainActivity extends ActionBarActivity implements
	MainFragment.OnFragmentInteractionListener,
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
	
	/** The reference to shared preferences for the application. */
	private SharedPreferences prefs = null;
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
		
		prefs = PreferenceUtils.getPreferences(this);
		
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
		//maincameraFlashUtil.close();
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
	/// Inner fragment - Moved to MainFragment.java
	////////////////////////////////////////////////////////////////////////////////////////////////
	
	/////////////////////////////////////////////////////////////////////////////////////////////////
	/// Helper functions 
	////////////////////////////////////////////////////////////////////////////////////////////////
	/** Switches the fragment back to the default. */
	private void setFragToDefault(){
		setFragmentAsContent(
				MainFragment.newInstance(inputMessage, false),
				MainFragment.TAG //as per doc.
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
			setScreenBrightnessMax(true);
			setFullscreen(true);
		} else {
			//swap back in main.
			setFragToDefault();
			flashFrag = null;			
			setScreenBrightnessMax(false);
			setFullscreen(false);
		}		
	}
	/** Sets the activity screen brightness to max or default value.
	 * @param max <code>true</code>: set to max, <code>false</code>: set to default.
	 */
	private void setScreenBrightnessMax(boolean max){
		//FIXME Incompatible with "WindowManager.LayoutParams.FLAG_FULLSCREEN", which takes presedence 
		//May be niche bug as it was found on Samsung API10 device.
		WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.screenBrightness = max 	? WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_FULL 
        							: WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE;
        getWindow().setAttributes(lp);
	}
	/**
	 * Sets the activity to be fullscreen, removing the action bar and status
	 * bar. Also sets isFullscreen to the value passed.
	 * @param fullscreen <code>true</code> to set fullscreen,
	 * <code>false</code> to return to normal screen.
	 */
	private void setFullscreen(boolean fullscreen){
		Window window = getWindow();
		if (fullscreen){
			window.setFlags(
					WindowManager.LayoutParams.FLAG_FULLSCREEN, 
					WindowManager.LayoutParams.FLAG_FULLSCREEN);
			window.clearFlags(
					WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
			getSupportActionBar().hide();			
		} else {
			window.setFlags(
					WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN, 
					WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
			window.clearFlags(
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
			
		} else {
			MainActivity.this.runOnUiThread(new Runnable(){
			    public void run(){
			    	if (flashFrag != null){
			    		synchronized (flashFrag) {
			    			if (flashFrag != null) flashFrag.flashBackground(state);
			    		}
			    	}
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
				    	if (mainFrag != null){
				    		synchronized (mainFrag) {
				    			if (mainFrag != null) mainFrag.setMessageComplete(true);
				    		}
				    	}
				    	
				    }
			    });
			}
		}
	}
	
	/** Gets boolean preference based on bool id. Default value is false. */
	private boolean getBoolPref(int boolKeyId) {
		return prefs.getBoolean(getResources().getString(boolKeyId), false);
	}
	
	/////////////////////////////////////////////////////////////////////////////////////////////////
	/// Implemented listeners
	////////////////////////////////////////////////////////////////////////////////////////////////
	
	@Override
	public void onUpdateCameraFlashUtil(CameraFlashUtil cameraFlashUtil){
		maincameraFlashUtil = cameraFlashUtil;
	}
	
	@Override
	public void onSendButton(String message) {
			boolean loop  = 
				getBoolPref(R.string.com_ovrhere_morseflash_pref_KEY_LOOP_MESSAGE);
		isMessageByFlashLight = 
				getBoolPref(R.string.com_ovrhere_morseflash_pref_KEY_USE_CAMERA_FLASH);
		
		morseTranscriber.setMessage(message);
		morseTranscriber.setLoop(loop);
		inputMessage = message;
		
		if (!isMessageByFlashLight){
			startFlashFrag(true);
		} else {
			morseTranscriber.start();
		}
	}
	
	
	@Override	@Deprecated
	public void onSendButton(String message, boolean looped,
			boolean useCameraFlash) {
		onSendButton(message);
	}
	
	@Override
	public void onCancelButton() {
		//Overlapping interfaces, how urgent is this to fix?
		Log.d(CLASS_NAME, "onCancelButton");
		if (currentFragmentTag.equals(MainFragment.TAG)){
			if (isMessageByFlashLight){
					isMessageByFlashLight = false;
				try {
					if (maincameraFlashUtil != null){
						maincameraFlashUtil.flashLed(false); 
					}
				} catch (IllegalStateException e){}
			}
		} 
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
