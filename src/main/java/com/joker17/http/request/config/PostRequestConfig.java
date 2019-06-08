package com.joker17.http.request.config;

import lombok.*;

@Data
@RequiredArgsConstructor(staticName = "of")
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class PostRequestConfig extends RequestBodyAndFileConfig<PostRequestConfig> {


}
