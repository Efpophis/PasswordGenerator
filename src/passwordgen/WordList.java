/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package passwordgen;

/**
 * Copyright 2011 wordlist
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

//package com.googlecode.wordlist;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
//import org.apache.commons.lang.NotImplementedException;

/**
 * 
 * @author Bharanidharan A J
 * @author Srivathsa S
 * 
 */
public class WordList implements Serializable {

	private static final long serialVersionUID = 8949126647512996259L;
	private final Object listLock = new Object();
	private final Object propertyLock = new Object();

	public enum Difficulty {
		EASY, MEDIUM, HARD
	};

	public enum SortOrder {
		ALPHABET, LENGTH
	};

	private String type;
	private Difficulty difficulty;
	private ArrayList<String> wordList;

	/**
	 * No-arg constructor which constructs a simple word list which can be later
	 * populated with attributes.
	 */
	public WordList() {
		wordList = new ArrayList<String>();
	}

	/**
	 * Creates a simple word list of a particular type
	 * 
	 * @param type
	 *            Type of the word list
	 */
	public WordList(String type) {
		this.type = type;
		wordList = new ArrayList<String>();
	}

	/**
	 * Creates a simple word list of a particular difficulty E.g., EASY, MEDIUM
	 * or HARD
	 * 
	 * @param difficulty
	 *            Difficulty of the word list
	 */
	public WordList(Difficulty difficulty) {
		this.setDifficulty(difficulty);
		wordList = new ArrayList<String>();
	}

	/**
	 * Creates a simple word list with a specified type and a difficulty
	 * 
	 * @param type
	 *            Type of the word list (e.g., animals, verbs...)
	 * @param difficulty
	 *            Difficulty of the word list (EASY, MEDIUM or HARD)
	 */
	public WordList(String type, Difficulty difficulty) {
		this.setType(type);
		this.setDifficulty(difficulty);
		wordList = new ArrayList<String>();
	}

	/**
	 * Imports a word list from a flat text file with one word in each line
	 * 
	 * @param filePath
	 *            Path of the file containing words
	 * @param clearList
	 *            Remove the existing words from the list or not
	 * @throws IOException
	 *             Thrown when either file is not found or there is an issue
	 *             with reading the file
	 */
	public void importFromTextFile(String filePath, boolean clearList)
			throws IOException {
		this.importFromSpecialFile(filePath, "\r\n", clearList);
	}

	/**
	 * Imports the word list from a file containing a list of words delimitted
	 * by a special character or a sequence
	 * 
	 * @param filePath
	 *            Path of the file containing words
	 * @param delimiter
	 *            The character or a sequence which delimits each word
	 * @param clearList
	 *            Remove the existing words from the list or not
	 * @throws IOException
	 *             Thrown when either file is not found or there is an issue
	 *             with reading the file
	 */
	public void importFromSpecialFile(String filePath, String delimiter,
			boolean clearList) throws IOException {
		if (clearList) {
			synchronized (listLock) {
				wordList.clear();
			}
		}
		FileInputStream fis = new FileInputStream(filePath);
		DataInputStream dis = new DataInputStream(fis);
		BufferedReader br = new BufferedReader(new InputStreamReader(dis));
		ArrayList<String> fileContent = new ArrayList<String>();
		String content = null;
		String[] list = null;
		while ((content = br.readLine()) != null) {
			fileContent.add(content);
		}
		for (String line : fileContent) {
			list = line.split(delimiter);
			for (String word : list) {
				this.add(word);
			}
		}
		br.close();
		dis.close();
		fis.close();
	}

        public void importFromInputStream( InputStream is, String delimiter,
			boolean clearList) throws IOException
        {
            if (clearList) {
			synchronized (listLock) {
				wordList.clear();
			}
		}
            DataInputStream dis = new DataInputStream(is);
		BufferedReader br = new BufferedReader(new InputStreamReader(dis));
		ArrayList<String> fileContent = new ArrayList<String>();
		String content = null;
		String[] list = null;
		while ((content = br.readLine()) != null) {
			fileContent.add(content);
		}
		for (String line : fileContent) {
			list = line.split(delimiter);
			for (String word : list) {
				this.add(word);
			}
		}
		br.close();
		dis.close();
        }
        
