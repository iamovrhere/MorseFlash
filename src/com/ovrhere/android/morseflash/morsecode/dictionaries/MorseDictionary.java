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
package com.ovrhere.android.morseflash.morsecode.dictionaries;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Defines the basic functions necessary
 * to encode readable characters into a morse code character
 * and decode morse code characters to readable characters. 
 * @author Jason J. 
 * @version 0.2.0-20140513
 */
public interface MorseDictionary {
	/** Value representing a dot. */
	final static public int DOT = 1;
	/** Value representing a dash. */
	final static public int DASH = 3;
		
	
	/** Decodes a Morse code character into a readable character.
	 * @param mCharacter The Morse Code character to decode.
	 * @return The readable character equivalent or <code>null</code>
	 * if not found.
	 */
	public Character decodeChar(MorseCharacter mCharacter);
	/** Encodes a readable character into a Morse code character.
	 * @param character The readable character to encode. 
	 * @return The Morse code character equivalent or <code>null</code>
	 * if not found.
	 */
	public MorseCharacter encodeChar(Character character);
	
	/**
	 * <p>Container representing a single character in Morse code.
	 * Dashes are represented as <code>true</code>, dots as <code>false</code>.
	 * </p>
	 * <p>Includes the appropriate functions for comparisons. 
	 * Suggested usage is using the 
	 * {@link MorseCharacter.Builder} to create characters.  
	 * </p>
	 * @author Jason J.
	 * @version 0.2.0-20140610
	 */
	static public class MorseCharacter {
		private List<Integer> pattern = new ArrayList<Integer>();
		
		protected MorseCharacter() {}
		/** Creates pattern from array
		 * @param pattern The pattern to use for this character.
		 * Dashes are represented as {@link MorseDictionary#DASH}, 
		 * dots as {@link MorseDictionary#DOT}.
		 */
		protected MorseCharacter(Integer pattern[]){
			this.pattern = new ArrayList<Integer>(Arrays.asList(pattern));
		}
		
		/** Sets pattern.
		 * @param pattern The pattern to use for this character.
		 * Dashes are represented as {@link MorseDictionary#DASH}, 
		 * dots as {@link MorseDictionary#DOT}.
		 */
		protected void setPattern(List<Integer> pattern ){
			this.pattern = pattern;
		}
	
				
		/**  
		 * @return A list representing the pattern of the morse character.
		 * Dashes are represented as {@link MorseDictionary#DASH}, 
		 * dots as {@link MorseDictionary#DOT}.
		 */
		public List<Integer> getPattern(){
			return this.pattern;
		}
		
		/**
		 * @author Jason J.
		 * @version 0.3.0-20140610
		 */
		static public class Builder {
			private List<Integer> bpattern = new ArrayList<Integer>();
			/** Creates a {@link MorseCharacter} with the arguments supplied to  
			 * the function. Note that this uses string manipulation;
			 *  using {@link #addDash()} and {@link #addDot()} are more efficient in building.
			 * @param pattern A pattern of dashes('-' or '_') and dots ('.'). 
			 * Note that any other character will be ignored.
			 * @return {@link MorseCharacter}
			 */
			static public MorseCharacter create(String pattern){
				String[] symbols = pattern.split("");
				Builder builder = new Builder();
				for (String string : symbols) {
					if (string.equals("-") || string.equals("_")){
						builder.addDash();
					} else if (string.equals(".")) {
						builder.addDot();
					}
				}
				return builder.create();
			}
			/** Creates a {@link MorseCharacter} with the arguments supplied to this 
			 * builder. 
			 * @return {@link MorseCharacter} 
			 */
			public MorseCharacter create(){
				MorseCharacter morseChar = new MorseCharacter();
				morseChar.setPattern(bpattern);
				return morseChar;
			}
			/** Adds a dot to the morse code pattern. 	
			 * @return MorseCharBuilder for chaining.		 */
			public Builder addDot(){
				bpattern.add(DOT);
				return this;
			}
			/** Adds a dash to the morse code pattern. 	
			 * @return MorseCharBuilder for chaining.		 */
			public Builder addDash(){
				bpattern.add(DASH);
				return this;
			}
		}
		
		/////////////////////////////////////////////////////////////////////////////////////////////////
		/// The overridden functions for comparisons
		////////////////////////////////////////////////////////////////////////////////////////////////
		
		@Override
		public String toString() {
			StringBuilder sBuilder = new StringBuilder();
			final int SIZE = pattern.size();
			for (int index = 0; index < SIZE; index++) {
				if (pattern.get(index).equals(DOT)){
					sBuilder.append(".");
				} else if (pattern.get(index).equals(DASH)){
					sBuilder.append("-");
				}				
			}
			return this.getClass().getName() + 
					"('"+sBuilder.toString()+"')"; //the character as a morse string. 
		}
		
		@Override
		public boolean equals(Object obj) {
			if (obj == null) {
		        return false;
		    }
		    if (getClass() != obj.getClass()) {
		        return false;
		    }
			final MorseCharacter morseChar = (MorseCharacter) obj;
			final int SIZE = pattern.size();
			if (SIZE != morseChar.pattern.size()){
				return false;
			}
			for (int index = 0; index < SIZE; index++) {
				if (morseChar.pattern.get(index) != pattern.get(index)){
					return false;
				}
			}
			return true;
		}
		
		@Override
		public int hashCode() {
			return pattern.hashCode();
		}
	}	
}
