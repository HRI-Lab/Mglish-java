package main;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import com.ibm.watson.developer_cloud.http.HttpMediaType;
import com.ibm.watson.developer_cloud.speech_to_text.v1.SpeechToText;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.RecognizeOptions;

public class MainApp {
	// Input file names to test
	private static final String audioFileName = "Sample2_mix.mp3";
	private static final String textFileName = "Sample2_mix.txt";
	
	// for multiple test cases
	private static ArrayList<TestCase> testCases = new ArrayList<TestCase>();
	private static SpeechToText service = new SpeechToText();
	
	// Initialize TestCases
	private static void initTestCases() throws IOException {
		TestCase testCase = new TestCase();
		
		testCase = new TestCase();
		testCase.resourcePath = "resources/" + audioFileName;
		testCase.transcript = readFile("resources/" + textFileName, Charset.forName("utf-8"));
		testCase.options = new RecognizeOptions.Builder()
				.timestamps(true)
				.speakerLabels(true)
			    .contentType(HttpMediaType.AUDIO_MP3)
			    .build();
		testCases.add(testCase);
	}
	
	// Initialize SpeechToText
	private static void initSpeechToText() {
		service.setUsernameAndPassword(Credentials.UserName, Credentials.Password);
	}
	
	private static String readFile(String path, Charset encoding) throws IOException {
	  byte[] encoded = Files.readAllBytes(Paths.get(path));
	  return new String(encoded, encoding);
	}
	
	public static void main(String[] args) throws IOException {
		initTestCases();
		initSpeechToText();
		
		for (TestCase t: testCases) {
			File audio = new File(t.resourcePath);
			
			t.result = service.recognize(audio, t.options).execute();
			
			Extractor extractor = new Extractor(t);
			
			extractor.extract();
			extractor.printTimeStampResult();
			extractor.printConversation();
			
			//System.out.println(t.transcript);
		}
	}
}