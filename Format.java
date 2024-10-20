import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Format {
    //command line inputs: L (length of line) and name of file

    //helper func. to read text file & return ArrayList
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

    //helper func. to write overall penalty and formatted string to file
    protected static void writeFile(int penalty, int[] breakpoints, List<String> words, String outfile){
        List<Integer> bList = new ArrayList<Integer>();
        int reverse = breakpoints.length - 1;
        while (reverse > 0){
            bList.add(breakpoints[reverse]);
            reverse = breakpoints[reverse];
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outfile))) {
            writer.write(Integer.toString(penalty));
            writer.newLine();
            int bpoint = bList.size() - 2;
            writer.write(words.get(0));
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

    //helper func. to calculate length in characters of previously calculated lines
    //Does space belong to one before or after? lowkey either
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
        int l = 7;
        List<String> words = new ArrayList<String>();
        words.add("Stop");
        words.add("to");
        words.add("hop");
        words.add("on");
        words.add("pop");
        //words.add("yesterday");
        //words.add("evening");

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
                    //this line might be a problem
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
        System.out.println(Arrays.toString(breakpoint));
        System.out.println(Arrays.toString(penalty));

        writeFile(penalty[penalty.length - 1], breakpoint, words, "/Users/nick/cs140/results.txt");
        
        
        }     
    }


