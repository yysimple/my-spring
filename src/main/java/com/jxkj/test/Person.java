package com.jxkj.test;

import com.jxkj.spring.framework.annotation.MyService;
import lombok.Data;

/**
 * 功能描述：
 *
 * @author wcx
 * @version 1.0
 */
@Data
@MyService
public class Person {

    private String name = "wcx";

    private Integer age = 18;
}
