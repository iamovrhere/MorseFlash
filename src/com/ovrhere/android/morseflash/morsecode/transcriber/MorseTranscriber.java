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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.ovrhere.android.morseflash.morsecode.dictionaries.MorseDictionary;
import com.ovrhere.android.morseflash.morsecode.dictionaries.MorseDictionary.MorseCharacter;

/**
 * <p>Performs the basic transcription of a string message into morse code
 * by means of a {@link MorseDictionary}. This follows the general rules of
 * <a href="http://en.wikipedia.org/wiki/Morse_code">Morse code</a>, namely:
 * <ul>
 * <li>dots are 1 Time Unit (TU)</li>
 * <li>dashes are {@value #REL_INTERVAL_DASH}TU</li>
 * <li>intervals between intra-characters (dashes/dots) are 1TU</li>
 * <li>intervals between characters (a b c) are {@value #REL_INTERVAL_CHARACTER}TU</li>
 * <li>intervals between words are {@value #REL_INTERVAL_WORD}TU</li> 
 * </ul>
 * An additional (non-standard) interval of {@value #REL_INTERVAL_LOOP_MESSAGE}TU
 * is given between looped-message iterations. Default unit time is 
 * {@value #DEFAULT_UNIT_TIME}ms.
 * </p>
 * 
 * <p>There is an additional delay before/after a message set by 
 * 
 * Default pad time is {@value #DEFAULT_UNIT_TIME}ms.</p>
 * 
 * <p>Please note this class is not safe for within rotation contexts.
 * Consider using {@link MorseTranscriberHeadlessFragment} within activities.
 * </p> 
 * 
 * @author Jason J.
 * @version 0.4.1-20140713
 */
public class MorseTranscriber implements IMorseTranscriber {
	/** The tag used for logging. */
	@SuppressWarnings("unused")
	final static private String LOGTAG = MorseTranscriber.class.getSimpleName();
	/** The default unit time in milliseconds. */
	final private static int DEFAULT_UNIT_TIME = 100; //ms
	/** The default pad time in milliseconds. */
	final private static int DEFAULT_PAD_TIME = 500; //ms
	
	/* * The relative interval between each pattern unit. */
	//final private static int REL_INTERVAL_PATTERN_UNIT = 1; //units
	/** The relative interval for each dash. */
	final private static int REL_INTERVAL_DASH = 3; //units
	/** The relative interval between each morse letter. */
	final private static int REL_INTERVAL_CHARACTER = 3; //units
	/** The relative interval between each word. */
	final private static int REL_INTERVAL_WORD = 7; //units
	/** The relative interval between each message (if looped). */
	final private static int REL_INTERVAL_LOOP_MESSAGE = 15; //units
	
	/////////////////////////////////////////////////////////////////////////////////////////////////
	/// End constants 
	////////////////////////////////////////////////////////////////////////////////////////////////
	/** The number of milliseconds to wait before sending a message. */
	private int messagePadTime = DEFAULT_PAD_TIME; 
	
	/** The number of milliseconds considered to be one unit. 
	 * Default value is {@value #DEFAULT_UNIT_TIME}. */ 
	private int unitTime = DEFAULT_UNIT_TIME; //ms
	/** The timing for between dots and dashes is unit time. */
	private int morseSymbolInterval = unitTime;
	/** The timing for dots is unit time. */
	private int morseDotInterval = unitTime;	
	/** The timing for dashes ({@link #unitTime}* {@link #REL_INTERVAL_DASH}). */
	private int morseDashInterval = unitTime * REL_INTERVAL_DASH;
	/** The timing between characters ({@link #unitTime}* {@link #REL_INTERVAL_CHARACTER}). */
	private int morseCharInterval = unitTime * REL_INTERVAL_CHARACTER;
	/** The timing between words ({@link #unitTime}* {@link #REL_INTERVAL_WORD}). */
	private int morseWordInterval = unitTime * REL_INTERVAL_WORD;
	/** The timing between messages ({@link #unitTime}* {@link #REL_INTERVAL_LOOP_MESSAGE}). */
	private int morseLoopMessageInterval = unitTime * REL_INTERVAL_LOOP_MESSAGE;
	
	/** The dictionary to lookup characters in. */
	private MorseDictionary dictionary = null;
	
	/** The signal listener that defines the actions to do for morse code. */
	private OnSignalListener m_SignalListener = null;
	/** The morse listener for events. Can be null. */
	private OnTranscriptionListener m_MorseListener = null;
	
	/** The time to use to schedule morse signals. */
	private Timer signalTimer = new Timer();	
	
	/** Whether the message should continue processing. Default false. */
	volatile private boolean continueMessageProcessing = false;
	/** Whether the message is currently sending. */
	volatile private boolean messageIsSending = false;
	
	/** Whether or not to play the message again when completed. Default false. */
	volatile private boolean loopMessage = false; 

	/** A list of the words in the message after said message has been exploded. */
	private String[] messageList = new String[]{};
	
