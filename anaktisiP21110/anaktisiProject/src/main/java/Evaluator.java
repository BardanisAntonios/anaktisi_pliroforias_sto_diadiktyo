import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.FSDirectory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.file.Paths;
import java.util.*;

public class Evaluator {

    // Διαδρομές αρχείων
    String indexDir = "MyIndexDir";
    String qrelsFile = "qrels.text";
    String queryFile = "query.text";

    public static void main(String[] args) {
        try {
            Evaluator eval = new Evaluator();

            // --- ΜΕΡΟΣ 1: Vector Space Model (Classic) ---
            System.out.println("=========================================");
            System.out.println("Τρέξιμο 1: Vector Space Model (Classic)");
            System.out.println("=========================================");
            eval.evaluateSystem(new ClassicSimilarity());

            System.out.println("\n\n");

            // --- ΜΕΡΟΣ 2: Okapi BM25 ---
            System.out.println("=========================================");
            System.out.println("Τρέξιμο 2: BM25 Model");
            System.out.println("=========================================");
            eval.evaluateSystem(new BM25Similarity());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Η κύρια μέθοδος που τρέχει την αξιολόγηση
    public void evaluateSystem(Similarity similarityModel) throws Exception {
        // 1. Φόρτωση QRELS και Queries
        Map<Integer, Set<String>> qrels = loadQrels(qrelsFile);
        Map<Integer, String> queries = loadQueries(queryFile);

        // 2. Προετοιμασία Lucene
        DirectoryReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(indexDir)));
        IndexSearcher searcher = new IndexSearcher(reader);
        searcher.setSimilarity(similarityModel);

        MyAnalyzer analyzer = new MyAnalyzer();
        QueryParser parser = new QueryParser("content", analyzer);

        double sumAveragePrecision = 0;
        int queriesCounted = 0;

        List<Integer> sortedQueryIds = new ArrayList<>(queries.keySet());
        Collections.sort(sortedQueryIds);

        for (Integer qId : sortedQueryIds) {
            String queryText = queries.get(qId);

            if (!qrels.containsKey(qId)) continue;

            Set<String> relevantDocs = qrels.get(qId);

            // Καθαρισμός ερώτησης
            queryText = queryText.replace("/", " ").replace("?", " ").replace("-", " ");

            Query query = parser.parse(QueryParser.escape(queryText));

            TopDocs results = searcher.search(query, 50);
            ScoreDoc[] hits = results.scoreDocs;

            int relevantRetrieved = 0;
            double averagePrecisionSum = 0;

            // Εκτύπωση για την πρώτη ερώτηση (για έλεγχο)
            boolean printDetails = (qId == 1);
            if (printDetails) {
                System.out.println("--- Αποτελέσματα για Query ID: " + qId + " ---");
                System.out.println("Rank\tDocID\tRelevant?\tPrecision\tRecall");
            }

            for (int i = 0; i < hits.length; i++) {
                int rank = i + 1;
                Document doc = searcher.doc(hits[i].doc);
                String docId = doc.get("id");

                boolean isRelevant = relevantDocs.contains(docId);

                if (isRelevant) {
                    relevantRetrieved++;
                }

                double precision = (double) relevantRetrieved / rank;
                double recall = (double) relevantRetrieved / relevantDocs.size();

                if (isRelevant) {
                    averagePrecisionSum += precision;
                }

                if (printDetails) {
                    System.out.println(rank + "\t" + docId + "\t" + isRelevant + "\t"
                            + String.format("%.4f", precision) + "\t"
                            + String.format("%.4f", recall));
                }
            }

            double averagePrecision = (relevantRetrieved > 0) ? averagePrecisionSum / relevantDocs.size() : 0;
            sumAveragePrecision += averagePrecision;
            queriesCounted++;
        }

        System.out.println("--------------------------------------------------");
        System.out.println("Mean Average Precision (MAP): " + (sumAveragePrecision / queriesCounted));
        reader.close();
    }

    // Βοηθητική μέθοδος για qrels.text
    private Map<Integer, Set<String>> loadQrels(String path) throws Exception {
        Map<Integer, Set<String>> map = new HashMap<>();
        BufferedReader br = new BufferedReader(new FileReader(path));
        String line;
        while ((line = br.readLine()) != null) {
            String[] parts = line.trim().split("\\s+");
            if (parts.length < 2) continue;

            Integer qId = Integer.parseInt(parts[0]);
            String docId = parts[1];

            map.putIfAbsent(qId, new HashSet<>());
            map.get(qId).add(docId);
        }
        return map;
    }

    // Βοηθητική μέθοδος για query.text
    private Map<Integer, String> loadQueries(String path) throws Exception {
        Map<Integer, String> map = new HashMap<>();
        BufferedReader br = new BufferedReader(new FileReader(path));
        String line;
        String id = "";
        StringBuilder content = new StringBuilder();

        while ((line = br.readLine()) != null) {
            if (line.startsWith(".I")) {
                if (!id.isEmpty()) {
                    map.put(Integer.parseInt(id), content.toString().trim());
                }
                id = line.substring(3).trim();
                content = new StringBuilder();
            } else if (line.startsWith(".W")) {
                // skip
            } else if (line.startsWith(".")) {
                // skip other tags
            } else {
                content.append(line).append(" ");
            }
        }
        if (!id.isEmpty()) {
            map.put(Integer.parseInt(id), content.toString().trim());
        }
        return map;
    }
}