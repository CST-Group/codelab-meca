package br.unicamp.MECA_Demo.util.graph;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by du on 05/12/16.
 */
public class Graph {

    private List<Node> roots;

    private List<Node> auxRoots;

    private String name;

    public Graph(String name) {
        setName(name);
        setRoots(new ArrayList<Node>());
        auxRoots = new ArrayList<Node>();
    }

    public void addNode(Node newNode, Link newLink, Node referenceNode) {

        if (getRoots().size() != 0) {

            Node foundNode = null;
            Node existNewNode = null;

            for (int i = 0; i < getRoots().size(); i++) {
                Node root = getRoots().get(i);

                resetVisited(root, new ArrayList<Link>());

                foundNode = findNode(root, referenceNode.getName());

                resetVisited(root, new ArrayList<Link>());

                existNewNode = findNode(root, newNode.getName());

                Node previousNode = foundNode == null ? referenceNode : foundNode;
                newLink.setPreviousNode(previousNode);
                previousNode.addOutgoingLink(newLink);

                Node nextNode = existNewNode == null ? newNode : existNewNode;
                newLink.setNextNode(nextNode);
                nextNode.addIncommingLink(newLink);

                if (foundNode != null) {
                    break;
                }
            }

            if (foundNode == null)
                auxRoots.add(referenceNode);
        } else {

            newLink.setPreviousNode(referenceNode);
            referenceNode.addOutgoingLink(newLink);
            newLink.setNextNode(newNode);

            addRoot(referenceNode);

        }

    }


    public Node findNode(Node currentNode, String referenceNode) {

        Node foundNode = null;

        if (currentNode.getName().equals(referenceNode)) {
            return currentNode;
        } else {

            List<Link> links = currentNode.getOutgoingLinks();

            for (int i = 0; i < links.size(); i++) {
                if (!links.get(i).isVisited()) {
                    links.get(i).setVisited(true);
                    foundNode = findNode(links.get(i).getNextNode(), referenceNode);

                    if(foundNode != null)
                        break;
                }
            }


        }

        return foundNode;
    }


    public void addRoot(Node newRoot) {
        getRoots().add(newRoot);
    }

    public void resetVisited(Node node, List<Link> visitedLink) {
        if (node != null) {

            List<Link> links = node.getOutgoingLinks();
            if (links.size() != 0) {
                for (int i = 0; i < links.size(); i++) {
                    if (!visitedLink.contains(links.get(i))) {
                        links.get(i).setVisited(false);
                        visitedLink.add(links.get(i));
                        resetVisited(links.get(i).getNextNode(), visitedLink);
                    }

                }
            }
        }
    }

    public List<Node> getRoots() {
        return roots;
    }

    private void setRoots(List<Node> roots) {
        this.roots = roots;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
