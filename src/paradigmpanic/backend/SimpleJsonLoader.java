package paradigmpanic.backend;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import paradigmpanic.backend.Question.Category;
import paradigmpanic.backend.Question.Difficulty;
import paradigmpanic.backend.Question.Type;

/**
 * Minimal JSON loader tailored to the predictable structure of questions.json.
 * Not a general-purpose parser but keeps dependencies zero.
 */
public class SimpleJsonLoader {

	private static final Pattern STRING_FIELD = Pattern.compile("\"%s\"\\s*:\\s*\"(.*?)\"", Pattern.DOTALL);
	private static final Pattern NUMBER_FIELD = Pattern.compile("\"%s\"\\s*:\\s*(\\d+)");
	private static final Pattern ARRAY_FIELD = Pattern.compile("\"%s\"\\s*:\\s*\\[(.*?)\\]", Pattern.DOTALL);

	public List<Question> load(InputStream inputStream) throws IOException {
		if (inputStream == null) {
			throw new IOException("Unable to locate questions.json");
		}
		String json = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
		return parseQuestions(json);
	}

	private List<Question> parseQuestions(String json) {
		List<Question> questions = new ArrayList<>();
		for (String objectBody : extractQuestionObjects(json)) {
			List<String> options = parseOptions(objectBody);
			String id = extractString(objectBody, "id", "");
			String text = extractString(objectBody, "text", "???");
			String hint = extractString(objectBody, "hint", "");
			String snippet = extractString(objectBody, "snippet", "");
			int answer = extractNumber(objectBody, "answer", 0);
			int timeLimit = extractNumber(objectBody, "timeLimit", -1);
			Difficulty difficulty = parseEnum(objectBody, "difficulty", Difficulty.EASY, Difficulty.class);
			Category category = parseEnum(objectBody, "category", Category.THEORY, Category.class);
			Type type = parseEnum(objectBody, "type", Type.NORMAL, Type.class);
			if (timeLimit <= 0) {
				timeLimit = category == Category.PRACTICAL ? 40 : 25;
			}
			questions.add(new Question(id, text, options, answer, timeLimit, hint, snippet, difficulty, category, type));
		}
		return questions;
	}

	private List<String> extractQuestionObjects(String json) {
		List<String> objects = new ArrayList<>();
		int questionsKey = json.indexOf("\"questions\"");
		if (questionsKey < 0) {
			return objects;
		}
		int arrayStart = json.indexOf('[', questionsKey);
		if (arrayStart < 0) {
			return objects;
		}
		int arrayEnd = findMatchingBracket(json, arrayStart);
		if (arrayEnd < 0) {
			return objects;
		}
		String body = json.substring(arrayStart + 1, arrayEnd);
		StringBuilder current = new StringBuilder();
		int depth = 0;
		boolean inString = false;
		for (int i = 0; i < body.length(); i++) {
			char c = body.charAt(i);
			current.append(c);
			if (c == '"' && (i == 0 || body.charAt(i - 1) != '\\')) {
				inString = !inString;
			}
			if (inString) {
				continue;
			}
			if (c == '{') {
				depth++;
			} else if (c == '}') {
				depth--;
				if (depth == 0) {
					objects.add(current.toString());
					current.setLength(0);
				}
			}
		}
		return objects;
	}

	private int findMatchingBracket(String json, int startIndex) {
		int depth = 0;
		boolean inString = false;
		for (int i = startIndex; i < json.length(); i++) {
			char c = json.charAt(i);
			if (c == '"' && (i == 0 || json.charAt(i - 1) != '\\')) {
				inString = !inString;
			}
			if (inString) {
				continue;
			}
			if (c == '[') {
				depth++;
			} else if (c == ']') {
				depth--;
				if (depth == 0) {
					return i;
				}
			}
		}
		return -1;
	}

	private List<String> parseOptions(String objectBody) {
		List<String> options = new ArrayList<>();
		Pattern pattern = Pattern.compile(String.format(ARRAY_FIELD.pattern(), Pattern.quote("options")), Pattern.DOTALL);
		Matcher matcher = pattern.matcher(objectBody);
		while (matcher.find()) {
			String arrayContent = matcher.group(1);
			String[] split = arrayContent.split(",");
			for (String raw : split) {
				String cleaned = raw.trim();
				if (cleaned.startsWith("\"") && cleaned.endsWith("\"")) {
					cleaned = cleaned.substring(1, cleaned.length() - 1);
				}
				if (!cleaned.isEmpty()) {
					options.add(cleaned.replace("\\\"", "\""));
				}
			}
			if (!options.isEmpty()) {
				break;
			}
		}
		return options;
	}

	private String extractString(String body, String key, String defaultValue) {
		Pattern pattern = Pattern.compile(String.format(STRING_FIELD.pattern(), Pattern.quote(key)), Pattern.DOTALL);
		Matcher matcher = pattern.matcher(body);
		if (matcher.find()) {
			return matcher.group(1).replace("\\\"", "\"");
		}
		return defaultValue;
	}

	private int extractNumber(String body, String key, int defaultValue) {
		Pattern pattern = Pattern.compile(String.format(NUMBER_FIELD.pattern(), Pattern.quote(key)));
		Matcher matcher = pattern.matcher(body);
		if (matcher.find()) {
			return Integer.parseInt(matcher.group(1));
		}
		return defaultValue;
	}

	private <E extends Enum<E>> E parseEnum(String body, String key, E fallback, Class<E> enumClass) {
		String value = extractString(body, key, null);
		if (value == null) {
			return fallback;
		}
		try {
			return Enum.valueOf(enumClass, value.trim().toUpperCase());
		} catch (IllegalArgumentException ex) {
			return fallback;
		}
	}
}

