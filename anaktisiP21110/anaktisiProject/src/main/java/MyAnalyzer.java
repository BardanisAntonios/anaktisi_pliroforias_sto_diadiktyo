import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class MyAnalyzer extends Analyzer {
    private CharArraySet stopWords;

    // Στον constructor διαβάζουμε το αρχείο με τις κοινές λέξεις
    public MyAnalyzer() {
        try {
            List<String> stops = Files.readAllLines(Paths.get("path/to/common_words.txt")); // ΠΡΟΣΟΧΗ ΣΤΟ PATH
            this.stopWords = new CharArraySet(stops, true);
        } catch (IOException e) {
            e.printStackTrace();
            this.stopWords = CharArraySet.EMPTY_SET;
        }
    }

    @Override
    protected TokenStreamComponents createComponents(String fieldName) {
        Tokenizer source = new StandardTokenizer();
        TokenStream result = new LowerCaseFilter(source); // Όλα μικρά
        result = new StopFilter(result, stopWords);       // Αφαίρεση stopwords
        result = new PorterStemFilter(result);            // Stemming (ρίζες λέξεων)
        return new TokenStreamComponents(source, result);
    }
}