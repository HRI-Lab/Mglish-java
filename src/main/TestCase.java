package main;

import java.util.List;

import com.ibm.watson.developer_cloud.speech_to_text.v1.model.RecognizeOptions;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.SpeechAlternative;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.SpeechResults;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.SpeechTimestamp;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.Transcript;

public class TestCase {
	public String resourcePath = "";
	public String transcript = "";
	public SpeechResults result;
	public RecognizeOptions options;
	
	// Watson에서 받은 결과의 자막정보 객체를 전부 연결하여 반환한다.
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
	
	// Watson에서 받은 결과의 자막정보를 스트링으로 전부 연결하여 반환한다.
	public String resultTranscript() {
		String results = "";
		for (Transcript t: this.result.getResults()) {
			for (SpeechAlternative a: t.getAlternatives()) {
				results += a.getTranscript();
			}
		}
		return results;
	}
}
