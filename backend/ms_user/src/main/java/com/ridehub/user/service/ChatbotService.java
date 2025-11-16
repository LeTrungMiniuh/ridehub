package com.ridehub.user.service;

import ai.z.openapi.ZaiClient;
import ai.z.openapi.service.model.ChatCompletionCreateParams;
import ai.z.openapi.service.model.ChatCompletionResponse;
import ai.z.openapi.service.model.ChatFunction;
import ai.z.openapi.service.model.ChatFunctionCall;
import ai.z.openapi.service.model.ChatFunctionParameterProperty;
import ai.z.openapi.service.model.ChatFunctionParameters;
import ai.z.openapi.service.model.ChatMessage;
import ai.z.openapi.service.model.ChatMessageRole;
import ai.z.openapi.service.model.ChatTool;
import ai.z.openapi.service.model.ChatToolType;
import ai.z.openapi.service.model.ToolCalls;
import com.fasterxml.jackson.databind.JsonNode;
import com.ridehub.msroute.client.api.ChatbotTripSearchResourceMsrouteApi;
import com.ridehub.msroute.client.model.ChatbotTripSearchRequest;
import com.ridehub.msroute.client.model.PageTripDTO;
import com.ridehub.msroute.client.model.RouteDTO;
import com.ridehub.msroute.client.model.StationDTO;
import com.ridehub.msroute.client.model.TripDTO;
import com.ridehub.msroute.client.model.VehicleDTO;
import com.ridehub.user.config.ZaiChatbotProperties;
import com.ridehub.user.service.dto.ChatbotRequestDTO;
import com.ridehub.user.service.dto.ChatbotResponseDTO;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class ChatbotService {

        private static final DateTimeFormatter DMY_FORMATTER = DateTimeFormatter.ofPattern("d/M/yyyy");
        private static final ZoneId VN_ZONE = ZoneId.of("Asia/Ho_Chi_Minh");

        private final ZaiClient zaiClient;
        private final ChatbotTripSearchResourceMsrouteApi chatbotTripSearchResourceMsrouteApi;
        private final ZaiChatbotProperties zaiChatbotProperties;

        public ChatbotService(ZaiClient zaiClient,
                        ChatbotTripSearchResourceMsrouteApi chatbotTripSearchResourceMsrouteApi,
                        ZaiChatbotProperties zaiChatbotProperties) {
                this.zaiClient = zaiClient;
                this.chatbotTripSearchResourceMsrouteApi = chatbotTripSearchResourceMsrouteApi;
                this.zaiChatbotProperties = zaiChatbotProperties;
        }

        public ChatbotResponseDTO chat(ChatbotRequestDTO request) {

                try {
                        // ===== 0) System message to strongly guide tool usage =====
                        ChatMessage systemMessage = ChatMessage.builder()
                                        .role(ChatMessageRole.SYSTEM.value())
                                        .content(
                                                        "Bạn là trợ lý đặt vé xe cho hệ thống RideHub.\n" +
                                                                        "QUAN TRỌNG: Khi người dùng hỏi về chuyến đi, BẮT BUỘC phải gọi tool `get_trips` với các tham số chính xác.\n"
                                                                        +
                                                                        "\n" +
                                                                        "CÁCH XÁC ĐỊNH THAM SỐ:\n" +
                                                                        "- origin = điểm xuất phát (tỉnh/thành phố) - LUÔN LUÔN đi sau từ 'từ' hoặc là vị trí hiện tại của người dùng\n"
                                                                        +
                                                                        "- destination = điểm đến (tỉnh/thành phố) - LUÔN LUÔN đi sau từ 'đi', 'đến', 'về'\n"
                                                                        +
                                                                        "- departureTimeFrom = giờ khởi hành sớm nhất theo định dạng HH:mm nếu có\n"
                                                                        +
                                                                        "- departureTimeTo = giờ khởi hành muộn nhất theo định dạng HH:mm nếu có\n"
                                                                        +
                                                                        "- arrivalTimeFrom = giờ đến sớm nhất theo định dạng HH:mm nếu có\n"
                                                                        +
                                                                        "- arrivalTimeTo = giờ đến muộn nhất theo định dạng HH:mm nếu có\n"
                                                                        +
                                                                        "- date = ngày đi theo định dạng yyyy-MM-dd nếu có\n"
                                                                        +
                                                                        "\n" +
                                                                        "VÍ DỤ CỤ THỂ:\n" +
                                                                        "- 'Mình muốn tìm xe đi Tiền Giang từ Thanh Hóa khởi hành sau 8 giờ sáng' -> origin='Thanh Hóa', destination='Tiền Giang', departureTimeFrom='08:00'\n"
                                                                        +
                                                                        "- 'Tìm xe từ Hà Nội đi Đà Nẵng ngày 2025-11-23 đến trước 5 giờ chiều' -> origin='Hà Nội', destination='Đà Nẵng', date='2025-11-23', arrivalTimeTo='17:00'\n"
                                                                        +
                                                                        "- 'Tôi đang muốn về quê ở Thanh Hóa, đang ở Tiền Giang' -> origin='Tiền Giang', destination='Thanh Hóa'\n"
                                                                        +
                                                                        "\n" +
                                                                        "LUẬT BẮT BUỘC:\n" +
                                                                        "- PHẢI gọi get_trips với origin và destination KHÔNG ĐƯỢC null\n"
                                                                        +
                                                                        "- Nếu không chắc chắn, vẫn phải suy ra dựa trên ngữ cảnh\n"
                                                                        +
                                                                        "- Không bao giờ được để trống cả origin và destination\n"
                                                                        +
                                                                        "- Các từ khóa 'tìm xe', 'đi', 'đến', 'từ', 'về quê' đều yêu cầu gọi get_trips")
                                        .build();

                        // ===== 1) Define function schema for trip search =====
                        ChatFunctionParameterProperty originProperty = ChatFunctionParameterProperty.builder()
                                        .type("string")
                                        .description(
                                                        "Điểm xuất phát (tỉnh/thành phố). " +
                                                                        "Ví dụ: trong câu 'Mình muốn tìm xe đi Tiền Giang từ Thanh Hóa' thì origin = 'Thanh Hóa'.")
                                        .build();

                        ChatFunctionParameterProperty destinationProperty = ChatFunctionParameterProperty.builder()
                                        .type("string")
                                        .description(
                                                        "Điểm đến (tỉnh/thành phố). " +
                                                                        "Ví dụ: trong câu 'Mình muốn tìm xe đi Tiền Giang từ Thanh Hóa' thì destination = 'Tiền Giang'.")
                                        .build();

                        ChatFunctionParameterProperty departureTimeFromProperty = ChatFunctionParameterProperty
                                        .builder()
                                        .type("string")
                                        .description("Giờ khởi hành sớm nhất theo định dạng HH:mm, ví dụ: '08:00'. Có thể để trống.")
                                        .build();

                        ChatFunctionParameterProperty departureTimeToProperty = ChatFunctionParameterProperty.builder()
                                        .type("string")
                                        .description("Giờ khởi hành muộn nhất theo định dạng HH:mm, ví dụ: '12:00'. Có thể để trống.")
                                        .build();

                        ChatFunctionParameterProperty arrivalTimeFromProperty = ChatFunctionParameterProperty.builder()
                                        .type("string")
                                        .description("Giờ đến sớm nhất theo định dạng HH:mm, ví dụ: '14:00'. Có thể để trống.")
                                        .build();

                        ChatFunctionParameterProperty arrivalTimeToProperty = ChatFunctionParameterProperty.builder()
                                        .type("string")
                                        .description("Giờ đến muộn nhất theo định dạng HH:mm, ví dụ: '18:00'. Có thể để trống.")
                                        .build();

                        ChatFunctionParameterProperty dateProperty = ChatFunctionParameterProperty.builder()
                                        .type("string")
                                        .description("Ngày đi theo định dạng yyyy-MM-dd, ví dụ: '2025-11-23'. Có thể để trống.")
                                        .build();

                        Map<String, ChatFunctionParameterProperty> properties = new HashMap<>();
                        properties.put("origin", originProperty);
                        properties.put("destination", destinationProperty);
                        properties.put("departureTimeFrom", departureTimeFromProperty);
                        properties.put("departureTimeTo", departureTimeToProperty);
                        properties.put("arrivalTimeFrom", arrivalTimeFromProperty);
                        properties.put("arrivalTimeTo", arrivalTimeToProperty);
                        properties.put("date", dateProperty);

                        ChatTool tripTool = ChatTool.builder()
                                        .type(ChatToolType.FUNCTION.value())
                                        .function(ChatFunction.builder()
                                                        .name("get_trips")
                                                        .description("Tìm chuyến xe phù hợp theo điểm đi (origin), điểm đến (destination) và các tiêu chí thời gian (departureTimeFrom, departureTimeTo, arrivalTimeFrom, arrivalTimeTo).")
                                                        .parameters(ChatFunctionParameters.builder()
                                                                        .type("object")
                                                                        .properties(properties)
                                                                        // bắt buộc origin và destination, date optional
                                                                        .required(Arrays.asList("origin",
                                                                                        "destination"))
                                                                        .build())
                                                        .build())
                                        .build();

                        // ===== 2) Build chat request =====
                        ChatMessage userMessage = ChatMessage.builder()
                                        .role(ChatMessageRole.USER.value())
                                        .content(request.getMessage())
                                        .build();

                        ChatCompletionCreateParams aiRequest = ChatCompletionCreateParams.builder()
                                        .model(zaiChatbotProperties.getModel())
                                        .messages(Arrays.asList(systemMessage, userMessage))
                                        .tools(Collections.singletonList(tripTool))
                                        .toolChoice("auto")
                                        .temperature(zaiChatbotProperties.getTemperature())
                                        .maxTokens(zaiChatbotProperties.getMaxTokens())
                                        .build();

                        // ===== 3) Send to Z-AI =====
                        ChatCompletionResponse result = zaiClient.chat().createChatCompletion(aiRequest);

                        if (!result.isSuccess()) {
                                return ChatbotResponseDTO.builder()
                                                .sessionId(request.getSessionId())
                                                .timestamp(Instant.now())
                                                .success(false)
                                                .error("Z-AI Error: " + result.getMsg())
                                                .message("Xin lỗi, mình không xử lý được yêu cầu của bạn lúc này.")
                                                .build();
                        }

                        ChatMessage assistantMessage = result.getData().getChoices().get(0).getMessage();

                        // ===== 4) Handle tool-calling =====
                        if (assistantMessage.getToolCalls() != null && !assistantMessage.getToolCalls().isEmpty()) {

                                for (ToolCalls toolCall : assistantMessage.getToolCalls()) {

                                        ChatFunctionCall func = toolCall.getFunction();
                                        String functionName = func.getName();
                                        JsonNode args = func.getArguments();

                                        if (!"get_trips".equals(functionName)) {
                                                // Nếu model gọi nhầm tên tool nào khác, bỏ qua
                                                continue;
                                        }

                                        String origin = args != null && args.hasNonNull("origin")
                                                        ? args.get("origin").asText()
                                                        : null;
                                        String destination = args != null && args.hasNonNull("destination")
                                                        ? args.get("destination").asText()
                                                        : null;
                                        String departureTimeFromStr = args != null
                                                        && args.hasNonNull("departureTimeFrom")
                                                                        ? args.get("departureTimeFrom").asText()
                                                        : null;
                                        String departureTimeToStr = args != null && args.hasNonNull("departureTimeTo")
                                                        ? args.get("departureTimeTo").asText()
                                                        : null;
                                        String arrivalTimeFromStr = args != null && args.hasNonNull("arrivalTimeFrom")
                                                        ? args.get("arrivalTimeFrom").asText()
                                                        : null;
                                        String arrivalTimeToStr = args != null && args.hasNonNull("arrivalTimeTo")
                                                        ? args.get("arrivalTimeTo").asText()
                                                        : null;
                                        String dateStr = args != null && args.hasNonNull("date")
                                                        ? args.get("date").asText()
                                                        : null;

                                        System.out.printf(
                                                        "Tool args -> origin='%s', destination='%s', departureTimeFrom='%s', departureTimeTo='%s', arrivalTimeFrom='%s', arrivalTimeTo='%s', date='%s'%n",
                                                        origin, destination, departureTimeFromStr, departureTimeToStr,
                                                        arrivalTimeFromStr, arrivalTimeToStr, dateStr);

                                        // ===== Fallback: Extract parameters using regex if AI fails =====
                                        if ((origin == null || origin.isBlank())
                                                        || (destination == null || destination.isBlank())) {

                                                String messageText = request.getMessage();

                                                // ========= ORIGIN =========
                                                if ((origin == null || origin.isBlank())) {
                                                        java.util.regex.Pattern originPattern = java.util.regex.Pattern
                                                                        .compile("từ\\s+([a-zA-Zà-ỹÀ-Ỹ\\s]+?)(?:\\s+đến|\\s+đi|\\s+về|$)",
                                                                                                java.util.regex.Pattern.CASE_INSENSITIVE);
                                                        java.util.regex.Matcher originMatcher = originPattern.matcher(messageText);
                                                        if (originMatcher.find()) {
                                                                origin = originMatcher.group(1).trim();
                                                        }
                                                }

                                                // ========= DESTINATION =========
                                                if ((destination == null || destination.isBlank())) {
                                                        // First try to find "đến [destination]" pattern
                                                        java.util.regex.Pattern destPattern1 = java.util.regex.Pattern
                                                                        .compile("đến\\s+([a-zA-Zà-ỹÀ-Ỹ\\s]+?)(?:\\s+ngày|\\s+từ|$)",
                                                                                                java.util.regex.Pattern.CASE_INSENSITIVE);
                                                        java.util.regex.Matcher destMatcher1 = destPattern1.matcher(messageText);
                                                        if (destMatcher1.find()) {
                                                                destination = destMatcher1.group(1).trim();
                                                        } else {
                                                                // Try pattern for "đi [destination] từ [origin]" 
                                                                java.util.regex.Pattern destPattern2 = java.util.regex.Pattern
                                                                                .compile("đi\\s+([a-zA-Zà-ỹÀ-Ỹ\\s]+?)\\s+từ\\s+[a-zA-Zà-ỹÀ-Ỹ\\s]+?(?:\\s+ngày|$)",
                                                                                                java.util.regex.Pattern.CASE_INSENSITIVE);
                                                                java.util.regex.Matcher destMatcher2 = destPattern2.matcher(messageText);
                                                                if (destMatcher2.find()) {
                                                                        destination = destMatcher2.group(1).trim();
                                                                } else {
                                                                        // Fallback to standard pattern for "đi [destination]"
                                                                        java.util.regex.Pattern destPattern3 = java.util.regex.Pattern
                                                                                        .compile("đi\\s+([a-zA-Zà-ỹÀ-Ỹ\\s]+?)(?:\\s+ngày|\\s+từ|$)",
                                                                                                java.util.regex.Pattern.CASE_INSENSITIVE);
                                                                        java.util.regex.Matcher destMatcher3 = destPattern3.matcher(messageText);
                                                                        if (destMatcher3.find()) {
                                                                                destination = destMatcher3.group(1).trim();
                                                                        }
                                                                }
                                                        }
                                                }

                                                // ========= DEPARTURE TIME FROM =========
                                                if ((departureTimeFromStr == null || departureTimeFromStr.isBlank())) {
                                                        java.util.regex.Pattern depFromPattern = java.util.regex.Pattern
                                                                        .compile("(?:sau|từ|khoảng)\\s+(\\d{1,2}:\\d{2})\\s*(?:giờ|sáng|chiều|tối)?",
                                                                                                java.util.regex.Pattern.CASE_INSENSITIVE);
                                                        java.util.regex.Matcher depFromMatcher = depFromPattern
                                                                        .matcher(messageText);
                                                        if (depFromMatcher.find()) {
                                                                departureTimeFromStr = depFromMatcher.group(1).trim();
                                                        }
                                                }

                                                // ========= DEPARTURE TIME TO =========
                                                if ((departureTimeToStr == null || departureTimeToStr.isBlank())) {
                                                        java.util.regex.Pattern depToPattern = java.util.regex.Pattern
                                                                        .compile("(?:trước|đến)\\s+(\\d{1,2}:\\d{2})\\s*(?:giờ|sáng|chiều|tối)?",
                                                                                                java.util.regex.Pattern.CASE_INSENSITIVE);
                                                        java.util.regex.Matcher depToMatcher = depToPattern
                                                                        .matcher(messageText);
                                                        if (depToMatcher.find()) {
                                                                departureTimeToStr = depToMatcher.group(1).trim();
                                                        }
                                                }

                                                // ========= ARRIVAL TIME FROM =========
                                                if ((arrivalTimeFromStr == null || arrivalTimeFromStr.isBlank())) {
                                                        java.util.regex.Pattern arrFromPattern = java.util.regex.Pattern
                                                                        .compile("(?:đến\\s+sau|tới\\s+sau)\\s+(\\d{1,2}:\\d{2})\\s*(?:giờ|sáng|chiều|tối)?",
                                                                                                java.util.regex.Pattern.CASE_INSENSITIVE);
                                                        java.util.regex.Matcher arrFromMatcher = arrFromPattern
                                                                        .matcher(messageText);
                                                        if (arrFromMatcher.find()) {
                                                                arrivalTimeFromStr = arrFromMatcher.group(1).trim();
                                                        }
                                                }

                                                // ========= ARRIVAL TIME TO =========
                                                if ((arrivalTimeToStr == null || arrivalTimeToStr.isBlank())) {
                                                        java.util.regex.Pattern arrToPattern = java.util.regex.Pattern
                                                                        .compile("(?:đến\\s+trước|tới\\s+trước)\\s+(\\d{1,2}:\\d{2})\\s*(?:giờ|sáng|chiều|tối)?",
                                                                                                java.util.regex.Pattern.CASE_INSENSITIVE);
                                                        java.util.regex.Matcher arrToMatcher = arrToPattern
                                                                        .matcher(messageText);
                                                        if (arrToMatcher.find()) {
                                                                arrivalTimeToStr = arrToMatcher.group(1).trim();
                                                        }
                                                }

                                                // ========= DATE (ISO, dd/MM/yyyy, natural language) =========
                                                if (dateStr == null || dateStr.isBlank()) {
                                                        String lower = messageText.toLowerCase(Locale.ROOT);

                                                        // 1) Explicit ISO date: ngày 2025-11-23
                                                        java.util.regex.Pattern isoPattern = java.util.regex.Pattern
                                                                        .compile("ngày\\s+(\\d{4}-\\d{2}-\\d{2})",
                                                                                        java.util.regex.Pattern.CASE_INSENSITIVE);
                                                        java.util.regex.Matcher isoMatcher = isoPattern.matcher(messageText);
                                                        if (isoMatcher.find()) {
                                                                dateStr = isoMatcher.group(1).trim();
                                                        }

                                                        // 2) dd/MM/yyyy or dd-MM-yyyy: ngày 23/11/2025
                                                        if (dateStr == null || dateStr.isBlank()) {
                                                                java.util.regex.Pattern dmyPattern = java.util.regex.Pattern
                                                                                .compile("ngày\\s+(\\d{1,2})[/-](\\d{1,2})[/-](\\d{4})",
                                                                                                java.util.regex.Pattern.CASE_INSENSITIVE);
                                                                java.util.regex.Matcher dmyMatcher = dmyPattern.matcher(messageText);
                                                                if (dmyMatcher.find()) {
                                                                        String dmy = dmyMatcher.group(1) + "/" + dmyMatcher.group(2)
                                                                                        + "/" + dmyMatcher.group(3);
                                                                        // convert to ISO later
                                                                        try {
                                                                                LocalDate parsed = LocalDate.parse(dmy, DMY_FORMATTER);
                                                                                dateStr = parsed.toString(); // yyyy-MM-dd
                                                                        } catch (Exception ignored) {
                                                                        }
                                                                }
                                                        }

                                                        // 3) Natural Vietnamese: hôm nay / ngày mai / mai
                                                        if (dateStr == null || dateStr.isBlank()) {
                                                                LocalDate today = LocalDate.now(VN_ZONE);
                                                                if (lower.contains("hôm nay")) {
                                                                        dateStr = today.toString();
                                                                } else if (lower.contains("ngày mai") || lower.contains("mai")) {
                                                                        dateStr = today.plusDays(1).toString();
                                                                }
                                                        }
                                                }

                                                System.out.printf(
                                                                "Fallback extraction -> origin='%s', destination='%s', departureTimeFrom='%s', departureTimeTo='%s', arrivalTimeFrom='%s', arrivalTimeTo='%s', date='%s'%n",
                                                                origin, destination, departureTimeFromStr, departureTimeToStr,
                                                                arrivalTimeFromStr, arrivalTimeToStr, dateStr);
                                        }

                                        // Nếu model vẫn trả về null hết -> trả lời rõ ràng cho user, tránh gọi ms_route
                                        // vô nghĩa
                                        if ((origin == null || origin.isBlank())
                                                        && (destination == null || destination.isBlank())) {
                                                return ChatbotResponseDTO.builder()
                                                                .sessionId(request.getSessionId())
                                                                .timestamp(Instant.now())
                                                                .success(false)
                                                                .message("Mình chưa hiểu rõ điểm đi và điểm đến của bạn. Bạn có thể nói rõ hơn không? Ví dụ: 'Tìm xe từ Thanh Hóa đi Tiền Giang ngày 2025-11-23'.")
                                                                .build();
                                        }

                                        // ===== 5) Build chatbot trip search request =====
                                        ChatbotTripSearchRequest searchReq = new ChatbotTripSearchRequest();

                                        if (origin != null && !origin.isBlank()) {
                                                searchReq.setOrigin(origin);
                                        }
                                        if (destination != null && !destination.isBlank()) {
                                                searchReq.setDestination(destination);
                                        }

                                        // Set time fields as strings (the model expects String types)
                                        if (departureTimeFromStr != null && !departureTimeFromStr.isBlank()) {
                                                searchReq.setDepartureTimeFrom(departureTimeFromStr);
                                        }

                                        if (departureTimeToStr != null && !departureTimeToStr.isBlank()) {
                                                searchReq.setDepartureTimeTo(departureTimeToStr);
                                        }

                                        if (arrivalTimeFromStr != null && !arrivalTimeFromStr.isBlank()) {
                                                searchReq.setArrivalTimeFrom(arrivalTimeFromStr);
                                        }

                                        if (arrivalTimeToStr != null && !arrivalTimeToStr.isBlank()) {
                                                searchReq.setArrivalTimeTo(arrivalTimeToStr);
                                        }

                                        // Set date if provided
                                        if (dateStr != null && !dateStr.isBlank()) {
                                                try {
                                                        LocalDate date = LocalDate.parse(dateStr);
                                                        searchReq.setDate(date);
                                                        System.out.println("Setting search date: " + date);
                                                } catch (Exception e) {
                                                        System.err.println("Invalid date format: " + dateStr);
                                                }
                                        } else {
                                                System.out.println("No date provided, using default search");
                                        }

                                        // Optional: default date if still null (e.g., hôm nay)
                                        // if (searchReq.getDate() == null) {
                                        // searchReq.setDate(LocalDate.now());
                                        // }

                                        System.out.println("Sending search request to ms_route:");
                                        System.out.println("  Origin: " + searchReq.getOrigin());
                                        System.out.println("  Destination: " + searchReq.getDestination());
                                        System.out.println("  Date: " + searchReq.getDate());
                                        System.out.println("  DepartureTimeFrom: " + searchReq.getDepartureTimeFrom());
                                        System.out.println("  DepartureTimeTo: " + searchReq.getDepartureTimeTo());

                                        // Page 0, size 3 → get first matching trip
                                        PageTripDTO pageResult = chatbotTripSearchResourceMsrouteApi.searchTrips(
                                                        searchReq,
                                                        0, // page
                                                        3, // size
                                                        null // sort
                                        );

                                        TripDTO trip = null;
                                        if (pageResult != null
                                                        && pageResult.getContent() != null
                                                        && !pageResult.getContent().isEmpty()) {
                                                trip = pageResult.getContent().get(0);
                                        }

                                        String contextLocation;
                                        if (origin != null && destination != null) {
                                                contextLocation = origin + " → " + destination;
                                        } else if (origin != null) {
                                                contextLocation = origin;
                                        } else {
                                                contextLocation = "khu vực bạn yêu cầu";
                                        }

                                        String suggestion = buildTripSuggestion(trip, contextLocation);

                                        return ChatbotResponseDTO.builder()
                                                        .sessionId(request.getSessionId())
                                                        .timestamp(Instant.now())
                                                        .success(true)
                                                        .message(suggestion)
                                                        .build();
                                }

                                // Tool calls existed but none we handle
                                return ChatbotResponseDTO.builder()
                                                .sessionId(request.getSessionId())
                                                .timestamp(Instant.now())
                                                .success(false)
                                                .error("Unsupported tool call received.")
                                                .message("Mô hình đã yêu cầu một công cụ không được hỗ trợ.")
                                                .build();
                        }

                        // ===== 6) Normal response (no tool call) =====
                        Object contentObj = assistantMessage.getContent();
                        String content = contentObj instanceof String ? (String) contentObj : null;
                        if (content == null || content.isBlank()) {
                                content = "Xin lỗi, mình không tìm được câu trả lời phù hợp.";
                        }

                        return ChatbotResponseDTO.builder()
                                        .sessionId(request.getSessionId())
                                        .timestamp(Instant.now())
                                        .message(content)
                                        .success(true)
                                        .build();

                } catch (Exception e) {
                        return ChatbotResponseDTO.builder()
                                        .sessionId(request.getSessionId())
                                        .timestamp(Instant.now())
                                        .success(false)
                                        .error(e.getMessage())
                                        .message("Đã xảy ra lỗi nội bộ, vui lòng thử lại sau.")
                                        .build();
                }
        }

        /**
         * Build a short, human-friendly suggestion from TripDTO.
         */
        private String buildTripSuggestion(TripDTO trip, String contextLocation) {
                if (trip == null) {
                        String base = "Hiện tại mình chưa tìm thấy chuyến đi phù hợp";
                        if (contextLocation != null) {
                                base += " cho tuyến: " + contextLocation;
                        }
                        return base + ".";
                }

                RouteDTO route = trip.getRoute();
                VehicleDTO vehicle = trip.getVehicle();

                StationDTO origin = route != null ? route.getOrigin() : null;
                StationDTO destination = route != null ? route.getDestination() : null;

                String originName = origin != null && origin.getName() != null ? origin.getName()
                                : "Điểm đi chưa xác định";
                String destinationName = destination != null && destination.getName() != null ? destination.getName()
                                : "Điểm đến chưa xác định";

                Object distanceKm = route != null ? route.getDistanceKm() : null;
                Object baseFare = route != null ? route.getBaseFare() : null;

                String vehicleType = vehicle != null && vehicle.getType() != null
                                ? String.valueOf(vehicle.getType())
                                : "Chưa rõ loại xe";
                String plateNumber = vehicle != null && vehicle.getPlateNumber() != null
                                ? vehicle.getPlateNumber()
                                : "Chưa rõ biển số";

                String departureTime = trip.getDepartureTime() != null
                                ? trip.getDepartureTime().atZoneSameInstant(VN_ZONE).format(DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy"))
                                : "Chưa rõ giờ khởi hành";
                String arrivalTime = trip.getArrivalTime() != null
                                ? trip.getArrivalTime().atZoneSameInstant(VN_ZONE).format(DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy"))
                                : "Chưa rõ giờ đến";

                StringBuilder sb = new StringBuilder();

                sb.append("Mình tìm được một chuyến đi phù hợp cho bạn");
                if (contextLocation != null && !contextLocation.isBlank()) {
                        sb.append(" trên tuyến: ").append(contextLocation);
                }
                sb.append(":\n\n");

                sb.append("- Tuyến: ").append(originName).append(" → ").append(destinationName).append("\n");

                if (distanceKm != null) {
                        sb.append("- Quãng đường: ").append(distanceKm).append(" km\n");
                }

                if (baseFare != null) {
                        sb.append("- Giá cơ bản (chưa khuyến mãi): ").append(baseFare).append(" VND\n");
                }

                sb.append("- Loại xe: ").append(vehicleType).append("\n");
                sb.append("- Biển số: ").append(plateNumber).append("\n");
                sb.append("- Giờ khởi hành: ").append(departureTime).append("\n");
                sb.append("- Giờ đến dự kiến: ").append(arrivalTime).append("\n");

                sb.append("\nNếu bạn muốn, mình có thể hỗ trợ tìm thêm các chuyến khác hoặc lọc theo giờ/loại xe.");

                return sb.toString();
        }
}