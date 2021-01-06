package com.keza.clickhouse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.*;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.sql.*;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;

@Service
public class ClickhouseService {

    @Autowired
    private Environment env;

    public PostDto getPostStatistics(Long id) throws UnsupportedEncodingException {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<String>(headers);
        RestTemplate restTemplate = new RestTemplate();

        StringBuilder builder = new StringBuilder();
        builder.append(env.getProperty("clickhouse.uri"));
        builder.append("?query=SELECT * from events_count_mv where post_id=");
        builder.append(id);
        builder.append(" format JSONEachRow");

        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setSupportedMediaTypes(Arrays.asList(MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON));
        restTemplate.getMessageConverters().add(0, converter);

        String uri = URLDecoder.decode(builder.toString(), "UTF-8");

        ResponseEntity<PostDto> postResponseEntity;

        Instant start = Instant.now();

        postResponseEntity = restTemplate.exchange(uri, HttpMethod.GET, entity, PostDto.class);

        Instant finish = Instant.now();
        long timeElapsed = Duration.between(start, finish).toMillis();
        System.out.println(timeElapsed);

        return postResponseEntity.getBody();
    }


    public PostDto getPostStaticsWithNativeJDBC(Long id) throws ClassNotFoundException, SQLException {
        Class.forName("com.github.housepower.jdbc.ClickHouseDriver");
        Connection connection = DriverManager.getConnection(env.getProperty("clickhouse.jdbc"));
        PreparedStatement stmt = connection.prepareStatement("SELECT type, post_id, count from events_count_mv where post_id=?");
        stmt.setLong(1,id);

        Instant start = Instant.now();

        ResultSet rs = stmt.executeQuery();

        Instant finish = Instant.now();
        long timeElapsed = Duration.between(start, finish).toMillis();
        System.out.println(timeElapsed);

        PostDto postDto = null;

        while(rs.next()) {
            String type = rs.getString("type");
            int postId = rs.getInt("post_id");
            int count = rs.getInt("count");
            postDto = new PostDto(type,postId,count);
        }

        rs.close();
        stmt.close();
        connection.close();

        return postDto;
    }

}
