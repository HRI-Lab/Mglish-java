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
		// ����ó��
		//System.out.println("getResult : " + startIndex + " " + endIndex + " " + startIndex2 + " " + endIndex2);
		startIndex2 += 1;
		if (startIndex > endIndex || startIndex2 > endIndex2)
			return;
		
		String[] transcriptArr = this.transcript.split(" ");
		List<SpeechTimestamp> ts = this.result.getResults().get(0).getAlternatives().get(0).getTimestamps();
		double fStartTime, fEndTime;
		
		// ù �ܾ���� Ʋ�� ���
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
			// ȭ�ں� �ܾ ��� �����ð� ���
			double time = this.averageTimeOfWord(word);
			// ��� �����ð��� ���� ��� ��սð����� å��
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
		// OFFSET�� ����
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
		// �ν� :	 	this Ranma do you
		// Answer :	this book to grandma do you
		int foreIndex = -1;
		int foreTranscriptIndex = -1;
		int wrongIndex = -1;

		for (int i = 0; i < resultArr.length; i++) {
			
			// Ʋ���� : ex)Ranma
			if (!(resultArr[i].equals(transcriptArr[i + offset]))) {
				boolean flag = false;
				// �������� MAX_OFFSET �̳��� ���� ���� ã�´�. (offset�� ���Ѵ�.)
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
					// ���� ��ġ ���
					foreIndex = i;
					foreTranscriptIndex = i + offset;
					wrongIndex = -1;
					continue;
				}
				
				// Ʋ�� �ڸ��� ������
				wrongList.add(i);
				wrongIndex = i;
				System.out.println("Wrong Word : " + i + " : " + resultArr[i] + " ");
				
				
			} else {
				// ������
				//if (wrongList.isEmpty()) {
				// Ʋ�� �ڸ� ����Ʈ�� ���������
				if (wrongIndex == -1) {
					// ���� Ʋ�� �ڸ��� ������
					// ���� ��ġ ���
					foreIndex = i;
					foreTranscriptIndex = i + offset;
					// ����� ���� �ڸ� ���� �߰�
					this.getResult(i);
					continue;
				}
				// Ʋ�� �ڸ��� �־��� == ������ Ʋ�� �ڸ� ���� ó�� ���� �ڸ��̶��
				this.getResult(foreIndex, i, foreTranscriptIndex, i + offset);
				this.getResult(i);
				// ���� ��ġ ���
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
			// ������
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
