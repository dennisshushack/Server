package ch.uzh.ifi.hase.soprafs21.entity;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.transaction.Transactional;

@Entity
@Transactional
public class Item {

    @GeneratedValue
    @Id
    private long id;
}