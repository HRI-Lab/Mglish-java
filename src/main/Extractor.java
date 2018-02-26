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
	
	// 결과를 저장할 TimeStamp클래스
	private class TimeStamp{
		// 단어, 시작시간, 끝시간, 화자 정보를 가지고 있다.
		public String word = "";
		public double startTime = 0.0;
		public double endTime = 0.0;
		public int speaker = 0;
		
		TimeStamp(){
			
		}
		
		TimeStamp(String word, double startTime, double endTime, int speaker){
			this.word = word;
			this.startTime = startTime;
			this.endTime = endTime;
			this.speaker = speaker;
		}
	}
	
	// 생성자
	Extractor(TestCase testCase) {
		this.testCase = testCase;
	}
	
	//	동영상 내 음성신호 에 대해 STT(Speech to Text)변환
	//    →정보화 된 음성인식 결과  찾기
	// 추출 함수
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
			if ((i + offset) < resultArr.length && !(transcriptArr[i].equals(resultArr[i + offset]))) {
				// Not Found
				boolean flag = false;
				// MAXOFFSET만큼 찾는다.
				for (int j = 1; (i + j + offset) < resultArr.length && j <= MAXOFFSET; j++) {
					if (transcriptArr[i].equals(resultArr[i + j + offset])) {
						// 뒷 부분에 맞는 단어가 있을 경우
						// j값 만큼 offset을 더한다.
						offset += j;
						// Found
						flag = true;
						break;
					} else if ((i - j + offset) > 0 && transcriptArr[i].equals(resultArr[i - j + offset])) {
						// 앞 부분에 맞는 단어가 있을 경우
						// j값 만큼 offset을 뺀다.
						offset -= j;
						// Found
						flag = true;
						break;
					}
				}
				
				// 맞는 단어를 찾았을 경우
				if (flag) {
					// 해당 index의 단어를 결과에 추가한다.
					this.getResult(foreIndex, i + offset, foreTranscriptIndex, i);
					this.addResult(i + offset, i);
					foreIndex = i + offset;
					foreTranscriptIndex = i;
					wrongIndex = -1;
					continue;
				}
				
				wrongIndex = i;
				//System.out.println("Wrong Word : " + i + " : " + transcriptArr[i] + " ");
			} else {
				// 기존에 틀린 값이 없을 경우 해당 Index의 단어만 저장한다.
				if (wrongIndex == -1) {
					foreIndex = i + offset;
					foreTranscriptIndex = i;
					this.addResult(i + offset, i);
					continue;
				}
				// 기존에 틀린 값이 있을 경우 해당 값 Index까지의 단어들을 저장한다.
				this.getResult(foreIndex, i + offset, foreTranscriptIndex, i);
				this.addResult(i + offset, i);
				foreIndex = i + offset;
				foreTranscriptIndex = i;
				wrongIndex = -1;
			}
		}
		
		// 후처리 함수
		postProcess();
	}
	
	// Speaker를 모르는 정보화 자막에 대해 가지고 있는 자막과 나온 결과를 비교하여 화자정보를 찾아 넣는 함수
	private void postProcess() {
		// 해당 단어의 줄
		int lineNumber = 0;
		for (TimeStamp t: this.finalResult) {
			// 화자정보가 없는 값에 대해
			if (t.speaker == 0) {
				// 해당 단어의 인덱스를 얻는다.
				int index = finalResult.indexOf(t);
				// 해당 단어의 줄 번호를 얻는다.
				lineNumber = lineNumberOf(index);
				for (int i = index, j = 0; i + j < finalResult.size(); j++) {
					// 해당 단어와 같은 줄 번호를 가지고 있고 화자정보를 가지고 있는 결과값을 찾는다. (앞 뒤로 동시 탐색)
					// 그리고 같은 줄 번호를 가지고 있는 단어 정보의 화자 번호를 대입한다.
					
					TimeStamp f = this.finalResult.get(i + j);
					if (f.speaker != 0 && lineNumberOf(i + j) == lineNumber) {
						t.speaker = f.speaker;
						break;
					}
					if (i - j >= 0) {
						TimeStamp f2 = this.finalResult.get(i - j);
						if (f2.speaker != 0 && lineNumberOf(i - j) == lineNumber) {
							t.speaker = f2.speaker;
							break;
						}
					}
				}
			}
		}
	}
	
	// 해당 index의 단어가 몇번째 줄에 있는지 반환하는 함수
	private int lineNumberOf(int index) {
		final String[] transcriptArr = getSTRFilterNewLine(testCase.transcript).split(" ");
		int i = 0;
		int line = 1;
		// 줄단위로 나눈 String배열
		String[] lines = testCase.transcript.split("\\r?\\n");
		// 각 줄 For문
		for (String s: lines) {
			// 각 줄의 단어 For문
			for (String s2: s.split(" ")) {
				if (i == index)
					// 해당 Index를 만나면 그 단어의 줄번호를 반환한다.
					return line;
				i++;
			}
			line++;
		}
		
		return 0;
	}
	
	// 단어,화자,타이밍 정보를 받아 db에 저장하는 함수 (단어의 각 정보를 변수로 받는 함수)
	private void appendResult(String word, double startTime, double endTime, int speaker) {
		TimeStamp t = new TimeStamp(word, startTime, endTime, speaker);
		
		// SDB를 Update한다.
		sdbManager.updateSDB(speaker, word, (endTime - startTime));
		
		finalResult.add(t);
	}
	
	// 단어,화자,타이밍 정보를 받아 db에 저장하는 함수 (TimeStamp를 변수르 받는 함수)
	private void appendResult(TimeStamp nt) {
		TimeStamp t = new TimeStamp(nt.word, nt.startTime, nt.endTime, nt.speaker);
		
		// SDB를 Update한다.
		sdbManager.updateSDB(t.speaker, t.word, (t.endTime - t.startTime));
		
		finalResult.add(t);
	}
	
	// 잘 인식된 단어를 바로 저장하는 함수
	private void addResult(int index, int index2) {
		if (index >= testCase.resultTimestamps().size())
			return;
		
		final String[] transcriptArr = getSTRFilterNewLine(testCase.transcript).split(" ");
		
		SpeechTimestamp t = testCase.resultTimestamps().get(index);
		
		// Watson에서 받은 결과를 그대로 변환하여 DB에 저장한다.
		appendResult(transcriptArr[index2], t.getStartTime(), t.getEndTime(), getSpeaker(t));
	}
	
	// 해당 타이밍정보에 맞는 화자를 찾아주는 함수
	private int getSpeaker(SpeechTimestamp t) {
		int speaker = -1;
		for (SpeakerLabel s: testCase.result.getSpeakerLabels()) {
			// Watson에서 받은 결과에서 해당 단어 정보의 타이밍이 정확히 일치하면 그 화자가 맞다고 간주한다.
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
		final String word = transcriptArr[startIndex2];

		int a = -1, b = -2;
		// 특정 음량(dB) T 이상의 신호가 시작 되는 지점을 찾을 수 없으므로 전 단어의 발음이 끝나는 시간(E_a)으로 정한다.
		// 전 단어가 없는 첫 단어일 경우 0 으로 정한다.
		if (startIndex != -1) {
			// S_b = E_a
			fStartTime = ts.get(startIndex).getEndTime();
			// 전단어가 있는 경우 a는 전단어의 화자번호
			a = getSpeaker(ts.get(startIndex));
		}
		if (endIndex < ts.size()) {
			if (startIndex == -1)
				// 전단어가 있는 경우 a는 전단어의 화자번호
				a = getSpeaker(ts.get(endIndex));
			// 후단어가 있는 경우 a는 후단어의 화자번호
			b = getSpeaker(ts.get(endIndex));
		}
		
		// 양쪽 끝의 Speaker가 같은경우 해당 Speaker가 말한 것으로 본다. 
		if (a == b)
			speaker = a;
		
		// 끝 시간은 후 단어의 시작시간
		fEndTime = ts.get(endIndex).getStartTime();
		appendResult(word, fStartTime, fEndTime, speaker);
	}
	
	private void algorithmN3(int startIndex, int endIndex, int startIndex2, int endIndex2) {
		final String[] transcriptArr = getSTRFilterNewLine(testCase.transcript).split(" ");
		final List<SpeechTimestamp> ts = testCase.resultTimestamps();
		
		ArrayList<TimeStamp> list = new ArrayList<TimeStamp>();
		// 모르는 단어의 수 = N
		int count = 0;
		// List에 전부 삽입
		for (int i = startIndex2; i < endIndex2; i++) {
			TimeStamp t = new TimeStamp();
			final String word = transcriptArr[i];
			
			t.word = word;
			
			list.add(t);
			count++;
		}
		
		// 맞는 첫 단어 결과(타이밍 값을 가지고 있는)
		SpeechTimestamp startTS = ts.get(startIndex);
		// 맞는 끝 단어 결과(타이밍 값을 가지고 있는)
		SpeechTimestamp endTS = ts.get(endIndex);
		
		// 첫 번째 모르는단어의 시작시간 = 맞는 첫 단어의 끝시간
		list.get(0).startTime = startTS.getEndTime();
		// 끝 번째 모르는단어의 시작시간 = 맞는 끝 단어의 첫시간
		list.get(count-1).endTime = endTS.getStartTime();
		
		// SDB에 없는 부분 순열을 찾기 위한 index
		int subStartIndex = -1;
		int subEndIndex = -1;
		
		// 부분 순열을 찾는 for문
		for (int i = 0; i < count; i++) {
			// 양쪽 끝 Timestamp
			TimeStamp startT = list.get(i);
			TimeStamp endT = list.get(count - i - 1);
			
			// 부분 순열의 시작을 아직 못 찾았을 경우
			if (subStartIndex == -1) {
				if (startT.endTime == 0) {
					if (!sdbManager.isExist(startT.speaker, startT.word)) {
						subStartIndex = i;
					} else {
						startT.endTime = startT.startTime + sdbManager.averageTimeOf(startT.speaker, startT.word);
						list.get(i+1).startTime = startT.endTime;
					}
				} else {
					list.get(i+1).startTime = startT.endTime;
				}
			}
			
			// 부분 순열의 끝을 아직 못 찾았을 경우
			if (subEndIndex == -1) {
				if (endT.startTime == 0) {
					if (!sdbManager.isExist(endT.speaker, endT.word)) {
						subEndIndex = i;
					} else {
						endT.startTime = endT.endTime - sdbManager.averageTimeOf(endT.speaker, endT.word);
						list.get(i-1).endTime = endT.startTime;
					}
				} else {
					list.get(i-1).endTime = endT.startTime;
				}
			}
		}
		
		// 부분순열이 존재하는 경우
		if (!(subStartIndex != -1 && subEndIndex != -1)) {
			TimeStamp startT = list.get(subStartIndex);
			TimeStamp endT = list.get(subEndIndex);
			
			// 부분순열의 총 발음 시간
			double totalTime = endT.endTime - startT.startTime;
			
			// 부분순열의 알파벳 숫자를 구한다. (평균시간을 측정하기 위해)
			int wordCount = 0;
			for (int i = subStartIndex; i <= subEndIndex; i++) {
				wordCount += list.get(i).word.length();
			}
			
			// 해당 S-DB가 존재하지 않을 경우 평균시간으로 계산한다.
			double averageTime = totalTime / wordCount;
			
			// 부분 순열의 타이밍 정보를 수정하는 반복문
			for (int i = subStartIndex; i < subEndIndex; i++) {
				// 현재 단어의 정보
				TimeStamp t1 = list.get(i);
				// 그 다음 단어의 정보
				TimeStamp t2 = list.get(i+1);
				
				double time = (averageTime * t1.word.length());
				
				// 해당 S-DB가 존재할 경우 그 S-DB를 사용한다.
				if (sdbManager.isExist(t1.speaker, t1.word)) {
					time = sdbManager.averageTimeOf(t1.speaker, t1.word);
				}
				
				t1.endTime = t1.startTime + time;
				t2.startTime = t1.endTime;
			}
		}

		for (TimeStamp t: list) {
			appendResult(t);
		}
	}
	
	// startIndex와 endIndex : Watson에서 받은 결과 값의 배열 Index
	// startIndex2와 endIndex2 : 가지고 있는 자막 파일의 자막의 배열 Index
	// -> startIndex ~ endIndex 사이의 값들이 오인식된 단어 정보임을 뜻함.
	private void getResult(int startIndex, int endIndex, int startIndex2, int endIndex2) {
//		System.out.println("startIndex : " + startIndex + " endIndex : " + endIndex);
//		System.out.println("startIndex2 : " + startIndex2 + " endIndex2 : " + endIndex2);
//		System.out.println();
		// prevent exceptions
		startIndex2 += 1;
		if (startIndex2 > endIndex2)
			return;
		
		int n = endIndex2 - startIndex2;
		int n2 = endIndex - startIndex - 1;
		// n과 n2 즉, (결과와 기존 자막파일에서) 틀린 단어의 개수가 같을경우
		if (n == 1 && n2 == 1) {
			// 1개일경우 알고리즘 1을 적용
			algorithmN1(startIndex, endIndex, startIndex2, endIndex2);
			return;
		} else if (n == n2) {
			// 2개 이상일 경우 알고리즘 3을 적용
			algorithmN3(startIndex, endIndex, startIndex2, endIndex2);
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
		
		// fEndTime은 마지막 단어의 끝 시간이므로 맨 뒷 단어의 시작 시간으로 정한다.
		fEndTime = ts.get(endIndex).getStartTime();
		
		// 각 단어의 시작시간 끝시간 변수 선언
		double startTime = fStartTime;
		double endTime = fEndTime;
		
		// 평균 시간을 측정하기 위해 알파벳 숫자를 계산
		int wordCount = 0;
		for (int i = startIndex2; i < endIndex2; i++) {
			wordCount += transcriptArr[i].length();
		}
		
		// 알파벳당 평균 시간 측정
		double averageTime = (fEndTime - fStartTime) / (wordCount);
		for (int i = startIndex2; i < endIndex2; i++) {
			String word = transcriptArr[i];
			int a = -1, b = -2;
			// 알고리즘1에서 적용한 바와 마찬가지로 앞 단어와 뒷 단어의 화자 정보가 같을 경우엔 그 화자인 것으로 간주한다.
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
			
			// 발음 시간이 S-DB에 있을 경우 그 S-DB를 사용하고
			if (sdbManager.isExist(speaker, word))
				time = sdbManager.averageTimeOf(speaker, word);
			else
				// 그렇지 않을 경우 평균 시간값을 사용한다.
				time = averageTime * word.length();
			
			endTime = startTime + time;
			// 만약 현재 단어의 끝시간이 가장 끝의 단어의 끝시간을 벗어날 경우 그 값으로 대체한다.
			if (endTime > fEndTime)
				endTime = fEndTime;
			
			this.appendResult(word, startTime, endTime, speaker);
			
			// 다음 단어의 시작시간은 현재 단어의 끝 시간으로 결정
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
			str += String.format("\t [%.2f ~ %.2f]", t.startTime, t.endTime) + "\r\n";
		}
		str += "\r\n";
		
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
				str += "\r\n";
				beforeSpeaker = t.speaker;
				str += "Speaker " + t.speaker + " : ";
			}
			str += t.word + " ";
		}
		str += "\r\n";
		
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
