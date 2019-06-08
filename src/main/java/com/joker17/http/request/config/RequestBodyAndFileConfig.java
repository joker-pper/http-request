package com.joker17.http.request.config;

import com.joker17.http.request.support.ValidateUtils;
import lombok.Data;
import lombok.ToString;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Data
@ToString(callSuper = true)
public class RequestBodyAndFileConfig<T> extends RequestBodyConfig<T> {

    private Map<String, List<File>> fileParameterMap = new LinkedHashMap<>(16);

    public T addFile(File... files) {
        return addFile("file", files);
    }

    public T addFile(String name, File... files) {
        ValidateUtils.checkKeyNameNotEmpty(name);
        List<File> fileList = fileParameterMap.get(name);
        if (fileList == null) {
            fileList = new ArrayList<>(16);
            fileParameterMap.put(name, fileList);
        }
        if (files != null) {
            for (File file : files) {
                if (file != null) {
                    fileList.add(file);
                }
            }
        }
        return (T)this;
    }



}
