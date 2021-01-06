package com.keza.clickhouse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;
import java.sql.SQLException;

@RestController
@RequestMapping("/events")
public class EventController {

    @Autowired
    ClickhouseService clickhouseService;

    @GetMapping("/post/http/{id}")
    public ResponseEntity getPostStatistics(@PathVariable("id") Long id) throws UnsupportedEncodingException {

        PostDto postDto = clickhouseService.getPostStatistics(id);

        return ResponseEntity.ok(postDto);
    }

    @GetMapping("/post/jdbc/{id}")
    public ResponseEntity getPostStatisticsWithNativeJDBC(@PathVariable("id") Long id) throws SQLException, ClassNotFoundException {

        PostDto postDto = clickhouseService.getPostStaticsWithNativeJDBC(id);

        return ResponseEntity.ok(postDto);
    }
}
