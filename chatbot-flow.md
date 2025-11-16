# RideHub Staff Chatbot Flow & Service Integration

## Overview
This document outlines the staff chatbot functionality and service integration flow for RideHub, focusing on staff operations across ms_user, ms_route, and ms_booking services.

**Key Distinction:**
- **Client** = End customers who purchase tickets
- **User** = Staff members who manage the system via chatbot

## Chatbot Architecture (ms_user)

### Core Entities
- **ChatSession**: Manages staff conversation context and state
- **ChatMessage**: Stores individual staff messages with intent recognition
- **UserQuery**: Tracks staff queries and responses
- **TripRecommendation**: AI-powered trip suggestions for customers
- **UserStatistics**: Staff performance and activity analytics

### Staff Chatbot Flow

#### 1. Staff Session Initialization
```
Staff → Gateway → ms_user (ChatSession)
- Create new ChatSession with unique sessionId
- Initialize staff context and permissions
- Link to existing AppUser (staff) via Keycloak authentication
- Log staff activity for audit trail
```

#### 2. Intent Recognition & Query Processing
```
Staff Message → NLP Processing → UserQuery → Response Generation
```

**Supported Query Types:**
- `BOOKING_MANAGEMENT`: Create, modify, cancel bookings
- `CUSTOMER_SUPPORT`: Lookup customer info and history
- `TRIP_OPERATIONS`: Manage trips, seats, and schedules
- `SYSTEM_MONITORING`: Revenue, occupancy, and performance metrics
- `STAFF_OPERATIONS`: Staff-specific administrative tasks

## Staff Capabilities & Permissions

### Role-Based Access Control
```java
// Staff Roles and Permissions
AGENT: Basic booking creation, customer lookup
SUPERVISOR: Booking modifications, refunds, basic reports
MANAGER: Full booking operations, advanced reports, staff management
ADMIN: System configuration, full access to all operations
```

### Customer Data Access Patterns
```
1. Customer Identification:
   - Phone number lookup (most common)
   - Email address lookup
   - Booking code lookup
   - ID number lookup (with proper authorization)

2. Customer Information Available to Staff:
   - Basic contact info (name, phone, email)
   - Booking history (with permission level)
   - Payment status (masked sensitive data)
   - Travel preferences (for recommendations)

3. Restricted Customer Data:
   - Full payment details (only finance roles)
   - ID document images (only with customer consent)
   - Sensitive personal information
   - Other customers' data on same booking
```

### Staff Operation Examples

#### Agent Level Operations
```
"Create booking for 0912345678, Hanoi to Da Nang tomorrow"
"Check status of booking ABC123"
"Find customer by phone 0909876543"
"Show available trips for Hanoi-HCMC today"
```

#### Supervisor Level Operations
```
"Cancel booking XYZ789 and process refund"
"Modify booking ABC456, change to next trip"
"Apply promotion SAVE20 to booking DEF123"
"Show daily revenue report"
"Handle customer complaint for booking GHI456"
```

#### Manager Level Operations
```
"Show staff performance for this week"
"Generate monthly occupancy report"
"Approve refund over 1,000,000 VND"
"Configure new promotion rules"
"Manage staff permissions and roles"
```

#### 3. Cross-Service Integration Flow

### A. Customer Booking Creation Flow (Staff-Assisted)
```
1. Staff: "Create booking for customer 09xx-xxx-xxx, Hanoi to HCMC tomorrow"
2. ms_user: Parse intent → UserQuery(BOOKING_MANAGEMENT)
3. ms_user → ms_route: Search available routes
   - Query: Route(origin=Hanoi, destination=HCMC)
   - Response: Available routes with stations
4. ms_user → ms_route: Get schedules and trips
   - Query: Trip with date filters
   - Response: Available trips with pricing
5. Staff: "Book 2 seats on trip XYZ for this customer"
6. ms_user → ms_route: Validate trip availability
   - Check Trip details and seat availability
   - Get SeatMap and pricing
7. ms_user → ms_booking: Initiate booking
   - Create Booking with DRAFT status
   - Link to customer via phone/email lookup
   - Generate idempotencyKey
8. ms_user → ms_route: Lock seats
   - Create SeatLock entries
9. ms_user: Present booking confirmation to staff
10. Staff: Confirm payment method and process
11. ms_user → ms_booking: Process payment
    - Update Booking status to AWAITING_PAYMENT
    - Create PaymentTransaction
12. ms_booking: Handle payment webhook
    - Update status to PAID/CONFIRMED
13. ms_user: Update StaffStatistics (operations performed)
```

