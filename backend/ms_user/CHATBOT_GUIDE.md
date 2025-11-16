# RideHub AI Chatbot - User Guide

## Overview

The RideHub AI Chatbot is an intelligent conversational assistant that can help you with transportation, ride-sharing, and mobility-related questions. It features **function calling** capabilities, allowing it to automatically access and interact with RideHub's APIs to provide real-time trip suggestions and statistics.

## Features

### 🤖 Conversational AI
- Natural language interaction
- Context-aware conversations
- Session management
- Message history tracking

### 🔧 Function Calling
- **Trip Suggestions**: Get personalized trip recommendations
- **Trip Statistics**: Access real-time route and vehicle statistics
- **User Data**: Retrieve profile and preference information
- **Dynamic API Calls**: Automatic API integration based on user intent

## Getting Started

### 1. API Endpoint

**Base URL**: `http://localhost:8082/api/chatbot`

### 2. Authentication

The chatbot uses OAuth2 authentication. Include your access token in the request headers:

```http
Authorization: Bearer <your-access-token>
Content-Type: application/json
```

### 3. Basic Chat Request

Send a message to the chatbot:

```bash
curl -X POST http://localhost:8082/api/chatbot/chat \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <your-token>" \
  -d '{
    "message": "Hello, I need help with trip planning",
    "sessionId": null,
    "userId": "user123"
  }'
```

**Response Format**:
```json
{
  "sessionId": "456",
  "message": "Hello! I'm here to help you plan your trips. Where would you like to go?",
  "timestamp": "2025-11-19T14:30:00Z",
  "success": true
}
```

## Function Calling Examples

### 🚗 Trip Suggestions

The chatbot can automatically suggest trips when you ask for travel recommendations:

#### Example 1: Basic Trip Request
```bash
curl -X POST http://localhost:8082/api/chatbot/chat \
  -H "Content-Type: application/json" \
  -d '{
    "message": "I want to travel from New York to Boston tomorrow with a medium budget",
    "sessionId": "456",
    "userId": "user123"
  }'
```

**What happens behind the scenes:**
1. AI identifies trip suggestion intent
2. Calls `suggest_trip` function with parameters:
   - origin: "New York"
   - destination: "Boston" 
   - travelDate: "2025-11-20"
   - budgetRange: "medium"
3. Function calls TripRecommendationService
4. AI formats the response conversationally

**Sample Response**:
```json
{
  "sessionId": "456",
  "message": "I've found some great trip options from New York to Boston for tomorrow! Here are my recommendations:\n\n🚗 Standard Car - $40-60, 2 hours\n🚂 Train - $35-50, 2.5 hours\n🚌 Premium Bus - $30-45, 2.5 hours\n\nRecommendation: Book in advance for better prices! Your trip suggestion has been saved with ID 123.",
  "timestamp": "2025-11-19T14:35:00Z",
  "success": true
}
```

#### Example 2: Budget-Conscious Trip
```bash
{
  "message": "Find me the cheapest way to get from Los Angeles to San Francisco this weekend",
  "sessionId": "456",
  "userId": "user123"
}
```

#### Example 3: Luxury Travel
```bash
{
  "message": "I need a premium travel option from Chicago to Miami next week",
  "sessionId": "456", 
  "userId": "user123"
}
```

### 📊 Trip Statistics

Get real-time statistics for routes and vehicles:

#### Example 1: Route Statistics
```bash
{
  "message": "Show me statistics for route 123",
  "sessionId": "456",
  "userId": "user123"
}
```

**Behind the scenes:**
1. AI identifies statistics request
2. Calls `get_trip_statistics` function with routeId: 123
3. Retrieves data from TripStatisticsService
4. Formats statistics in readable format

**Sample Response**:
```json
{
  "sessionId": "456",
  "message": "Trip Statistics for Route 123:\n\n📊 Total Bookings: 150\n💰 Total Revenue: $12500.50\n📈 Average Price: $83.37\n🪑 Occupancy Rate: 75%\n❌ Cancellation Rate: 5%\n⭐ Customer Satisfaction: 4.2/5.0\n🎯 Popular Seat Types: Window, Economy\n⏰ Peak Travel Times: 8-10 AM, 5-7 PM\n📅 Valid Period: 2025-10-19 to 2025-11-19\n📈 Trend: Increasing trend: +15% this month",
  "timestamp": "2025-11-19T14:40:00Z",
  "success": true
}
```

#### Example 2: Vehicle-Specific Statistics
```bash
{
  "message": "What are the statistics for CAR vehicles on route 456?",
  "sessionId": "456",
  "userId": "user123"
}
```

#### Example 3: Period-Based Statistics
```bash
{
  "message": "Show me monthly statistics for route 789",
  "sessionId": "456",
  "userId": "user123"
}
```

## Session Management

### Continuing Conversations

Use the `sessionId` from previous responses to maintain conversation context:

```bash
{
  "message": "That sounds good, can you find me a hotel in Boston too?",
  "sessionId": "456",
  "userId": "user123"
}
```

### Starting New Sessions

