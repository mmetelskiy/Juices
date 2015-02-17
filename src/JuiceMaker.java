import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.*;

public class JuiceMaker {
    ArrayList<Set> juices = new ArrayList<Set>();
    LinkedHashSet<String> allIngredients = new LinkedHashSet<String>();

    JuiceMaker(String fileName) {
        scanFile(fileName);
        MyThread thread = new MyThread();
        thread.start();

        ArrayList<String> sortedIngredients = new ArrayList<String>();
        allIngredients
                .stream()
                .sorted((s1, s2) -> s1.compareTo(s2))
                .forEach(ingredient -> sortedIngredients.add(ingredient));

        writeToFile("juice1.out", allIngredients);
        writeToFile("juice2.out", sortedIngredients);
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.exit(1);
        }
        writeToFile("juice3.out", Arrays.asList(thread.count));

        for(int index: bestPermutation) {
            Set<String> juice = juices.get(index);
            for(String ingredient: juice)
                System.out.print(ingredient + " ");
            System.out.println();
        }
        System.out.println(thread.count);
    }

    class MyThread extends Thread {
        int count;
        int[] index;

        @Override
        public void run() {
            index = new int[juices.size()];
            count = juices.size();

            for(int i = 0; i < index.length; ++i)
                index[i] = i;

            permutation(0);
        }

        void permutation(int k) {
            if(k == index.length) {
                int washes = countWashes();
                if(washes < count) {
                    count = washes;
                    bestPermutation = Arrays.copyOf(index, index.length);
                }
            } else {
                for(int i = k; i < index.length; ++i) {
                    int temp = index[i];
                    index[i] = index[k];
                    index[k] = temp;
                    permutation(k + 1);
                    temp = index[i];
                    index[i] = index[k];
                    index[k] = temp;
                }
            }
        }

        int countWashes() {
//            System.out.println(++t);
            int k = 1;
            for(int i = 0; i < index.length - 1; ++i) {
                if (!juices.get(index[i + 1]).containsAll(juices.get(index[i])))
                    ++k;
                if (k == count) return k;
            }
            return k;
        }
    }
    int t = 0;
    int[] bestPermutation;

    void writeToFile(String fileName, Collection<?> collection) {
        try(PrintWriter writer = new PrintWriter(fileName)) {
            collection
                    .stream()
                    .forEach(s -> writer.println(s));
        } catch (FileNotFoundException e) {
        }
    }

    void scanFile(String fileName) {
        Scanner scan = null;
        HashSet<TreeSet<String>> juiceSet = new HashSet<TreeSet<String>>();
        try
        {
            scan = new Scanner(new File(fileName));
        } catch (FileNotFoundException e)
        {
            System.err.println("No file found");
            return;
        }
        while(scan.hasNext()) {
            String[] ingredients = scan.nextLine().split("\\s+");
            if(ingredients[0].length() == 0) break;
            TreeSet<String> juice = new TreeSet<String>();
            for(String ingredient: ingredients) {
                juice.add(ingredient);
                allIngredients.add(ingredient);
            }
            juiceSet.add(juice);
        }
        juices.addAll(juiceSet);
    }
}
