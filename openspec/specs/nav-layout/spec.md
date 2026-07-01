## Purpose

Provide an application shell with a persistent left sidebar navigation and route-based page switching. TBD: detailed rationale.

## Requirements

### Requirement: Application shell with left sidebar navigation

The application provides a persistent layout with a left-hand sidebar containing navigation links. The content area renders the current page based on the active route. The sidebar includes exactly three navigation items: Ingredients, Nutrients, and Recipes.

#### Scenario: Sidebar shows all three navigation items
- **GIVEN** the user is authenticated
- **WHEN** the application layout renders
- **THEN** the sidebar displays links labelled "Ingredients", "Nutrients", and "Recipes"

#### Scenario: Clicking a nav item navigates to the corresponding resource page
- **GIVEN** the user is authenticated and viewing any page
- **WHEN** they click "Ingredients" in the sidebar
- **THEN** the URL changes to `/ingredients`
- **AND** the content area shows the ingredients listing page

#### Scenario: Active nav item is visually highlighted
- **GIVEN** the user is on the `/nutrients` page
- **WHEN** the sidebar renders
- **THEN** the "Nutrients" nav item has a visual active state

#### Scenario: Unauthenticated user cannot access the layout
- **GIVEN** the user is not authenticated
- **WHEN** they navigate to `/ingredients`
- **THEN** they are redirected to the login page
- **AND** the sidebar layout is not rendered

### Requirement: Responsive sidebar behavior

On narrow viewports the sidebar should be collapsible or hidden to preserve content space.

#### Scenario: Sidebar collapses on mobile viewport
- **GIVEN** the viewport width is less than 768px
- **WHEN** the application layout renders
- **THEN** the sidebar is hidden by default
- **AND** a hamburger menu button is displayed to toggle it
