import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Main {

    static class Wierzcholek {

        int czestosc;
        char znak;
        Wierzcholek lewy;
        Wierzcholek prawy;

        Wierzcholek(char znak, int czestosc) {
            this.znak = znak;
            this.czestosc = czestosc;
        }

        Wierzcholek(char c, int czestosc, Wierzcholek lewy, Wierzcholek prawy) {
            this.czestosc = czestosc;
            this.lewy = lewy;
            this.prawy = prawy;
        }

        boolean isLeaf() {
            return lewy == null && prawy == null;
        }

        public String koduj (String slowo) {
            Map<Character, Integer> czestosci;
            Map<Character, String> kody;



            czestosci = new HashMap<>();
            kody = new HashMap<>();
            for (char c : slowo.toCharArray()) {
                czestosci.put(c, czestosci.getOrDefault(c, 0) + 1);
            }

            PriorityQueue<Wierzcholek> kolejka = new PriorityQueue<>();

            for (Map.Entry<Character, Integer> entry : czestosci.entrySet()) {
                kolejka.offer(new Wierzcholek(entry.getKey(), entry.getValue()));
            }

            while (kolejka.size() > 1) {
                Wierzcholek lewy = kolejka.poll();
                Wierzcholek prawy = kolejka.poll();
                Wierzcholek rodzic = new Wierzcholek('\0', lewy.czestosc + prawy.czestosc, lewy,prawy);
                kolejka.offer(rodzic);
            }

            Wierzcholek korzen = kolejka.poll();
            if (korzen == null) {
                return "";
            }

            StringBuilder sb = new StringBuilder();
            for (char c : slowo.toCharArray()) {
                sb.append(kody.get(c));
            }
            return sb.toString();
        }

    }

    public static void main(String[] args) throws FileNotFoundException, IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Wybierz opcję:");
        System.out.println("1 - Wprowadź nowe słowo");
        System.out.println("2 - Skompresuj plik za pomocą metody Huffmana");
        System.out.println("3 - Zdekompresuj plik za pomocą metody Huffmana");
        int wybor = scanner.nextInt();
        scanner.nextLine();

        String slowo = "";

        switch (wybor) {
            case 1:
                System.out.print("Wpisz słowo: ");
                slowo = scanner.nextLine();
                String slowoPosortowane = posortujIWyczysc(slowo);

                System.out.println("Słownik: " + slowoPosortowane + "\n");
                System.out.println("Długość tekstu przed kompresją: " + slowo.length());
                System.out.println("Unikalnych liter: " + zliczUnikatoweLitery(slowo));
                System.out.println("Średnia liczba bitów na znak: " + liczbaBitowNaZnak(slowo));

            case 2:
                File file = new File("do_kompresji.txt");
                Map<Character, Integer> freqMap = new HashMap<>();
                try {
                    Scanner odczyt = new Scanner(file);
                    while (odczyt.hasNextLine()) {
                        String line = odczyt.nextLine();
                        for (int i = 0; i < line.length(); i++) {
                            char c = line.charAt(i);
                            int frequency = freqMap.getOrDefault(c, 0);
                            freqMap.put(c, frequency + 1);
                        }
                    }
                    odczyt.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                System.out.println(freqMap);

                PriorityQueue<Wierzcholek> pq = new PriorityQueue<>(Comparator.comparingInt(Wierzcholek -> Wierzcholek.czestosc));
                for (Map.Entry<Character, Integer> entry : freqMap.entrySet()) {
                    pq.offer(new Wierzcholek(entry.getKey(), entry.getValue()));
                }
                while (pq.size() > 1) {
                    Wierzcholek lewy = pq.poll();
                    Wierzcholek prawy = pq.poll();
                    Wierzcholek parent = new Wierzcholek('\0', lewy.czestosc + prawy.czestosc, lewy, prawy);
                    pq.offer(parent);
                }
                Wierzcholek root = pq.poll();

                Map<Character, String> codes = new HashMap<>();
                generateCodes(root, "", codes);

                try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("kompresja.txt"), StandardCharsets.UTF_8))) {
                    BufferedWriter writer2 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("kody_huffmana.txt"), StandardCharsets.UTF_8));

                    Scanner odczyt = new Scanner(file);
                    while (odczyt.hasNextLine()) {
                        String line = odczyt.nextLine();
                        for (int i = 0; i < line.length(); i++) {
                            char c = line.charAt(i);
                            String code = codes.get(c);
                            writer.write(code);
                        }
                    }
                    odczyt.close();

                    for (Map.Entry<Character, String> entry : codes.entrySet()) {
                        writer2.write(entry.getKey() + ": " + entry.getValue() + System.lineSeparator());
                    }
                    writer2.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case 3:
                System.out.println("Przejdź do klasy DecompressHuffman (w folderze src po lewej stronie)");
                break;
        }
    }

    private static void generateCodes(Wierzcholek root, String code, Map<Character, String> codes) {
        if (root.isLeaf()) {
            codes.put(root.znak, code);
            return;
        }
        generateCodes(root.lewy, code + "0", codes);
        generateCodes(root.prawy, code + "1", codes);
    }

    public static String posortujIWyczysc(String slowo) {
        char[] slowoTablica = slowo.toLowerCase().toCharArray();
        Arrays.sort(slowoTablica);
        return usunDuplikaty(slowoTablica);
    }

    public static String usunDuplikaty(char[] slowoTablica) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < slowoTablica.length; i++) {
            if (i == 0 || slowoTablica[i] != slowoTablica[i - 1]) {
                sb.append(slowoTablica[i]);
            }
        }
        return sb.toString();
    }

    public static int zliczUnikatoweLitery(String slowo) {
        boolean[] litery = new boolean[26];
        int unikatoweLitery = 0;
        slowo = slowo.toLowerCase();
        for (int i = 0; i < slowo.length(); i++) {
            char litera = slowo.charAt(i);
            if (Character.isLetter(litera) && !litery[litera - 'a']) {
                litery[litera - 'a'] = true;
                unikatoweLitery++;
            }
        }
        return unikatoweLitery;
    }

    public static double liczbaBitowNaZnak(String slowo) {
        int dlugoscSlowka = slowo.length();
        int sumaBitow = 0;

        for (int i = 0; i < dlugoscSlowka; i++) {
            char znak = slowo.charAt(i);
            int kodZnaku = (int) znak;
            int liczbaBitow = Integer.bitCount(kodZnaku);
            sumaBitow += liczbaBitow;
        }
        double sredniaLiczbaBitow = (double) sumaBitow / dlugoscSlowka;
        return sredniaLiczbaBitow;
    }
}



