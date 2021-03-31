package ch.uzh.ifi.hase.soprafs21.repository;


import ch.uzh.ifi.hase.soprafs21.entity.FilesDB;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository("fileDBRepository")
public interface FileDBRepository extends JpaRepository<FilesDB, Long> {

}
