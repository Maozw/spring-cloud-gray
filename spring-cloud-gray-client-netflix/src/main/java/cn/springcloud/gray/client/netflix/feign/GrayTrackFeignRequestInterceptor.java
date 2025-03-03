package cn.springcloud.gray.client.netflix.feign;

import cn.springcloud.gray.request.GrayHttpTrackInfo;
import cn.springcloud.gray.request.GrayTrackInfo;
import cn.springcloud.gray.request.RequestLocalStorage;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;


public class GrayTrackFeignRequestInterceptor implements RequestInterceptor {

    private static final Logger log = LoggerFactory.getLogger(GrayTrackFeignRequestInterceptor.class);

    private RequestLocalStorage requestLocalStorage;


    public GrayTrackFeignRequestInterceptor(RequestLocalStorage requestLocalStorage) {
        this.requestLocalStorage = requestLocalStorage;
    }

    @Override
    public void apply(RequestTemplate template) {
        GrayHttpTrackInfo grayTrack = getGrayHttpTrackInfo();
        if (grayTrack != null) {
            if (StringUtils.isNotEmpty(grayTrack.getUri())) {
                template.header(GrayHttpTrackInfo.GRAY_TRACK_URI, grayTrack.getUri());
            }
            if (StringUtils.isNotEmpty(grayTrack.getTraceIp())) {
                template.header(GrayHttpTrackInfo.GRAY_TRACK_TRACE_IP, grayTrack.getTraceIp());
            }
            if (StringUtils.isNotEmpty(grayTrack.getMethod())) {
                template.header(GrayHttpTrackInfo.GRAY_TRACK_METHOD, grayTrack.getMethod());
            }
            if (grayTrack.getParameters() != null && !grayTrack.getParameters().isEmpty()) {
                grayTrack.getParameters().entrySet().forEach(entry -> {
                    String name = new StringBuilder().append(GrayHttpTrackInfo.GRAY_TRACK_PARAMETER_PREFIX)
                            .append(GrayTrackInfo.GRAY_TRACK_SEPARATE)
                            .append(entry.getKey()).toString();
                    template.header(name, entry.getValue());
                });
            }
            if (grayTrack.getHeaders() != null && !grayTrack.getHeaders().isEmpty()) {
                grayTrack.getHeaders().entrySet().forEach(entry -> {
                    String name = new StringBuilder().append(GrayHttpTrackInfo.GRAY_TRACK_HEADER_PREFIX)
                            .append(GrayTrackInfo.GRAY_TRACK_SEPARATE)
                            .append(entry.getKey()).toString();
                    template.header(name, entry.getValue());
                });
            }

            appendGrayTrackInfoToHeader(GrayTrackInfo.GRAY_TRACK_ATTRIBUTE_PREFIX, grayTrack.getAttributes(), template);
        }
    }

    private GrayHttpTrackInfo getGrayHttpTrackInfo() {
        try {
            return (GrayHttpTrackInfo) requestLocalStorage.getGrayTrackInfo();
        } catch (Exception e) {
            log.error("从requestLocalStorage中获取GrayTrackInfo对象失败.", e);
            return null;
        }
    }

    private void appendGrayTrackInfoToHeader(String grayPrefix, Map<String, String> infos, RequestTemplate template) {
        if (MapUtils.isNotEmpty(infos)) {
            infos.entrySet().forEach(entry -> {
                String name = new StringBuilder().append(grayPrefix)
                        .append(GrayTrackInfo.GRAY_TRACK_SEPARATE)
                        .append(entry.getKey()).toString();
                template.header(name, entry.getValue());
            });
        }
    }
}
