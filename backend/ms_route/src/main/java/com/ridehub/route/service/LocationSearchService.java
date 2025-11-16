package com.ridehub.route.service;

import java.util.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.ridehub.route.helper.TextUtils;

@Service
public class LocationSearchService {

    private final RedisTemplate<String, String> redisTemplate;

    public LocationSearchService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public List<String> resolveCodes(String userInput) {
        if (userInput == null || userInput.isBlank()) {
            return List.of();
        }

        // 1) Try direct code match (province/district)
        String trimmed = userInput.trim();
        String directKeyProvince = "province:code:" + trimmed;
        String directKeyDistrict = "district:code:" + trimmed;
        if (Boolean.TRUE.equals(redisTemplate.hasKey(directKeyProvince))
                || Boolean.TRUE.equals(redisTemplate.hasKey(directKeyDistrict))) {
            return List.of(trimmed);
        }

        // 2) Treat as name → build multiple normalized candidates
        String cleaned = trimmed.toLowerCase(Locale.ROOT);

        // base normalized (no prefix stripping)
        String baseNorm = TextUtils.normalizeName(cleaned);

        // alias cleaned: strip "tinh/tỉnh/thanh pho/thành phố/tp"
        String aliasClean = cleaned
                .replaceFirst("^(tinh|tỉnh)\\s+", "")
                .replaceFirst("^(thanh pho|thành phố|tp)\\s+", "");

        String aliasNorm = TextUtils.normalizeName(aliasClean);

        // Build candidate normalized keys in priority order
        Set<String> candidates = new LinkedHashSet<>();
        candidates.add(aliasNorm); // "tiengiang"
        candidates.add(baseNorm); // "tinhtiengiang" if input already includes "tinh"

        // Also try with added prefixes on alias (handles input without "tinh")
        candidates.add(TextUtils.normalizeName("tinh " + aliasClean)); // "tinhtiengiang"
        candidates.add(TextUtils.normalizeName("tỉnh " + aliasClean)); // "tinhtiengiang" with diacritics
        candidates.add(TextUtils.normalizeName("thanh pho " + aliasClean)); // for cities
        candidates.add(TextUtils.normalizeName("thành phố " + aliasClean));

        // 3) Try each candidate until we find a non-empty Redis set
        for (String norm : candidates) {
            String nameKey = "location:name:" + norm;
            Set<String> codes = redisTemplate.opsForSet().members(nameKey);

            if (codes != null && !codes.isEmpty()) {
                System.out.println("Codes for key " + nameKey + ": " + codes);
                return new ArrayList<>(codes);
            } else {
                System.out.println("No codes found for key: " + nameKey);
            }
        }

        return List.of();
    }

    public Optional<String> resolveSingleCode(String raw) {
        List<String> codes = resolveCodes(raw);
        if (codes.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(codes.get(0));
    }
}
