import edu.willamette.cs1.wordle.WordleDictionary;
import edu.willamette.cs1.wordle.WordleGWindow;
//import edu.willamette.cs1.wordle.WriteToFile;
import javax.swing.Timer;
import java.util.*;
import java.awt.Color;


public class Wordle {
    Map <String, String> guesses = new HashMap<String, String>();
    private Timer timer = new Timer(500, e -> colorRow());

    public void run() {
        
        randomWord = WordleDictionary.FIVE_LETTER_WORDS[(int) (Math.random() * (WordleDictionary.FIVE_LETTER_WORDS.length - 1))].toUpperCase();
        gw = new WordleGWindow();
        gw.addEnterListener((s) -> enterAction(s));
    }


/*
 * Called when the user hits the RETURN key or clicks the ENTER button,
 * passing in the string of characters on the current row.
 */

    public void enterAction(String s) {

        if (isValidWord(s)) {

            String hint = getHint(s, randomWord);
            
            int row = gw.getCurrentRow(); 

            guesses.put(s, hint);
            
            for (int col = 0; col < WordleGWindow.N_COLS; col++) {
                char hintChar = hint.charAt(col);
                if (hintChar == '*') {
                    gw.setSquareColor(row, col, WordleGWindow.MISSING_COLOR);
                } else if (Character.isUpperCase(hintChar)) {
                    gw.setSquareColor(row, col, WordleGWindow.CORRECT_COLOR);
                } else {
                    gw.setSquareColor(row, col, WordleGWindow.PRESENT_COLOR);
                }
            }

            for (int i = 0; i < s.length(); i++) {
                char hintChar = hint.charAt(i);
                
                if (Character.isUpperCase(hintChar)) {
                    gw.setKeyColor(s.substring(i, i + 1), WordleGWindow.CORRECT_COLOR);
                } 
                if (gw.getKeyColor(s.substring(i, i + 1)) != WordleGWindow.CORRECT_COLOR && Character.isLowerCase(hintChar)) {
                    gw.setKeyColor(s.substring(i, i + 1), WordleGWindow.PRESENT_COLOR);
                }
                if (gw.getKeyColor(s.substring(i, i + 1)) != WordleGWindow.CORRECT_COLOR && gw.getKeyColor(s.substring(i, i + 1)) != WordleGWindow.PRESENT_COLOR && hintChar == '*') {
                    gw.setKeyColor(s.substring(i, i + 1), WordleGWindow.MISSING_COLOR);
                }
            }
        

    
            if (hint.equals(randomWord)) {
                gw.showMessage("Congratulations! You've guessed the word.");

                // WriteToFile.updateFile(String.valueOf(gw.getCurrentRow()+1));
                // int[] history = new int[6];


                // for (String line : WriteToFile.readFile()) {
                //     history[Integer.parseInt(line)-1] ++;
                // }

                // for (int i=0; i < 6; i++) {
                //     System.out.print(i+1);
                //     System.out.print('\t');
                //     System.out.println(history[i]);

                // }


                for (int i=0; i<WordleGWindow.N_COLS; i++) {
                    timer.start();
                }
            } else {
                int currentRow = gw.getCurrentRow();
                if (currentRow < WordleGWindow.N_ROWS - 1) {
                    gw.setCurrentRow(currentRow + 1);
                    gw.showMessage("Nice try.");
                    
                } else {
                    gw.showMessage("Game over! The correct word was: " + randomWord);
                }
            }
        } else {
            if (s.length() < 5) {
                System.out.println(listOfPossibleWords(guesses, WordleDictionary.FIVE_LETTER_WORDS));
            }

            gw.showMessage("Not in word list");
        }
    }

    private final String[] rainbowColors = {"#e81416", "#ffa500", "#faeb36", "#79c314", "#487de7", "#4b369d", "#70369d"};
    private int currentColorIndex = 0;