### B. Customer Support Flow
```
1. Staff: "Check booking status for XYZ123"
2. ms_user: Parse intent → UserQuery(CUSTOMER_SUPPORT)
3. ms_user → ms_booking: Query booking details
   - Filter by bookingCode
   - Return Booking with Tickets and Payment status
4. ms_user: Format and present status to staff
5. Staff: "Cancel this booking and process refund"
6. ms_user → ms_booking: Process cancellation
   - Update Booking status to CANCELED
   - Create refund transaction
7. ms_user: Update StaffStatistics (cancellations processed)
```

### C. Trip Operations Flow
```
1. Staff: "Show all trips departing in next 2 hours"
2. ms_user: Parse intent → UserQuery(TRIP_OPERATIONS)
3. ms_user → ms_route: Query active trips
   - Filter by departureTime window
   - Return Trip with Vehicle and Staff assignments
4. Staff: "Lock seat A5 for customer on trip TRIP789"
5. ms_user → ms_route: Create seat lock
   - Validate seat availability
   - Create SeatLock with timeout
6. ms_user: Update StaffStatistics (operations performed)
```

### D. System Monitoring Flow
```
1. Staff: "Show today's revenue summary"
2. ms_user: Parse intent → UserQuery(SYSTEM_MONITORING)
3. ms_user → ms_booking: Query revenue data
   - Sum completed payments by date
   - Calculate metrics (occupancy, cancellations)
4. ms_user: Format and present dashboard data
5. Staff: "Show occupancy rates for Hanoi-HCMC route"
6. ms_user → ms_route + ms_booking: Calculate occupancy
   - Get trip capacity vs booked seats
   - Return percentage and trends
```

## Service Communication Patterns

### 1. Synchronous API Calls
- **ms_user → ms_route**: Route search, trip details, seat availability
- **ms_user → ms_booking**: Booking creation, status queries
- **ms_user → ms_promotion**: Apply promotions (if needed)

### 2. Asynchronous Events (Kafka)
```
ms_booking → Kafka → ms_user:
- BookingCreatedEvent
- BookingStatusChangedEvent
- PaymentCompletedEvent

ms_route → Kafka → ms_user:
- TripCreatedEvent
- SeatLockStatusChangedEvent
```

## Data Flow Details

### Trip Recommendation Algorithm (Staff-Assisted)
```java
// In TripRecommendation entity
- userId: Staff member ID who made the recommendation
- origin/destination: From customer request
- travelDate: Customer's requested date
- recommendedTrips: JSON array of trip options for customer
- confidenceScore: AI confidence level
- isBooked: Whether customer accepted the recommendation
```

### Staff Statistics Updates
```java
// Triggers for updating UserStatistics (Staff Activity):
- Booking created/modified/cancelled by staff
- Customer support interactions handled
- Trip operations performed
- Payment transactions processed
- System queries executed
- Performance metrics (response time, resolution rate)
```

## API Endpoints Required

### ms_user Service (Staff Chatbot)
```
POST /api/chat/sessions - Create staff chat session
POST /api/chat/messages - Send staff message
GET /api/chat/sessions/{id}/messages - Get chat history
POST /api/chat/query - Process staff query
GET /api/recommendations - Get trip recommendations for customers
GET /api/users/{id}/statistics - Get staff performance stats
POST /api/customers/lookup - Find customer by phone/email
GET /api/staff/permissions - Get staff permissions
```

### Integration Endpoints (Staff Operations)
```
ms_route:
GET /api/routes/search - Search routes for customers
GET /api/trips/search - Search trips for booking
GET /api/trips/{id}/seats - Get seat availability
POST /api/seat-locks - Lock seats for customers
GET /api/trips/active - Get currently active trips
GET /api/trips/occupancy - Get occupancy rates

ms_booking:
POST /api/bookings - Create customer booking
GET /api/bookings/search - Search bookings by code/customer
GET /api/bookings/customer/{customerId} - Get customer bookings
PUT /api/bookings/{id}/status - Update booking status
POST /api/bookings/{id}/cancel - Cancel booking with refund
GET /api/bookings/revenue/daily - Get daily revenue reports
GET /api/bookings/occupancy/route - Get route occupancy metrics

ms_promotion:
GET /api/promotions/applicable - Find applicable promotions
POST /api/promotions/apply - Apply promotion to booking
```

