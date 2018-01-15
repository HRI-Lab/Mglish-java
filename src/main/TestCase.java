package main;

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
	
	public int findIndexOfWord() {
		String[] resultArr, transcriptArr;
		resultArr = this.resultTranscript().split(" ");
		transcriptArr = this.transcript.split(" ");
		
		for (int i = 0; i < resultArr.length; i++) {
			if (!(resultArr[i].equals(transcriptArr[i])))
				return i;
		}
		return -1;
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
