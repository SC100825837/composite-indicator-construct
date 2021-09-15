package com.jc.research.entity.DTO;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.io.Serializable;

/**
 * @program: composite-indicator-construct
 * @description: 指数构建对象
 * @author: SunChao
 * @create: 2021-08-30 09:58
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class IndicatorConstructTargetDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId
    private Long id;
}
