package com.leyou.common.exception.pojo;

import com.leyou.common.exception.LyException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.joda.time.DateTime;

@Getter
public class ExceptionResult {
    private int status;//响应码
    private String message;//响应信息
    private String timestamp;//响应时间搓

    public ExceptionResult(LyException e) {
        this.status = e.getStatus();
        this.message = e.getMessage();
        this.timestamp = DateTime.now().toString("yyyy-MM-dd HH:mm:ss");
    }

    public ExceptionResult() {
    }

    public ExceptionResult(int status,String message) {
        this.status = status;
        this.message = message;
        this.timestamp = DateTime.now().toString("yyyy-MM-dd HH:mm:ss");
    }
}