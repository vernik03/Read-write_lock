package com.lab4;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import static java.lang.Thread.sleep;

public class FileManager {

    ArrayList<Thread> readers_by_phone = new ArrayList<Thread>();
    ArrayList<Thread> readers_by_name = new ArrayList<Thread>();
    ArrayList<Thread> writers = new ArrayList<Thread>();

    String file_path = "src/com/lab4/file.txt";

    ReadWriteLock lock = new ReadWriteLock();

    public FileManager() {

        readers_by_phone = new ArrayList<Thread>();
        readers_by_name = new ArrayList<Thread>();
        writers = new ArrayList<Thread>();

        lock = new ReadWriteLock();



        for (int i = 0; i < 10; i++) {
            readers_by_phone.add(new Thread(new ReaderFindPhone()));
            readers_by_name.add(new Thread(new ReaderFindName()));
            writers.add(new Thread(new Writer()));

            readers_by_phone.get(i).start();
            readers_by_name.get(i).start();
            writers.get(i).start();
        }

    }


    public ArrayList<String> readFileToArrayList() {
        ArrayList<String> list = new ArrayList<String>();
        try {
            FileReader file_reader = new FileReader("src\\com\\lab4\\file.txt");
            BufferedReader bufferedReader = new BufferedReader(file_reader);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                list.add(line);
            }
            bufferedReader.close();
            file_reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    public void writeToFile(ArrayList<String> list) {
        try {
            FileWriter file_writer = new FileWriter("src\\com\\lab4\\file.txt", false);
            for (String line : list) {
                file_writer.write(line + "\n");
            }
            file_writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    class ReaderFindPhone implements Runnable{
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
                ArrayList<String> list = readFileToArrayList();
                for (String line : list) {
                    if (line.contains(surname)) {
                        System.out.println("Номер телефону: " + line.substring(line.indexOf(" ") + 1));
                    }
                }
                lock.unlockRead();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }



    class ReaderFindName implements Runnable{
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
                ArrayList<String> list = readFileToArrayList();
                for (String line : list) {
                    if (line.contains(phone)) {
                        System.out.println("Прізвище: " + line.substring(0, line.indexOf(" ")));
                    }
                }
                lock.unlockRead();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    class Writer implements Runnable{
        Writer(){ }

        public void run(){
            while (true) {
                try {
                    while (true) {
                        sleep(3000);
                        add("Кондратюк Світлана Володимирівна", "36-69-68");
                        sleep(2000);
                        add("Шпак Надія Федорівна", "36-69-66");
                        sleep(3000);
                        remove("Кондратюк Світлана Володимирівна", "36-69-68");
                        sleep(3000);
                        add("Мірошніченко Світлана Анатоліївна", "36-69-68");
                        sleep(2000);
                        add("Твердохліб Інна Борисівна", "36-69-24");
                        sleep(4000);
                        add("Зінченко Дмитро Сергійович", "36-69-67");
                        sleep(200);
                        add("Тертичний Сергій Васильович", "36-69-51");
                        sleep(3000);
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
                FileWriter file_writer = new FileWriter(file_path, true);
                file_writer.write(name + " " + phone + "\n");
                System.out.println("Додано запис: " + name + " " + phone);
                file_writer.close();
                lock.unlockWrite();
            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
            }
        }

        public void remove(String name, String phone){
            try {
                lock.lockWrite();
                ArrayList<String> list = readFileToArrayList();
                boolean flag = false;
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).equals(name + " " + phone)) {
                        list.remove(i);
                        System.out.println("Видалено запис: " + name + " " + phone);
                        flag = true;
                    }
                }
                if (!flag) {
                    System.out.println("Запису " + name + " " + phone + " не існує");
                }
                writeToFile(list);
                lock.unlockWrite();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
