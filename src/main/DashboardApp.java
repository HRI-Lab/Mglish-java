package main;

import java.io.*;

import com.ibm.watson.developer_cloud.http.HttpMediaType;
import com.ibm.watson.developer_cloud.speech_to_text.v1.SpeechToText;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.RecognizeOptions;

public class DashboardApp {
	private static final int numberOfTestCases = 1;
	private static TestCase[] testCases;
	
	private static SpeechToText service = new SpeechToText();
	private static RecognizeOptions options;
	
	private static void initTestCases() {
		testCases = new TestCase[numberOfTestCases];
		
		testCases[0] = new TestCase();
		testCases[0].resourcePath = "resources/SpeechSample.wav";
		testCases[0].transcript = "several tornadoes touch down as a line of severe1 thunderstorms swept through Colorado on Sunday";
	}
	
	private static void initSpeechToText() {
		service.setUsernameAndPassword(Credentials.UserName, Credentials.Password);

		options = new RecognizeOptions.Builder()
				.timestamps(true)
			    .contentType(HttpMediaType.AUDIO_WAV)
			    .build();
	}
	
	public static void main(String[] args) {
		initTestCases();
		initSpeechToText();
 
		// Test
		for (TestCase t: testCases) {
			File audio = new File(t.resourcePath);
			
			t.result = service.recognize(audio, options).execute();
			if (t.isCorrect())
				System.out.println(t.isCorrect());
			else 
				System.out.println(t.findIndexOfWord());
			
			//System.out.println(t.result.getResults().get(0).getAlternatives().get(0).getTranscript());
			
			//System.out.println(t.result.getResults().get(0).getAlternatives().get(0).getTimestamps());
		}
	}
}