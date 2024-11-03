package shop.example;

import jdbm.PrimaryHashMap;
import jdbm.RecordManager;
import jdbm.RecordManagerFactory;

import java.io.IOException;

public class KVExample {

    public static void main(String[] args) {
        try {
            //Create Records
            RecordManager recordManager = RecordManagerFactory.createRecordManager("./customerKVDB");
            String recordName = "exampleRecord";

            //Create HashMap
            PrimaryHashMap<Integer,String> exampleMap = recordManager.hashMap(recordName);

            //Put Stuff into the Map
            exampleMap.put(1, "One");
            exampleMap.put(2, "Two");
            System.out.println(exampleMap.entrySet());
            exampleMap.put(3, "Three");
            System.out.println(exampleMap.entrySet());

            //Commit Map Changes to save them to disk (persist!)
            recordManager.commit();

            //Close Records
            recordManager.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
