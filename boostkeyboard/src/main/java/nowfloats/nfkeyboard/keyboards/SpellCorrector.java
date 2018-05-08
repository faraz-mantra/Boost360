package nowfloats.nfkeyboard.keyboards;

import android.content.Context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import nowfloats.nfkeyboard.keyboards.models.Node;
import nowfloats.nfkeyboard.keyboards.models.PQElement;
import nowfloats.nfkeyboard.keyboards.models.TST;
import nowfloats.nfkeyboard.keyboards.models.WordTree;
import nowfloats.nfkeyboard.models.KeywordModel;

public class SpellCorrector {

    private static Context mContext;

    public SpellCorrector(Context context) {
        this.mContext = context;
        if (tst.getRoot() == null && mContext != null) {
            WordTree.createTree(mContext, tst);
        }
    }


    private static Logger log = Logger.getLogger(SpellCorrector.class.getName());
    private static TST tst = new TST();

    private int EDIT_LIMIT = 2;
    private int SUGGESTED_WORD_LIST_LIMIT = 3;
    private int PRIORITY_QUEUE_SIZE_LIMIT = 3;
    private String inputString = "";
    private PriorityQueue<PQElement> suggestedWords = new PriorityQueue<>(PRIORITY_QUEUE_SIZE_LIMIT);

    // create the ternary search tree and populate with words.

    /**
     * Get the current value of EDIT_LIMIT
     */
    public int getEditLimit() {
        return this.EDIT_LIMIT;
    }

    public void setEditLimit(int edit_limit) {
        if (edit_limit < 0) {
            log.log(Level.WARNING, "Word edit limit cannot be negative.");
            return;
        }
        this.EDIT_LIMIT = edit_limit;
    }

    /**
     * return the current SUGGESTED_WORD_LIST_LIMIT value as integer
     */
    public int getSuggestedWordListLimit() {
        return this.SUGGESTED_WORD_LIST_LIMIT;
    }

    /**
     * Set the max number of suggested words in output. Default is 10.
     *
     * @param word_list_limit
     */
    public void setSuggestedWordListLimit(int word_list_limit) {
        if (word_list_limit <= 0) {
            log.log(Level.WARNING, "word list limit cannot be less than 1.");
            return;
        }
        this.SUGGESTED_WORD_LIST_LIMIT = word_list_limit;
    }

    /**
     * Returns a linkedHashMap where key is the suggested word and value is its
     * Levenshtein Distance from input word. Result is sorted ascending by edit
     * distance and then by its frequency of occurrence in the language.
     *
     * @param str
     * @return
     * @throws IllegalArgumentException
     */
    public ArrayList<KeywordModel> correct(String str) throws IllegalArgumentException {
        suggestedWords.clear();
        if (str == null || str.equals("")) {
            log.log(Level.FINE, "Input word is empty.");
            //throw new IllegalArgumentException("Input string is blank.");
        }
        inputString = str;
        traverse(tst.getRoot(), "");

        // adding words to linkedHashMap for output.
        LinkedHashMap<String, Integer> outputMap = new LinkedHashMap<String, Integer>();
        for (int i = 0; suggestedWords != null && suggestedWords.isEmpty() == false && i < SUGGESTED_WORD_LIST_LIMIT; i++) {
            PQElement element = suggestedWords.poll();
            if (element != null) {
                outputMap.put(element.getWord(), element.getDistance());
            }
        }
        Set<Map.Entry<String, Integer>> set = outputMap.entrySet();
        List<Map.Entry<String, Integer>> list = new ArrayList<>(set);
        Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                return (o2.getValue()).compareTo(o1.getValue());
            }
        });
        HashMap sortedHashMap = new LinkedHashMap();
        for (Iterator it = list.iterator(); it.hasNext();) {
            Map.Entry entry = (Map.Entry) it.next();
            sortedHashMap.put(entry.getKey(), entry.getValue());
        }
        ArrayList<KeywordModel> models = new ArrayList<>();
        Set set2 = sortedHashMap.entrySet();
        Iterator iterator2 = set2.iterator();
        while(iterator2.hasNext()) {
            Map.Entry me2 = (Map.Entry)iterator2.next();
            KeywordModel model = new KeywordModel();
            model.setWord(me2.getKey() + "");
            model.setType(KeywordModel.CORRECTED_WORD);
            models.add(model);
        }
       /* List<String> results = new ArrayList<>(outputMap.keySet());
        for (String result : results) {
            KeywordModel model = new KeywordModel();
            model.setWord(result.trim());
            model.setType(KeywordModel.CORRECTED_WORD);
            models.add(model);
        }*/

        return models;
    }

    private void traverse(Node root, String str) {
        if (root == null)
            return;

        int distance = getEditDistance(inputString, str + root.getData());
        // skip traversing the nodes below which distance is grater than
        // EDIT_LIMIT.
        if ((str.length() < inputString.length())
                && (getEditDistance(str, inputString.substring(0, str.length() + 1)) > EDIT_LIMIT)) {
            return;
        } else if (str.length() > inputString.length() + EDIT_LIMIT) {
            return;
        } else if (Math.abs(str.length() - inputString.length()) <= EDIT_LIMIT && distance > EDIT_LIMIT) {
            return;
        }

        // recursively traverse through the nodes for words
        try {
            if (root != null) {
                traverse(root.getLeft(), str);
                if (root.getIsEnd() == true && distance <= EDIT_LIMIT) {
                    PQElement element = new PQElement(str + root.getData(), distance, root.getFrequency());
                    if (element != null && suggestedWords != null) {
                        suggestedWords.add(element);
                    }
                }
                traverse(root.getEqual(), str + root.getData());
                traverse(root.getRight(), str);
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    // Levenshtein distance
    private int getEditDistance(String a, String b) {
        int[] costs = new int[b.length() + 1];
        for (int j = 0; j < costs.length; j++)
            costs[j] = j;
        for (int i = 1; i <= a.length(); i++) {
            costs[0] = i;
            int nw = i - 1;
            for (int j = 1; j <= b.length(); j++) {
                int cj = Math.min(1 + Math.min(costs[j], costs[j - 1]),
                        a.charAt(i - 1) == b.charAt(j - 1) ? nw : nw + 1);
                nw = costs[j];
                costs[j] = cj;
            }
        }
        return costs[b.length()];
    }
}
