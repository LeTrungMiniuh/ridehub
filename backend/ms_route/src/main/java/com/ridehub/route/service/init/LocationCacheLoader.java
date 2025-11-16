package com.ridehub.route.service.init;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ridehub.route.helper.TextUtils;

import jakarta.annotation.PostConstruct;

@Service
public class LocationCacheLoader {

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    private final HttpClient httpClient = HttpClient.newHttpClient();

    public LocationCacheLoader(RedisTemplate<String, String> redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    // 1) Run once on startup
    @EventListener(ApplicationReadyEvent.class)
    public void onStartup() {
        reloadLocations();
    }

    // 2) Run every day at 1:00 AM
    @Scheduled(cron = "0 0 */2 * * ?", zone = "Asia/Ho_Chi_Minh")
    public void dailyJob() {
        reloadLocations();
    }

    // Your shared logic
    private void reloadLocations() {
        loadLocations();
    }

    public void loadLocations() {
        try {
            if (Boolean.TRUE.equals(redisTemplate.hasKey("location:loaded"))) {
                return;
            }
            // ========= FAST HTTP CALL (no RestTemplate) =========
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://provinces.open-api.vn/api/v1/?depth=3"))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            // Convert JSON → objects
            List<ApiProvince> provinces = objectMapper.readValue(
                    response.body(),
                    new TypeReference<List<ApiProvince>>() {
                    });

            if (provinces == null || provinces.isEmpty()) {
                return;
            }

            // ========= Process provinces =========
            for (ApiProvince p : provinces) {
                String provinceCode = p.getCodeAsString();
                if (provinceCode == null) {
                    continue;
                }

                // province:code:<code>
                redisTemplate.opsForValue()
                        .set("province:code:" + provinceCode, writeJson(p));
                redisTemplate.opsForSet()
                        .add("location:name:" + normalize(p.getName()), provinceCode);

                if (p.getDistricts() == null) {
                    continue;
                }

                for (ApiDistrict d : p.getDistricts()) {
                    String districtCode = d.getCodeAsString();
                    if (districtCode == null) {
                        continue;
                    }

                    // district:code:<code>
                    redisTemplate.opsForValue()
                            .set("district:code:" + districtCode, writeJson(d));
                    redisTemplate.opsForSet()
                            .add("location:name:" + normalize(d.getName()), districtCode);

                    if (d.getWards() == null) {
                        continue;
                    }

                    // for (ApiWard w : d.getWards()) {
                    // String wardCode = w.getCodeAsString();
                    // if (wardCode == null) {
                    // continue;
                    // }

                    // // ward:code:<code>
                    // redisTemplate.opsForValue()
                    // .set("ward:code:" + wardCode, writeJson(w));
                    // redisTemplate.opsForSet()
                    // .add("location:name:" + normalize(w.getName()), wardCode);
                    // }
                }
            }
            redisTemplate.opsForValue().set("location:loaded", "1");

        } catch (Exception e) {
            throw new RuntimeException("Failed to load locations", e);
        }
    }

    private String normalize(String name) {
        return TextUtils.normalizeName(name);
    }

    private String writeJson(Object o) {
        try {
            return objectMapper.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    // ============================
    // Internal API DTOs
    // ============================

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class ApiProvince {
        // from schema: code (integer), name, division_type, codename, phone_code,
        // districts[]
        private Integer code;
        private String name;
        private String division_type;
        private String codename;
        private Integer phone_code;
        private List<ApiDistrict> districts;

        public Integer getCode() {
            return code;
        }

        public void setCode(Integer code) {
            this.code = code;
        }

        public String getCodeAsString() {
            return code != null ? String.valueOf(code) : null;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDivision_type() {
            return division_type;
        }

        public void setDivision_type(String division_type) {
            this.division_type = division_type;
        }

        public String getCodename() {
            return codename;
        }

        public void setCodename(String codename) {
            this.codename = codename;
        }

        public Integer getPhone_code() {
            return phone_code;
        }

        public void setPhone_code(Integer phone_code) {
            this.phone_code = phone_code;
        }

        public List<ApiDistrict> getDistricts() {
            return districts;
        }

        public void setDistricts(List<ApiDistrict> districts) {
            this.districts = districts;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class ApiDistrict {
        // from schema: code, name, division_type, codename, province_code, wards[]
        private Integer code;
        private String name;
        private String division_type;
        private String codename;
        private Integer province_code;
        private List<ApiWard> wards;

        public Integer getCode() {
            return code;
        }

        public void setCode(Integer code) {
            this.code = code;
        }

        public String getCodeAsString() {
            return code != null ? String.valueOf(code) : null;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDivision_type() {
            return division_type;
        }

        public void setDivision_type(String division_type) {
            this.division_type = division_type;
        }

        public String getCodename() {
            return codename;
        }

        public void setCodename(String codename) {
            this.codename = codename;
        }

        public Integer getProvince_code() {
            return province_code;
        }

        public void setProvince_code(Integer province_code) {
            this.province_code = province_code;
        }

        public List<ApiWard> getWards() {
            return wards;
        }

        public void setWards(List<ApiWard> wards) {
            this.wards = wards;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class ApiWard {
        // from schema: code, name, division_type, codename, district_code
        private Integer code;
        private String name;
        private String division_type;
        private String codename;
        private Integer district_code;

        public Integer getCode() {
            return code;
        }

        public void setCode(Integer code) {
            this.code = code;
        }

        public String getCodeAsString() {
            return code != null ? String.valueOf(code) : null;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDivision_type() {
            return division_type;
        }

        public void setDivision_type(String division_type) {
            this.division_type = division_type;
        }

        public String getCodename() {
            return codename;
        }

        public void setCodename(String codename) {
            this.codename = codename;
        }

        public Integer getDistrict_code() {
            return district_code;
        }

        public void setDistrict_code(Integer district_code) {
            this.district_code = district_code;
        }
    }
}
