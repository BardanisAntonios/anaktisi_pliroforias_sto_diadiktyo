# Information Retrieval System with Apache Lucene

This repository contains the implementation of a complete Information Retrieval System using the **Apache Lucene** library[cite: 1]. It was developed as part of the assignment for the "Information Retrieval and Web Search" course at the Department of Informatics, University of Piraeus[cite: 1].

**Author:** Antonios Bardanis (Registration Number: P21110)[cite: 1]

## 🛠️ Technologies
* **Programming Language:** Java[cite: 1]
* **IR Library:** Apache Lucene (Version 9.8.0)[cite: 1]
* **Graphical User Interface:** Java Swing[cite: 1]

## ✨ Key Features

### 1. Text Preprocessing (Analyzer)
For text analysis, a custom `MyAnalyzer` class was created, which performs the following steps[cite: 1]:
* **Tokenization:** Splitting the text into words (tokens)[cite: 1].
* **Lowercasing:** Converting all characters to lowercase[cite: 1].
* **Stopwords Removal:** Removing common words (e.g., articles, prepositions) based on the `common_words.txt` file[cite: 1].
* **Stemming:** Applying the Porter Stemmer algorithm to extract the root of the words (e.g., "computing" -> "comput")[cite: 1].

### 2. Indexing
The `Indexer` class handles reading and indexing the **CACM** document collection (`cacm.all` file)[cite: 1]. The parser recognizes the tags `.I` (ID), `.T` (Title), `.W` (Abstract/Content), and `.A` (Author)[cite: 1]. The content field is fully indexed to be available for searching[cite: 1].

### 3. Search and Graphical User Interface (GUI)
User interaction is handled through a desktop application (`SearchApp`), where the user enters keywords[cite: 1]. The results are sorted by relevance (score) and for each result, the following are displayed[cite: 1]:
* The title and ID of the document[cite: 1].
* A text snippet with highlighted search terms using Lucene's `Highlighter` class[cite: 1].

### 4. System Evaluation
The system is evaluated through the `Evaluator` class using a set of 64 natural language queries (`query.text`) and their correct relevance judgments (`qrels.text`)[cite: 1]. For each query, the following metrics are calculated[cite: 1]:
* **Precision:** The percentage of retrieved documents that are relevant[cite: 1].
* **Recall:** The percentage of relevant documents that were retrieved[cite: 1].

The evaluation compares two retrieval models[cite: 1]:
1. **Classic Similarity (Vector Space Model - TF-IDF)**[cite: 1].
2. **BM25 Similarity (Okapi BM25)**[cite: 1].

## 📊 Results & Conclusions
Based on the Precision-Recall analysis, it was concluded that the **Okapi BM25 model generally exhibits better performance** compared to the Classic Model, especially in the top results (low recall)[cite: 1]. This superiority is due to its handling of term frequency saturation and document length normalization, preventing excessive weight from being given to terms that repeat many times in the same text[cite: 1].
