## ADDED Requirements

### Requirement: Paged resource table

Each resource page displays a table of items fetched from `GET /v1/{resource}` with pagination controls. The table shows the resource items in rows and supports navigating between pages.

#### Scenario: Table loads and displays first page
- **GIVEN** the backend has 25 resource items
- **WHEN** the resource page loads
- **THEN** a GET request is sent to `/v1/{resource}?page=0&size=20`
- **AND** the first 20 items are displayed in the table
- **AND** pagination shows page 1 of 2

#### Scenario: User navigates to next page
- **GIVEN** the first page of results is displayed
- **WHEN** the user clicks the "Next" pagination button
- **THEN** a GET request is sent with `page=1`
- **AND** the next page of items replaces the current content

#### Scenario: Loading state shows a spinner
- **GIVEN** the resource page is loading
- **WHEN** the fetch request is in flight
- **THEN** a loading indicator is displayed instead of the table

#### Scenario: Empty state shows a message
- **GIVEN** the backend returns zero items
- **WHEN** the fetch completes with `totalElements: 0`
- **THEN** a message "No items found" is displayed instead of the table

#### Scenario: Error state shows an error message
- **GIVEN** the backend returns a 5xx error
- **WHEN** the fetch fails
- **THEN** an error message "Failed to load data" is displayed
- **AND** a "Retry" button is shown

### Requirement: Generic column configuration

Each resource page can define its own set of columns. The table component accepts a column configuration that maps data fields to column headers.

#### Scenario: Different resources show different columns
- **GIVEN** the user navigates to the ingredients page
- **WHEN** the table renders
- **THEN** columns include "ID" and "Name"
- **WHEN** the user navigates to the nutrients page
- **THEN** columns include "ID" and "Name"
