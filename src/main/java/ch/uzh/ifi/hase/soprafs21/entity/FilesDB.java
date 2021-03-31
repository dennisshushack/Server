package ch.uzh.ifi.hase.soprafs21.entity;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name="picfiles")
public class FilesDB {
    @Id
    @GeneratedValue
    private Long id;

    private String name;

    private String type;

    // This is for the data we save in the Database
    // Blob = is for storing binary data
    @Lob
    private byte[] data;

    public FilesDB() {
    }

    public FilesDB(String name, String type, byte[] data) {
        this.name = name;
        this.type = type;
        this.data = data;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

}




