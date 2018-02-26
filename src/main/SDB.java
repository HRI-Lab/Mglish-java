package main;

import java.util.HashMap;
import java.util.Map;

// S-DB
// (단어 W에 대한 i번째 화자의 이전 평균 발음시간 D_i(W), 이전 출현 횟수 U_i(W))
public class SDB {
	public int spearker = 0;
	Map<String, Word> wordMap = new HashMap<String, Word>();
	
	private class Word {
		// D
		public double time = 0.0;
		// U
		public int count = 0;
	}
	
	// 해당 단어의 발음시간을 반환한다.
	public double getTimeOfWord(String word) {
		Word result = this.wordMap.get(word);
		return result.time;
	}
	
	// 해당 단어가 있는지 여부를 반환
	public boolean isWordExist(String word) {
		return wordMap.containsKey(word);
	}
	
	// 새로운 단어를 생성하여 추가하는 함수
	public void appendNewWord(String word, double time) {
		Word newWord = new Word();
		newWord.count = 1;
		newWord.time = time;
		
		wordMap.put(word, newWord);
	}
	
	// 기존 단어를 찾아 수정하는 함수
	public void appendExistWord(String word, double time) {
		Word oldWord = wordMap.get(word);
		oldWord.count = oldWord.count + 1;
		
		// U
		int u = oldWord.count; 
		// D
		double d = oldWord.time; 
		
		// D <- D * (U-1) / U + time / U
		oldWord.time = d * d * (u - 1) / u + time / u ;
	}
}
