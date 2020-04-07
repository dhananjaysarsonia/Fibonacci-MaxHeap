package com.company;

import java.util.HashMap;

public class Heap {

    private HashMap<String, Node> hashMap = new HashMap<>();
    private Node max;

    public Node peek(){
        return max;
    }

    public boolean insert(Node node){
        if(!hashMap.containsKey(node.tag.getName()))
        {
            if(max != null){
                node.left = max;
                node.right = max.right;
                max.right = node;

                if(node.right != null){
                    node.right.left = node;
                }
                else{
                    node.right = max;
                    max.left = node;

                }
                if(node.tag.getCount() > max.tag.getCount())
                {
                    max = node;
                }
            }
            else{
                max = node;
            }
            return true;

        }
        else
        {
            increaseKey(node);
            return true;
        }

    }

    public void increaseKey(Node node){
        int value = hashMap.get(node.tag.getName()).tag.getCount() + node.tag.getCount();
        //assuming value will be always greater

        node.tag.setCount(value);
        Node parent = node.parent;
        if(parent != null && node.tag.getCount() > parent.tag.getCount()){
            childCut(node, parent);

            //need to cut the parent if the parent is marked true
            cascadingCut(parent);
        }

        //set the key is greater than the max, then set the new max node

        if(node.tag.getCount() > max.tag.getCount()){
            max = node;
        }
    }


    public void childCut(Node child, Node parent){
        child.left.right = child.right;
        child.right.left = child.left;

        //check if the child was direct child of the parent
        if(parent.child == child){
            parent.child = child.right;
        }
        //else let the parent remain as it is
        parent.degree--;

        if(parent.degree == 0){
            parent.child = null;
        }

        //attaching the child with the max node at root level
        child.left = max;
        child.right = max.right;
        max.right = child;
        child.right.left = child;
        child.parent = null;
        child.toCut = false;

    }

    public void cascadingCut(Node node){
        Node parent = node.parent;

        if(parent != null) {
            if (node.toCut) {
                childCut(node, parent);
                cascadingCut(parent);
            } else {
                node.toCut = true;
            }
        }
        //else do nothing
    }

    public void makeFirstChildOfSecond(Node child, Node parent){

        child.left.right = child.right;
        child.right.left = child.left;

        child.parent = parent;
        if(parent.child != null){
         child.left = parent.child;
         child.right = parent.child.right;
         parent.child.right = child;
         child.right.left = child;
        }
        else
        {
            parent.child = child;
            child.right = child;
            child.left = child;

        }

        parent.degree++;
        child.toCut = false;



    }
    public void mergeNodeWithDegree(){

        Node[] degreeManager = new Node[Util.NODE_TABLE_SIZE];

        Node root = max;
        //counting the number of nodes at root;
        int n = 0;
        if(root != null){
            n++;
            root = root.right;
            while(root != max){
                n++;
                root = root.right;
            }
        }

        //Iterating through root nodes;
        for(; n > 0; n--){
            int degree = root.degree;

            Node next = root.right;
            while(true){
                Node current = degreeManager[degree];
                if(current != null) {

                    if(root.degree < current.degree){
                        Node temp = current;
                        current = root;
                        root = temp;
                    }

                    makeFirstChildOfSecond(current, root);
                    degreeManager[degree] = null;
                    degree++;


                }else {
                    break;
                }

            }
            degreeManager[degree] = root;
            root = next;

        }

        //reconfiguring the tree roots
        max = null;
        for(int i = 0; i < Util.NODE_TABLE_SIZE; i++){
            Node current = degreeManager[i];
            if(current == null) {continue;}
            if(max == null) {
                max = current;
            }else{
                current.left.right = current.right;
                current.right.left = current.left;

                current.left = max;
                current.right = max.right;
                max.right = current;
                current.right.left = current;

                if(current.tag.getCount() > root.tag.getCount()){
                    max = current;
                }

            }

        }


    }





public Node removeMax(){
        if(max == null){
            return max; //returning null if there is no nodes in the tree
        }

        Node root = max;
        hashMap.remove(root.tag.getName());

        int nChilderen = root.degree;
        Node child = root.child;
        Node tRight;

        for(; nChilderen > 0; nChilderen --){
            tRight = child.right;

            //removing node
            child.left.right = child.right;
            child.right.left = child.left;

            child.left = max;
            child.right = max.right;
            max.right = child;
            child.right.left = child;

            child.parent = null;
            child = tRight;

        }

        root.left.right = root.right;
        root.right.left = root.left;

        if(root == root.right){
            max = null;
        }else{
            max = root.right;
            mergeNodeWithDegree();
            return root;
        }
        return null;

}





}
