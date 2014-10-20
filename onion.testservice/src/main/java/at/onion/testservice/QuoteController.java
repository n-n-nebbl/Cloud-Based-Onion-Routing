package at.onion.testservice;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class QuoteController {
	
	Logger logger = LoggerFactory.getLogger(getClass());
	
	@RequestMapping(value="/", produces="application/json")
	@ResponseBody
	public String quote() {
		String quote = "TestQuote";
		//TODO: get quote;
		JSONObject result = new JSONObject().append("quote", quote);
		
		logger.debug(result.toString());
		
		return result.toString();
	}	
}
