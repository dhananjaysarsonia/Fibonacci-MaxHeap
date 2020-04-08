package com.company;

import java.util.HashMap;

public class Heap {
    private Node max;
    //Hash table will help us with increase key. By saving node references to hash we can make node lookup time reduce to o(1)
    private HashMap<String, Node> table;

    private int nodeCount = 0;

    //initializing hash table in the constructor
    public Heap(){
        table = new HashMap<>();
    }
//peek function will allow us to view the current max without popping it.
    public Node peek(){
        return max;
    }

//Logic implemented from the references below
// [1]Thomas H. Cormen, Charles E. Leiserson, Ronald L. Rivest, and Clifford Stein. 2009. Introduction to Algorithms, Third Edition (3rd. ed.). The MIT Press.
// [2] Sartaj Sahni UF Advance Data Structures lectures

    public void insert(Node node)
    {
        //here we first check if the node already exists. if the node already exists, then it will increase the value by the value passed in the method.
        //this logic is implemented with accordance to the problem statement.
        if(table.containsKey(node.tag.getName()))
        {
            //if node exists then find the new value and call increase key on the node
            Node existing = table.get(node.tag.getName());
            int newCount = existing.tag.getCount()+node.tag.getCount();
            increaseKey(existing, newCount);
        }
        else{
            //otherwise put the value in the table
            table.put(node.tag.getName(), node);
            if (max != null) {
                //if max != null then we will meld the node directly beside the maximum node
                //node.left will point to max
                node.left = max;
                //node.right will point to max right
                node.right = max.right;
                //max.right will point to the node
                max.right = node;
                //if a node in the right exists, then it's left will point to our new node
                //as we are inserting in between
                if ( node.right!=null) {
                    node.right.left = node;
                }
                //if the node.right == null, then we will circle the node to the max itself
                if ( node.right==null)
                {
                    node.right = max;
                    max.left = node;
                }
                //check if the new node has a value greater than the current maxnode
                if (node.tag.getCount() > max.tag.getCount()) {
                    //update max if the new node is greater than the max
                    max = node;
                }
            } else {
                //set max directly to node if there was no max
                //that is tree was empty
                max = node;
            }
            nodeCount++;

        }

    }

    //important part, this is where a lot of magic happens
    public Node removeMax()
    {
        //save the current max in a variable. as this will be removed and popped
        Node currentMax = max;
        //if it is not null then proceed otherwise we can return null and stop
        //as there will be no nodes in the tree if the tree is empty
        if (currentMax != null) {
            //get the degree of the node
            int numberofChildren = currentMax.degree;
            //get the child of the node
            Node child = currentMax.child;
            Node tempRight;
            //for all childeren we need to push them beside the max node
            //we are sending all the children of the max node and sending it at root level
            for(; numberofChildren > 0; numberofChildren--){
                //save the current right of the child
                tempRight = child.right;
                //as the child will be removed, point the reference of the right of the child to the left of child
                child.left.right = child.right;
                //similarly do the reverse for the left of the child
                child.right.left = child.left;

                //point the child left to max, thus sending it to root level
                child.left = max;
                //child will be inserted between max and the right of the max
                child.right = max.right;
                max.right = child;
                child.right.left = child;

                //set child parent to null as it now the root node
                child.parent = null;
                //go the next childere
                child = tempRight;
            }
            //set the left and right of the current max point to each other, so that current max can be removed
            currentMax.left.right = currentMax.right;
            currentMax.right.left = currentMax.left;
            //if current max right == current max that means there were only one node, we can set max to null
            if (currentMax == currentMax.right) {
                max = null;

            } else {
                //else we temporarily point to a new max
                max = currentMax.right;
                //we will degree wise merge the tree.
                degreewiseTableMerge();
            }
            //remove the node from the hash table
            table.remove(currentMax.tag.getName());
            nodeCount--;
            return currentMax;
        }

        return null;
    }



    private void cutChildFromParent(Node child, Node parent)
    {
        //here we will cut child from parent
        //this is a helper function which simply cuts the child
        //it does not perform cascading cut. or cascading cut will follow this
        //this function is called by our cascading cut
        //we first reduce the degree
        parent.degree--;
        //point the left and right of the child to each other so that child can be removed
        child.left.right = child.right;
        child.right.left = child.left;
        //if the the child was the direct child of the parent, and then we point the child pointer from
        //parent to the right of the child
        //I know java doesn't use pointers and it's called references in java, but for me it is more natural to use the term while explaining the logic
        if (parent.child == child) {
            parent.child = child.right;
        }
        //if parent degree is now zero then we can simply add null to the child pointer of parent
        if (parent.degree == 0) {
            parent.child = null;
        }
        //we are pushing the child to root here
        //so point the left of the child to the max
        child.left = max;
        //point the right of the child to the right of the max
        //this is to not break the chain and smoothly insert the node between the chain
        child.right = max.right;
        //then point right of the max to the child
        max.right = child;
        //we also point the left of the right node(to the child) to the child now.
        //I know it's confusing but it's a doubly linkedlist, so we need the pointers to point back to the node
        child.right.left = child;
        //set the parent of the child to null as it the root node now
        child.parent = null;
        //reset the child cut value
        child.toCut = false;
    }

