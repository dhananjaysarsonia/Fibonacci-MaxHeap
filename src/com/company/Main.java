package com.company;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Pattern;

public class Main {


    public static void main(String[] args) {
        args = new String[1];
        args[0] = "sampleInput.txt";
      //  args[1] = "sampleOutput.txt";
        if(args.length == 0){
            System.out.println("Please provide the name of the input file OR both input and output file");
        }
        try {
            WriterHelper writer;

        boolean isOFileAvailable = args.length == 2;
        if(isOFileAvailable)
        {
             writer = new WriterHelper(true, args[1]);
        }
        else{
             writer = new WriterHelper(false, "");
        }


        File file = new File(args[0]);
        HeapBack heap = new HeapBack();

            Scanner sc = new Scanner(file);
            while (sc.hasNextLine()){
                String line = sc.nextLine();

                if(line.equalsIgnoreCase("STOP")){
                    break;
                }
                else if(line.startsWith("#")){
                    line = line.substring(1);
                    String[] kv = line.split(Pattern.quote(" "));

                   String key = kv[0];
                   int value = Integer.parseInt(kv[1]);
                   HashTag hash = new HashTag();
                   hash.setCount(value);
                   hash.setName(key);
                   Node node = new Node(hash);
                   heap.insert(node);

                }
                else{
                    int num = Integer.parseInt(line);
                    ArrayList<Node> removedNodes = new ArrayList<>();

                    StringBuilder buffer = new StringBuilder();

                     while (heap.peek() != null && num > 0){
                        Node node = heap.removeMax();
                        removedNodes.add(node);
                        buffer.append(node.tag.getName()).append(", ");
                       // System.out.print(node.tag.getName() +" Count: "+ node.tag.getCount() +", ");
                        num--;
                    }
                     buffer.setLength(buffer.length() - 2);
                     writer.write(buffer.toString());

                    for(Node node: removedNodes)
                    {
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
