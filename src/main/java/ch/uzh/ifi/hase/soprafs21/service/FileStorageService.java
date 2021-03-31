package ch.uzh.ifi.hase.soprafs21.service;

import java.io.IOException;
import java.util.stream.Stream;

import ch.uzh.ifi.hase.soprafs21.entity.FilesDB;
import ch.uzh.ifi.hase.soprafs21.repository.FileDBRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

/*
 * This is specifically made for the upload and retreival from all pictures from the database
 */


@Service
public class FileStorageService {

    @Autowired
    private FileDBRepository fileDBRepository;

    // Receives MultipartFile object, transform to FileDB object and save it to Database
    public FilesDB store(MultipartFile file) throws IOException {
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        FilesDB filesDB = new FilesDB(fileName, file.getContentType(), file.getBytes());
        fileDBRepository.save(filesDB);
        return filesDB;
    }

    // Return a FileDB object by provided id
    public FilesDB getFile(Long id) {
        return fileDBRepository.findById(id).get();
    }

    // Returns all files in our Database
    public Stream<FilesDB> getAllFiles() {
        return fileDBRepository.findAll().stream();
    }
}