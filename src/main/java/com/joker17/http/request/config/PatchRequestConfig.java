package com.joker17.http.request.config;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Data
@RequiredArgsConstructor(staticName = "of")
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class PatchRequestConfig extends RequestBodyAndFileConfig<PatchRequestConfig> {


}
