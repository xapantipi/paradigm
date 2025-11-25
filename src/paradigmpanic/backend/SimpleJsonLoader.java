package paradigmpanic.backend;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import paradigmpanic.backend.Question.Category;
import paradigmpanic.backend.Question.Difficulty;
import paradigmpanic.backend.Question.Type;

/**
 * Minimal JSON loader tailored to questions.json without relying on fragile regex parsing.
 */
public class SimpleJsonLoader {

	public List<Question> load(InputStream inputStream) throws IOException {
		if (inputStream == null) {
			throw new IOException("Unable to locate questions.json");
		}
		String json = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
		return parseQuestions(json);
	}

	private List<Question> parseQuestions(String json) throws IOException {
		List<Question> questions = new ArrayList<>();
		Object root = new JsonParser(json).parse();
		if (!(root instanceof Map<?, ?> map)) {
			return questions;
		}
		Object questionsNode = map.get("questions");
		if (!(questionsNode instanceof List<?> list)) {
			return questions;
		}
		for (Object entry : list) {
			if (!(entry instanceof Map<?, ?> node)) {
				continue;
			}
			String id = asString(node.get("id"));
			String text = asString(node.get("text"));
			String hint = asString(node.get("hint"));
			String snippet = asString(node.get("snippet"));
			List<String> options = asStringList(node.get("options"));
			int answer = asInt(node.get("answer"), 0);
			int timeLimit = asInt(node.get("timeLimit"), -1);

			Difficulty difficulty = parseEnum(node.get("difficulty"), Difficulty.EASY, Difficulty.class);
			Category category = parseEnum(node.get("category"), Category.THEORY, Category.class);
			Type type = parseEnum(node.get("type"), Type.NORMAL, Type.class);

			if (timeLimit <= 0) {
				timeLimit = category == Category.PRACTICAL ? 40 : 25;
			}

			if (!text.isBlank() && !options.isEmpty()) {
				questions.add(new Question(
						id,
						text,
						options,
						answer,
						timeLimit,
						hint,
						snippet,
						difficulty,
						category,
						type));
			}
		}
		return questions;
	}

	private String asString(Object value) {
		return value == null ? "" : value.toString();
	}

	private List<String> asStringList(Object value) {
		List<String> list = new ArrayList<>();
		if (value instanceof List<?> rawList) {
			for (Object option : rawList) {
				list.add(asString(option));
			}
		}
		return list;
	}

	private int asInt(Object value, int fallback) {
		if (value instanceof Number number) {
			return number.intValue();
		}
		if (value instanceof String str && !str.isBlank()) {
			try {
				return Integer.parseInt(str.trim());
			} catch (NumberFormatException ignore) {
			}
		}
		return fallback;
	}

	private <T extends Enum<T>> T parseEnum(Object value, T defaultValue, Class<T> enumClass) {
		String raw = asString(value);
		if (raw.isBlank()) {
			return defaultValue;
		}
		try {
			return Enum.valueOf(enumClass, raw.trim().toUpperCase());
		} catch (IllegalArgumentException ex) {
			return defaultValue;
		}
	}

	private static final class JsonParser {
		private final String input;
		private int index;

		private JsonParser(String input) {
			this.input = input;
		}

		private Object parse() throws IOException {
			skipWhitespace();
			Object value = parseValue();
			skipWhitespace();
			if (!isEnd()) {
				throw error("Unexpected trailing content");
			}
			return value;
		}

		private Object parseValue() throws IOException {
			skipWhitespace();
			if (isEnd()) {
				throw error("Unexpected end of input");
			}
			char c = peek();
			if (c == '{') {
				return parseObject();
			}
			if (c == '[') {
				return parseArray();
			}
			if (c == '"') {
				return parseString();
			}
			if (c == '-' || Character.isDigit(c)) {
				return parseNumber();
			}
			if (matchLiteral("true")) {
				return Boolean.TRUE;
			}
			if (matchLiteral("false")) {
				return Boolean.FALSE;
			}
			if (matchLiteral("null")) {
				return null;
			}
			throw error("Unexpected character: " + c);
		}

