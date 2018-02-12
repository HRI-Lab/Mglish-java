package main;

import java.util.ArrayList;

// S-DB를 관리하는 클래스
public class SDBManager {
	public ArrayList<SDB> sdbArr = new ArrayList<SDB>();
	
	public void updateSDB(int speaker, String word, double time) {
		// 1.Speaker가 존재하는 경우
		if (isSpeakerExist(speaker)) {
			// 해당 S-DB
			SDB sdb = findBySpeaker(speaker);
			
			// 1.1 해당 단어 가 검색되지 않은 경우
			if (!sdb.isWordExist(word)) {
				sdb.appendNewWord(word, time);
			} else {
				// 1.2 해당 단어 가 검색된 경우
				sdb.appendExistWord(word, time);
			}
			
		} else {
			// 2. Speaker가 존재하지 않는 경우
			// S-DB에 새로운 list 에 추가
			SDB newSDB = new SDB();
			newSDB.spearker = speaker;
			newSDB.appendNewWord(word, time);
			
			sdbArr.add(newSDB);
		}
	}
	
	// i번째 화자가 해당 단어 word를 발음한 적 있는 경우가 있으면 true
	public boolean isExist(int speaker, String word) {
		// 해당 스피커가 존재하는 경우
		if (isSpeakerExist(speaker)) {
			// 해당 S-DB
			SDB sdb = findBySpeaker(speaker);
			
			// 해당 단어가 검색된 경우
			if (sdb.isWordExist(word))
				return true;
		}
		return false;
	}
	
	// i번째 화자가 해당 단어 word를 발음한 시간
	public double averageTimeOf(int speaker, String word) {
		// 해당 S-DB
		SDB sdb = findBySpeaker(speaker);
		
		return sdb.getTimeOfWord(word);
	}
	
	private SDB findBySpeaker(int speaker) {
		for (SDB sdb: sdbArr) {
			if (speaker == sdb.spearker)
				return sdb;
		}
		return new SDB();
	}
	
	private boolean isSpeakerExist(int speaker) {
		for (SDB sdb: sdbArr) {
			if (speaker == sdb.spearker)
				return true;
		}
		return false;
	}
}
