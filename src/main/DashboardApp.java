package main;

import java.io.*;

import com.ibm.watson.developer_cloud.http.HttpMediaType;
import com.ibm.watson.developer_cloud.speech_to_text.v1.SpeechToText;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.RecognizeOptions;

public class DashboardApp {
	private static final int numberOfTestCases = 3;
	private static TestCase[] testCases;
	
	private static SpeechToText service = new SpeechToText();
	
	private static void initTestCases() {
		testCases = new TestCase[numberOfTestCases];
		
		testCases[0] = new TestCase();
		testCases[0].resourcePath = "resources/SpeechSample.wav";
		testCases[0].transcript = "several tornadoes touch down as a line of severe1 thunderstorms swept through Colorado on Sunday";
		testCases[0].options = new RecognizeOptions.Builder()
				.timestamps(true)
			    .contentType(HttpMediaType.AUDIO_WAV)
			    .build();
		
		testCases[1] = new TestCase();
		testCases[1].resourcePath = "resources/Sample2.mp3";
		testCases[1].transcript = "dad I want to send this book to grandma do you have a box yeah I've got this one to put photo albums in but it's a bit small the box looks big enough for the book can I use it";
		testCases[1].options = new RecognizeOptions.Builder()
				.timestamps(true)
				.speakerLabels(true)
			    .contentType(HttpMediaType.AUDIO_MP3)
			    .build();
		
		testCases[2] = new TestCase();
		testCases[2].resourcePath = "resources/Sample2_mix.mp3";
		testCases[2].transcript = "dad I want to send this book to grandma do you have a box yeah I've got this one to put photo albums in but it's a bit small the box looks big enough for the book can I use it";
		testCases[2].options = new RecognizeOptions.Builder()
				.timestamps(true)
			    .contentType(HttpMediaType.AUDIO_MP3)
			    .build();
	}
	
	private static void initSpeechToText() {
		service.setUsernameAndPassword(Credentials.UserName, Credentials.Password);
	}
	
	public static void main(String[] args) {
		initTestCases();
		initSpeechToText();
 
		// Test
		int i = 0;
		
		for (TestCase t: testCases) {
			File audio = new File(t.resourcePath);
			
			t.result = service.recognize(audio, t.options).execute();
			
			System.out.println(i++);
			System.out.println(t.result);
			
			if (t.isCorrect())
				System.out.println(t.isCorrect());
			else 
				System.out.println(t.findIndexOfWord());
			
			//System.out.println(t.result.getResults().get(0).getAlternatives().get(0).getTranscript());
			
			//System.out.println(t.result.getResults().get(0).getAlternatives().get(0).getTimestamps());
		}
	}
}