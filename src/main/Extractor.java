package main;

import java.util.ArrayList;
import java.util.List;

import com.ibm.watson.developer_cloud.speech_to_text.v1.model.SpeakerLabel;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.SpeechTimestamp;

public class Extractor {
	// S-DB를 관리하는 클래스
	private SDBManager sdbManager = new SDBManager();
	public ArrayList<TimeStamp> finalResult = new ArrayList<TimeStamp>();
	private TestCase testCase;
	
	int MAXOFFSET = 3;
	
	private class TimeStamp{
		public String word = "";
		public double startTime = 0.0;
		public double endTime = 0.0;
		public int speaker = 0;
		
		TimeStamp(String word, double startTime, double endTime, int speaker){
			this.word = word;
			this.startTime = startTime;
			this.endTime = endTime;
			this.speaker = speaker;
		}
	}
	
	Extractor(TestCase testCase) {
		this.testCase = testCase;
	}
	
	//	동영상 내 음성신호 에 대해 STT(Speech to Text)변환
	//    →정보화 된 음성인식 결과  찾기
	public void extract() {
		final String[] resultArr, transcriptArr;
		resultArr = getSTRFilter(testCase.resultTranscript()).toLowerCase().split(" ");
		final String transcript = getSTRFilter(testCase.transcript).toLowerCase();
		transcriptArr = transcript.split(" ");
		
		// System.out.println("tr : " + getSTRFilterNewLine(testCase.transcript));
		// System.out.println("result : " + getSTRFilter(testCase.resultTranscript()).toLowerCase());
		// System.out.println("transcript : " + transcript);
		
		int offset = 0, foreIndex = -1, foreTranscriptIndex = -1, wrongIndex = -1, i = 0;

		for (i = 0; i < transcriptArr.length; i++) {
			// 앞이나 뒤에 맞는 단어가 있을 경우를 대비하여 OFFSET을 사용한다.
			
			//System.out.println("transcript : " + transcriptArr[i]);
			//System.out.println("result : " + resultArr[i + offset]);
			if ((i + offset) < resultArr.length && !(transcriptArr[i].equals(resultArr[i + offset]))) {
				// Not Found
				boolean flag = false;
				// MAXOFFSET만큼 찾는다.
				for (int j = 1; (i + j + offset) < resultArr.length && j <= MAXOFFSET; j++) {
					if (transcriptArr[i].equals(resultArr[i + j + offset])) {
						offset += j;
						// Found
						flag = true;
						break;
					} else if ((i - j + offset) > 0 && transcriptArr[i].equals(resultArr[i - j + offset])) {
						offset -= j;
						// Found
						flag = true;
						break;
					}
				}
				
				// 맞는 단어를 찾았을 경우 해당 값 만큼 offset을 더한다.
				if (flag) {
					this.getResult(foreIndex, i + offset, foreTranscriptIndex, i);
					this.addResult(i + offset);
					foreIndex = i + offset;
					foreTranscriptIndex = i;
					wrongIndex = -1;
					continue;
				}
				
				wrongIndex = i;
				//System.out.println("Wrong Word : " + i + " : " + transcriptArr[i] + " ");
			} else {
				if (wrongIndex == -1) {
					foreIndex = i + offset;
					foreTranscriptIndex = i;
					this.addResult(i + offset);
					continue;
				}
				this.getResult(foreIndex, i + offset, foreTranscriptIndex, i);
				this.addResult(i + offset);
				foreIndex = i + offset;
				foreTranscriptIndex = i;
				wrongIndex = -1;
			}
		}
		
//		if (wrongIndex != -1) {
//			this.getResult(foreIndex, i + offset, foreTranscriptIndex, i);
//			this.addResult(i + offset);
//		}
	}
	
	private void appendResult(String word, double startTime, double endTime, int speaker) {
		TimeStamp t = new TimeStamp(word, startTime, endTime, speaker);
		
		// SDB를 Update한다.
		sdbManager.updateSDB(speaker, word, (endTime - startTime));
		
		finalResult.add(t);
	}
	
	private void addResult(int index) {
		if (index >= testCase.resultTimestamps().size())
			return;
		SpeechTimestamp t = testCase.resultTimestamps().get(index);
		
		appendResult(t.getWord(), t.getStartTime(), t.getEndTime(), getSpeaker(t));
	}
	
	private int getSpeaker(SpeechTimestamp t) {
		int speaker = -1;
		for (SpeakerLabel s: testCase.result.getSpeakerLabels()) {
			if (s.getFrom().equals(t.getStartTime()) && s.getTo().equals(t.getEndTime())) {
				speaker = s.getSpeaker();
				break;
			}
		}
		return speaker + 1;
	}
	
