package main;

import java.awt.*;
import java.util.Random;
import java.util.Scanner;

public class App
{
    private static Point[] centroids;
    private static Point point;
    private static StringBuilder inputMask;
    private static StringBuilder outputMask;
    private static int[] dist;
    private static Random random = new Random();
    private static Scanner scanner;


    private static final String RAM = "signal RAM: ram_type := (0 => std_logic_vector(to_unsigned( ii , 8)),\n" +
            "                         1 => std_logic_vector(to_unsigned( xx1 , 8)),\n" +
            "                         2 => std_logic_vector(to_unsigned( yy1 , 8)),\n" +
            "                         3 => std_logic_vector(to_unsigned( xx2 , 8)),\n" +
            "                         4 => std_logic_vector(to_unsigned( yy2 , 8)),\n" +
            "                         5 => std_logic_vector(to_unsigned( xx3 , 8)),\n" +
            "                         6 => std_logic_vector(to_unsigned( yy3 , 8)),\n" +
            "                         7 => std_logic_vector(to_unsigned( xx4 , 8)),\n" +
            "                         8 => std_logic_vector(to_unsigned( yy4 , 8)),\n" +
            "                         9 => std_logic_vector(to_unsigned( xx5 , 8)),\n" +
            "                         10 => std_logic_vector(to_unsigned( yy5 , 8)),\n" +
            "                         11 => std_logic_vector(to_unsigned( xx6 , 8)),\n" +
            "                         12 => std_logic_vector(to_unsigned( yy6 , 8)),\n" +
            "                         13 => std_logic_vector(to_unsigned( xx7 , 8)),\n" +
            "                         14 => std_logic_vector(to_unsigned( yy7 , 8)),\n" +
            "                         15 => std_logic_vector(to_unsigned( xx8 , 8)),\n" +
            "                         16 => std_logic_vector(to_unsigned( yy8 , 8)),\n" +
            "                         17 => std_logic_vector(to_unsigned( pp1 , 8)),\n" +
            "                         18 => std_logic_vector(to_unsigned( pp2 , 8)),\n" +
            "       others => (others =>'0'));";

    private static final String result = "assert RAM(19) = std_logic_vector(to_unsigned( xx , 8)) report \"TEST FALLITO\" severity failure;";

    public static void main(String[] args)
    {
        scanner = new Scanner(System.in);
        String line = "";

        while (!line.equals("0"))
        {
            System.out.print("Seleziona un'opzione [0] Esci, [1] Genera dati random, [2] Risolvi, [3] Genera RAM: ");
            line = scanner.nextLine();
            switch (line)
            {
                case "1":
                    randGenInputMask();
                    randGenPoint();
                    randGenCentroids();
                    resolve();
                    break;
                case "2":
                    getData();
                    resolve();
                    break;
                case "3":
                    genRam();
                    break;
            }
        }
    }

    private static void getData()
    {
        centroids = new Point[8];
        inputMask = new StringBuilder(8);

        System.out.print("Inserisci l'input mask: ");
        inputMask.append(scanner.nextLine());
        System.out.print("Inserisci le coordinate del punto separate da uno spazio: ");
        String[] coords = scanner.nextLine().split(" ");
        point = new Point(Integer.parseInt(coords[0]), Integer.parseInt(coords[1]));
        for(int i = 0; i < centroids.length; i++)
        {
            System.out.print("Inserisci le coordinate del centroide "+i+" separate da uno spazio: ");
            coords = scanner.nextLine().split(" ");
            centroids[i] = new Point(Integer.parseInt(coords[0]), Integer.parseInt(coords[1]));
        }
    }

    private static void resolve()
    {
        if(centroids == null)randGenCentroids();
        if(point == null)randGenPoint();
        if(inputMask == null)randGenInputMask();

        dist = new int[8];
        int distMin = 512;
        outputMask = new StringBuilder();
        outputMask.delete(0, 8);
        outputMask.append("00000000");
        for(int i = 0; i < centroids.length; i++)
        {
            int centrX = centroids[i].x;
            int centrY = centroids[i].y;
            dist[i] = Math.abs(centrX-point.x)+Math.abs(centrY-point.y);
            if(inputMask.charAt(i) == '1')
            {
                if(dist[i] < distMin)
                {
                    distMin = dist[i];
                    outputMask.delete(0, 8);
                    outputMask.append("00000000");
                    outputMask.setCharAt(i, '1');
                }
                else if(dist[i] == distMin)
                {
                    outputMask.setCharAt(i, '1');
                }
            }
        }

        outputMask.reverse();
        inputMask.reverse();

        System.out.println("=============================================================================");
        System.out.println("=============================================================================");
        System.out.println("Point: \t\t\t("+point.x+", "+point.y+")");
        System.out.print("Centroids: \t\t");
        for(int i = centroids.length-1; i >= 0; i--) System.out.print("("+ centroids[i].x+", "+ centroids[i].y+") ");
        //System.out.println();
        //for(int i = dist.length-1; i >= 0; i--)System.out.print(dist[i]+" ");
        System.out.println();
        System.out.println("Input mask: \t"+inputMask.toString());
        System.out.println("Output mask: \t"+outputMask.toString());
        System.out.println("=============================================================================");
        System.out.println("=============================================================================");
    }

    private static void randGenCentroids()
    {
        centroids = new Point[8];
        for (int i = 0; i < centroids.length; i++) centroids[i] = new Point(random.nextInt(256), random.nextInt(256));
    }

    private static void randGenPoint()
    {
        point = new Point(random.nextInt(256), random.nextInt(256));
    }

    private static void randGenInputMask()
    {
        inputMask = new StringBuilder();
        for(int i = 0; i < 8; i++)
        {
            if(Math.random() > 0.4)inputMask.append('1');
            else inputMask.append('0');
        }
    }

    private static void genRam()
    {
        String ram = RAM;
        String res = result.replace("xx", String.valueOf(toInteger(outputMask.toString())));

        ram = ram.replace("ii", String.valueOf(toInteger(inputMask.toString())));
        ram = ram.replace("pp1", String.valueOf(point.x));
        ram = ram.replace("pp2", String.valueOf(point.y));

        for(int i = 0; i < centroids.length; i++)
        {
            ram = ram.replace("xx"+(i+1), String.valueOf(centroids[i].x));
            ram = ram.replace("yy"+(i+1), String.valueOf(centroids[i].y));
        }

        System.out.println("=============================================================================");
        System.out.println("=============================================================================");
        System.out.println();
        System.out.println(ram);
        System.out.println();
        System.out.println();
        System.out.println(res);
        System.out.println();
        System.out.println("=============================================================================");
        System.out.println("=============================================================================");
    }

    private static int toInteger(String data)
    {
        int sum = 0;
        for(int i = 0; i < data.length(); i++)
        {
            sum += (data.charAt(i) == '0') ? 0 : Math.pow(2, data.length()-(i+1));
        }
        return sum;
    }
}
