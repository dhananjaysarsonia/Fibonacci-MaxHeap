package com.company;

public class Node {
    Node left, right, child, parent;
    int degree;
    boolean toCut;
    HashTag tag;

    public Node(HashTag tag){


        this.tag = tag;
        this.left = this;
        this.right = this;
        this.degree = 0;
        this.toCut = false;
    }
}
