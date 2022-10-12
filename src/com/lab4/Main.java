package com.lab4;

import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static java.lang.Thread.sleep;

public class Main {

    static ArrayList<Thread> readers_by_phone = new ArrayList<Thread>();
    static ArrayList<Thread> readers_by_name = new ArrayList<Thread>();
    static ArrayList<Thread> writers = new ArrayList<Thread>();

    static FileWriter file_writer;
    static FileReader file_reader;

    static ReadWriteLock lock = new ReadWriteLock();

    public static void main(String[] args) throws Exception {

        FileWriter file_writer = new FileWriter("src\\com\\lab4\\file.txt");
        FileReader file_reader = new FileReader("src\\com\\lab4\\file.txt");

        for (int i = 0; i < 10; i++) {
            readers_by_phone.add(new Thread(new ReaderFindPhone()));
            readers_by_name.add(new Thread(new ReaderFindName()));
            writers.add(new Thread(new Writer()));

            readers_by_phone.get(i).start();
            readers_by_name.get(i).start();
            writers.get(i).start();
        }

    }

    static class ReaderFindPhone implements Runnable{
        ReaderFindPhone(){ }

        public void run(){
            try {
                while (true) {
                    sleep(800);
                    findPhoneBySurname("Сєкунова");
                    sleep(500);
                    findPhoneBySurname("Рибалко");
                    sleep(1500);
                    findPhoneBySurname("Савченко");
                    sleep(700);
                    findPhoneBySurname("Корнієнко");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        public void findPhoneBySurname(String surname){
            try {
                lock.lockRead();
                System.out.println("ReaderFindPhone: " + surname);
                lock.unlockRead();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    static class ReaderFindName implements Runnable{
        ReaderFindName(){ }

        public void run(){
            try {
                while (true) {
                    sleep(700);
                    findNameByPhone("36-69-60");
                    sleep(800);
                    findNameByPhone("36-69-63");
                    sleep(300);
                    findNameByPhone("36-69-62");
                    sleep(500);
                    findNameByPhone("36-59-65");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        public void findNameByPhone(String phone){
            try {
                lock.lockRead();
                System.out.println("ReaderFindName: " + phone);
                lock.unlockRead();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    static class Writer implements Runnable{
        Writer(){ }

        public void run(){
            while (true) {
                try {
                    while (true) {
                        sleep(3000);
                        add("Шпак Надія Федорівна", "36-69-66");
                        sleep(2000);
                        remove("Кондратюк Світлана Володимирівна", "36-69-68");
                        sleep(3000);
                        add("Твердохліб Інна Борисівна", "36-69-24");
                        sleep(1000);
                        add("Зінченко Дмитро Сергійович", "36-69-67");
                        sleep(1500);
                        add("Тертичний Сергій Васильович", "36-69-51");
                        sleep(200);
                        remove("Мірошніченко Світлана Анатоліївна", "36-69-68");
                        sleep(2000);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        public void add(String name, String phone){
            try {
                lock.lockWrite();
                System.out.println("Writer: add");
                lock.unlockWrite();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        public void remove(String name, String phone){
            try {
                lock.lockWrite();
                System.out.println("Writer: remove");
                lock.unlockWrite();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}