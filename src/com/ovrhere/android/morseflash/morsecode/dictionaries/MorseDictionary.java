package com.ovrhere.android.morseflash.morsecode.dictionaries;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Defines the basic functions necessary
 * to encode readable characters into a morse code character
 * and decode morse code characters to readable characters. 
 * @author Jason J. 
 * @version 0.1.0-20140512
 */
public interface MorseDictionary {
	/** Value representing a dot. */
	final static public boolean DOT = false;
	/** Value representing a dash. */
	final static public boolean DASH = true;
	
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
	 * @version 0.1.0-20140512
	 */
	static public class MorseCharacter {
		private List<Boolean> pattern = new ArrayList<Boolean>();
		
		protected MorseCharacter() {}
		/** Creates pattern from array
		 * @param pattern The pattern to use for this character.
		 * Dashes are represented as {@link MorseDictionary#DASH}, 
		 * dots as {@link MorseDictionary#DOT}.
		 */
		protected MorseCharacter(Boolean pattern[]){
			this.pattern = new ArrayList<Boolean>(Arrays.asList(pattern));
		}
		
		/** Sets pattern.
		 * @param pattern The pattern to use for this character.
		 * Dashes are represented as {@link MorseDictionary#DASH}, 
		 * dots as {@link MorseDictionary#DOT}.
		 */
		protected void setPattern(List<Boolean> pattern ){
			this.pattern = pattern;
		}
	
				
		/**  
		 * @return A list representing the pattern of the morse character.
		 * Dashes are represented as {@link MorseDictionary#DASH}, 
		 * dots as {@link MorseDictionary#DOT}.
		 */
		public List<Boolean> getPattern(){
			return this.pattern;
		}
		
		/**
		 * @author Jason J.
		 * @version 0.1.0-20140512
		 */
		static public class Builder {
			private List<Boolean> bpattern = new ArrayList<Boolean>();
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
