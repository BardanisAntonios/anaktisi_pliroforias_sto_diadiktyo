import javax.swing.*;
import java.awt.*;
import java.nio.file.Paths;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.search.*;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.queryparser.classic.QueryParser;
// ... (άλλα imports που χρειάζονται για Highlighter)

public class SearchApp extends JFrame {

    public SearchApp() {
        // Στήσιμο γραφικού περιβάλλοντος
        setTitle("Lucene Search Engine");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JTextField searchField = new JTextField(30);
        JButton searchButton = new JButton("Search");
        JTextArea resultsArea = new JTextArea();

        JPanel panel = new JPanel();
        panel.add(searchField);
        panel.add(searchButton);
        add(panel, BorderLayout.NORTH);
        add(new JScrollPane(resultsArea), BorderLayout.CENTER);

        searchButton.addActionListener(e -> {
            String queryText = searchField.getText();
            try {
                resultsArea.setText(search(queryText));
            } catch (Exception ex) { ex.printStackTrace(); }
        });

        setVisible(true);
    }

    public String search(String text) throws Exception {
        // Άνοιγμα του Index
        DirectoryReader reader = DirectoryReader.open(FSDirectory.open(Paths.get("MyIndexDir")));
        IndexSearcher searcher = new IndexSearcher(reader);
        MyAnalyzer analyzer = new MyAnalyzer();

        // Parsing του ερωτήματος (ψάχνουμε στο content)
        QueryParser parser = new QueryParser("content", analyzer);
        Query query = parser.parse(text);

        // Αναζήτηση Top 10
        TopDocs results = searcher.search(query, 10);
        ScoreDoc[] hits = results.scoreDocs;

        StringBuilder sb = new StringBuilder();
        for(ScoreDoc hit : hits) {
            int docId = hit.doc;
            org.apache.lucene.document.Document doc = searcher.doc(docId);
            sb.append("ID: ").append(doc.get("id")).append("\n");
            sb.append("Title: ").append(doc.get("title")).append("\n");
            sb.append("Score: ").append(hit.score).append("\n");
            sb.append("----------------\n");
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        new SearchApp();
    }
}