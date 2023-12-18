package org.example;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {

//        Task 1. Scv to json
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String fileName = "/home/xenogear/IdeaProjects/converter/src/main/java/org/example/data.csv";
        List<Employee> list = parseCSV(columnMapping, fileName);
        String json = listToJson(list);
        writeString(json, "data.json");

//        Task 2. Xml to json
        List<Employee> list2 = parseXML("/home/xenogear/IdeaProjects/converter/src/main/java/org/example/data.xml");
        String json2 = listToJson(list2);
        writeString(json2, "data2.json");
    }

    public static List parseCSV(String[] columnMapping, String fileName) {
        try (CSVReader reader = new CSVReader(new FileReader(fileName))) {
            ColumnPositionMappingStrategy<Employee> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping("id", "firstName", "lastName", "country", "age");
            CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(reader)
                    .withMappingStrategy(strategy)
                    .build();
            return csv.parse();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ArrayList();
    }

    public static String listToJson(List<Employee> list) {
        Type listType = new TypeToken<List<Employee>>() {}.getType();
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.setPrettyPrinting().create();
        String json = gson.toJson(list, listType);
        return json;
    }

    public static void writeString(String json, String filename) {
        String path = "/home/xenogear/IdeaProjects/converter/src/main/java/org/example/" + filename;
        try (FileWriter file = new FileWriter(path)) {
            file.write(json);
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List parseXML(String file_path) {
        ArrayList<Employee> employees = new ArrayList();
        try {
            var factory = DocumentBuilderFactory.newInstance();
            var builder = factory.newDocumentBuilder();
            var filePath = new File(file_path);
            Document doc = builder.parse(filePath);
            Node root = doc.getDocumentElement();
            NodeList nodeList = root.getChildNodes();


            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node_ = nodeList.item(i);
                if (Node.ELEMENT_NODE == node_.getNodeType()) {
                    Element element = (Element) node_;
                    NodeList list = element.getChildNodes();
                    Map<String, String> information = new HashMap<>();
                    for (int a = 0; a < list.getLength(); a++) {
                        if (list.item(a).getNodeType() == Node.ELEMENT_NODE) {
                            information.put(list.item(a).getNodeName(), list.item(a).getTextContent());
                        }
                    }
                    Employee employee = new Employee(Long.parseLong(information.get("id")), information.get("firstName"), information.get("lastName"), information.get("country"), Integer.parseInt(information.get("age")));
                    employees.add(employee);
                }
            }
            return employees;
        } catch (IOException | SAXException | ParserConfigurationException exception) {
            exception.printStackTrace();
        }
        return new ArrayList();
    }
}