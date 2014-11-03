package at.onion.testservice;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class QuoteController {

	Logger	logger	= LoggerFactory.getLogger(getClass());

	private String getQuote() {
		File paperFile = new File("/tmp/fluxgate.txt");

		StringBuilder fileContent = new StringBuilder();

		FileInputStream fstream1 = null;
		try {
			BufferedReader br1 = new BufferedReader(new InputStreamReader(new FileInputStream(paperFile), "UTF-8"));
			String paperString = fileContent.toString().trim();
			String forbiddenChars = "@:(){}<>[]";
			List<String> quotes = new ArrayList<String>();
			String fileLine = "";

			while ((fileLine = br1.readLine()) != null)
				fileContent.append(fileLine).append(' ');

			BreakIterator boundary = BreakIterator.getSentenceInstance();
			boundary.setText(paperString);

			int start = boundary.first();
			for (int end = boundary.next(); end != BreakIterator.DONE; start = end, end = boundary.next()) {
				String line = fileContent.substring(start, end).trim();

				// If it is a sentence
				if (!line.isEmpty()) {
					boolean add = true;

					for (char c : forbiddenChars.toCharArray()) {
						if (line.contains("" + c)) add = false;
					}

					if (add) quotes.add(line);
				}
			}

			return quotes.get(new Random().nextInt(quotes.size() - 1));

		} catch (Exception e) {
			logger.error("Error reading quote: " + e.getMessage());
		} finally {
			if (fstream1 != null) {
				try {
					fstream1.close();
				} catch (IOException e) {
				}
			}
		}

		return "No quote found.";
	}

	@RequestMapping(value = "/", produces = "application/json")
	@ResponseBody
	public String quote() {
		String quote = "TestQuote";
		// TODO: get quote;
		JSONObject result = new JSONObject().append("quote", quote);

		// logger.debug(result.toString());

		return result.toString();
	}
}
