package paradigmpanic.backend;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Leaderboard {

	private static final int MAX_SIZE = 5;

	private final Path savePath;
	private final List<Player> players = new ArrayList<>();

	public Leaderboard() {
		this(getDefaultPath());
	}

	public Leaderboard(Path customPath) {
		this.savePath = customPath;
		load();
	}

	public synchronized List<Player> getTopPlayers() {
		return Collections.unmodifiableList(new ArrayList<>(players));
	}

	public synchronized void record(Player player) {
		if (player == null) {
			return;
		}
		players.add(player);
		Collections.sort(players);
		while (players.size() > MAX_SIZE) {
			players.remove(players.size() - 1);
		}
		save();
	}

	private void load() {
		if (!Files.exists(savePath)) {
			return;
		}
		try (ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(savePath))) {
			Object obj = ois.readObject();
			if (obj instanceof List<?> list) {
				players.clear();
				for (Object entry : list) {
					if (entry instanceof Player p) {
						players.add(p);
					}
				}
			}
		} catch (IOException | ClassNotFoundException ignored) {
			// treat as empty board
		}
	}

	private void save() {
		try {
			Files.createDirectories(savePath.getParent());
			try (ObjectOutputStream oos = new ObjectOutputStream(Files.newOutputStream(savePath))) {
				oos.writeObject(new ArrayList<>(players));
			}
		} catch (IOException ignored) {
			// persistence best-effort
		}
	}

	private static Path getDefaultPath() {
		Path homeDir = Paths.get(System.getProperty("user.home"), ".paradigmpanic");
		return homeDir.resolve("leaderboard.ser");
	}
}

