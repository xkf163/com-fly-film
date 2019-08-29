
package com.fly.entity;
import lombok.Data;



import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by F on 2017/11/1.
 * 明星：多媒体中包含的人物
 */
@Entity
@Data
@Table(name = "fm_star")
public class Star extends BaseEntity implements Serializable{

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    private String nameExtend;

    private String douBanNo;

    @OneToOne
    private Person person;

    private String asDirector;

    private String asActor;

    private Integer asDirectorNumber = 0;

    private Integer asActorNumber = 0;

}