Set `sessionId` to `null` or omit it to start a new conversation:

```bash
{
  "message": "Help me plan a business trip",
  "sessionId": null,
  "userId": "user123"
}
```

## Available Functions

### 1. suggest_trip

**Purpose**: Generate AI-powered trip recommendations

**Parameters**:
- `origin` (required): Starting location
- `destination` (required): Destination  
- `travelDate` (optional): Travel date in YYYY-MM-DD format
- `budgetRange` (optional): "low", "medium", or "high"

**Example Triggers**:
- "I want to go from X to Y"
- "Plan a trip to Boston"
- "Find me a ride to the airport"
- "What are my options for getting to downtown?"

### 2. get_trip_statistics

**Purpose**: Retrieve statistics for routes and vehicles

**Parameters**:
- `routeId` (required): Numeric route identifier
- `vehicleType` (optional): "CAR", "BUS", "TRAIN", "MOTORCYCLE", "BICYCLE"
- `period` (optional): "daily", "weekly", "monthly"

**Example Triggers**:
- "Show me statistics for route 123"
- "How's route 456 performing?"
- "What are the stats for buses on route 789?"
- "Monthly statistics for route 101"

## Error Handling

### Common Error Responses

#### 1. Missing API Key
```json
{
  "error": "AI service temporarily unavailable",
  "timestamp": "2025-11-19T14:45:00Z",
  "success": false
}
```

**Solution**: Set the `ZAI_API_KEY` environment variable

#### 2. Invalid Request
```json
{
  "error": "Invalid request parameters",
  "timestamp": "2025-11-19T14:45:00Z", 
  "success": false
}
```

**Solution**: Check request format and required fields

#### 3. Function Execution Error
```json
{
  "sessionId": "456",
  "message": "I encountered an error while processing your request. The trip suggestion service is currently unavailable. Please try again later.",
  "timestamp": "2025-11-19T14:45:00Z",
  "success": true
}
```

## Configuration

### Environment Variables

```bash
# Required: Z.AI API Key
export ZAI_API_KEY=your-zai-api-key-here

# Optional: Override default settings
export ZAI_CHATBOT_MODEL=glm-4.5-flash
export ZAI_CHATBOT_TEMPERATURE=1.0
export ZAI_CHATBOT_MAX_TOKENS=1024
```

### Application Properties

```yaml
zai:
  chatbot:
    api-key: ${ZAI_API_KEY}
    model: glm-4.5-flash
    temperature: 1.0
    max-tokens: 1024
    base-url: https://api.z.ai/api/paas/v4/
```

## Best Practices

### 1. Message Design
- Be specific about origins and destinations
- Include dates and time preferences
- Mention budget constraints
- Specify vehicle preferences

### 2. Session Management
- Reuse session IDs for related queries
- Start new sessions for different topics
- Include userId for personalization

### 3. Error Handling
- Check the `success` field in responses
- Implement retry logic for failed requests
- Provide fallback options when AI is unavailable

### 4. Performance
- Use appropriate session timeouts
- Limit conversation history length
- Cache frequently requested statistics

## Advanced Usage

### Multi-Turn Conversations

```bash
# Turn 1
{
  "message": "I need to go from New York to Washington DC",
  "sessionId": null,
  "userId": "user123"
}

# Response includes sessionId: 789

# Turn 2  
{
  "message": "Make it for tomorrow morning",
  "sessionId": "789",
  "userId": "user123"
}

# Turn 3
{
  "message": "I prefer the train option",
  "sessionId": "789", 
  "userId": "user123"
}
```

### Complex Queries

The chatbot can handle complex, multi-part requests:

```bash
{
  "message": "I need to travel from Boston to Philadelphia next Friday with a high budget, and I want to see the statistics for that route first",
  "sessionId": null,
  "userId": "user123"
}
```

**Expected Behavior**:
1. First calls `get_trip_statistics` for the route
2. Then calls `suggest_trip` with high budget preference
3. Provides comprehensive response with both statistics and suggestions

## Health Check

Monitor the chatbot service status:

```bash
curl -X GET http://localhost:8082/api/chatbot/health
```

**Response**: `Chatbot service is running`

## Troubleshooting

### Common Issues

1. **Function Not Called**
   - Check if your message clearly indicates intent
   - Use trigger phrases like "suggest", "show me", "find me"

2. **Missing Parameters**
   - Ensure required parameters (origin, destination, routeId) are included
   - Be specific about locations and preferences

3. **Session Loss**
   - Always include the sessionId from previous responses
   - Store session IDs in your application state

4. **Rate Limiting**
   - Implement exponential backoff for failed requests
   - Monitor API usage and implement caching

### Debug Mode

Enable debug logging:

```yaml
logging:
  level:
    com.ridehub.user.service.ZaiChatbotService: DEBUG
```

## Support

For issues or questions:
1. Check the application logs
2. Verify API key configuration
3. Test with the health endpoint
4. Review function calling examples above

---

**Last Updated**: November 19, 2025
**Version**: 1.0.0
**API Version**: v1