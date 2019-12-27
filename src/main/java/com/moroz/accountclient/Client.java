package com.moroz.accountclient;

import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

public class Client {

    private final String URL;

    private int rCount;
    private int wCount;
    private List<Integer> idList;

    private Random random = new Random();

    public Client() {
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream(Thread.currentThread().getContextClassLoader()
                    .getResource("application.properties").getPath()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        URL = "http://"+properties.getProperty("server.address")+":"+properties.getProperty("server.port")+"/service/";

        int idListStart = Integer.parseInt(properties.getProperty("id.list.start"));
        int idListEnd = Integer.parseInt(properties.getProperty("id.list.end"));
        rCount = Integer.parseInt(properties.getProperty("rCount"));
        wCount = Integer.parseInt(properties.getProperty("wCount"));

        List<Integer> ids = new ArrayList<>();
        for(int i=idListStart;i<=idListEnd;i++) {
            ids.add(i);
        }
        idList = ids;
    }

    public static void main(String[] args) {
        new Client().start();
    }

    private void start() {
        boolean work = true;
        Scanner scanner = new Scanner(System.in);

        while (work) {
            System.out.println("Choose operation: \n" +
                    "run - run read\\write threads\n" +
                    "statistic - show server statistic\n" +
                    "clear - clear all server statistic\n" +
                    "exit - close client");

            switch (scanner.nextLine()) {
                case "run" :
                    runThreads();
                    break;
                case "statistic":
                    showStatistic();
                    break;
                case "clear":
                    clearStatistic();
                    break;
                case "exit":
                    work = false;
                    break;
                default:
                    System.out.println("Wrong operation!");
                    break;
            }
        }
    }

    private void runThreads() {
        RestTemplate restTemplate = new RestTemplate();

        Set<Thread> threads = new HashSet<>();
        for(int i=0;i<rCount;i++) {
            threads.add(new Thread(() -> {
                try {
                    int randomId = random.nextInt(idList.size());
                    System.out.println(restTemplate.getForObject(URL+idList.get(randomId), Long.class));
                } catch (HttpClientErrorException e) {
                    System.out.println("ERROR! -> "+e.getResponseBodyAsString());
                }
            }));
        }

        for(int i=0;i<wCount;i++) {
            threads.add(new Thread(() -> {
                try {
                    int randomId = random.nextInt(idList.size());
                    restTemplate.postForObject(URL+idList.get(randomId)+"/"+(random.nextInt(11)-5),
                            null, Void.class);
                } catch (HttpClientErrorException e) {
                    System.out.println("ERROR! -> "+e.getResponseBodyAsString());
                }
            }));
        }

        for(Thread thread : threads) {
            thread.start();
        }
    }

    private void showStatistic() {
        RestTemplate restTemplate = new RestTemplate();
        System.out.println(restTemplate.getForObject(URL+"statistic", Map.class));
    }

    private void clearStatistic() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.delete(URL+"statistic");
    }
}