	private void algorithmN1(int startIndex, int endIndex, int startIndex2, int endIndex2) {
		final String[] transcriptArr = getSTRFilterNewLine(testCase.transcript).split(" ");
		final List<SpeechTimestamp> ts = testCase.resultTimestamps();
		double fStartTime = 0, fEndTime = 0;
		int speaker = 0;
		final String word = transcriptArr[startIndex2+1];

		int a = -1, b = -2;
		// 특정 음량(dB) T 이상의 신호가 시작 되는 지점을 찾을 수 없으므로 전 단어의 발음이 끝나는 시간(E_a)으로 정한다.
		// 전 단어가 없는 첫 단어일 경우 0 으로 정한다.
		if (startIndex != -1) {
			// S_b = E_a
			fStartTime = ts.get(startIndex).getEndTime();
			a = getSpeaker(ts.get(startIndex));
		}
		if (endIndex < ts.size()) {
			if (startIndex == -1)
				a = getSpeaker(ts.get(endIndex));
			b = getSpeaker(ts.get(endIndex));
		}
		
		// 양쪽 끝의 Speaker가 같은경우 해당 Speaker가 말한 것으로 본다. 
		if (a == b)
			speaker = a;
		
		fEndTime = ts.get(endIndex).getStartTime();
		appendResult(word, fStartTime, fEndTime, speaker);
	}
	
	// 2-2-1
	// 이면 4를 수행
	// 이면 5를 수행
	// 이면 6를 수행
	// startIndex와 endIndex : Watson에서 받은 결과 값의 배열 Index
	// startIndex2와 endIndex2 : 가지고 있는 자막 파일의 자막의 배열 Index
	// -> startIndex ~ endIndex 사이의 값들이 오인식된 단어 정보임을 뜻함.
	private void getResult(int startIndex, int endIndex, int startIndex2, int endIndex2) {
		// System.out.println("startIndex2 : " + startIndex2);
		// System.out.println("endIndex2 : " + endIndex2);
		// prevent exceptions
		startIndex2 += 1;
		if (startIndex > endIndex || startIndex2 > endIndex2)
			return;
		
		int n = endIndex2 - startIndex2 - 2;
		if (n == 1) {
			algorithmN1(startIndex, endIndex, startIndex2, endIndex2);
			return;
		}
		
		final String[] transcriptArr = getSTRFilterNewLine(testCase.transcript).split(" ");
		final List<SpeechTimestamp> ts = testCase.resultTimestamps();
		double fStartTime, fEndTime;

		// 특정 음량(dB) T 이상의 신호가 시작 되는 지점을 찾을 수 없으므로 전 단어의 발음이 끝나는 시간으로 정한다.
		if (startIndex != -1)
			fStartTime = ts.get(startIndex).getEndTime();
		else
			// 전 단어가 없는 첫 단어일 경우 0 으로 정한다.
			fStartTime = 0;
		
		fEndTime = ts.get(endIndex).getStartTime();
		
		double startTime = fStartTime;
		double endTime = fEndTime;
		
		int wordCount = 0;
		for (int i = startIndex2; i < endIndex2; i++) {
			wordCount += transcriptArr[i].length();
		}
		
		double averageTime = (fEndTime - fStartTime) / (wordCount);
		for (int i = startIndex2; i < endIndex2; i++) {
			String word = transcriptArr[i];
			int a = -1, b = -2;
			if (startIndex != -1)
				a = getSpeaker(ts.get(startIndex));
			if (endIndex < ts.size()) {
				if (startIndex == -1)
					a = getSpeaker(ts.get(endIndex));
				b = getSpeaker(ts.get(endIndex));
			}
			int speaker = 0;
			if (a == b)
				speaker = a;
			
			double time = 0.0;
			
			if (sdbManager.isExist(speaker, word))
				time = sdbManager.averageTimeOf(speaker, word);
			else
				time = averageTime;
			
			endTime = startTime + time;
			if (endTime > fEndTime)
				endTime = fEndTime;
			
			this.appendResult(word, startTime, endTime, speaker);
			
			startTime = endTime;
		}
	}
	
	private String getSTRFilter(String str){ 
		 // 특수문자 제거
		 String match = "[^\uAC00-\uD7A3xfe0-9a-zA-Z\\s]";
		 String rstr = str.replaceAll(match, "");
		 // 개행 제거
		 rstr = rstr.replaceAll("(\r\n|\r|\n|\n\r)", " ");
		 return rstr;
	}
	
	private String getSTRFilterNewLine(String str){ 
		 // 개행 제거
		 String rstr = str.replaceAll("(\r\n|\r|\n|\n\r)", " ");
		 return rstr;
	}

	public String timeStampResultString() {
		String str = "";
		for (TimeStamp t: this.finalResult) {
			str += "Speaker " + t.speaker + " : " + t.word;
			if (t.word.length() < 4)
				str += "\t";
			str += String.format("\t [%.2f ~ %.2f]", t.startTime, t.endTime) + "\n";
		}
		str += "\n";
		
		return str;
	}
	
	public void printTimeStampResult() {
		System.out.print(timeStampResultString());
	}
	
	public String conversationString() {
		String str = "";
		
		int beforeSpeaker = -1;
		for (TimeStamp t: this.finalResult) {
			if (t.speaker != beforeSpeaker) {
				str += "\n";
				beforeSpeaker = t.speaker;
				str += "Speaker " + t.speaker + " : ";
			}
			str += t.word + " ";
		}
		str += "\n";
		
		return str;
	}
	
	public void printConversation() {
		System.out.print(conversationString());
	}
	
	public void printTranscriptResult() {
		System.out.println("Complemented Results : ");
		for (TimeStamp t: this.finalResult) {
			System.out.print(t.word + " ");
		}
	}
}
