package main;

import java.util.ArrayList;
import java.util.List;

import com.ibm.watson.developer_cloud.http.HttpMediaType;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.RecognizeOptions;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.SpeakerLabel;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.SpeechAlternative;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.SpeechResults;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.SpeechTimestamp;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.Transcript;

public class TestCase {
	
	class TimeStamp{
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
	
	public String resourcePath = "";
	public String transcript = "";
	public SpeechResults result;
	public RecognizeOptions options;
	public ArrayList<TimeStamp> finalResult = new ArrayList<TimeStamp>();
	
	public List<SpeechTimestamp> resultTimestamps() {
		List<SpeechTimestamp> ts = this.result.getResults().get(0).getAlternatives().get(0).getTimestamps();
		boolean isFirst = true;
		for (Transcript t: this.result.getResults()) {
			if (isFirst) {
				isFirst = false;
				continue;
			}
			for (SpeechAlternative a: t.getAlternatives()) {
				ts.addAll(a.getTimestamps());
			}
		}
		return ts;
	}
	
	public String resultTranscript() {
		String results = "";
		for (Transcript t: this.result.getResults()) {
			for (SpeechAlternative a: t.getAlternatives()) {
				results += a.getTranscript();
			}
		}
		return results;
	}
	
	public boolean isCorrect() {
		String resultTranscript = this.resultTranscript();
		resultTranscript = trimRight(resultTranscript);
		
		//System.out.println(resultTranscript);
		//System.out.println(transcript);
		
		return resultTranscript.equals(transcript);
	}
	
	private void addResult(String word, double startTime, double endTime, int speaker) {
		TimeStamp t = new TimeStamp(word, startTime, endTime, speaker);
		finalResult.add(t);
	}
	
	private void addResult2(String word, double startTime, double endTime) {
		TimeStamp t = new TimeStamp(word, startTime, endTime, 0);
		finalResult.add(t);
	}
	
	private int getSpeaker(SpeechTimestamp t) {
		int speaker = -1;
		for (SpeakerLabel s: this.result.getSpeakerLabels()) {
			if (s.getFrom().equals(t.getStartTime()) && s.getTo().equals(t.getEndTime())) {
				speaker = s.getSpeaker();
				break;
			}
		}
		return speaker + 1;
	}
	
	private void getResult(int index) {
		if (index >= this.resultTimestamps().size())
			return;
		SpeechTimestamp t = this.resultTimestamps().get(index);
		
		addResult(t.getWord(), t.getStartTime(), t.getEndTime(), getSpeaker(t));
	}
	
	private void getResult2(int index) {
		//System.out.println("getResult : " + index);
		SpeechTimestamp t = this.result.getResults().get(0).getAlternatives().get(0).getTimestamps().get(index);
		addResult2(t.getWord(), t.getStartTime(), t.getEndTime());
	}
	
	// Result / Transcript
	// Result[startIndex] == Transcript[startIndex2]
	// Result[endIndex] == Transcript[endIndex2]
	private void getResult(int startIndex, int endIndex, int startIndex2, int endIndex2) {
		// prevent exceptions
		startIndex2 += 1;
		if (startIndex > endIndex || startIndex2 > endIndex2)
			return;
		
		String[] transcriptArr = this.transcript.split(" ");
		List<SpeechTimestamp> ts = resultTimestamps();
		double fStartTime, fEndTime;

		if (startIndex != -1) {
			fStartTime = ts.get(startIndex).getEndTime();
		}
		else
			// if First word
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
			
			double time = this.averageTimeOfWord(word, speaker);
			if (time == 0.0)
				time = averageTime;
			
			endTime = startTime + time;
			if (endTime > fEndTime)
				endTime = fEndTime;
			
			this.addResult(word, startTime, endTime, speaker);
			
			startTime = endTime;
		}
	}
	
