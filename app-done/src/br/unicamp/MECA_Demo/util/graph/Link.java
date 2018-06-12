package br.unicamp.MECA_Demo.util.graph;

/**
 * Created by du on 05/12/16.
 */
public class Link {

    private Node previousNode;

    private Node nextNode;

    private double weigth;

    private boolean visited;

    private String name;

    public Link(String name, double weigth){
        this.setPreviousNode(null);
        this.setNextNode(null);
        this.setWeigth(weigth);
        this.setName(name);
        setVisited(false);
    }

    public Node getPreviousNode() {
        return previousNode;
    }

    public void setVisited(boolean visited){
        this.visited = visited;
    }

    public void setPreviousNode(Node previousNode) {
        this.previousNode = previousNode;
    }

    public Node getNextNode() {
        return nextNode;
    }

    public void setNextNode(Node nextNode) {
        this.nextNode = nextNode;
    }

    public double getWeigth() {
        return weigth;
    }

    public void setWeigth(double weigth) {
        this.weigth = weigth;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isVisited() {
        return visited;
    }
}
