# Ticket Management APIs - Cancel, Refund, and Exchange

This document explains the new ticket management APIs that allow users to cancel, refund, and exchange tickets without modifying the underlying entity structure.

## Overview

The ticket management system provides three core operations:
- **Cancel**: Mark a ticket as cancelled
- **Refund**: Request a refund for a ticket with specified amount
- **Exchange**: Request to exchange a ticket for a different trip/route/seat

## API Endpoints

### 1. Cancel Ticket
```
POST /api/tickets/{code}/cancel
```

**Request Body:**
```json
{
  "reason": "Customer requested cancellation due to schedule change"
}
```

**Response:**
```json
{
  "ticket": {
    "id": 123,
    "ticketCode": "TKT-ABC123",
    "status": "CANCELLED",
    "updatedAt": "2025-11-04T22:15:00Z",
    // ... other ticket fields
  },
  "message": "Ticket cancelled successfully",
  "success": true
}
```

### 2. Refund Ticket
```
POST /api/tickets/{code}/refund
```

**Request Body:**
```json
{
  "reason": "Customer requested refund due to medical emergency",
  "refundAmount": 50.00
}
```

**Response:**
```json
{
  "ticket": {
    "id": 123,
    "ticketCode": "TKT-ABC123",
    "status": "REFUND_REQUESTED",
    "refundStatus": "REFUND_REQUESTED",
    "refundReason": "Customer requested refund due to medical emergency",
    "refundAmount": 50.00,
    "refundRequestedAt": "2025-11-04T22:15:00Z",
    "updatedAt": "2025-11-04T22:15:00Z",
    // ... other ticket fields
  },
  "message": "Refund request submitted successfully",
  "success": true
}
```

### 3. Exchange Ticket
```
POST /api/tickets/{code}/exchange
```

**Request Body:**
```json
{
  "newTripId": 456,
  "newRouteId": 789,
  "newSeatId": 101112,
  "reason": "Customer wants to change to earlier departure time"
}
```

**Response:**
```json
{
  "ticket": {
    "id": 123,
    "ticketCode": "TKT-ABC123",
    "status": "EXCHANGE_REQUESTED",
    "exchangeStatus": "EXCHANGE_REQUESTED",
    "exchangeReason": "Customer wants to change to earlier departure time",
    "exchangeRequestedAt": "2025-11-04T22:15:00Z",
    "updatedAt": "2025-11-04T22:15:00Z",
    // ... other ticket fields
  },
  "message": "Exchange request submitted successfully",
  "success": true
}
```

## Business Logic Flow

### Cancel Flow
1. **Validation**: Check if ticket can be cancelled
   - ❌ Ticket is already checked in
   - ❌ Ticket is already cancelled, refunded, or exchanged
   - ✅ Ticket is in valid state for cancellation

2. **Status Update**: Update ticket status and metadata
   - Set `status` = `CANCELLED`
   - Set `updatedAt` = current timestamp

3. **Response**: Return updated ticket with success message

### Refund Flow
1. **Validation**: Check if ticket can be refunded
   - ❌ Ticket is already checked in
   - ❌ Ticket is already refunded, failed, or exchanged
   - ✅ Ticket is in valid state for refund

2. **Status Update**: Update ticket refund information
   - Set `status` = `REFUND_REQUESTED`
   - Set `refundStatus` = `REFUND_REQUESTED`
   - Store `refundReason` from request
   - Store `refundAmount` from request
   - Set `refundRequestedAt` = current timestamp
   - Set `updatedAt` = current timestamp

3. **Response**: Return updated ticket with success message

### Exchange Flow
1. **Validation**: Check if ticket can be exchanged
   - ❌ Ticket is already checked in
   - ❌ Ticket is already exchanged, rejected, refunded, or cancelled
   - ✅ Ticket is in valid state for exchange

2. **Status Update**: Update ticket exchange information
   - Set `status` = `EXCHANGE_REQUESTED`
   - Set `exchangeStatus` = `EXCHANGE_REQUESTED`
   - Store `exchangeReason` from request
   - Store new trip/route/seat information for future processing
   - Set `exchangeRequestedAt` = current timestamp
   - Set `updatedAt` = current timestamp

