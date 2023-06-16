package com.example.nycschool;

import java.util.Optional;

/**
 * POJO for SAT data and scores for a school. The getters in this class will return Optional ints
 * because it's possible that the data set did not contain an int.
 */
public class SchoolSatData {

    private int numTestTakers;
    private int avgReadingScore;
    private int avgMathScore;
    private int avgWritingScore;

    SchoolSatData() {
        this.numTestTakers = numTestTakers;
        this.avgReadingScore = avgReadingScore;
        this.avgMathScore = avgMathScore;
        this.avgWritingScore = avgWritingScore;
    }

    public void setNumTestTakers(int numTestTakers) {
        this.numTestTakers = numTestTakers;
    }

    public void setAvgReadingScore(int avgReadingScore) {
        this.avgReadingScore = avgReadingScore;
    }

    public void setAvgMathScore(int avgMathScore) {
        this.avgMathScore = avgMathScore;
    }

    public void setAvgWritingScore(int avgWritingScore) {
        this.avgWritingScore = avgWritingScore;
    }

    public Optional<Integer> getNumTestTakers() {
        return Optional.ofNullable(numTestTakers);
    }

    public Optional<Integer> getAvgReadingScore() {
        return Optional.of(avgReadingScore);
    }

    public Optional<Integer> getAvgMathScore() {
        return Optional.of(avgMathScore);
    }

    public Optional<Integer> getAvgWritingScore() {
        return Optional.of(avgWritingScore);
    }
}
