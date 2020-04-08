package com.company;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Pattern;

public class hashtagcounter {


    public static void main(String[] args) {
        //diver logic
        //if there are no arguments, we throw an error
        if(args.length == 0){
            System.out.println("Please provide the name of the input file OR both input and output file");
            System.exit(0);
        }

        try {
            //this is our common WriteHelper. If there is an output file provided, it will write into an output file
            //otherwise it will print the output on the screen.
            WriterHelper writer;
            //set the flag if output file is available
            boolean isOFileAvailable = args.length == 2;

            if(isOFileAvailable)
            {
                 writer = new WriterHelper(true, args[1]);
            }
            else{
                 writer = new WriterHelper(false, "");
            }

            //read the input file
            File file = new File(args[0]);
            //create heap
            Heap heap = new Heap();
            //initialize scanner
            Scanner sc = new Scanner(file);
            while (sc.hasNextLine()){
                String line = sc.nextLine();
                //parse the input. stop when STOP is encountered
                if(line.equalsIgnoreCase("STOP")){
                    break;
                }
                else if(line.startsWith("#")){
                    //parse the input if hashtag with count is found
                    line = line.substring(1);
                    String[] kv = line.split(Pattern.quote(" "));
                    String key = kv[0];
                    int value = Integer.parseInt(kv[1]);
                    //create hashtag object
                    HashTag hash = new HashTag();
                    hash.setCount(value);
                    hash.setName(key);
                    //put hashtag in the node
                    Node node = new Node(hash);
                    heap.insert(node);

                }
                else{
                    //otherwise give out top n hashtags
                    int num = Integer.parseInt(line);
                    ArrayList<Node> removedNodes = new ArrayList<>();
                    StringBuilder buffer = new StringBuilder();
                    //remove min until num is 0 or heap is empty
                    while (heap.peek() != null && num > 0){
                        Node node = heap.removeMax();
                        removedNodes.add(node);
                        buffer.append(node.tag.getName()).append(", ");
                           // System.out.print(node.tag.getName() +" Count: "+ node.tag.getCount() +", ");
                        num--;
                    }
                    //remove the comma and space from the last word
                    buffer.setLength(buffer.length() - 2);
                    //write to screen or output depending on our writerHelper flags
                    writer.write(buffer.toString());
                    //we need to reinsert the removed nodes
                    for(Node node: removedNodes)
                    {
                        //create a new tag and node object ans copy the value
                        //then reinsert
                        HashTag tag = new HashTag();
                        tag.setCount(node.tag.getCount());
                        tag.setName(node.tag.getName());
                        Node newNode = new Node(tag);
                        heap.insert(newNode);
                    }
                    System.out.println("");

                }




                }


            writer.close();



        } catch (IOException e) {
            System.out.println("Some error with files occurred");
            e.printStackTrace();
        }

    }
}