    private void cascadingCut(Node node)
    {
        //save the parent
        Node parent = node.parent;
        //if parent is null then we don't need to cut, and this can be stopped right here
        if (parent != null) {
            //if the node is marked then we need to remove it
            if(node.toCut){
                //remove the node from parent
                cutChildFromParent(node, parent);
                //perform cascading cut again on parent to check marked parents and remove them
                cascadingCut(parent);
            }
            else {
                //mark the node if it was unmarked before
                node.toCut = true;
            }
        }
    }

    private void increaseKey(Node node, int value)
    {
        //first set the new key
        node.tag.setCount(value);
        //save the parent
        Node parent = node.parent;
        //check if the new key is greater than the parent and parent is not null
        if ((parent != null) && (node.tag.getCount() > parent.tag.getCount())) {
            //if it is greater than cut the child, and perfrom cascade cut on the parent
            cutChildFromParent(node, parent);
            cascadingCut(parent);
        }
        //check if the increased key is greater than the max.
        if (node.tag.getCount() > max.tag.getCount()) {
            //if it is greater than the max then update the key
            max = node;
        }
    }



    private void degreewiseTableMerge()
    {

        //degree wise merge requires a table where we need to store if the there is an existing node at root leve
        //for the degree.
        //we are creating a node array here and using indexes as the degree of the node
        //if the value at a particular index is null, that means that we don't have any other node of the same degree
        //if there is a node at the index, that means we need to merge the nodes
        //this process will be repeated
        //The node array is initialized to a default table size of 50 in this code, it will be better if a better algorithm is implemented to aprroximate
        //the max degree of the node

        Node[] degreeMergeTable =
                new Node[calculateTableSize()];
        //we are merging the nodes in two phases
        //first we merge the nodes which have the common degree and save them in the degree table
        //then we will iterate through the degree table and connect them to each other

        //merge the common degree at root level
        mergePhaseOne(degreeMergeTable);
        //iterate through degree table and join the nodes together in a doubly linkedlist.
        mergePhaseTwo(degreeMergeTable);

    }
    private int calculateTableSize(){
//        Math.floor(Math.log(nodeCount)*(1/(1+Math.sqrt(5))))

         return ((int)Math.floor(Math.log(nodeCount) * (1.0 / Math.log((1.0 + Math.sqrt(5.0)) / 2.0)))) + 1;
    }

    public void mergePhaseOne(Node[] degreeMergeTable){
        Node current = max;
        int countAtRoot = 0;
        //here we count the number of nodes at the root level

        if (current != null) {
            //we first increment as there will be one node atleast as the code enters in the block
            countAtRoot++;
            //set current to the right
            current = current.right;
            //cycle though the doubly linked list and count the number of nodes until it cycles back to the max node
            while (current != max) {
                countAtRoot++;
                current = current.right;
            }
        }


        for(;countAtRoot > 0; countAtRoot--){
            int d = current.degree;
            //get the degree of the current node
            Node next = current.right;
            while (true) {
                //check if there is an existing node of the degree
                Node child = degreeMergeTable[d];
                if (child == null) {
                    //if there is no such node then proceed to the other childeren
                    break;
                }

                //if there exists, then we check which node has greater count, current one or the one existing in the table
                if (current.tag.getCount() < child.tag.getCount()) {
                    //if the one existing in the table has the greater count then we exchange the node
                    Node temp = child;
                    child = current;
                    current = temp;
                }
                //we merge the nodes by making the node with lower count child of the one with higher count
                makeLeftChildOfRight(child, current);
                //we set the table at the older degree as null, as the new merged node will have a new degree
                degreeMergeTable[d] = null;
                //we increment the degree, and check
                d++;
            }

            degreeMergeTable[d] = current;


            current = next;

        }

    }
    public void mergePhaseTwo(Node[] degreeMergeTable){

        max = null;

        //here we iterate every degree and check if there is a node in the table
        //against the degree
        for (int i = 0; i < calculateTableSize(); i++) {
            Node selectedNode = degreeMergeTable[i];
            //if there is no node, then we will can continue the loop
            if (selectedNode != null) {
                //if there is node, then we will check if max is null, if max is null, we will update the max with node
                if(max == null){
                    max = selectedNode;
                }
                else {
                    //if there is max, then we will out the node on right of the max node
                    selectedNode.left.right = selectedNode.right;
                    selectedNode.right.left = selectedNode.left;
                    //we are smoothly inserting and maintaining the right and left pointers
                    //so that we do not break the circular doubly linkedlist
                    selectedNode.left = max;
                    selectedNode.right = max.right;
                    max.right = selectedNode;
                    selectedNode.right.left = selectedNode;
                    //we update the max, if the  count of the current selected node in the table is greater the max node
                    if (selectedNode.tag.getCount() > max.tag.getCount()) {
                        max = selectedNode;

                    }
                }
            }
        }

    }

    private void makeLeftChildOfRight(Node child, Node parent)
    {
        //here we make attach the node to a new new parent node
        //first smoothly remove the child without breaking the doubly linkedlist at the child's level
        child.left.right = child.right;
        child.right.left = child.left;

        //set the parent pointer of the child to the new parent
        child.parent = parent;
        //if there were no children of the parent before,
        //then we can simply add the children to the parent
        if (parent.child == null) {
            parent.child = child;
            child.right = child;
            child.left = child;
        } else {
            //otherwise we push the node to the right of the existing child node of the parent node
            child.left = parent.child;
            child.right = parent.child.right;
            parent.child.right = child;
            child.right.left = child;
        }
        //we increment the degree of the parent
        parent.degree++;
        //unmark the child node as it has got a new parent now.
        child.toCut = false;
    }


}
