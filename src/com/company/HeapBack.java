package com.company;

import java.util.HashMap;

public class HeapBack {


    private Node max;
    private HashMap<String, Node> table;

    public HeapBack(){
        table = new HashMap<>();
    }

    public Node peek(){
        return max;
    }




    public Node removeMax()
    {
        Node currentMax = max;
        if (currentMax != null) {
            int numberofChildren = currentMax.degree;
            Node child = currentMax.child;
            Node tempRight;

            while (numberofChildren > 0) {
                tempRight = child.right;

                child.left.right = child.right;
                child.right.left = child.left;

                child.left = max;
                child.right = max.right;
                max.right = child;
                child.right.left = child;

                child.parent = null;
                child = tempRight;
                numberofChildren--;

            }


            currentMax.left.right = currentMax.right;
            currentMax.right.left = currentMax.left;

            if (currentMax == currentMax.right) {
                max = null;

            } else {
                max = currentMax.right;
                degreewiseTableMerge();
            }
            table.remove(currentMax.tag.getName());
            return currentMax;
        }
        return null;
    }

    public void insert(Node node)
    {

        if(table.containsKey(node.tag.getName()))
        {
            Node existing = table.get(node.tag.getName());
            int newCount = existing.tag.getCount()+node.tag.getCount();
            increaseKey(existing, newCount);
        }
        else{
            table.put(node.tag.getName(), node);
            if (max != null) {
                node.left = max;
                node.right = max.right;
                max.right = node;
                if ( node.right!=null) {
                    node.right.left = node;
                }
                if ( node.right==null)
                {
                    node.right = max;
                    max.left = node;
                }
                if (node.tag.getCount() > max.tag.getCount()) {
                    max = node;
                }
            } else {
                max = node;

            }

        }

    }


    private void cutChildFromParent(Node child, Node parent)
    {
        parent.degree--;
        child.left.right = child.right;
        child.right.left = child.left;


        if (parent.child == child) {
            parent.child = child.right;
        }

        if (parent.degree == 0) {
            parent.child = null;
        }

        child.left = max;
        child.right = max.right;
        max.right = child;
        child.right.left = child;

        child.parent = null;

        child.toCut = false;
    }

    private void cascadingCut(Node node)
    {
        Node parent = node.parent;

        if (parent != null) {
            if (!node.toCut) {
                node.toCut = true;
            } else {
                cutChildFromParent(node, parent);
                cascadingCut(parent);
            }
        }
    }

    private void increaseKey(Node node, int value)
    {
        node.tag.setCount(value);
        Node parent = node.parent;
        if ((parent != null) && (node.tag.getCount() > parent.tag.getCount())) {
            cutChildFromParent(node, parent);
            cascadingCut(parent);
        }

        if (node.tag.getCount() > max.tag.getCount()) {
            max = node;
        }
    }



    private void degreewiseTableMerge()
    {
        Node[] degreeTable =
                new Node[Util.NODE_TABLE_SIZE];

        int numRoots = 0;
        Node current = max;

        if (current != null) {
            numRoots++;
            current = current.right;

            while (current != max) {
                numRoots++;
                current = current.right;
            }
        }

        while (numRoots > 0) {

            int d = current.degree;
            Node next = current.right;

            for (;;) {
                Node child = degreeTable[d];
                if (child == null) {
                    break;
                }

                if (current.tag.getCount() < child.tag.getCount()) {
                    Node temp = child;
                    child = current;
                    current = temp;
                }

                makeLeftChildOfRight(child, current);

                degreeTable[d] = null;
                d++;
            }

            degreeTable[d] = current;

            current = next;
            numRoots--;
        }

        max = null;

        for (int i = 0; i < Util.NODE_TABLE_SIZE; i++) {
            Node y = degreeTable[i];
            if (y == null) {
                continue;
            }

            if(max == null){
                max = y;
            }
            else {
                y.left.right = y.right;
                y.right.left = y.left;

                y.left = max;
                y.right = max.right;
                max.right = y;
                y.right.left = y;

                if (y.tag.getCount() > max.tag.getCount()) {
                    max = y;

                }
            }

        
        }
    }

    private void makeLeftChildOfRight(Node child, Node parent)
    {
        child.left.right = child.right;
        child.right.left = child.left;

        child.parent = parent;

        if (parent.child == null) {
            parent.child = child;
            child.right = child;
            child.left = child;
        } else {
            child.left = parent.child;
            child.right = parent.child.right;
            parent.child.right = child;
            child.right.left = child;
        }

        parent.degree++;

        child.toCut = false;
    }


}