	private void getResult2(int startIndex, int endIndex, int startIndex2, int endIndex2) {
		// ����ó��
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
			double time = this.averageTimeOfWord2(word);
			// ��� �����ð��� ���� ��� ��սð����� å��
			if (time == 0.0)
				time = averageTime;
			
			endTime = startTime + time;
			if (endTime > fEndTime)
				endTime = fEndTime;
			
			this.addResult2(word, startTime, endTime);
			
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
					this.getResult2(foreIndex, i, foreTranscriptIndex, i + offset);
					this.getResult2(i);
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
					this.getResult2(i);
					continue;
				}
				// Ʋ�� �ڸ��� �־��� == ������ Ʋ�� �ڸ� ���� ó�� ���� �ڸ��̶��
				this.getResult2(foreIndex, i, foreTranscriptIndex, i + offset);
				this.getResult2(i);
				// ���� ��ġ ���
				foreIndex = i;
				foreTranscriptIndex = i + offset;
				wrongIndex = -1;
			}
		}
		
		return wrongList;
	}
	
	public ArrayList<Integer> findByTranscript() {
		final int MAXOFFSET = 3;
		String[] resultArr, transcriptArr;
		resultArr = this.resultTranscript().split(" ");
		transcriptArr = this.transcript.split(" ");
		ArrayList<Integer> wrongList = new ArrayList<Integer>();
		
		// Example
		// I want to send this Ranma do you have a box yeah I found this one to put the photo albums in five it's a bit small
		// dad I want to send this book to grandma do you have a box yeah I've got this one to put photo albums in but it's a bit small
		int offset = 0;
		int foreIndex = -1;
		int foreTranscriptIndex = -1;
		int wrongIndex = -1;
		int i = 0;

		for (i = 0; i < transcriptArr.length; i++) {
			
			if ((i + offset) < resultArr.length && !(transcriptArr[i].equals(resultArr[i + offset]))) {
//				System.out.println(transcriptArr[i] + " 와 " + resultArr[i + offset] + "비교");
				// Not Found
				boolean flag = false;
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
				
				if (flag) {
					this.getResult(foreIndex, i + offset, foreTranscriptIndex, i);
					this.getResult(i + offset);
					foreIndex = i + offset;
					foreTranscriptIndex = i;
					wrongIndex = -1;
					continue;
				}
				
				wrongList.add(i);
				wrongIndex = i;
				//System.out.println("Wrong Word : " + i + " : " + transcriptArr[i] + " ");
			} else {
				if (wrongIndex == -1) {
					foreIndex = i + offset;
					foreTranscriptIndex = i;
					this.getResult(i + offset);
					continue;
				}
				this.getResult(foreIndex, i + offset, foreTranscriptIndex, i);
				this.getResult(i + offset);
				foreIndex = i + offset;
				foreTranscriptIndex = i;
				wrongIndex = -1;
			}
		}
		
		return wrongList;
	}
	
	private double averageTimeOfWord(String word, int speaker) {
		double totalTime = 0.0;
		double totalCount = 0; 
		
		for (SpeechTimestamp e: resultTimestamps()) {
			if (e.getWord() == word && getSpeaker(e) == speaker) {
				totalTime += e.getEndTime() - e.getStartTime();
				totalCount++;
			}
		}
		if (totalCount == 0)
			return 0.0;
		else 
			return totalTime / totalCount;
	}
	
	private double averageTimeOfWord2(String word) {
		double totalTime = 0.0;
		double totalCount = 0; 
		
		for (SpeechTimestamp e: resultTimestamps()) {
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
		for (TimeStamp t: this.finalResult) {
			System.out.print("Speaker " + t.speaker + " : " + t.word);
			if (t.word.length() < 5)
				System.out.print("\t");
			System.out.println( String.format("\t [%.2f ~ %.2f]", t.startTime, t.endTime));
		}
	}
	
	public void printConversation() {
		int beforeSpeaker = -1;
		for (TimeStamp t: this.finalResult) {
			if (t.speaker != beforeSpeaker) {
				System.out.println();
				beforeSpeaker = t.speaker;
				System.out.print("Speaker " + t.speaker + " : ");
			}
			System.out.print(t.word + " ");
		}
		System.out.println();
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
    
    public void test() {
    		System.out.println(this.resultTranscript());
    }
}
