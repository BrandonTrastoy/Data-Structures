package lse;

import java.io.*;
import java.util.*;

/**
 * This class builds an index of keywords. Each keyword maps to a set of pages in
 * which it occurs, with frequency of occurrence in each page.
 *
 */
public class LittleSearchEngine {
	
	public static void main(String[] args) {
		
		LittleSearchEngine run = new LittleSearchEngine();
		
		 try {
			run.makeIndex("docs.txt", "noisewords.txt");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		run.top5search("going", "volume");
		
	}
	
	/**
	 * This is a hash table of all keywords. The key is the actual keyword, and the associated value is
	 * an array list of all occurrences of the keyword in documents. The array list is maintained in 
	 * DESCENDING order of frequencies.
	 */
	HashMap<String,ArrayList<Occurrence>> keywordsIndex;
	
	/**
	 * The hash set of all noise words.
	 */
	HashSet<String> noiseWords;
	
	/**
	 * Creates the keyWordsIndex and noiseWords hash tables.
	 */
	public LittleSearchEngine() {
		keywordsIndex = new HashMap<String,ArrayList<Occurrence>>(1000,2.0f);
		noiseWords = new HashSet<String>(100,2.0f);
	}
	
	/**
	 * Scans a document, and loads all keywords found into a hash table of keyword occurrences
	 * in the document. Uses the getKeyWord method to separate keywords from other words.
	 * 
	 * @param docFile Name of the document file to be scanned and loaded
	 * @return Hash table of keywords in the given document, each associated with an Occurrence object
	 * @throws FileNotFoundException If the document file is not found on disk
	 */
	public HashMap<String,Occurrence> loadKeywordsFromDocument(String docFile) 
	throws FileNotFoundException {
		
		if (docFile == null) throw new FileNotFoundException();
		
		HashMap<String, Occurrence> listKeyWords = new HashMap<String, Occurrence>();

		Scanner file = new Scanner(new File(docFile));

		while (file.hasNext()) {
			String word = getKeyword(file.next());
			
			if (word != null) {
				
				if (listKeyWords.containsKey(word)) {
					Occurrence occurrence = listKeyWords.get(word);
					occurrence.frequency++;
				}
				
				else {
					Occurrence occurrence = new Occurrence(docFile, 1);
					listKeyWords.put(word, occurrence);
				}
			}
		}
		
		file.close();
		
		return listKeyWords;
	}
	
	/**
	 * Merges the keywords for a single document into the master keywordsIndex
	 * hash table. For each keyword, its Occurrence in the current document
	 * must be inserted in the correct place (according to descending order of
	 * frequency) in the same keyword's Occurrence list in the master hash table. 
	 * This is done by calling the insertLastOccurrence method.
	 * 
	 * @param kws Keywords hash table for a document
	 */
	public void mergeKeywords(HashMap<String,Occurrence> kws) {
		
		for (String word : kws.keySet()) {
			
			ArrayList<Occurrence> list = new ArrayList<Occurrence>();

			if (keywordsIndex.containsKey(word)) list = keywordsIndex.get(word);
			
			list.add(kws.get(word));
			insertLastOccurrence(list);
			keywordsIndex.put(word, list);
		}
	}
	
	/**
	 * Given a word, returns it as a keyword if it passes the keyword test,
	 * otherwise returns null. A keyword is any word that, after being stripped of any
	 * trailing punctuation, consists only of alphabetic letters, and is not
	 * a noise word. All words are treated in a case-INsensitive manner.
	 * 
	 * Punctuation characters are the following: '.', ',', '?', ':', ';' and '!'
	 * 
	 * @param word Candidate word
	 * @return Keyword (word without trailing punctuation, LOWER CASE)
	 */
	public String getKeyword(String word) {
		
		if(word.equals("") || word == null || hasImbededSymbol(word)) return null;
		
		word = (removeTail(word)).toLowerCase();
		
		if (noiseWords.contains(word)) return null;
		
		return word;
	}
	
	private boolean hasImbededSymbol(String word) { // This is checking for random symbols in the word not at the end
		
		boolean letterAfter = false;
		
		for (int i = 0; i < word.length(); i++) {
			
			char current = word.charAt(i);
			
			if(!Character.isLetter(current) && i == 0) {
				return true;
			}
			
			if(!Character.isLetter(current)) {
				letterAfter = true;
			}
			
			if(letterAfter && Character.isLetter(current)) {
				return true; // This is for cases where there are multiple punctuations
			}
		}
		
		return false;
	}
	
	private String removeTail(String word) { // Returns a string without the end
		
		for(int i = 0; i < word.length(); i++) {
			
			if(!Character.isLetter(word.charAt(i))) {
				return word.substring(0, i);
			}
		}
		
		return word;
	}
	
	/**
	 * Inserts the last occurrence in the parameter list in the correct position in the
	 * list, based on ordering occurrences on descending frequencies. The elements
	 * 0..n-2 in the list are already in the correct order. Insertion is done by
	 * first finding the correct spot using binary search, then inserting at that spot.
	 * 
	 * @param occs List of Occurrences
	 * @return Sequence of mid point indexes in the input list checked by the binary search process,
	 *         null if the size of the input list is 1. This returned array list is only used to test
	 *         your code - it is not used elsewhere in the program.
	 */
	public ArrayList<Integer> insertLastOccurrence(ArrayList<Occurrence> occs) {
		
		if (occs.size() == 1) return null;

		int lo = 0, mid = 0, hi = occs.size()-2, target = occs.get(occs.size()-1).frequency, data = 0;
		ArrayList<Integer> midpointIndexes = new ArrayList<>();
		

		while (hi >= lo) { // Binary Search on arraylist
			
			mid = ((lo + hi) / 2);
			data = occs.get(mid).frequency;
			midpointIndexes.add(mid);

			if (data == target) break; // Found

			else if (data < target) { // Left
				hi = mid - 1;
			}

			else if (data > target) { // Right
				lo = mid + 1;
				if (hi <= mid)
					mid = mid + 1;
			}
		}
		
		midpointIndexes.add(mid);

		Occurrence temp = occs.remove(occs.size()-1);
		occs.add(midpointIndexes.get(midpointIndexes.size()-1), temp);

		return midpointIndexes;
	}
	
	/**
	 * This method indexes all keywords found in all the input documents. When this
	 * method is done, the keywordsIndex hash table will be filled with all keywords,
	 * each of which is associated with an array list of Occurrence objects, arranged
	 * in decreasing frequencies of occurrence.
	 * 
	 * @param docsFile Name of file that has a list of all the document file names, one name per line
	 * @param noiseWordsFile Name of file that has a list of noise words, one noise word per line
	 * @throws FileNotFoundException If there is a problem locating any of the input files on disk
	 */
	public void makeIndex(String docsFile, String noiseWordsFile) 
	throws FileNotFoundException {
		// load noise words to hash table
		Scanner sc = new Scanner(new File(noiseWordsFile));
		while (sc.hasNext()) {
			String word = sc.next();
			noiseWords.add(word);
		}
		
		// index all keywords
		sc = new Scanner(new File(docsFile));
		while (sc.hasNext()) {
			String docFile = sc.next();
			HashMap<String,Occurrence> kws = loadKeywordsFromDocument(docFile);
			mergeKeywords(kws);
		}
		sc.close();
	}
	
	/**
	 * Search result for "kw1 or kw2". A document is in the result set if kw1 or kw2 occurs in that
	 * document. Result set is arranged in descending order of document frequencies. (Note that a
	 * matching document will only appear once in the result.) Ties in frequency values are broken
	 * in favor of the first keyword. (That is, if kw1 is in doc1 with frequency f1, and kw2 is in doc2
	 * also with the same frequency f1, then doc1 will take precedence over doc2 in the result. 
	 * The result set is limited to 5 entries. If there are no matches at all, result is null.
	 * 
	 * @param kw1 First keyword
	 * @param kw1 Second keyword
	 * @return List of documents in which either kw1 or kw2 occurs, arranged in descending order of
	 *         frequencies. The result size is limited to 5 documents. If there are no matches, returns null.
	 */
	public ArrayList<String> top5search(String kw1, String kw2) {
		
		ArrayList<String> top5 = new ArrayList<String>();
		
		ArrayList<Occurrence> 
			kw1List = new ArrayList<Occurrence>(), 
			kw2List = new ArrayList<Occurrence>(),
			wordMatch = new ArrayList<Occurrence>();
		
		if (keywordsIndex.containsKey(kw1)) kw1List = keywordsIndex.get(kw1); // Retrieve kw1
		
		if (keywordsIndex.containsKey(kw2)) kw2List = keywordsIndex.get(kw2); // Retrieve kw2
		
		wordMatch.addAll(kw1List); // Add kw1 to list
		wordMatch.addAll(kw2List); // Add kw2 to list
		
		if (!(kw1List.isEmpty()) && !(kw2List.isEmpty())) {
			
			bubbleSort(wordMatch); // Sorts array list by reference
			removeDups(wordMatch); // Removes duplicate occurrences
		}

		while (wordMatch.size() > 5) wordMatch.remove(wordMatch.size()-1);
		
		for (Occurrence oc : wordMatch) top5.add(oc.document);

		return top5;
	}
	
	private void bubbleSort(ArrayList<Occurrence> wordMatch) { // sorting algorithm O(n^2)
		
		int size = wordMatch.size();
		
		for (int j = 0; j < (size - 1); j++) {
			
			for (int k = 1; k < (size - j); k++) {
				
				if (wordMatch.get(k-1).frequency < wordMatch.get(k).frequency) {
					
					Occurrence temp = wordMatch.get(k-1);
					wordMatch.set(k-1, wordMatch.get(k));
					wordMatch.set(k, temp);
				}
			}
		}
	}

	private void removeDups(ArrayList<Occurrence> wordMatch) { // removes duplicates O(n^2)
		
		for (int i = 0; i < wordMatch.size()-1; i++) {
			
			for (int y = i + 1; y < wordMatch.size(); y++) {
				
				if (wordMatch.get(i).document == wordMatch.get(y).document) wordMatch.remove(y);
			}
		}
	}

}