	/**
	 * @param dictionary The dictionary used to translate between strings and morse.
	 * @param signalListener The listener for when morse start/end signals are sent.
	 */
	public MorseTranscriber(MorseDictionary dictionary, OnSignalListener signalListener) {
		this.dictionary = dictionary;
		this.m_SignalListener = signalListener;
	}
	
	/////////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessors/mutators begin here
	//
	// NOTE: DO NOT add public functions here without adding them to 
	// IMorseTranscriber first
	////////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void setOnMorseListener(OnTranscriptionListener morseListener) {
		this.m_MorseListener = morseListener;
	}
	
	@Override
	public void setOnSignalListener(OnSignalListener signalListener) {
		this.m_SignalListener  = signalListener;
	}
	
	@Override
	public void setMessage(String msg){
		synchronized (messageList){
			this.messageList = msg.split("\\s+");
		}
	}
	/**
	 * If set, loops every {@value #REL_INTERVAL_LOOP_MESSAGE} units.
	 * @param loopMessage Whether or not to loop the message and replay it infinitely. 
	 */
	@Override
	public void setLoop(boolean loopMessage) {
		this.loopMessage = loopMessage;
	}
	
	@Override
	public boolean isRunning() {
		return messageIsSending || continueMessageProcessing;
	}
	
	@Override 
	public boolean isMessageLooped() {
		return loopMessage;
	}
	@Override
	public void setPadTime(int padTime) {
		if (padTime < 0){
			throw new IllegalArgumentException("Pad time cannot be < 0");
		}
		this.messagePadTime = padTime;		
	}
	@Override
	public void setUnitTime(int unitTime) {
		if (unitTime < 1){
			throw new IllegalArgumentException("Unit time cannot be <=0");
		}
		this.unitTime = unitTime;
		morseDotInterval = unitTime;
		morseSymbolInterval = unitTime;
		morseDashInterval = unitTime * REL_INTERVAL_DASH;
		morseCharInterval = unitTime * REL_INTERVAL_CHARACTER;		
		morseWordInterval = unitTime * REL_INTERVAL_WORD;
		morseLoopMessageInterval = unitTime * REL_INTERVAL_LOOP_MESSAGE;
	}
	@Override 
	public int getUnitTime() {
		return unitTime;
	}
	
	/////////////////////////////////////////////////////////////////////////////////////////////////
	/// End mutators + accessors
	////////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean start (OnTranscriptionListener onMorseListener){
		setOnMorseListener(onMorseListener);
		return start();
	}
	
	@Override
	public boolean start(){
		if (messageList.length == 0 ){
			//prevent blank messages
			return false;
		}
		//start thread. safety to prevent multiple starts.		
		try {
			//throws exception if already started.
			new Thread(morseParser).start();
		} catch (Exception e){
			return false;
		}
		return true;
	}
	
	@Override
	public boolean cancel(){
		boolean isCancelSuccess = false;
		if (continueMessageProcessing){
			continueMessageProcessing = false;			
			isCancelSuccess = true;
		}	
		if (messageIsSending){
			messageIsSending = false;
			synchronized (signalTimer) {				
				signalTimer.cancel();
				signalTimer.purge();
				signalTimer = new Timer();
			}
			isCancelSuccess = true;
		}
		return isCancelSuccess;
	}
		
	
	/////////////////////////////////////////////////////////////////////////////////////////////////
	/// Helper functions
	////////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Converts a signal word into a morse message as best as possible.
	 * @param word The word to explode and translate to morse.
	 * @return The word converted to Morse code.
	 */
	private List<MorseCharacter> wordToMorse(String word){
		List<MorseCharacter> charList = 
				new ArrayList<MorseCharacter>();
		char[] letters = word.toCharArray();
		final int SIZE = letters.length;
		for (int index = 0; index < SIZE; index++) {
			MorseCharacter mchar = 
					dictionary.encodeChar(letters[index]);
			if (mchar != null){
				charList.add(mchar);
			}
		}		
		return charList;
	}
	/** Parses a morse word and schedules the times to state/end dots+dashes.
	 * @param start The starting time for the word.
	 * @param morseWord The word to decode into characters and dots+dashes.
	 */
	private void parseMorseWord(Date start, List<MorseCharacter> morseWord){
		final int SIZE = morseWord.size();
		for (int index = 0; index < SIZE; index++) {
			parseMorseCharacter(start, morseWord.get(index));
			//if there are more letters in the word.
			if(index + 1 < SIZE){
				//set's the next character's start time as 1 unit from the last.
				offsetTime(start, morseCharInterval * (index+1));
			}
		}
	}
	
	/**
	 * Parses morse character and schedules the times to start/end dots+dashes. 
	 * @param start The start time for the character
	 * @param morseCharacter The character to decode into dots+dashes.
	 */
	private void parseMorseCharacter(Date start, MorseCharacter morseCharacter) {
		final List<Integer> pattern = morseCharacter.getPattern();
		final int SIZE = pattern.size();
		for(int index = 0; index < SIZE; index++){
		//for (Integer symbol : pattern) {
			if (!continueMessageProcessing){ //if we are not to continue
				break;
			}
			Integer symbol = pattern.get(index);
			
			MorseSignalTask startSignal = new MorseSignalTask(true, m_SignalListener);
			MorseSignalTask endSignal = new MorseSignalTask(false, m_SignalListener);
			Date end = null;
			
			if (symbol == MorseDictionary.DOT){
				end = new Date(start.getTime() + morseDotInterval);
			} else if (symbol == MorseDictionary.DASH){
				end = new Date(start.getTime() + morseDashInterval);
			} else {
				continue;
			}				
			signalTimer.schedule(startSignal, start);
			signalTimer.schedule(endSignal, end);
			
			start.setTime(end.getTime());
			if(index + 1 < SIZE){
				offsetTime(start, morseSymbolInterval * (index+1));
			}
		}
	}
	
	
	/////////////////////////////////////////////////////////////////////////////////////////////////
	/// Utility functions
	////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Convenience function.
	 * Adds the time to {@link Date#getTime()} and resets the date's time.
	 * @param date The time to offset (Reference)
	 * @param time The time in milliseconds to add.
	 */
	static private void offsetTime(Date date, long time) {
		date.setTime(date.getTime() + time);
	}
	
	/////////////////////////////////////////////////////////////////////////////////////////////////
	/// Internal classes
	////////////////////////////////////////////////////////////////////////////////////////////////
	
	
	/**
	 * Denotes either the beginning or end of a morse signal unit (Dot/Dash).
	 * @version 0.1.0-20140512
	 */
	static private class MorseSignalTask extends TimerTask {		
		protected boolean isOnSignal = false;
		protected OnSignalListener onSignalListener = null;
		public MorseSignalTask(boolean on, OnSignalListener onSignalListener) {
			this.isOnSignal = on;
			this.onSignalListener = onSignalListener;
		}
		@Override
		public void run() {
			if (isOnSignal){
				onSignalListener.onSignalStart();
			} else {
				onSignalListener.onSignalEnd();
			}
		}
	}
		
	/////////////////////////////////////////////////////////////////////////////////////////////////
	/// Internal runnable
	////////////////////////////////////////////////////////////////////////////////////////////////
	
	/** Defines how to parse the message into morse. 
	 * @throws IllegalThreadStateException If attempted to run twice. */
	private Runnable morseParser = new Runnable() {
		@Override
		public void run() {
			//if running, bad state.
			if (continueMessageProcessing){
				throw new IllegalThreadStateException(
						"Cannot start already running task");
			}
			continueMessageProcessing = true;
			final List<ArrayList<MorseCharacter>> message = 
					new ArrayList<ArrayList<MorseCharacter>>();		
			
			synchronized (messageList) {
				final int SIZE  = messageList.length;				
				for (int index = 0; index < SIZE && continueMessageProcessing; index++) {
					message.add(
								(ArrayList<MorseCharacter>) 
								wordToMorse(messageList[index])
							);
				}
			}
			if (m_MorseListener != null){
				m_MorseListener.onMorseParsed();
			}
			
			
			Date delay = new Date();
			offsetTime(delay, messagePadTime);
			signalTimer.schedule(new TimerTask() {						
				@Override
				public void run() { sendMorseMessage(message);}
			}, delay);					
		}
		/**
		 * Sends the morse message.
		 * @param message The message to send via parseMorseWord.
		 */
		private void sendMorseMessage(final List<ArrayList<MorseCharacter>> message) {
			synchronized (signalTimer) {
				messageIsSending = true;
				if (!continueMessageProcessing){
					//we have finished
					messageIsSending = false;
					
					if (m_MorseListener != null){
						m_MorseListener.onMorseCompleted();
					}
					return;
				}
				
				final int SIZE2 = message.size();
				Date start = new Date();				
				for (int 	index = 0; 
							index < SIZE2 && continueMessageProcessing; 
							index++) {
					parseMorseWord(start, message.get(index));
					//if not the last word of the message.
					if(index + 1 < SIZE2){
						//offset by a word interval
						offsetTime(start, morseWordInterval * (index+1));
					}
				}				
				
				if (loopMessage){
					//keep looping if set true.
					offsetTime(start, morseLoopMessageInterval);					
				} else {
					//if not looping, no more processing.
					continueMessageProcessing = false;	
					offsetTime(start, messagePadTime);
				}
				signalTimer.schedule(new TimerTask() {						
					@Override
					public void run() { sendMorseMessage(message);}
				}, start);
			}
		}
	};
	
	/////////////////////////////////////////////////////////////////////////////////////////////////
	/// Internal Listener interfaces
	////////////////////////////////////////////////////////////////////////////////////////////////
	
	//moved to IMorseTranscriber	
}
