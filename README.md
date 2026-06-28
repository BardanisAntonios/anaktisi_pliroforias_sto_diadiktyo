# Information Retrieval System with Apache Lucene

This repository contains the implementation of a complete Information Retrieval System using the **Apache Lucene** library. It was developed as part of the assignment for the "Information Retrieval and Web Search" course at the Department of Informatics, University of Piraeus.

**Author:** Antonios Bardanis (Registration Number: P21110)

## 🛠️ Technologies
* **Programming Language:** Java
* **IR Library:** Apache Lucene (Version 9.8.0)
* **Graphical User Interface:** Java Swing

## ✨ Key Features

### 1. Text Preprocessing (Analyzer)
For text analysis, a custom `MyAnalyzer` class was created, which performs the following steps:
* **Tokenization:** Splitting the text into words (tokens).
* **Lowercasing:** Converting all characters to lowercase.
* **Stopwords Removal:** Removing common words (e.g., articles, prepositions) based on the `common_words.txt` file.
* **Stemming:** Applying the Porter Stemmer algorithm to extract the root of the words (e.g., "computing" -> "comput").

### 2. Indexing
The `Indexer` class handles reading and indexing the **CACM** document collection (`cacm.all` file). The parser recognizes the tags `.I` (ID), `.T` (Title), `.W` (Abstract/Content), and `.A` (Author). The content field is fully indexed to be available for searching.

### 3. Search and Graphical User Interface (GUI)
User interaction is handled through a desktop application (`SearchApp`), where the user enters keywords. The results are sorted by relevance (score) and for each result, the following are displayed:
* The title and ID of the document.
* A text snippet with highlighted search terms using Lucene's `Highlighter` class.

### 4. System Evaluation
The system is evaluated through the `Evaluator` class using a set of 64 natural language queries (`query.text`) and their correct relevance judgments (`qrels.text`). For each query, the following metrics are calculated:
* **Precision:** The percentage of retrieved documents that are relevant.
* **Recall:** The percentage of relevant documents that were retrieved.

The evaluation compares two retrieval models:
1. **Classic Similarity (Vector Space Model - TF-IDF)**.
2. **BM25 Similarity (Okapi BM25)**.

## 📊 Results & Conclusions
Based on the Precision-Recall analysis, it was concluded that the **Okapi BM25 model generally exhibits better performance** compared to the Classic Model, especially in the top results (low recall). This superiority is due to its handling of term frequency saturation and document length normalization, preventing excessive weight from being given to terms that repeat many times in the same text.
