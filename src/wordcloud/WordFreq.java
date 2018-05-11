package wordcloud;


import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.seg.common.Term;

import java.util.*;



public class WordFreq {
    private static final double EXPECT_MODIFIER = 5;
    private static final int WORDS_MAX = 56008;
    private String mCharSet;
    private int mMapSizeRef;
    private String mContent;
    private List<Term> mWords = new LinkedList<>();
    private HashMap<String, Integer> mStringMap;
    private Queue<StringFreqType> mSortedWordQueue;

    private void parseWords() {
        for (Term T : HanLP.segment(mContent)) { mWords.add(T); }
    }

    private void calFreq() {
        parseWords();
        String word;
        for (Term t_word : this.mWords) {
            word = t_word.toString();
            char c = word.charAt(0);

            /**
             * 暂时的过滤方法
             * 新方法 isChinese(char c) 需要实现。
             * isChinese 需要考虑mCharSet
             */
            if (Character.isSpaceChar(c) || Character.isWhitespace(c)) {
                continue;
            }
            if (this.mStringMap.containsKey(word)) {
                Integer f = mStringMap.get(word);
                this.mStringMap.replace(word, ++f);
            } else {
                this.mStringMap.put(word, 1);
            }
        }
        mSortedWordQueue = new PriorityQueue<>(mStringMap.size(), (o1, o2) -> { return o2.getFreq().compareTo(o1.getFreq()); });
        Iterator iter = mStringMap.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<String, Integer> entry = (Map.Entry) iter.next();
            mSortedWordQueue.add(new StringFreqType(entry.getKey(), entry.getValue()));
        }
    }

    public String getContent() { return this.mContent; }
    public void setContent(String content) {
        this.mContent = content;

        /**
         * According to 《现代汉语常用词表》
         * Available
         * https://wenku.baidu.com/view/2bc33b0a581b6bd97f19ea28.html
         *
         *
         * ---
         * 4.3
         * _
         *
         * 本表共收录常用词语56 008个，包括单音节词3 181个，
         * 双音节词语40 351个，三音节词语6 459个，
         * 四音节词语5 855个，五音节和五音节以上词语162个。
         * 表内条目按频级升序排列，频级相同的按汉语拼音音序排列。
         * ---
         *
         * Expectation calculation :
         *
         * Words = 3181 * f1 + 40351 * f2 + 6459 * f3 + 5855 * f4 + 162 * f5
         *       = W
         *
         * Expectation of Length = (3181 * 1 * f1 + 40351 * 2 * f2 + 6459 * 3 * f3 + 5855 * 4 * f4 + 162 * 5 * f5) / W
         *                       = EXPECTED_MODIFIER
         */

        int tmp_size = (int) (content.length() / EXPECT_MODIFIER);
        this.mMapSizeRef = (tmp_size >= WORDS_MAX) ? WORDS_MAX : tmp_size;
        this.mStringMap = new HashMap(content.length() / mMapSizeRef);
    }

    public Queue<StringFreqType> getStringFreqArrList() {
        if (this.mSortedWordQueue == null) { calFreq(); }
        return this.mSortedWordQueue;
    }

    public void setCharset(String charset) {
        this.mCharSet = charset;
    }

    public WordFreq(String content) {
        this.setContent(content);
    }

}
