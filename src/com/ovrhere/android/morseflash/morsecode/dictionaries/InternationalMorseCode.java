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

import java.util.HashMap;
import java.util.Map.Entry;
/**
 * Dictionary of International Morse Code. 
 * This dictionary is partial compliant with 
 * <a href="http://www.itu.int/rec/R-REC-M.1677-1-200910-I/">official standards</a>.
 * 
 * @author Jason J.
 * @version 0.2.1-20140610
 */
public class InternationalMorseCode implements MorseDictionary {
	/** The list of readable characters and their morse code equivalent. */
	static private HashMap<Character, MorseCharacter> dictionary = 
			new  HashMap<Character, MorseCharacter>(
				);
	
	static {
		dictionary.put(	
				'a',
				new MorseCharacter(new Integer[]{DOT, DASH})
				);
		dictionary.put(	
				'b',
				new MorseCharacter(new Integer[]{DASH, DOT, DOT, DOT})
				);
		dictionary.put(	
				'c',
				new MorseCharacter(new Integer[]{DASH, DOT, DASH, DOT})
				);
		dictionary.put(	
				'd',
				new MorseCharacter(new Integer[]{DASH, DOT, DOT})
				);
		dictionary.put(	
				'e',
				new MorseCharacter(new Integer[]{DOT})
				);
		dictionary.put(	
				'f',
				new MorseCharacter(new Integer[]{DOT, DOT, DASH, DOT})
				);
		dictionary.put(	
				'g',
				new MorseCharacter(new Integer[]{DASH, DASH, DOT})
				);
		dictionary.put(	
				'h',
				new MorseCharacter(new Integer[]{DOT, DOT, DOT, DOT})
				);
		dictionary.put(	
				'i',
				new MorseCharacter(new Integer[]{DOT, DOT})
				);
		dictionary.put(	
				'j',
				new MorseCharacter(new Integer[]{DOT, DASH, DASH, DASH})
				);
		//10
		
		dictionary.put(	
				'k',
				new MorseCharacter(new Integer[]{DASH, DOT, DASH})
				);
		dictionary.put(	
				'l',
				new MorseCharacter(new Integer[]{DOT, DASH, DOT, DOT})
				);
		dictionary.put(	
				'm',
				new MorseCharacter(new Integer[]{DASH, DASH})
				);
		dictionary.put(	
				'n',
				new MorseCharacter(new Integer[]{DASH, DOT})
				);
		dictionary.put(	
				'o',
				new MorseCharacter(new Integer[]{DASH, DASH, DASH})
				);		
		dictionary.put(	
				'p',
				new MorseCharacter(new Integer[]{DOT, DASH, DASH, DOT})
				);
		dictionary.put(	
				'q',
				new MorseCharacter(new Integer[]{DASH, DASH, DOT, DASH})
				);
		dictionary.put(	
				'r',
				new MorseCharacter(new Integer[]{DOT, DASH, DOT})
				);
		dictionary.put(	
				's',
				new MorseCharacter(new Integer[]{DOT, DOT, DOT})
				);
		dictionary.put(	
				't',
				new MorseCharacter(new Integer[]{DASH})
				);
		//20
		
		dictionary.put(	
				'u',
				new MorseCharacter(new Integer[]{DOT, DOT, DASH})
				);
		dictionary.put(	
				'v',
				new MorseCharacter(new Integer[]{DOT, DOT, DOT, DASH})
				);
		dictionary.put(	
				'w',
				new MorseCharacter(new Integer[]{DOT, DASH, DASH})
				);
		dictionary.put(	
				'x',
				new MorseCharacter(new Integer[]{DASH, DOT, DOT, DASH})
				);
		dictionary.put(	
				'y',
				new MorseCharacter(new Integer[]{DASH, DOT, DASH, DASH})
				);
		dictionary.put(	
				'z',
				new MorseCharacter(new Integer[]{DASH, DASH, DOT, DOT})
				);
		//26
		
		//numbers
		dictionary.put(	
				'0',
				new MorseCharacter(new Integer[]{DASH, DASH, DASH, DASH, DASH})
				);
		dictionary.put(	
				'1',
				new MorseCharacter(new Integer[]{DOT, DASH, DASH, DASH, DASH})
				);
		dictionary.put(	
				'2',
				new MorseCharacter(new Integer[]{DOT, DOT, DASH, DASH, DASH})
				);
		dictionary.put(	
				'3',
				new MorseCharacter(new Integer[]{DOT, DOT, DOT, DASH, DASH})
				);
		dictionary.put(	
				'4',
				new MorseCharacter(new Integer[]{DOT, DOT, DOT, DOT, DASH})
				);
		dictionary.put(	
				'5',
				new MorseCharacter(new Integer[]{DOT, DOT, DOT, DOT, DOT})
				);
		dictionary.put(	
				'6',
				new MorseCharacter(new Integer[]{DASH, DOT, DOT, DOT, DOT})
				);
		dictionary.put(	
				'7',
				new MorseCharacter(new Integer[]{DASH, DASH, DOT, DOT, DOT})
				);
		dictionary.put(	
				'8',
				new MorseCharacter(new Integer[]{DASH, DASH, DASH, DOT, DOT})
				);
		dictionary.put(	
				'9',
				new MorseCharacter(new Integer[]{DASH, DASH, DASH, DASH, DOT})
				);
		
		//basic punctuation
		dictionary.put(	
				'.',
				new MorseCharacter(new Integer[]{DOT, DASH, DOT, DASH, DOT, DASH})
				);
		dictionary.put(	
				'?',
				new MorseCharacter(new Integer[]{DOT, DOT, DASH, DASH, DOT, DOT})
				);
		dictionary.put(	
				'!',
				new MorseCharacter(new Integer[]{DASH, DOT, DASH, DOT, DASH, DASH})
				);
		dictionary.put(	
				'(',
				new MorseCharacter(new Integer[]{DASH, DOT, DASH, DASH, DOT})
				);
		dictionary.put(	
				')',
				new MorseCharacter(new Integer[]{DASH, DOT, DASH, DASH, DOT, DASH})
				);
		dictionary.put(	
				':',
				new MorseCharacter(new Integer[]{DASH, DASH, DASH, DOT, DOT, DOT})
				);
		dictionary.put(	
				'=',
				new MorseCharacter(new Integer[]{DASH, DOT, DOT, DOT, DASH})
				);
		dictionary.put(	
				'-',
				new MorseCharacter(new Integer[]{DASH, DOT, DOT, DOT, DOT, DASH})
				);
		dictionary.put(	
				'"',
				new MorseCharacter(new Integer[]{DOT, DASH, DOT, DOT, DASH, DOT})
				);
		dictionary.put(	
				',',
				new MorseCharacter(new Integer[]{DASH, DASH, DOT, DOT, DASH, DASH})
				);
		dictionary.put(	
				'\'',
				new MorseCharacter(new Integer[]{DOT, DASH, DASH, DASH, DASH, DOT})
				);
		dictionary.put(	
				'/',
				new MorseCharacter(new Integer[]{DASH, DOT, DOT, DASH, DOT})
				);
		dictionary.put(	
				';',
				new MorseCharacter(new Integer[]{DASH, DOT, DASH, DOT, DASH, DOT})
				);
		dictionary.put(	
				'_',
				new MorseCharacter(new Integer[]{DOT, DOT, DASH, DASH, DOT, DASH})
				);
		dictionary.put(	
				Character.toLowerCase('@'),
				new MorseCharacter(new Integer[]{DOT, DASH, DASH, DOT, DASH, DOT})
				);
		
	}
	
	@Override
	public Character decodeChar(MorseCharacter mcharacter) {
		if (dictionary.containsValue(mcharacter)){
			for (Entry<Character, MorseCharacter> entry : dictionary.entrySet()) {
				if (entry.getValue().equals(mcharacter)){
					return entry.getKey();
				}
			}
		}
		return null;
	}

	@Override
	public MorseCharacter encodeChar(Character character) {
		if (dictionary.containsKey(character)){
			return dictionary.get(Character.toLowerCase(character));
		}
		return null;
	}

}
