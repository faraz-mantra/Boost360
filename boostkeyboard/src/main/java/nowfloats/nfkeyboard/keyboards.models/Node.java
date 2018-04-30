package nowfloats.nfkeyboard.keyboards.models;

public class Node {

    private char data = ' ';
    private boolean isEnd = false; // end of a word?
    private Node left = null, equal = null, right = null;
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

    public void setLeft(Node left) {
        this.left = left;
    }

    public Node getEqual() {
        return equal;
    }

    public void setEqual(Node equal) {
        this.equal = equal;
    }

    public Node getRight() {
        return right;
    }

    public void setRight(Node right) {
        this.right = right;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }
}
