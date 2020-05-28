package nfkeyboard.keyboards.models;

public class Node {

    private char data = ' ';
    private boolean isEnd = false; // end of a word?
    private nfkeyboard.keyboards.models.Node left = null, equal = null, right = null;
    private String frequency = null;

    public Node(char data) {
        this.data = data;
    }

    public char getData() {
        return data;
    }

    public void setData(char data) {
        this.data = data;
    }

    public boolean getIsEnd() {
        return isEnd;
    }

    public void setIsEnd(boolean end) {
        isEnd = end;
    }

    public Node getLeft() {
        return left;
    }

    public void setLeft(nfkeyboard.keyboards.models.Node left) {
        this.left = left;
    }

    public nfkeyboard.keyboards.models.Node getEqual() {
        return equal;
    }

    public void setEqual(nfkeyboard.keyboards.models.Node equal) {
        this.equal = equal;
    }

    public nfkeyboard.keyboards.models.Node getRight() {
        return right;
    }

    public void setRight(nfkeyboard.keyboards.models.Node right) {
        this.right = right;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }
}
