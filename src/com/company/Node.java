package com.company;

public class Node {
    Node left;
    Node right;
    Node child;
    Node parent;
    int degree;
    boolean toCut;
    HashTag tag;

    public Node(HashTag tag){
        this.tag = tag;
        this.left = this;
        this.right = this;
        this.toCut = false;
        this.degree = 0;
    }
}
