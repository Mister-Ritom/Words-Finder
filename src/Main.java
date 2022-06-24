import javax.net.ssl.HttpsURLConnection;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class Main {
    public static ArrayList<String> words = new ArrayList<>();
    public static Random random  =  new Random();
    public static File file  = new File("words.txt");
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String argument;
        if (args.length  < 1) {
            System.out.println("Do you want sentences or words use \"sentences\" for sentence or \"words\" for word");
            argument = sc.nextLine();
        }
        else argument = args[0];
        System.out.println("How many " + argument + " do you want");
        int lines = sc.nextInt();
        long start = System.currentTimeMillis();
        addToArrayList();
        long end = System.currentTimeMillis();
        System.out.println("It took " + (end - start) + " milli Seconds to For the method addToArrayList() to run");
        long start2 = System.currentTimeMillis();
        for (int i = 0; i < lines; i++) {
            if (argument.equalsIgnoreCase("sentences")) {
                System.out.println();
                System.out.println("How many words do you want in your sentences");
                printRandomSentences(sc.nextInt());
            }
            else if (argument.equalsIgnoreCase("words")) {
                printAnswer();
            }
            else System.out.println("use argument sentences for sentence or words for words");
        }
        long end2 = System.currentTimeMillis();
        System.out.println();
        System.out.println("It took " + (end2 - start2) + " milli Seconds to run");
        System.out.println("Or Second: " + ((end2 - start2)/1000));
    }
    public static void printAnswer() {
        int wordLength = random.nextInt(5);
        if (wordLength >= 1) {
            String word =generateRandomWord(wordLength);
            while (!isAWord(word)) {
                word = generateRandomWord(wordLength);
            }
            System.out.println(word + " is a word saving...");
            try {
                saveWord(word);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static String generateRandomWord(int wordLength) {
        int correct = random.nextInt(4);
        int maybeCorrect = random.nextInt(4);
        if (correct == maybeCorrect) {
            System.out.println("Checking the word file for old words");
            return words.get(random.nextInt(words.size()-1));
        }
        StringBuilder sb = new StringBuilder(wordLength);
        for(int i = 0; i < wordLength; i++) { // For each letter in the word
            char tmp = (char) ('a' + random.nextInt('z' - 'a')); // Generate a letter between a and z
            sb.append(tmp); // Add it to the String
            while (!(sb.toString().contains("a")
                    || sb.toString().contains("e")
                    || sb.toString().contains("i")
                    || sb.toString().contains("o")
                    || sb.toString().contains("u"))) {
                char tmp2 = (char) ('a' + random.nextInt('z' - 'a')); // Generate a letter between a and z
                sb.append(tmp2);
            }
        }
        return sb.toString();
    }

    public static boolean isAWord(String word) {
        try{
            URL url = new URL("https://api.dictionaryapi.dev/api/v2/entries/en/"+word);
            HttpsURLConnection connection = (HttpsURLConnection)url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            int code = connection.getResponseCode();
            return code!=404;
        }catch(IOException e)
        {
            e.printStackTrace();
        }
        return false;
    }

    public static void saveWord(String word) throws Exception {
        if (file.exists()) {
            RandomAccessFile raf = new RandomAccessFile("words.txt", "rw");
            long fileLength = raf.length();
            raf.seek(fileLength);
            String toBeWritten = word + "\r\n";
            if (!words.contains(toBeWritten)) {
                words.add(toBeWritten);
                raf.writeBytes(toBeWritten);
            }
            raf.close();
        }

        else {
            if (!file.createNewFile()) {
                System.out.println("Could not create new file");
            }
            saveWord(word);
            System.out.println("Could not find word file trying to create new one...");
        }
    }

    public static void addToArrayList() {
        Thread thread = new Thread(() -> {
            if (file.exists()) {
                try(RandomAccessFile raFile = new RandomAccessFile(file, "rw")) {
                    //Reading each line using the readLine() method
                    if (raFile.length() <=1) {
                        return;
                    }
                    while(raFile.getFilePointer() < raFile.length()) {
                        String[] fileWords = raFile.readLine().split("\r\n");
                        for (String word: fileWords) {
                            if (!words.contains(word)) {
                                words.add(word);
                            }
                        }
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        thread.setDaemon(true);
        thread.start();
    }

    public static void printRandomSentences(int wordsLength) {
        ArrayList<String > sentenceWords = new ArrayList<>();
        while (sentenceWords.size() <=wordsLength) {
            if (!sentenceWords.contains(words.get(random.nextInt(words.size() - 1)) + " "))
                sentenceWords.add(words.get(random.nextInt(words.size() - 1)) + " ");
        }

        for (int i = 0; i <wordsLength; i++) {
            System.out.print(sentenceWords.get(i));
        }

    }

}