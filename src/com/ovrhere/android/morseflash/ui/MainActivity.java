package com.ovrhere.android.morseflash.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.ovrhere.android.morseflash.R;
import com.ovrhere.android.morseflash.morsecode.dictionaries.InternationalMorseCode;
import com.ovrhere.android.morseflash.morsecode.transcriber.MorseTranscriber;
import com.ovrhere.android.morseflash.morsecode.transcriber.MorseTranscriberHeadlessFragment;
import com.ovrhere.android.morseflash.ui.fragments.ScreenFlashFragment;
import com.ovrhere.android.morseflash.ui.fragments.ScreenFlashFragment.OnFragmentInteraction;

/**
 * The main activity for the application. This is the primary entry point
 * of the app.
 * @author Jason J.
 * @version 0.1.0-20140527
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
	
	/** The current fragment tag. String. */
	final static private String KEY_CURRENT_FRAG_TAG = 
			CLASS_NAME + ".KEY_CURRENT_FRAG_TAG";	
	/////////////////////////////////////////////////////////////////////////////////////////////////
	/// end constants
	////////////////////////////////////////////////////////////////////////////////////////////////
	
	/** The morse transcriber as set in the headless fragment. */
	private MorseTranscriber morseTranscriber  = null;
	
	/** The reference to the flasher, if attached. */
	private ScreenFlashFragment flashFrag = null;
	
	/** The current fragment tag. Top level is {@link MainFragment#TAG}. 
	 * Default empty string.*/
	private String currentFragmentTag = "";
	
	/////////////////////////////////////////////////////////////////////////////////////////////////
	/// End members
	////////////////////////////////////////////////////////////////////////////////////////////////
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString(KEY_CURRENT_FRAG_TAG, currentFragmentTag);
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
	public void onAttachFragment(Fragment fragment) {
		super.onAttachFragment(fragment);
		if (fragment instanceof ScreenFlashFragment){
			flashFrag = (ScreenFlashFragment) fragment;
			flashFrag.flashBackground(false);
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
	 * {@link OnFragmentInteraction} 
	 */
	public static class MainFragment extends Fragment 
		implements OnClickListener{
		/** The tag used in fragments. */
		final static public String TAG = MainFragment.class.getName();
		/** The message input content.String. */
		final static private String KEY_MESSAGE_INPUT_CONTENT = 
				MainFragment.class.getName() + ".KEY_MESSAGE_INPUT_CONTENT";

		public MainFragment() {
		}
		
		/** Sends the message. */
		private Button sendMessage = null;
		/** The message input for main. */
		private EditText messageInput = null;
		
		@Override
		public void onSaveInstanceState(Bundle outState) {
			super.onSaveInstanceState(outState);
			outState.putString(KEY_MESSAGE_INPUT_CONTENT, messageInput.getText().toString());
		}
	
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			messageInput = (EditText) rootView.findViewById(R.id.com_ovrhere_morseflash_frag_main_editext_textToMorse_input);
			sendMessage = (Button) rootView.findViewById(R.id.com_ovrhere_morseflash_frag_main_button_send);
			sendMessage.setOnClickListener(this);
			
			if (savedInstanceState != null){
				messageInput.setText(
							savedInstanceState.getString(KEY_MESSAGE_INPUT_CONTENT)
						);
			}
			return rootView;
		}
		
		@Override
		public void onClick(View arg0) {
			switch (arg0.getId()){
			case R.id.com_ovrhere_morseflash_frag_main_button_send:
				((MainActivity) getActivity())._onSendButton(
							messageInput.getText().toString()
						);
				
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
				new MainFragment(),
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
	
	/** The action to perform when the send button is sent. 
	 * @param message The raw message to pass on.
	 */
	private void _onSendButton(String message) {
		startFlashFrag(true);
		morseTranscriber.setMessage(message);	
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
	 * bar.
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
		//ensure the actionbar is in the correct location
		findViewById(R.id.container).requestLayout();
	}
	/**
	 * Prompts for the signal action (taking into account UI threading).
	 * @param state <code>true</code> for the on state, <code>false</code>
	 * for off state.
	 */
	private void signalAction(final boolean state){
		if (flashFrag != null){
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
		MainActivity.this.runOnUiThread(new Runnable(){
		    public void run(){
		    	startFlashFrag(false);
	    }});
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
