# Notification System Design

## Overview
The Notification System provides REST APIs for managing user notifications. It supports basic CRUD operations and a specialized sorting endpoint to fetch the Top 10 Priority Notifications.

## Data Model
- **id**: UUID string
- **type**: Enum (`Placement`, `Result`, `Event`)
- **message**: String content of the notification
- **timestamp**: Instant / ISO-8601 formatted datetime string
- **isRead**: Boolean flag

## Priority Logic
Priority is strictly defined as:
1. **Placement** (Highest)
2. **Result**
3. **Event** (Lowest)

If two notifications have the same priority type, the one with the latest (most recent) `timestamp` takes precedence.

## REST APIs

### `GET /notifications`
Returns a list of all current notifications in the system.

### `POST /notifications`
Creates a new notification.
- **Request Body**:
  ```json
  {
    "type": "Placement",
    "message": "New placement drive announced"
  }
  ```
- **Response**: The created Notification object with generated `id`, current `timestamp`, and `isRead=false`.

### `PATCH /notifications/{id}/read`
Marks a specific notification as read (`isRead=true`).
- **Response**: 204 No Content or the updated Notification.

### `DELETE /notifications/{id}`
Deletes a specific notification by ID.
- **Response**: 204 No Content.

### `GET /notifications/top`
Returns the Top 10 priority notifications.
- The system will filter out notifications that are read (optional, depending on business logic, but typically priority queues show active alerts. I'll return the top 10 irrespective of read status, but sorted correctly).
- Sorting follows the `Priority Logic` above.
- Returns an array of up to 10 Notification objects.

## Storage
For this implementation, an in-memory thread-safe collection (e.g., `ConcurrentHashMap`) is used to store notifications, mapping UUIDs to Notification objects.

## Concurrency
The sorting operation creates a snapshot or works on a stream of values to ensure thread safety during concurrent reads and writes.
