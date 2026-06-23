package com.marler.alumnus.controller;

import com.marler.alumnus.mapper.ClassMapper;
import com.marler.alumnus.mapper.CollegeMapper;
import com.marler.alumnus.mapper.MajorMapper;
import com.marler.alumnus.pojo.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/dict")
public class DictController {

    private static final Logger log = LoggerFactory.getLogger(DictController.class);

    @Autowired
    private CollegeMapper collegeMapper;

    @Autowired
    private MajorMapper majorMapper;

    @Autowired
    private ClassMapper classMapper;

    /**
     * 获取所有字典数据（学院、专业、班级）
     * GET /dict/all
     */
    @GetMapping("/all")
    public Result getAll() {
        log.debug("查询字典数据");

        Map<String, Object> data = new HashMap<>();
        data.put("colleges", collegeMapper.findAll());
        data.put("majors", majorMapper.findAll());
        data.put("classes", classMapper.findAll());

        log.info("查询字典数据成功 - 学院: {}条, 专业: {}条, 班级: {}条",
                ((java.util.List) data.get("colleges")).size(),
                ((java.util.List) data.get("majors")).size(),
                ((java.util.List) data.get("classes")).size());
        return Result.success(data);
    }
}
