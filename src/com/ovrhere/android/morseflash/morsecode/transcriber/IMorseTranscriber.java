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


/**
 * The general interface for the morse transcriber. 
 * Used to give a common interface between the object and the HeadlessFragment.
 * 
 * @author Jason J.
 * @version 0.4.0-20140623
 */
interface IMorseTranscriber {
	/////////////////////////////////////////////////////////////////////////////////////////////////
	/// Mutators/accessors
	////////////////////////////////////////////////////////////////////////////////////////////////
	
	/** Sets listener for events such as {@link OnTranscriptionListener#onMorseParsed()}
	 * and {@link OnTranscriptionListener#onMorseCompleted()}
	 * @param morseListener The listener to set.	 */
	public void setOnMorseListener(OnTranscriptionListener morseListener);
	/** Replaces and sets the listener for signal events. 
	 * @param signalListener The new listener to use.	 */
	public void setOnSignalListener(OnSignalListener signalListener);
	
	/** Sets the message for the transcriber. 
	 * @param msg The message to transcribe. Note that unsupported characters
	 * will be skipped.
	 */
	public void setMessage(String msg); 
	
	/** @param loopMessage Whether or not to loop the message and replay it 
	 * infinitely.	 */
	public void setLoop(boolean loopMessage);
	
	/** @return <code>true</code> if the message is set to loop on completion, 
	 * <code>false</code> otherwise.	 */
	public boolean isMessageLooped();
	/** @return <code>true</code> if the transcriber is transcribing, 
	 * <code>false</code> otherwise.
	 */
	public boolean isRunning();
	
	/** Sets the unit time equivalent to one dot.
	 * @param unitTime Time in milliseconds. Must be > 0. */
	public void setUnitTime(int unitTime);
	
	/** Sets the padd time before and after a message.
	 * @param padTime The time in milliseconds to wait before sending. 
	 * Must be >= 0. 	 */
	public void setPadTime(int padTime);
	
	/** @return The unit time equivalent to one dot in milliseconds.	 */
	public int getUnitTime();
	
	/////////////////////////////////////////////////////////////////////////////////////////////////
	/// Action functions
	////////////////////////////////////////////////////////////////////////////////////////////////
	
	/** Convenience function. Same as calling {@link #setOnMorseListener(OnTranscriptionListener)}
	 * and start().
	 * @see start*/
	public boolean start (OnTranscriptionListener onMorseListener);
	
	/** Starts the transcription/sending.
	 * Be sure to have implemented the {@link OnSignalListener} to display actions.
	 * @return <code>true</code> if successfully started, <code>false</code> otherwise.
	 */
	public boolean start();
	
	/** Cancels the sending.
	 * @return <code>true</code> if cancelled, <code>false</code> 
	 * if it cannot be cancelled (such as not running). 
	 */
	public boolean cancel();
	
	/////////////////////////////////////////////////////////////////////////////////////////////////
	/// Internal Listener interfaces
	////////////////////////////////////////////////////////////////////////////////////////////////
	
	//moved to IMorseTranscriber
	/** Listens to when the message has been parsed into Morse 
	* or has finished sending and notifies the listener.
	* @author Jason J.
	* @version 0.1.0-20140522
	*/
	public interface OnTranscriptionListener{
		/** Called when the message has been parsed into morse. */
		public void onMorseParsed();
		/** Called when the message has finished sending. */
		public void onMorseCompleted();
	}
	/**
	* Listens to when a Morse signal is being starting or ending.
	* This is to be implemented to determine how a message is to be transmitted. 
	* Note that this may occur in a thread.
	* @author Jason J.
	* @version 0.1.0-20140512
	*/
	public interface OnSignalListener {
		/** Sent when a signal unit starts. */
		public void onSignalStart();
		/** Sent when a signal unit ends. */
		public void onSignalEnd();
	}
}