3. **Response**: Return updated ticket with success message

## Status Transitions

### Cancel Operation
```
BOOKED → CANCELLED
AVAILABLE → CANCELLED
```

### Refund Operation
```
BOOKED → REFUND_REQUESTED → REFUND_APPROVED → REFUND_COMPLETED
AVAILABLE → REFUND_REQUESTED → REFUND_APPROVED → REFUND_COMPLETED
```

### Exchange Operation
```
BOOKED → EXCHANGE_REQUESTED → EXCHANGE_APPROVED → EXCHANGE_COMPLETED
AVAILABLE → EXCHANGE_REQUESTED → EXCHANGE_APPROVED → EXCHANGE_COMPLETED
```

## Error Handling

### Validation Errors
```json
{
  "ticket": null,
  "message": "Ticket cannot be cancelled",
  "success": false
}
```

### Common Error Messages
- `"Ticket not found"` - Ticket with provided code doesn't exist
- `"Ticket cannot be cancelled"` - Ticket is in invalid state for cancellation
- `"Ticket cannot be refunded"` - Ticket is in invalid state for refund
- `"Ticket cannot be exchanged"` - Ticket is in invalid state for exchange

## Business Rules

### Cancellation Rules
- ✅ Can cancel tickets with status: `BOOKED`, `AVAILABLE`
- ❌ Cannot cancel tickets with status: `CANCELLED`, `REFUND_COMPLETED`, `EXCHANGE_COMPLETED`
- ❌ Cannot cancel checked-in tickets

### Refund Rules
- ✅ Can refund tickets with status: `BOOKED`, `AVAILABLE`
- ❌ Cannot refund tickets with status: `REFUND_COMPLETED`, `REFUND_FAILED`, `EXCHANGE_COMPLETED`
- ❌ Cannot refund checked-in tickets
- Refund amount must be greater than 0

### Exchange Rules
- ✅ Can exchange tickets with status: `BOOKED`, `AVAILABLE`
- ❌ Cannot exchange tickets with status: `EXCHANGE_COMPLETED`, `EXCHANGE_REJECTED`, `REFUND_COMPLETED`, `CANCELLED`
- ❌ Cannot exchange checked-in tickets
- Must provide valid new trip, route, and seat IDs

## Implementation Details

### Request DTOs
- **TicketCancelRequestDTO**: Contains cancellation reason (max 500 chars)
- **TicketRefundRequestDTO**: Contains refund reason and amount
- **TicketExchangeRequestDTO**: Contains new trip/route/seat IDs and reason

### Response DTO
- **TicketOperationResponseDTO**: Standardized response with ticket data, message, and success flag

### Validation Methods
- `canCancelTicket()`: Validates cancellation eligibility
- `canRefundTicket()`: Validates refund eligibility  
- `canExchangeTicket()`: Validates exchange eligibility

## Integration Points

### Payment Gateway Integration
For refund operations, the system can integrate with payment gateways:
- VNPay, MoMo, ZaloPay integration points available
- Store `refundTransactionId` when payment processing completes
- Update `refundStatus` to `REFUND_COMPLETED` after successful payment

### Route Service Integration
For exchange operations, the system can integrate with route management:
- Validate new trip/route/seat availability
- Lock new seat while processing exchange
- Release old seat when exchange completes

### Notification System
After operations complete, trigger notifications:
- Email/SMS notifications to customers
- Kafka events for downstream services
- Audit logging for compliance

## Security Considerations

- All endpoints require valid authentication
- Ticket code validation prevents unauthorized access
- Request validation prevents malformed data
- Audit trail maintained through status changes and timestamps

## Testing

### Unit Tests
- Validation logic for each operation type
- Status transition testing
- Error scenario handling
- Request DTO validation

### Integration Tests
- End-to-end API testing
- Database transaction verification
- External service integration testing

## Future Enhancements

### Batch Operations
- Support for cancelling/refunding multiple tickets
- Batch exchange operations for groups

### Advanced Exchange
- Automatic seat selection based on preferences
- Price difference calculations and payments

### Analytics
- Cancellation/refund/exchange metrics
- Customer behavior analytics
- Revenue impact reporting