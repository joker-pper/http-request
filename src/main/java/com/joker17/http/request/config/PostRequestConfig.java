package com.joker17.http.request.config;

import com.joker17.http.request.core.HttpConstants;
import lombok.*;

@Data
@RequiredArgsConstructor(staticName = "of")
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class PostRequestConfig extends RequestBodyAndFileConfig<PostRequestConfig> {

    {
        method = HttpConstants.POST_METHOD;
    }
}
