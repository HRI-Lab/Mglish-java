package main;

import java.io.*;
import java.util.ArrayList;

import com.ibm.watson.developer_cloud.http.HttpMediaType;
import com.ibm.watson.developer_cloud.speech_to_text.v1.SpeechToText;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.RecognizeOptions;

public class DashboardApp {
	private static ArrayList<TestCase> testCases = new ArrayList<TestCase>();
	
	private static SpeechToText service = new SpeechToText();
	
	private static void initTestCases() {
		TestCase testCase = new TestCase();
		testCase.resourcePath = "resources/SpeechSample.wav";
		testCase.transcript = "several tornadoes touch down as a line of severe1 thunderstorms swept through Colorado on Sunday";
		testCase.options = new RecognizeOptions.Builder()
				.timestamps(true)
			    .contentType(HttpMediaType.AUDIO_WAV)
			    .build();
		//testCases.add(testCase);
		
		testCase = new TestCase();
		testCase.resourcePath = "resources/Sample2.mp3";
		testCase.transcript = "dad I want to send this book to grandma do you have a box yeah I've got this one to put photo albums in but it's a bit small the box looks big enough for the book can I use it";
		testCase.options = new RecognizeOptions.Builder()
				.timestamps(true)
				.speakerLabels(true)
			    .contentType(HttpMediaType.AUDIO_MP3)
			    .build();
		//testCases.add(testCase);
		
		testCase = new TestCase();
		testCase.resourcePath = "resources/Sample2_mix.mp3";
		testCase.transcript = "dad I want to send this book to grandma do you have a box yeah I've got this one to put photo albums in but it's a bit small the box looks big enough for the book can I use it";
		testCase.options = new RecognizeOptions.Builder()
				.timestamps(true)
			    .contentType(HttpMediaType.AUDIO_MP3)
			    .build();
		testCases.add(testCase);
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
			//System.out.println("Results from Watson" + t.result);
			
			if (t.isCorrect())
				System.out.println(t.isCorrect());
			else {
				System.out.println("Wrong Words Index : " + t.findByResult());
				System.out.println();
				t.printTimeStampResult();
				System.out.println();
				System.out.println("Results from Watson : \n" + t.resultTranscript());
				t.printTranscriptResult();
				System.out.println();
				System.out.println("Answer : \n" + t.transcript);
			}
			
			//System.out.println(t.result.getResults().get(0).getAlternatives().get(0).getTranscript());
			
			//System.out.println(t.result.getResults().get(0).getAlternatives().get(0).getTimestamps());
		}
	}
}