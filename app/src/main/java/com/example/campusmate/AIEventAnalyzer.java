package com.example.campusmate;

import java.util.*;

public class AIEventAnalyzer {

    public static class Prediction {
        public String category;
        public String result;
        public int score;
        public String reason;

        public Prediction(String category, String result, int score, String reason) {
            this.category = category;
            this.result = result;
            this.score = score;
            this.reason = reason;
        }
    }

    static class Sample {
        String text, category, result;

        Sample(String text, String category, String result) {
            this.text = text;
            this.category = category;
            this.result = result;
        }
    }

    private static final List<Sample> trainingData = Arrays.asList(
            new Sample("yapay zeka semineri yazılım teknoloji eğitim kariyer", "Teknoloji", "Uygun"),
            new Sample("kodlama atölyesi robotik programlama teknoloji", "Teknoloji", "Uygun"),
            new Sample("siber güvenlik konferansı eğitim seminer", "Teknoloji", "Uygun"),

            new Sample("resim sergisi sanat fotoğraf müzik konser", "Sanat", "Uygun"),
            new Sample("açık hava konseri müzik sahne sanat", "Sanat", "Uygun"),
            new Sample("tiyatro gösterisi kültür sanat", "Sanat", "Uygun"),

            new Sample("futbol turnuvası spor basketbol voleybol", "Spor", "Uygun"),
            new Sample("koşu etkinliği spor sağlıklı yaşam", "Spor", "Uygun"),

            new Sample("kavga dövüş şiddet adam dövme", "Riskli", "Uygun Değil"),
            new Sample("bahis alkol zararlı etkinlik", "Riskli", "Uygun Değil"),
            new Sample("şiddet içerikli kavga organizasyonu", "Riskli", "Uygun Değil"),
            new Sample("tanışma partisi öğrenci etkinliği kampüs eğlence", "Sosyal", "Uygun"),
            new Sample("hoşgeldin partisi öğrenci topluluğu müzik", "Sosyal", "Uygun"),
            new Sample("su savaşı eğlence kampüs oyun sosyal", "Sosyal", "Uygun"),
            new Sample("bahar eğlencesi öğrenci festivali", "Sosyal", "Uygun"),
            new Sample("kampüs oyun günü etkinliği", "Sosyal", "Uygun")

    );

    public static Prediction analyze(String title, String description) {
        String text = normalize(title + " " + description);

        String category = predictCategory(text);
        String result = predictResult(text);

        int score = 70;

        // Olumlu / faydalı etkinlik kelimeleri
        String[] positiveWords = {
                "kariyer", "günleri", "seminer", "eğitim", "atölye",
                "yapay", "zeka", "teknoloji", "yazılım", "konferans",
                "kulüp", "tanışma", "öğrenci", "sosyal", "sanat",
                "konser", "müzik", "spor", "turnuva", "sergi",
                "fotoğraf", "tiyatro", "kampüs", "festival"
        };

        // Riskli kelimeler
        String[] riskWords = {
                "kavga", "dövüş", "dövme", "şiddet", "silah",
                "bahis", "alkol", "zararlı", "tehlikeli", "yasak"
        };

        for (String word : positiveWords) {
            if (text.contains(word)) {
                score += 4;
            }
        }

        for (String word : riskWords) {
            if (text.contains(word)) {
                score -= 25;
            }
        }

        if (category.equals("Riskli")) {
            score -= 20;
        }

        if (category.equals("Teknoloji") || category.equals("Sanat") ||
                category.equals("Spor") || category.equals("Sosyal")) {
            score += 5;
        }

        if (score > 100) score = 100;
        if (score < 0) score = 0;

        if (score >= 70) {
            result = "Uygun";
        } else if (score >= 40) {
            result = "Şüpheli";
        } else {
            result = "Uygun Değil";
        }

        String reason;

        if (result.equals("Uygun Değil")) {
            reason = "Model, etkinlik metninde riskli kelimeler ve düşük uygunluk skoru tespit etti.";
        } else if (result.equals("Şüpheli")) {
            reason = "Model, etkinliği orta riskli olarak değerlendirdi; admin kontrolü önerilir.";
        } else {
            reason = "Model, etkinliği öğrenci katılımı için uygun ve faydalı olarak değerlendirdi.";
        }

        return new Prediction(category, result, score, reason);
    }
    private static String predictCategory(String text) {
        return predict(text, true);
    }

    private static String predictResult(String text) {
        return predict(text, false);
    }

    private static String predict(String text, boolean categoryMode) {
        Map<String, Integer> labelCounts = new HashMap<>();
        Map<String, Map<String, Integer>> wordCounts = new HashMap<>();
        Set<String> vocabulary = new HashSet<>();

        for (Sample sample : trainingData) {
            String label = categoryMode ? sample.category : sample.result;
            labelCounts.put(label, labelCounts.getOrDefault(label, 0) + 1);
            wordCounts.putIfAbsent(label, new HashMap<>());

            for (String word : tokenize(sample.text)) {
                vocabulary.add(word);
                Map<String, Integer> map = wordCounts.get(label);
                map.put(word, map.getOrDefault(word, 0) + 1);
            }
        }

        String bestLabel = "";
        double bestScore = Double.NEGATIVE_INFINITY;

        for (String label : labelCounts.keySet()) {
            double score = Math.log(labelCounts.get(label) / (double) trainingData.size());
            Map<String, Integer> map = wordCounts.get(label);

            int totalWords = 0;
            for (int count : map.values()) totalWords += count;

            for (String word : tokenize(text)) {
                int count = map.getOrDefault(word, 0);
                double probability = (count + 1.0) / (totalWords + vocabulary.size());
                score += Math.log(probability);
            }

            if (score > bestScore) {
                bestScore = score;
                bestLabel = label;
            }
        }

        return bestLabel;
    }

    private static List<String> tokenize(String text) {
        return Arrays.asList(normalize(text).split("\\s+"));
    }

    private static String normalize(String text) {
        return text.toLowerCase(Locale.ROOT)
                .replaceAll("[^a-zçğıöşü0-9\\s]", " ")
                .replaceAll("\\s+", " ")
                .trim();
    }

    public static String detectCategory(String title, String description) {
        return analyze(title, description).category;
    }

    public static int calculateScore(String title, String description) {
        return analyze(title, description).score;
    }

    public static String checkSuitability(int score) {
        if (score >= 70) return "Uygun";
        if (score >= 40) return "Şüpheli";
        return "Uygun Değil";
    }

    public static String generateReason(String title, String description) {
        return analyze(title, description).reason;
    }
}