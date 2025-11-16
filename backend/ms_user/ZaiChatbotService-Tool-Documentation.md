# ZaiChatbotService Tool Documentation

## Overview
The ZaiChatbotService provides AI-powered chat functionality with function calling capabilities to retrieve trip information from the ms_route microservice.

## Available Tools

### 1. get_trip_info
Retrieves trip information from the TripResourceMsrouteApi.

**Parameters:**
- `tripId` (integer, optional): Specific trip ID to retrieve detailed information
- `origin` (string, optional): Origin location for searching trips
- `destination` (string, optional): Destination location for searching trips

**Usage Examples:**
- "Show me trip 123" → Calls get_trip_info with tripId: 123
- "Find trips from Hanoi to HCMC" → Calls get_trip_info with origin: "Hanoi", destination: "HCMC"

## API Integration

### Methods Used
- `TripResourceMsrouteApi.getTripWithHttpInfo(tripId)` - Get single trip by ID
- `TripResourceMsrouteApi.getAllTripsWithHttpInfo(queryParams)` - Get all trips with filtering

### Data Sources
- **TripDTO**: Main trip information including:
  - id, tripCode, departureTime, arrivalTime, baseFare
  - route (origin/destination stations)
  - vehicle (type, plateNumber)
  - driver (firstName, lastName)
  - attendant (optional)

### Response Format
The chatbot formats trip information in a user-friendly way with emojis:

```
🚌 Trip Information:

🆔 Trip ID: 123
🎫 Trip Code: TRIP001
📍 Route: Hanoi → Ho Chi Minh City
⏰ Departure: 2025-01-20T08:00:00Z
🏁 Arrival: 2025-01-20T18:00:00Z
🎯 Occasion Factor: 1.0
🚐 Vehicle: Bus (29A-12345)
👨‍✈️ Driver: Nguyen Van A
```

## Function Calling Flow

1. **User Input**: User asks about trip information
2. **AI Analysis**: Z AI determines if function calling is needed
3. **Function Execution**: Chatbot calls appropriate API method
4. **Data Processing**: Results are formatted for user-friendly display
5. **Response**: Formatted trip information is returned to user

## Error Handling

- **API Errors**: Graceful fallback with error messages
- **Network Issues**: "AI service temporarily unavailable"
- **Data Not Found**: "Trip with ID X not found" or "No trips found from X to Y"
- **Parsing Errors**: "Error retrieving trip information: [details]"

## Configuration

The service uses the following configuration from `ZaiChatbotProperties`:
- `model`: AI model name (e.g., "glm-4.6")
- `temperature`: Response randomness (0.0-1.0)
- `maxTokens`: Maximum response length

## Dependencies

- Z AI Client for chat completion
- TripResourceMsrouteApi for trip data
- Jackson for JSON parsing
- Spring Boot for service management

## Usage in Chat

Users can interact with the chatbot using natural language:

- "What trips are available from Hanoi to Da Nang?"
- "Tell me about trip 456"
- "Show me buses going from Hue to Quy Nhon"
- "Get details for trip ABC123"

The chatbot will automatically detect the intent and call the appropriate function to retrieve real-time trip data from the microservice.