package com.joker17.http.request.config;

import lombok.*;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@RequiredArgsConstructor(staticName = "of")
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class GetRequestConfig extends BaseRequestConfig<GetRequestConfig> {


}
