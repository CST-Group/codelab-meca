package br.unicamp.MECA_Demo.util.graph;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by du on 05/12/16.
 */
public class Node {

    private String name;
    private Object value;
    private List<Link> outgoingLinks;
    private List<Link> incommingLinks;

    public Node(String name, Object value){
        this.setName(name);
        this.setValue(value);
        setOutgoingLinks(new ArrayList<Link>());
        setIncommingLinks(new ArrayList<Link>());

    }

    public void addOutgoingLink(Link link){
        getOutgoingLinks().add(link);
    }

    public void addOutgoingLinkList(List<Link> vertices){
        getOutgoingLinks().addAll(vertices);
    }

    public void addIncommingLink(Link link){
        getIncommingLinks().add(link);
    }

    public void addIncommingLinkList(List<Link> vertices){
        getOutgoingLinks().addAll(vertices);
    }

    private void setOutgoingLinks(ArrayList<Link> outgoingLinks) {
        this.outgoingLinks = outgoingLinks;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public List<Link> getOutgoingLinks() {
        return outgoingLinks;
    }

    public List<Link> getIncommingLinks() {
        return incommingLinks;
    }

    public void setIncommingLinks(List<Link> incommingLinks) {
        this.incommingLinks = incommingLinks;
    }
}
