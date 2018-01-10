package main;

import java.io.*;

import com.ibm.watson.developer_cloud.http.HttpMediaType;
import com.ibm.watson.developer_cloud.speech_to_text.v1.SpeechToText;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.RecognizeOptions;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.SpeechResults;;

public class DashboardApp {
	public static void main(String[] args) {
		System.out.println("Start DashboardApp");
		
		SpeechToText service = new SpeechToText();
		service.setUsernameAndPassword(Credentials.UserName, Credentials.Password);

		File audio = new File("resources/SpeechSample.wav");

		RecognizeOptions options = new RecognizeOptions.Builder()
		    .contentType(HttpMediaType.AUDIO_WAV)
		    .build();

		SpeechResults transcript = service.recognize(audio, options).execute();
		System.out.println(transcript);
		
		System.out.println("End of DashboardApp");
	}
}