        public void importFromString( String addThese, boolean clearList )
        {
            if (clearList) {
			synchronized (listLock) {
				wordList.clear();
			}
		}
            
            String[] words = addThese.split(" ");
            
            for ( String word : words )
            {
                this.add(word);
            }
        }
        
	/**
	 * Export the word list to a text file containing one word per line
	 * 
	 * @param filePath
	 *            Path of the file to which the word list has to be exported
	 * @param append
	 *            Whether to append in an existing file
	 * @throws IOException
	 *             Thrown if file not found when append is true or when the file
	 *             cannot be created or when there is an issue during the write
	 *             operation
	 */
	public void exportToTextFile(String filePath, boolean append)
			throws IOException {
		this.exportToSpecialFile(filePath, "\r\n", append);
	}

	/**
	 * Export the word list to a special file containing the words delimited by
	 * a special character
	 * 
	 * @param filePath
	 *            Path of the special file
	 * @param delimiter
	 *            Delimiter separating each word
	 * @param append
	 *            Whether to append in an existing file
	 * @throws IOException
	 *             Thrown if file not found when append is true or when the file
	 *             cannot be created or when there is an issue during the write
	 *             operation
	 */
	public void exportToSpecialFile(String filePath, String delimiter,
			boolean append) throws IOException {
		File file = new File(filePath);
		if (!file.exists()) {
			if (append) {
				throw new FileNotFoundException();
			} else {
				file.createNewFile();
			}
		}
		WordList tempWL = new WordList();
		if (append) {
			tempWL.importFromSpecialFile(filePath, delimiter, true);
		}
		FileOutputStream fos = new FileOutputStream(filePath, false);
		DataOutputStream dos = new DataOutputStream(fos);
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(dos));
		for (String word : tempWL.getWordList()) {
			bw.write(word + delimiter);
		}
		synchronized (listLock) {
			for (String word : wordList) {
				bw.write(word + delimiter);
			}
		}
		bw.close();
		dos.close();
		fos.close();
	}

	/**
	 * Gets the size of the word list
	 * 
	 * @return Size of the word list
	 */
	public int size() {
		synchronized (listLock) {
			return wordList.size();
		}
	}

	/**
	 * Filter the words in the list by removing all the words which are not of
	 * specified word length
	 * 
	 * @param length
	 *            Length of the word
	 */
	public ArrayList<String> filterWordListByLength(int length) {
		ArrayList<String> words = this.getWordList();
		for (String word : this.wordList) {
			if (word.length() != length) {
				words.remove(word);
			}
		}
                
                return words;
	}

        public ArrayList<String> filterWordListByMinLength( int length )
        {
            ArrayList<String> words = this.getWordList();
            for (String word : this.wordList) 
            {
		if (word.length() < length)
                {
                    words.remove(word);
		}
            }
            return words;
        }
        
	/**
	 * Filter words in the list by removing all the words which does not fall
	 * under the specified pattern
	 * 
	 * @param pattern
	 *            Pattern of the words which should be retained
	 */
	public ArrayList<String> filterWordListByPattern(String regex) {
		ArrayList<String> words = this.getWordList();
		Pattern pattern = Pattern.compile(regex);
		for (String word : this.wordList) {
			Matcher matcher = pattern.matcher(word);
			if (!matcher.find()) {
				words.remove(word);
			}
		}
                return words;
	}

	/**
	 * Add a word to the list
	 * 
	 * @param word
	 *            Word to be added
	 */
	public void add(String word) {
		//word = word.toLowerCase();
		synchronized (listLock) {
			//if (!wordList.contains(word)) {
				wordList.add(word);
			//}
		}
	}

	/**
	 * Remove a word from the list
	 * 
	 * @param word
	 *            Word to be removed
	 */
	public void remove(String word) {
		word = word.toLowerCase();
		synchronized (listLock) {
			if (wordList.contains(word)) {
				wordList.remove(word);
			} else {
				throw new UnsupportedOperationException(
						"Word not found in the word list");
			}
		}
	}

	/**
	 * Returns the first word in the list which is of the specified length. If
	 * no such word is found, null is returned
	 * 
	 * @param length
	 *            Length of the word
	 * @return First word with the specified length
	 */
	public String first(int length) {
		ArrayList<String> words = this.getWordList();
                
                this.filterWordListByLength(length);
                
                String result = words.get(0);
                
                return result;
	}

	/**
	 * Returns the first word in the list which is of the given pattern. If no
	 * such word is found, null is returned
	 * 
	 * @param regex
	 *            Pattern which should be matched
	 * @return First word which satisfies the pattern
	 */
	public String first(String regex) {
		ArrayList<String> words = this.getWordList();
                
                this.filterWordListByPattern(regex);
                
                String result = words.get(0);
                
                return result;
	}

	/**
	 * Returns a random word in the list which is of the specified length
         * or greater. If no such word is found, null is returned
	 * 
	 * @param length
	 *            Length of the word
	 * @return Random word with the specified length
	 */
	public String random(int minLength) {
		ArrayList<String> words = this.getWordList();
                
                if ( minLength > 0 )
                {
                    this.filterWordListByMinLength(minLength);
                }
                String result = words.get(myRandom(words.size()));
                
                return result;
	}

        
        public static int myRandom( int upper )
        {
            int idx = (int) ( Math.random()*upper );

            return idx;
        }
	/**
	 * Returns a random word in the list which is of the given pattern. If no
	 * such word is found, null is returned
	 * 
	 * @param regex
	 *            Pattern which should be matched
	 * @return Random word which satisfies the pattern
	 */
	public String random(String regex) {
		ArrayList<String> words = this.getWordList();
                
                this.filterWordListByPattern(regex);
                
                String result = words.get(myRandom(words.size()));
                
                return result;
	}

	/**
	 * Sort the word list based on a particular order
	 * 
	 * @param order
	 *            The order in which the word list has to be sorted
	 * @param reverse
	 *            Whether the list should be sorted in a decreasing order or not
	 */
	public void sortWordList(SortOrder order, boolean reverse) {
		// TODO Sort the word list based on a particular order
		throw new UnsupportedOperationException();
	}

	/**
	 * Clears the word list of all its contents and attributes
	 */
	public void reset() {
		synchronized (listLock) {
			wordList.clear();
		}
		synchronized (propertyLock) {
			this.setDifficulty(null);
			this.setType(null);
		}
	}

	/**
	 * Sets the type of the word list (e.g, animals, verbs...)
	 * 
	 * @param type
	 *            Type of the word list
	 */
	public void setType(String type) {
		synchronized (propertyLock) {
			this.type = type;
		}
	}

	/**
	 * Gets the type of the word list (e.g., animals, verbs...)
	 * 
	 * @return Type of the word list
	 */
	public String getType() {
		synchronized (propertyLock) {
			return type;
		}
	}

	/**
	 * Sets the difficulty of the word list to either EASY, MEDIUM or HARD
	 * 
	 * @param difficulty
	 *            Difficulty of the word list
	 */
	public void setDifficulty(Difficulty difficulty) {
		synchronized (propertyLock) {
			this.difficulty = difficulty;
		}
	}

	/**
	 * Gets the difficulty of the word list
	 * 
	 * @return Difficulty of the word list
	 */
	public Difficulty getDifficulty() {
		synchronized (propertyLock) {
			return difficulty;
		}
	}

	/**
	 * Gets a copy of the raw word list array
	 * 
	 * @return Copy of the word list
	 */
	public ArrayList<String> getWordList() {
		ArrayList<String> newWordList = new ArrayList<String>();
		for (String word : wordList) {
			newWordList.add(word);
		}
		return newWordList;
	}
}