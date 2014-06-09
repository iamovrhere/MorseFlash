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
package com.ovrhere.android.morseflash.morsecode.transcriber;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ovrhere.android.morseflash.morsecode.dictionaries.MorseDictionary;
import com.ovrhere.android.morseflash.morsecode.transcriber.IMorseTranscriber.OnSignalListener;

/**
 * Headless fragment to assist in the use of the {@link MorseTranscriber}.
 * In doing so, the transcriber can be used easiler with an activity even during 
 * rotations. 
 * 
 * @author Jason J.
 * @version 0.1.0-20140527
 */
public class MorseTranscriberHeadlessFragment extends Fragment {
//implements IMorseTranscriber{
	/** The internal reference to the morseTranscriber. */
	private MorseTranscriber morseTranscriber = null; 
	
	/////////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessors and mutators
	////////////////////////////////////////////////////////////////////////////////////////////////
	/** Sets the morse transcriber to one initialised elsewhere. */
	public void setMorseTranscriber(MorseTranscriber morseTranscriber) {
		this.morseTranscriber = morseTranscriber;
	}
	/** Configures the morse transcriber.
	 * @param dictionary The dictionary used to translate between strings and morse.
	 * @param signalListener The listener for when morse start/end signals are sent.
	 */
	public void setMorseTranscriber(MorseDictionary dictionary, OnSignalListener signalListener) {
		this.morseTranscriber = new MorseTranscriber(dictionary, signalListener);
	}
	/** Returns the morse transcriber. */
	public MorseTranscriber getMorseTranscriber() {
		return morseTranscriber;
	}
	
	/////////////////////////////////////////////////////////////////////////////////////////////////
	/// end accessors/mutators
	////////////////////////////////////////////////////////////////////////////////////////////////
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);	
		setRetainInstance(true);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return null;
	}
	
	/////////////////////////////////////////////////////////////////////////////////////////////////
	/// implement interface function for uniformity
	////////////////////////////////////////////////////////////////////////////////////////////////
	/*
	//Actions
	////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean start() {
		return morseTranscriber.start();
	}
	
	@Override
	public boolean start(OnMorseListener onMorseListener) {
		return morseTranscriber.start(onMorseListener);
	}
	
	@Override
	public boolean cancel() {
		return morseTranscriber.cancel();
	}
	
	//mutators/accessors
	////////////////////////////////////////////////////////////////////////////
	
	@Override
	public int getUnitTime() {
		return morseTranscriber.getUnitTime();
	}
	
	@Override
	public boolean isMessageLooped() {
		return morseTranscriber.isMessageLooped();
	}
	
	@Override
	public void setLoop(boolean loopMessage) {
		morseTranscriber.setLoop(loopMessage);
	}
	
	@Override
	public void setMessage(String msg) {
		morseTranscriber.setMessage(msg);
	}
	
	@Override
	public void setMorseListener(OnMorseListener morseListener) {
		morseTranscriber.setMorseListener(morseListener);
	}
	
	@Override
	public void setUnitTime(int unitTime) {
		morseTranscriber.setUnitTime(unitTime);
	}
*/	
	
}