		private Map<String, Object> parseObject() throws IOException {
			expect('{');
			LinkedHashMap<String, Object> map = new LinkedHashMap<>();
			skipWhitespace();
			if (peek() == '}') {
				index++;
				return map;
			}
			while (true) {
				skipWhitespace();
				String key = parseString();
				skipWhitespace();
				expect(':');
				Object value = parseValue();
				map.put(key, value);
				skipWhitespace();
				char c = peek();
				if (c == ',') {
					index++;
					continue;
				}
				if (c == '}') {
					index++;
					break;
				}
				throw error("Expected ',' or '}' in object");
			}
			return map;
		}

		private List<Object> parseArray() throws IOException {
			expect('[');
			List<Object> list = new ArrayList<>();
			skipWhitespace();
			if (peek() == ']') {
				index++;
				return list;
			}
			while (true) {
				Object value = parseValue();
				list.add(value);
				skipWhitespace();
				char c = peek();
				if (c == ',') {
					index++;
					continue;
				}
				if (c == ']') {
					index++;
					break;
				}
				throw error("Expected ',' or ']' in array");
			}
			return list;
		}

		private String parseString() throws IOException {
			expect('"');
			StringBuilder sb = new StringBuilder();
			while (!isEnd()) {
				char c = input.charAt(index++);
				if (c == '"') {
					return sb.toString();
				}
				if (c == '\\') {
					if (isEnd()) {
						throw error("Invalid escape sequence");
					}
					char escaped = input.charAt(index++);
					switch (escaped) {
					case '"':
					case '\\':
					case '/':
						sb.append(escaped);
						break;
					case 'b':
						sb.append('\b');
						break;
					case 'f':
						sb.append('\f');
						break;
					case 'n':
						sb.append('\n');
						break;
					case 'r':
						sb.append('\r');
						break;
					case 't':
						sb.append('\t');
						break;
					case 'u':
						sb.append(parseUnicode());
						break;
					default:
						throw error("Unknown escape sequence: \\" + escaped);
					}
				} else {
					sb.append(c);
				}
			}
			throw error("Unterminated string");
		}

		private char parseUnicode() throws IOException {
			if (index + 4 > input.length()) {
				throw error("Incomplete unicode escape");
			}
			String hex = input.substring(index, index + 4);
			index += 4;
			try {
				return (char) Integer.parseInt(hex, 16);
			} catch (NumberFormatException ex) {
				throw error("Invalid unicode escape: " + hex);
			}
		}

		private Number parseNumber() throws IOException {
			int start = index;
			if (peek() == '-') {
				index++;
			}
			while (!isEnd() && Character.isDigit(peek())) {
				index++;
			}
			if (!isEnd() && peek() == '.') {
				index++;
				while (!isEnd() && Character.isDigit(peek())) {
					index++;
				}
			}
			if (!isEnd() && (peek() == 'e' || peek() == 'E')) {
				index++;
				if (peek() == '+' || peek() == '-') {
					index++;
				}
				while (!isEnd() && Character.isDigit(peek())) {
					index++;
				}
			}
			String number = input.substring(start, index);
			try {
				if (number.contains(".") || number.contains("e") || number.contains("E")) {
					return Double.parseDouble(number);
				}
				return Long.parseLong(number);
			} catch (NumberFormatException ex) {
				throw error("Invalid number: " + number);
			}
		}

		private boolean matchLiteral(String literal) {
			int end = index + literal.length();
			if (end > input.length()) {
				return false;
			}
			if (!input.regionMatches(index, literal, 0, literal.length())) {
				return false;
			}
			if (end < input.length()) {
				char boundary = input.charAt(end);
				if (!isLiteralBoundary(boundary)) {
					return false;
				}
			}
			index = end;
			return true;
		}

		private boolean isLiteralBoundary(char c) {
			return c == ',' || c == '}' || c == ']' || c == ' ' || c == '\n' || c == '\r' || c == '\t';
		}

		private void skipWhitespace() {
			while (!isEnd()) {
				char c = input.charAt(index);
				if (c == ' ' || c == '\n' || c == '\r' || c == '\t') {
					index++;
				} else {
					break;
				}
			}
		}

		private void expect(char expected) throws IOException {
			if (isEnd() || input.charAt(index) != expected) {
				throw error("Expected '" + expected + "'");
			}
			index++;
		}

		private char peek() throws IOException {
			if (isEnd()) {
				throw error("Unexpected end of input");
			}
			return input.charAt(index);
		}

		private boolean isEnd() {
			return index >= input.length();
		}

		private IOException error(String message) {
			return new IOException(message + " at position " + index);
		}
	}
}