    private void colorRow() {

        Color[] colorArray = new Color[rainbowColors.length];
        for (int i = 0; i < rainbowColors.length; i++) {
        colorArray[i] = Color.decode(rainbowColors[i]);
        }
        for (int col = 0; col < WordleGWindow.N_COLS; col++) {
            gw.setSquareColor(gw.getCurrentRow(), col, colorArray[currentColorIndex]);
        }
        currentColorIndex = (currentColorIndex + 1) % colorArray.length;
    }

    private boolean isValidWord(String word) {

        word = word.toLowerCase();

        String[] wordList = WordleDictionary.FIVE_LETTER_WORDS;

        for (int i = 0; i < wordList.length; i++) {
            if (wordList[i].toLowerCase().equals(word)) {
                return true;
            }
        }

        return false;
    }

    public String getHint(String guess, String word) {

        String hint = "*****";
        String check = word;
        char guessedChar;
        char wordChar;

        
        for (int i = 0; i < guess.length(); i++) {
            guessedChar = guess.charAt(i);
            wordChar = word.charAt(i);

            
            if (guessedChar == wordChar) {
                check = check.replaceFirst(String.valueOf(guessedChar), "");

                hint = hint.substring(0,i) + guessedChar + hint.substring(i+1);
            }
        }
        
        for (int j = 0; j < guess.length(); j++) {

            guessedChar = guess.charAt(j);
            wordChar = word.charAt(j);
            


            if (check.contains(String.valueOf(guessedChar))) {

                if (hint.charAt(j) != '*') continue;

                check = check.replaceFirst(String.valueOf(guessedChar), "");
                hint = hint.substring(0,j) + Character.toLowerCase(guessedChar) + hint.substring(j+1);
            }


        }
    
        return hint;

    }

    /**
     * @param guessToClue a Map of guesses (so far) with their associated clues
     * @param dictionary the dictionary used for this Wordle
     * @return a List of all the possible words (given the guesses and clues so far)
     * 
     * example:  
     * guessToClue = {OMENS=**e**, HELMS=*el**, GRASP=G****, LYART=l****}
     * returns the following list of possible words: [glide, glued, guile]
     * 
     */

    public static List<String> listOfPossibleWords(Map<String, String> guessToClue, String[] dictionary) {
        List<String> copyDic = new ArrayList<>(Arrays.asList(dictionary));
        
        List<Character>[] availList = new ArrayList[5];
        boolean[] fixedList = new boolean[5];
        List<Character> needList = new ArrayList<Character>();

        for (int i = 0; i < 5; i++) {
            availList[i] = new ArrayList<Character>();
            for (char j = 'A'; j <= 'Z'; j++)
                availList[i].add(j);
        }

        for (String key : guessToClue.keySet()) {
            for (int i = 0; i < 5; i++) {
                if (Character.isUpperCase(guessToClue.get(key).charAt(i))) { 
                    fixedList[i] = true;
                    availList[i] = new ArrayList<Character>();
                    availList[i].add(Character.valueOf(key.charAt(i)));
                } else if (Character.isLowerCase(guessToClue.get(key).charAt(i))) { 
                    availList[i].remove(Character.valueOf(key.charAt(i)));
                    needList.add(Character.valueOf(key.charAt(i)));
                } else {
                    for (int j = 0; j < 5; j++)
                        if (!fixedList[j])
                            availList[j].remove(Character.valueOf(key.charAt(i)));
                }
            }
        }
        
        outerLoop:
        for (String word : dictionary) {
            
            for (Character c : needList) {
                if (word.indexOf(Character.toLowerCase(c)) < 0) {
                    copyDic.remove(word);
                    continue outerLoop;
                }
            }

            for (int i = 0; i < 5; i++) {
                if (!availList[i].contains(Character.toUpperCase(word.charAt(i)))) {
                    copyDic.remove(word);
                    continue outerLoop;
                }
            }
        }

        return copyDic;
    }

    

 

/* Startup code */

    public static void main(String[] args) {
        new Wordle().run();
    }

/* Private instance variables */

    private WordleGWindow gw;
    private String randomWord;

}
