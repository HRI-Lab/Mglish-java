package main;

import java.util.ArrayList;
import java.util.List;

import com.ibm.watson.developer_cloud.http.HttpMediaType;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.RecognizeOptions;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.SpeechResults;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.SpeechTimestamp;

public class TestCase {
	
	class TimeStamp{
		public String word = "";
		public double startTime = 0.0;
		public double endTime = 0.0;
		
		TimeStamp(String word, double startTime, double endTime){
			this.word = word;
			this.startTime = startTime;
			this.endTime = endTime;
		}
	}
	
	public String resourcePath = "";
	public String transcript = "";
	public SpeechResults result;
	public RecognizeOptions options;
	public ArrayList<TimeStamp> finalResult = new ArrayList<TimeStamp>();
	
	public String resultTranscript() {
		return this.result.getResults().get(0).getAlternatives().get(0).getTranscript();
	}
		
	public boolean isCorrect() {
		String resultTranscript = this.resultTranscript();
		resultTranscript = trimRight(resultTranscript);
		
		//System.out.println(resultTranscript);
		//System.out.println(transcript);
		
		return resultTranscript.equals(transcript);
	}
	
	private void addResult(String word, double startTime, double endTime) {
		TimeStamp t = new TimeStamp(word, startTime, endTime);
		finalResult.add(t);
	}
	
	private void getResult(int index) {
		//System.out.println("getResult : " + index);
		SpeechTimestamp t = this.result.getResults().get(0).getAlternatives().get(0).getTimestamps().get(index);
		addResult(t.getWord(), t.getStartTime(), t.getEndTime());
	}
	
	private void getResult(int startIndex, int endIndex, int startIndex2, int endIndex2) {
		// 예외처리
		//System.out.println("getResult : " + startIndex + " " + endIndex + " " + startIndex2 + " " + endIndex2);
		startIndex2 += 1;
		if (startIndex > endIndex || startIndex2 > endIndex2)
			return;
		
		String[] transcriptArr = this.transcript.split(" ");
		List<SpeechTimestamp> ts = this.result.getResults().get(0).getAlternatives().get(0).getTimestamps();
		double fStartTime, fEndTime;
		
		// 첫 단어부터 틀릴 경우
		if (startIndex != -1)
			fStartTime = ts.get(startIndex).getEndTime();
		else
			fStartTime = 0;
		
		fEndTime = ts.get(endIndex).getStartTime();
		
		double startTime = fStartTime;
		double endTime = fEndTime;
		
		double averageTime = (fEndTime - fStartTime) / (endIndex2 - startIndex2);
		for (int i = startIndex2; i < endIndex2; i++) {
			String word = transcriptArr[i];
			// 화자별 단어별 평균 발음시간 계산
			double time = this.averageTimeOfWord(word);
			// 평균 발음시간이 없을 경우 평균시간으로 책정
			if (time == 0.0)
				time = averageTime;
			
			endTime = startTime + time;
			if (endTime > fEndTime)
				endTime = fEndTime;
			
			this.addResult(word, startTime, endTime);
			
			startTime = endTime;
		}
		
	}
		
	public ArrayList<Integer> findByResult() {
		// OFFSET의 범위
		final int MAXOFFSET = 5;
		String[] resultArr, transcriptArr;
		resultArr = this.resultTranscript().split(" ");
		transcriptArr = this.transcript.split(" ");
		ArrayList<Integer> wrongList = new ArrayList<Integer>();
		
		// Example
		// I want to send this Ranma do you have a box yeah I found this one to put the photo albums in five it's a bit small
		// dad I want to send this book to grandma do you have a box yeah I've got this one to put photo albums in but it's a bit small
		int offset = 0;
		//			  0   1    2   3
		// 인식 :	 	this Ranma do you
		// Answer :	this book to grandma do you
		int foreIndex = -1;
		int foreTranscriptIndex = -1;
		int wrongIndex = -1;

		for (int i = 0; i < resultArr.length; i++) {
			
			// 틀리면 : ex)Ranma
			if (!(resultArr[i].equals(transcriptArr[i + offset]))) {
				boolean flag = false;
				// 오차범위 MAX_OFFSET 이내의 같은 값을 찾는다. (offset을 구한다.)
				for (int j = 1; j < resultArr.length && j <= MAXOFFSET; j++) {
					if (resultArr[i].equals(transcriptArr[i + j + offset])) {
						offset += j;
						flag = true;
						break;
					} else if ((i - j + offset) > 0 && resultArr[i].equals(transcriptArr[i - j + offset])) {
						offset -= j;
						flag = true;
						break;
					}
				}
				
				if (flag) {
					this.getResult(foreIndex, i, foreTranscriptIndex, i + offset);
					this.getResult(i);
					// 현재 위치 기록
					foreIndex = i;
					foreTranscriptIndex = i + offset;
					wrongIndex = -1;
					continue;
				}
				
				// 틀린 자막이 맞으면
				wrongList.add(i);
				wrongIndex = i;
				System.out.println("Wrong Word : " + i + " : " + resultArr[i] + " ");
				
				
			} else {
				// 맞으면
				//if (wrongList.isEmpty()) {
				// 틀린 자막 리스트가 비어있으면
				if (wrongIndex == -1) {
					// 현재 틀린 자막이 없으면
					// 현재 위치 기록
					foreIndex = i;
					foreTranscriptIndex = i + offset;
					// 결과에 현재 자막 값을 추가
					this.getResult(i);
					continue;
				}
				// 틀린 자막이 있었음 == 지금이 틀린 자막 이후 처음 맞은 자막이라면
				this.getResult(foreIndex, i, foreTranscriptIndex, i + offset);
				this.getResult(i);
				// 현재 위치 기록
				foreIndex = i;
				foreTranscriptIndex = i + offset;
				wrongIndex = -1;
			}
		}
		
		return wrongList;
	}
	
	private double averageTimeOfWord(String word) {
		double totalTime = 0.0;
		double totalCount = 0; 
		
		for (SpeechTimestamp e: this.result.getResults().get(0).getAlternatives().get(0).getTimestamps()) {
			// 같으면
			if (e.getWord() == word) {
				totalTime += e.getEndTime() - e.getStartTime();
				totalCount++;
			}
		}
		if (totalCount == 0)
			return 0.0;
		else 
			return totalTime / totalCount;
	}
	
	public void printTimeStampResult() {
		int i = 0;
		for (TimeStamp t: this.finalResult) {
			System.out.println(i + " : " + t.word);
			System.out.println("Time : " + t.startTime + " ~ " + t.endTime);
			i++;
		}
	}
	
	public void printTranscriptResult() {
		System.out.println("Complemented Results : ");
		for (TimeStamp t: this.finalResult) {
			System.out.print(t.word + " ");
		}
	}
	
    public static String trimRight(String s) {
        int i = s.length()-1;
        while (i >= 0 && Character.isWhitespace(s.charAt(i))) {
            i--;
        }
        return s.substring(0,i+1);
    }
    
    public static String trimLeft(String s) {
        int i = 0;
        while (i < s.length() && Character.isWhitespace(s.charAt(i))) {
            i++;
        }
        return s.substring(i);
    }
}
