import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class FileReader {
    public static char[][] readMazeFile(String filePath) throws IOException {
            List<String> lines = Files.readAllLines(Path.of(filePath));

            // Filter out empty lines and clean each line
            lines = lines.stream()
                    .filter(line -> !line.trim().isEmpty())
                    .map(line -> line.replace(" ", "")) // Remove all spaces
                    .toList();

            if (lines.isEmpty()) {
                throw new IOException("Maze file is empty or contains only empty lines");
            }

            // Convert to char[][]
            int rows = lines.size();
            int cols = lines.get(0).length();

            char[][] maze = new char[rows][cols];
            for (int i = 0; i < rows; i++) {
                String line = lines.get(i);
                for (int j = 0; j < line.length() && j < cols; j++) {
                    maze[i][j] = line.charAt(j);
                }
            }

            return maze;
        }
    }