## Error Handling & Edge Cases

### 1. Service Unavailability
- Implement circuit breakers for critical operations
- Provide fallback responses for customer-facing queries
- Queue failed requests for retry
- Notify staff of system issues via chatbot

### 2. Data Consistency
- Use idempotency keys for booking creation
- Implement seat lock timeout handling
- Handle payment webhook retries
- Ensure audit trail for all staff operations

### 3. Staff Experience
- Provide clear operation confirmations
- Handle ambiguous queries with clarification prompts
- Offer alternative suggestions when no exact matches
- Show operation progress and estimated completion times
- Provide error recovery suggestions

### 4. Customer Data Protection
- Validate staff permissions before customer data access
- Log all customer data accesses for audit
- Implement data masking for sensitive information
- Enforce role-based access controls

## Security Considerations

### 1. Staff Authentication
- OAuth2 token validation across services
- Staff context propagation with role information
- Secure session management with timeout
- Multi-factor authentication for sensitive operations

### 2. Staff Authorization
- Role-based access control (RBAC)
- Permission validation for each operation
- Customer data access restrictions
- Audit logging for all staff actions
- Rate limiting per staff member
- Input validation and sanitization

### 3. Customer Data Protection
- GDPR compliance for customer data
- Data encryption in transit and at rest
- Customer consent tracking
- Right to be forgotten implementation

## Performance Optimization

### 1. Caching Strategy
- Redis cache for frequent route searches
- User preferences caching
- Trip recommendations cache

### 2. Database Optimization
- Indexes on ChatSession(userId, startedAt)
- Composite indexes on Trip(departureTime, routeId)
- Partitioning for large chat history tables

## Monitoring & Analytics

### 1. Staff Chatbot Metrics
- Query success rate per staff member
- Average response time for operations
- Staff satisfaction scores
- Intent recognition accuracy
- Operations per hour per staff
- Error rates and resolution times

### 2. Business Metrics
- Staff-assisted booking conversion rate
- Customer support resolution time
- Popular routes and times (staff insights)
- Revenue attribution from staff operations
- Staff productivity metrics
- Customer satisfaction with staff-assisted bookings

### 3. System Performance
- API response times across services
- Database query performance
- Kafka message processing times
- Cache hit rates
- System availability and uptime

## Future Enhancements

### 1. Advanced AI Features for Staff
- Natural language understanding improvements
- Predictive customer service suggestions
- Voice support for hands-free operations
- Multi-language support for diverse staff
- Automated customer sentiment analysis

### 2. Proactive Staff Assistance
- High-demand route alerts
- Customer issue escalation predictions
- Automated rebooking suggestions during disruptions
- Staff shift optimization recommendations
- Inventory management alerts

### 3. Advanced Analytics
- Staff performance benchmarking
- Customer behavior pattern recognition
- Revenue optimization suggestions
- Predictive maintenance alerts for vehicles
- Dynamic pricing recommendations

## Implementation Priority

### Phase 1: Core Staff Functionality
1. Staff authentication and session management
2. Basic booking search and management
3. Customer lookup by phone/email
4. Simple trip status queries
5. Basic error handling and validation

### Phase 2: Enhanced Staff Operations
1. Advanced booking management (modify, cancel, refund)
2. Customer support workflows
3. Trip operations (seat locks, schedule management)
4. Staff performance tracking
5. Promotion application for customers
6. Basic reporting and analytics

### Phase 3: Advanced Staff AI Features
1. Predictive customer service suggestions
2. Automated workflow optimization
3. Advanced analytics and reporting
4. Voice support for staff operations
5. Multi-language support
6. Proactive system alerts and recommendations

## Testing Strategy

### 1. Unit Tests
- Staff intent recognition logic
- Service integration mocks
- Data transformation functions
- Permission validation logic
- Customer data protection functions

### 2. Integration Tests
- End-to-end staff chatbot flows
- Cross-service communication (ms_user ↔ ms_route ↔ ms_booking)
- Error scenarios and recovery
- Payment processing workflows
- Seat lock timeout handling

### 3. Staff Acceptance Tests
- Real staff operation scenarios
- Performance under peak load
- Security and permission testing
- Customer data privacy compliance
- Multi-user concurrent operations

### 4. Security Testing
- Penetration testing for staff access
- Data breach prevention
- Audit trail verification
- Role-based access control validation