package main;

import java.util.ArrayList;

import com.ibm.watson.developer_cloud.http.HttpMediaType;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.RecognizeOptions;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.SpeechResults;

public class TestCase {
	public String resourcePath = "";
	public String transcript = "";
	public SpeechResults result;
	public RecognizeOptions options;
	
	private String resultTranscript() {
		return this.result.getResults().get(0).getAlternatives().get(0).getTranscript();
	}
		
	public boolean isCorrect() {
		String resultTranscript = this.resultTranscript();
		resultTranscript = trimRight(resultTranscript);
		
		System.out.println(resultTranscript);
		System.out.println(transcript);
		
		return resultTranscript.equals(transcript);
	}
	
	public ArrayList<Integer> findIndexOfWord() {
		final int MAXOFFSET = 5;
		String[] resultArr, transcriptArr;
		resultArr = this.resultTranscript().split(" ");
		transcriptArr = this.transcript.split(" ");
		ArrayList<Integer> wrongList = new ArrayList<Integer>();
		
		// I want to send this Ranma do you have a box yeah I found this one to put the photo albums in five it's a bit small
		// dad I want to send this book to grandma do you have a box yeah I've got this one to put photo albums in but it's a bit small
		int offset = 0;
		for (int i = 0; i < resultArr.length; i++) {
			// 틀리면
			if (!(resultArr[i].equals(transcriptArr[i + offset]))) {
				wrongList.add(i+offset);
				// 오차범위 MAX_OFFSET 이내의 같은 값을 찾는다. (offset을 구한다.)
				for (int j = 1; j < resultArr.length && j <= MAXOFFSET; j++) {
					if (resultArr[i].equals(transcriptArr[i + j + offset])) {
						offset += j;
						break;
					}
				}
			}
			
		}
		
		return wrongList;
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
