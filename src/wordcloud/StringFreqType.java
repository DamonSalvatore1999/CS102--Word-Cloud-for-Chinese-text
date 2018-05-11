package wordcloud;

public class StringFreqType {
    String str;
    Integer freq;
    public StringFreqType(String str, Integer freq) {
        this.str = str;
        this.freq = freq;
    }
    public Integer getFreq() { return this.freq; }
    public String getStr() { return this.str; }
}