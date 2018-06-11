package nfkeyboard.keyboards.models;

public class PQElement implements Comparable<nowfloats.nfkeyboard.keyboards.models.PQElement> {
    private String word;
    private int editDistance;
    private String frequency;

    public PQElement(String word, int editDistance, String frequency) {
        this.word = word;
        this.editDistance = editDistance;
        this.frequency = frequency;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public int getDistance() {
        return editDistance;
    }

    public void setDistance(int editDistance) {
        this.editDistance = editDistance;
    }

    public String getFrequency() {
        return this.frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public int compareTo(nowfloats.nfkeyboard.keyboards.models.PQElement element) {
        if (element != null && element.getFrequency() != null) {
            if (this.getDistance() > element.getDistance()) {
                return 1;
            } else if (this.getDistance() < element.getDistance()) {
                return -1;
            } else if (this.getFrequency().length() < element.getFrequency().length()) {
                return 1;
            } else if (this.getFrequency().length() > element.getFrequency().length()) {
                return -1;
            }
            return element.getFrequency().compareTo(this.getFrequency());
        }
        return 0;
    }
}
