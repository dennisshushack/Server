package ch.uzh.ifi.hase.soprafs21.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.transaction.Transactional;

// Need to add a comment right here
@Entity
@Transactional
public class Item {

    @GeneratedValue
    @Id
    private long id;
}

