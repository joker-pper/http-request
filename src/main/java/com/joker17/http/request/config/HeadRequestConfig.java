package com.joker17.http.request.config;

import com.joker17.http.request.core.HttpConstants;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Data
@RequiredArgsConstructor(staticName = "of")
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class HeadRequestConfig extends BaseRequestConfig<HeadRequestConfig> {

    {
        method = HttpConstants.HEAD_METHOD;
    }

}
