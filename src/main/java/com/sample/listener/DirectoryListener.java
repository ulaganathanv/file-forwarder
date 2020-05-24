package com.sample.listener;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.FileSystems;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

import com.sample.service.AmazonClient;

import javax.annotation.PostConstruct;

@Component
public class DirectoryListener {

    @Value("${INCOMING_DIR}")
    private String directory;

    private WatchService watchService;
    private WatchKey watchKey;

    @Autowired
    AmazonClient amazonClient;

    @PostConstruct
    public void init() {
        try {
            Path directoryPath = FileSystems.getDefault().getPath(directory);
            watchService = directoryPath.getFileSystem().newWatchService();
            directoryPath.register(watchService, StandardWatchEventKinds.ENTRY_CREATE,
                    StandardWatchEventKinds.ENTRY_MODIFY, StandardWatchEventKinds.ENTRY_DELETE);
        }
        catch (Exception exception) {
            exception.printStackTrace();
            return;
        }
    }

    @Scheduled(fixedDelayString = "${DIRECTORY_POLLING_RATE}")
    private void watch() throws InterruptedException, IOException {
        watchKey = watchService.take();

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
        }
    }

    private void takeActionOnChangeEvent(WatchEvent<?> event) {
        Kind<?> kind = event.kind();

        if (kind.equals(StandardWatchEventKinds.ENTRY_CREATE)) {
            Path entryCreated = (Path) event.context();
            System.out.println("New entry created:" + entryCreated);
            System.out.println("Uploading the file to S3 ...");
            String fileObjKeyName = entryCreated.toString();
            String filePath = directory + "/" + entryCreated.toString();
            amazonClient.uploadObject("incoming-files-directory", fileObjKeyName, filePath);
            System.out.println("File upload is completed");
        } else if (kind.equals(StandardWatchEventKinds.ENTRY_DELETE)) {
            Path entryDeleted = (Path) event.context();
            System.out.println("Exissting entry deleted:" + entryDeleted);
        } else if (kind.equals(StandardWatchEventKinds.ENTRY_MODIFY)) {
            Path entryModified = (Path) event.context();
            System.out.println("Existing entry modified:"+ entryModified);
        }
    }
}