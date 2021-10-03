package com.cvicse.cic.module.datasource.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;


@Data
@AllArgsConstructor
public class DataOriginFile {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 文件名称
     */
    private String originFileName;

    /**
     * 上传的源文件所在文件服务器的bucket名字
     */
    private String bucketName;

    /**
     * 上传的源文件所在文件服务器的object名字
     */
    private String objectName;

    /**
     * 上传用户id
     */
    private Long uploadUserId;

    /**
     * 上传时间
     */
    private LocalDateTime uploadDate;
}
