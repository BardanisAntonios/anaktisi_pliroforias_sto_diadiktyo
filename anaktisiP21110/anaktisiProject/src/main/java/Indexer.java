import org.apache.lucene.document.*;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.file.Paths;

public class Indexer {

    public void buildIndex() {
        try {
            // Πού θα αποθηκευτεί το ευρετήριο στο δίσκο
            Directory dir = FSDirectory.open(Paths.get("MyIndexDir"));
            MyAnalyzer analyzer = new MyAnalyzer();
            IndexWriterConfig config = new IndexWriterConfig(analyzer);
            config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
            IndexWriter writer = new IndexWriter(dir, config);

            // Διάβασμα του CACM αρχείου
            BufferedReader br = new BufferedReader(new FileReader("cacm.all"));
            String line;
            String currentField = "";
            String id = "", title = "", content = "", author = "";

            // Απλή λογική για να διαβάζεις το αρχείο
            while ((line = br.readLine()) != null) {
                if (line.startsWith(".I")) {
                    // Πριν ξεκινήσουμε νέο έγγραφο, αποθηκεύουμε το προηγούμενο (αν υπάρχει)
                    if (!id.isEmpty()) {
                        addDoc(writer, id, title, content, author);
                    }
                    // Reset μεταβλητών για το νέο έγγραφο
                    id = line.substring(3).trim();
                    title = ""; content = ""; author = "";
                    currentField = "ID";
                } else if (line.startsWith(".T")) {
                    currentField = "TITLE";
                } else if (line.startsWith(".W")) {
                    currentField = "CONTENT";
                } else if (line.startsWith(".A")) {
                    currentField = "AUTHOR";
                } else if (line.startsWith(".")) {
                    currentField = "IGNORE"; // Άλλα πεδία που δεν μας νοιάζουν
                } else {
                    // Εδώ διαβάζουμε το κείμενο ανάλογα με το πεδίο
                    if (currentField.equals("TITLE")) title += line + " ";
                    else if (currentField.equals("CONTENT")) content += line + " ";
                    else if (currentField.equals("AUTHOR")) author += line + " ";
                }
            }
            // Προσθήκη του τελευταίου εγγράφου
            if (!id.isEmpty()) addDoc(writer, id, title, content, author);

            writer.close();
            System.out.println("Indexing Done!");

        } catch (Exception e) { e.printStackTrace(); }
    }

    private void addDoc(IndexWriter writer, String id, String title, String content, String author) throws Exception {
        Document doc = new Document();
        doc.add(new StoredField("id", id)); // Αποθηκεύουμε το ID αλλά δεν ψάχνουμε σε αυτό
        doc.add(new TextField("title", title, Field.Store.YES));
        doc.add(new TextField("author", author, Field.Store.YES));
        doc.add(new TextField("content", content, Field.Store.YES)); // Το κύριο κείμενο
        writer.addDocument(doc);
    }

    public static void main(String[] args) {
        new Indexer().buildIndex();
    }
}