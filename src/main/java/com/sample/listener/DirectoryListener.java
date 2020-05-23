package com.sample.listener;

import com.sample.service.AmazonClient;
import com.sample.util.PropertyReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

//@Component
public class DirectoryListener implements Runnable {
    private String directory;

    @Autowired
    AmazonClient amazonClient;

    public DirectoryListener(AmazonClient amazonClient) {
        this.amazonClient = amazonClient;
    }

    public String getDirectory() {

        return directory;
    }

    public void setDirectory(String directory) {

        this.directory = directory;
    }

    public void run() {
        try {
            this.directory = PropertyReader.getInstance().getProperty("INCOMING_DIR");
            Path directoryPath = FileSystems.getDefault().getPath(directory);
            WatchService watchService = directoryPath.getFileSystem().newWatchService();
            directoryPath.register(watchService, StandardWatchEventKinds.ENTRY_CREATE,
                    StandardWatchEventKinds.ENTRY_MODIFY, StandardWatchEventKinds.ENTRY_DELETE);

            //Start infinite loop to watch changes on the directory
            while (true) {

                WatchKey watchKey = watchService.take();

                // poll for file system events on the WatchKey
                for (final WatchEvent<?> event : watchKey.pollEvents()) {
                    //Calling method
                    takeActionOnChangeEvent(event);
                }

                //Break out of the loop if watch directory got deleted
                if (!watchKey.reset()) {
                    watchKey.cancel();
                    watchService.close();
                    System.out.println("Watch directory got deleted. Stop watching it.");
                    //Break out from the loop
                    break;
                }
            }

        } catch (InterruptedException interruptedException) {
            System.out.println("Thread got interrupted:" + interruptedException);
            return;
        } catch (Exception exception) {
            exception.printStackTrace();
            return;
        }
    }

    private void takeActionOnChangeEvent(WatchEvent<?> event) {

        Kind<?> kind = event.kind();

        if (kind.equals(StandardWatchEventKinds.ENTRY_CREATE)) {
            Path entryCreated = (Path) event.context();
            System.out.println("New entry created:" + entryCreated);
//            String fileName = PropertyReader.getInstance().getProperty("INCOMING_DIR") +
//                    "/" + entryCreated.toString();
//            System.out.println("File Name : " + fileName);
//            amazonClient.uploadObject("incoming-files-directory",
//                    "sample", "/Users/ulaganathan/Codebase/file-forwarder/incoming-files/sample.txt");
            amazonClient.listBuckets();
        } else if (kind.equals(StandardWatchEventKinds.ENTRY_DELETE)) {
            Path entryDeleted = (Path) event.context();
            System.out.println("Exissting entry deleted:" + entryDeleted);
        } else if (kind.equals(StandardWatchEventKinds.ENTRY_MODIFY)) {
            Path entryModified = (Path) event.context();
            System.out.println("Existing entry modified:"+ entryModified);
        }
    }
}