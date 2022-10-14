package com.lab4;

import javax.management.monitor.Monitor;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.management.MonitorInfo;
import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static java.lang.Thread.sleep;

public class Garden {

    Gardener gardener;
    Nature nature;
    MinitorFile minitorFile;
    MinitorOutput minitorOutput;

    ArrayList<ArrayList<Boolean>> garden_field;

    private static final String FILE_NAME = "src\\com\\lab4\\file.txt";

    ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public Garden() {


        gardener = new Gardener();
        nature = new Nature();
        minitorFile = new MinitorFile();
        minitorOutput = new MinitorOutput();

        garden_field = new ArrayList<ArrayList<Boolean>>();
        for (int i = 0; i < 10; i++) {
            garden_field.add(new ArrayList<Boolean>());
            for (int j = 0; j < 10; j++) {
                garden_field.get(i).add(true);
            }
        }

        gardener.start();
        nature.start();
        minitorFile.start();
        minitorOutput.start();
    }

    public void writeToFile(ArrayList<String> list) {
        try {
            FileWriter file_writer = new FileWriter("src\\com\\lab4\\file.txt", true);
            for (String line : list) {
                file_writer.write(line + "\n");
            }
            file_writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    class Gardener extends Thread {
        Gardener(){ }

        public void run(){
            try {
                while (true) {
                    sleep(3000);
                    waterPlants();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        public void waterPlants(){
            lock.writeLock().lock();
            try {
                for (int i = 0; i < 10; i++) {
                    for (int j = 0; j < 10; j++) {
                        garden_field.get(i).set(j, true);
                    }
                }
            } finally {
                lock.writeLock().unlock();
            }
        }
    }



    class Nature extends Thread {
        Nature(){ }

        public void run(){
            try {
                while (true) {
                    sleep(200);
                    int x = (int) (Math.random() * 10);
                    int y = (int) (Math.random() * 10);
                    lock.writeLock().lock();
                    try {
                        garden_field.get(x).set(y, false);
                    } finally {
                        lock.writeLock().unlock();
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    class MinitorFile extends Thread {
        MinitorFile(){ }

        public void run(){
            while (true) {
                try {
                    while (true) {
                        sleep(500);
                        printToFile();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        public void printToFile(){
            lock.writeLock().lock();
            ArrayList<String> list = new ArrayList<String>();
            for (int i = 0; i < 10; i++) {
                String line = "";
                for (int j = 0; j < 10; j++) {
                    if (garden_field.get(i).get(j)) {
                        line += "1 ";
                    } else {
                        line += "0 ";
                    }
                }
                list.add(line);
            }
            list.add("");
            writeToFile(list);
            lock.writeLock().unlock();
        }

    }

    class MinitorOutput extends Thread {
        MinitorOutput(){ }

        public void run(){
            while (true) {
                try {
                    while (true) {
                        sleep(500);
                        print();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        public void print(){
            lock.readLock().lock();
            try {
                for (int i = 0; i < 10; i++) {
                    for (int j = 0; j < 10; j++) {
                        if (garden_field.get(i).get(j)) {
                            System.out.print("🌳");
                        } else {
                            System.out.print("❌");
                        }
                    }
                    System.out.println();
                }
                System.out.println();
            } finally {
                lock.readLock().unlock();
            }
        }

    }
}
