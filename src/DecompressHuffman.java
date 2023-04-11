import java.io.*;
import java.util.*;

public class DecompressHuffman {

    public static void main(String[] args) throws  IOException {
        File file = new File("kompresja.txt");
        Map<String, Character> codeTable = new HashMap<>();
        try (Scanner scanner = new Scanner(new File("kody_huffmana.txt"))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] tokens = line.split("\\s+");
                codeTable.put(tokens[1], tokens[0].charAt(0));
            }
        }

        StringBuilder compressed = new StringBuilder();
        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                compressed.append(line);
            }
        }

        StringBuilder decompressed = new StringBuilder();
        StringBuilder temp = new StringBuilder();
        for (int i = 0; i < compressed.length(); i++) {
            temp.append(compressed.charAt(i));
            if (codeTable.containsKey(temp.toString())) {
                decompressed.append(codeTable.get(temp.toString()));
                temp.setLength(0);
            }
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("dekompresja.txt"))) {
            writer.write(decompressed.toString());
        }
    }
}


