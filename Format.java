import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Formats input text file to be smaller than an input length restriction such that the
 * penalty of (l-lineLength)^3 is minimized. DP solution.
 * 
 * @author Nick McGeveran 
 */
public class Format {
   

    /**
     * Reads a text file and splits words into list
     * 
     * @param filename file to read
     * @return arrayList of words in file
     */
    protected static List<String> readFile(String filename){
        List<String> al = new ArrayList<String>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null){
                String[] wordArray = line.split("\\s+");
                for (String word : wordArray){
                    al.add(word);
                }
            }
        } catch (IOException e) {
            System.err.println("Error: " + e);
        }
        return al;
    }

    /**
     * Writes to results file. The first line is the penalty of the entire text, and the
     * remaining lines are the formatted text
     * 
     * @param penalty the penalty to be printed
     * @param breakpoints list of last line break at every index 
     * @param words arraylist of words 
     * @param outfile file to write to
     */
    protected static void writeFile(int penalty, int[] breakpoints, List<String> words, String outfile){
        List<Integer> bList = new ArrayList<Integer>();
        int reverse = breakpoints.length - 1;
       //adds the places from breakpoint list where line breaks to a new list
        while (reverse > 0){
            bList.add(breakpoints[reverse]);
            reverse = breakpoints[reverse];
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outfile))) {
            //write the penalty
            writer.write(Integer.toString(penalty));
            writer.newLine();
            
            //start with first non-zero linebreak
            int bpoint = bList.size() - 2;
            writer.write(words.get(0));
            
            //write words to file. break line when necessary, otherwise add a space
            for (int i = 1; i < words.size(); i++){
                if (bpoint >= 0 && i == bList.get(bpoint)) {
                    writer.newLine();
                    bpoint--;
                } else {
                    writer.write(" ");
                }
                writer.write(words.get(i));
            }
        } catch (IOException e) {
            System.err.println("Error: " + e);
        }
    
    }

    //Maybe change how this is done -- this is poor time complexity to repeatedly call in the for loop
    /**
     * calculates the total length of the words we have considered
     * 
     * @param strings
     * @param start
     * @param end
     * @return
     */
    protected static int findLength (List<String> strings, int start, int end){
        int result = 0;
        while (start <= end){
            result += strings.get(start).length() + 1;
            start++;
        }
        result -= 1;
        return result;
    }

    //helper func. to calculate j (e.g. amount of words that can fit on line)
    protected static int findJ (List<String> strings, int i, int l){
        int result = -1;
        int count = 0;
        int whitespace = -1;
        //need to fix condition of loop for certain cases
        while (count + whitespace + strings.get(i).length() < l){
            count += strings.get(i).length();
            whitespace++;
            if (count + whitespace <= l){
                result = i;
            }
            i--;
        }
        return result;
    }



    public static void main(String[] args) {
        //takes in array of words and length of line L
        int l = Integer.parseInt(args[0]);
        String infile = args[1];
        
        List<String> words = readFile(infile);

        //function
        int[] penalty = new int[words.size() + 1];
        int[] breakpoint = new int[words.size() + 1];

        penalty[0] = 0;
        breakpoint[0] = 0;

        for (int i = 0; i < words.size(); i++) {
            int curLength = findLength(words, 0, i);
            if (curLength <= l){
                penalty[i+1] = (int) Math.pow(l - curLength, 3);
                breakpoint[i+1] = 0;              
            } else {
                int min = Integer.MAX_VALUE;
                int breakIndex = -1;
                int j = findJ(words, i, l);
                for ( ; j <= i; j++){
                    int curPenalty = penalty[j] + (int) Math.pow(l - findLength(words, j, i), 3);
                    if (curPenalty < min){
                        min = curPenalty;
                        breakIndex = j;
                    }
                }
                breakpoint[i+1] = breakIndex;
                penalty[i+1] = min; 
                }
            }

        writeFile(penalty[penalty.length - 1], breakpoint, words, "/Users/nick/cs140/results.txt"); 
        
        }     
    }


