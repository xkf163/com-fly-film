package com.fly.entity;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * 多媒体资料
 */
@Entity
@Table(name = "fm_media",uniqueConstraints = {@UniqueConstraint(name = "cons_media",columnNames = {"name","fullPath","deleted"})},indexes ={@Index(name = "index_media",columnList = "id,name")})
@Data
public class Media extends BaseEntity implements Serializable {

	@Id
	@GeneratedValue
	private Long id;

	private String name;

	private Long mediaSize; //占用磁盘空间（单位字节）

	private Float mediaSizeGB;//占用磁盘空间（单位GB）

	private String pcName; //电脑主机名

	private String diskName; //硬盘名称

	private String diskNo; //盘符

	private String fullPath; //全路径(去除盘符)

	@JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private Date gatherDate; //资源下载时间

	@JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private Date modifiedDate; //资源修改时间


	private Integer whetherFolder;//是否文件夹

	private Integer whetherTransfer; //是否需要转换成mediaVO

	private Integer whetherAlive; //全路径是否存在文件(夹)


	private String Director;

	private String Actor;

	private String Writer;

	private String nameChn;

	private String nameEng;

	private Short year;

	@OneToOne
	private Film film;

}
