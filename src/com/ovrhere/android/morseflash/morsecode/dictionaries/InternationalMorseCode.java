package com.ovrhere.android.morseflash.morsecode.dictionaries;

import java.util.HashMap;
import java.util.Map.Entry;
/**
 * Dictionary of International Morse Code. 
 * This dictionary is partial compliant with 
 * <a href="http://www.itu.int/rec/R-REC-M.1677-1-200910-I/">official standards</a>.
 * 
 * @author Jason J.
 * @version 0.1.0-20140513
 */
public class InternationalMorseCode implements MorseDictionary {
	/** The list of readable characters and their morse code equivalent. */
	static private HashMap<Character, MorseCharacter> dictionary = 
			new  HashMap<Character, MorseCharacter>(
				);
	
	static {
		dictionary.put(	
				'a',
				new MorseCharacter(new Boolean[]{DOT, DASH})
				);
		dictionary.put(	
				'b',
				new MorseCharacter(new Boolean[]{DASH, DOT, DOT, DOT})
				);
		dictionary.put(	
				'c',
				new MorseCharacter(new Boolean[]{DASH, DOT, DASH, DOT})
				);
		dictionary.put(	
				'd',
				new MorseCharacter(new Boolean[]{DASH, DOT, DOT})
				);
		dictionary.put(	
				'e',
				new MorseCharacter(new Boolean[]{DOT})
				);
		dictionary.put(	
				'f',
				new MorseCharacter(new Boolean[]{DOT, DOT, DASH, DOT})
				);
		dictionary.put(	
				'g',
				new MorseCharacter(new Boolean[]{DASH, DASH, DOT})
				);
		dictionary.put(	
				'h',
				new MorseCharacter(new Boolean[]{DOT, DOT, DOT, DOT})
				);
		dictionary.put(	
				'i',
				new MorseCharacter(new Boolean[]{DOT, DOT})
				);
		dictionary.put(	
				'j',
				new MorseCharacter(new Boolean[]{DOT, DASH, DASH, DASH})
				);
		//10
		
		dictionary.put(	
				'k',
				new MorseCharacter(new Boolean[]{DOT, DASH, DOT})
				);
		dictionary.put(	
				'l',
				new MorseCharacter(new Boolean[]{DOT, DASH, DOT, DOT})
				);
		dictionary.put(	
				'm',
				new MorseCharacter(new Boolean[]{DASH, DASH})
				);
		dictionary.put(	
				'n',
				new MorseCharacter(new Boolean[]{DASH, DOT})
				);
		dictionary.put(	
				'o',
				new MorseCharacter(new Boolean[]{DASH, DASH, DASH})
				);		
		dictionary.put(	
				'p',
				new MorseCharacter(new Boolean[]{DOT, DASH, DASH, DOT})
				);
		dictionary.put(	
				'q',
				new MorseCharacter(new Boolean[]{DASH, DASH, DOT, DASH})
				);
		dictionary.put(	
				'r',
				new MorseCharacter(new Boolean[]{DOT, DASH, DOT})
				);
		dictionary.put(	
				's',
				new MorseCharacter(new Boolean[]{DOT, DOT, DOT})
				);
		dictionary.put(	
				't',
				new MorseCharacter(new Boolean[]{DASH})
				);
		//20
		
		dictionary.put(	
				'u',
				new MorseCharacter(new Boolean[]{DOT, DOT, DASH})
				);
		dictionary.put(	
				'v',
				new MorseCharacter(new Boolean[]{DOT, DOT, DOT, DASH})
				);
		dictionary.put(	
				'w',
				new MorseCharacter(new Boolean[]{DOT, DASH, DASH})
				);
		dictionary.put(	
				'x',
				new MorseCharacter(new Boolean[]{DASH, DOT, DOT, DASH})
				);
		dictionary.put(	
				'y',
				new MorseCharacter(new Boolean[]{DASH, DOT, DASH, DASH})
				);
		dictionary.put(	
				'z',
				new MorseCharacter(new Boolean[]{DASH, DASH, DOT, DOT})
				);
		//26
		
		//numbers
		dictionary.put(	
				'0',
				new MorseCharacter(new Boolean[]{DASH, DASH, DASH, DASH, DASH})
				);
		dictionary.put(	
				'1',
				new MorseCharacter(new Boolean[]{DOT, DASH, DASH, DASH, DASH})
				);
		dictionary.put(	
				'2',
				new MorseCharacter(new Boolean[]{DOT, DOT, DASH, DASH, DASH})
				);
		dictionary.put(	
				'3',
				new MorseCharacter(new Boolean[]{DOT, DOT, DOT, DASH, DASH})
				);
		dictionary.put(	
				'4',
				new MorseCharacter(new Boolean[]{DOT, DOT, DOT, DOT, DASH})
				);
		dictionary.put(	
				'5',
				new MorseCharacter(new Boolean[]{DOT, DOT, DOT, DOT, DOT})
				);
		dictionary.put(	
				'6',
				new MorseCharacter(new Boolean[]{DASH, DOT, DOT, DOT, DOT})
				);
		dictionary.put(	
				'7',
				new MorseCharacter(new Boolean[]{DASH, DASH, DOT, DOT, DOT})
				);
		dictionary.put(	
				'8',
				new MorseCharacter(new Boolean[]{DASH, DASH, DASH, DOT, DOT})
				);
		dictionary.put(	
				'9',
				new MorseCharacter(new Boolean[]{DASH, DASH, DASH, DASH, DOT})
				);
		
		//basic punctuation
		dictionary.put(	
				'.',
				new MorseCharacter(new Boolean[]{DOT, DASH, DOT, DASH, DOT, DASH})
				);
		dictionary.put(	
				'?',
				new MorseCharacter(new Boolean[]{DOT, DOT, DASH, DASH, DOT, DOT})
				);
		dictionary.put(	
				'!',
				new MorseCharacter(new Boolean[]{DASH, DOT, DASH, DOT, DASH, DASH})
				);
		dictionary.put(	
				'(',
				new MorseCharacter(new Boolean[]{DASH, DOT, DASH, DASH, DOT})
				);
		dictionary.put(	
				')',
				new MorseCharacter(new Boolean[]{DASH, DOT, DASH, DASH, DOT, DASH})
				);
		dictionary.put(	
				':',
				new MorseCharacter(new Boolean[]{DASH, DASH, DASH, DOT, DOT, DOT})
				);
		dictionary.put(	
				'=',
				new MorseCharacter(new Boolean[]{DASH, DOT, DOT, DOT, DASH})
				);
		dictionary.put(	
				'-',
				new MorseCharacter(new Boolean[]{DASH, DOT, DOT, DOT, DOT, DASH})
				);
		dictionary.put(	
				'"',
				new MorseCharacter(new Boolean[]{DOT, DASH, DOT, DOT, DASH, DOT})
				);
		dictionary.put(	
				',',
				new MorseCharacter(new Boolean[]{DASH, DASH, DOT, DOT, DASH, DASH})
				);
		dictionary.put(	
				'\'',
				new MorseCharacter(new Boolean[]{DOT, DASH, DASH, DASH, DASH, DOT})
				);
		dictionary.put(	
				'/',
				new MorseCharacter(new Boolean[]{DASH, DOT, DOT, DASH, DOT})
				);
		dictionary.put(	
				';',
				new MorseCharacter(new Boolean[]{DASH, DOT, DASH, DOT, DASH, DOT})
				);
		dictionary.put(	
				'_',
				new MorseCharacter(new Boolean[]{DOT, DOT, DASH, DASH, DOT, DASH})
				);
		dictionary.put(	
				'@',
				new MorseCharacter(new Boolean[]{DOT, DASH, DASH, DOT, DASH, DOT})
				);
		
	}
	
	@Override
	public Character decodeChar(MorseCharacter mcharacter) {
		if (dictionary.containsValue(mcharacter)){
			for (Entry<Character, MorseCharacter> entry : dictionary.entrySet()) {
				if (entry.getValue() == mcharacter){
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
