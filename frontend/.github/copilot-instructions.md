<!-- Copilot instructions for the VHS frontend project -->

# Guidance for AI coding agents

This front-end is a small React (Vite) app that talks to a local backend at http://localhost:8080/api. Use the notes below to make safe, useful edits quickly.

- Project entry points and commands

  - Dev: `npm run dev` (runs `vite`) — fast HMR. Files compiled from `src/`.
  - Build: `npm run build` (Vite build). Preview: `npm run preview`.
  - Lint: `npm run lint` (ESLint).
  - See `package.json` for scripts and dependencies.

- Big picture architecture

  - Single-page React app (no global state library). `src/App.jsx` is the central coordinator:
    - App fetches users (`GET /api/users`) and rentals (`/api/rentals` or `/api/users/{id}/rentals`).
    - App passes data and callbacks down to components; components often call `onActionComplete()` or use `refreshTrigger` props to request a parent refresh.
  - Components in `src/components/` follow a simple pattern:
    - Presentational props: `users`, `rentals`, `isLoading`, `currentUser`.
    - Action callbacks: `onActionComplete`, `onVhsAdded`, etc.
    - Some components (e.g., `VhsList.jsx`) perform their own fetches using `axios` and accept `refreshTrigger` to re-run effects.

- API and data shapes (discoverable examples)

  - Users: `GET /api/users` -> array of { id, name, email } (see `App.jsx`, `UserList.jsx`).
  - VHS: `GET /api/vhs` -> array of { id, title, releaseYear, genre } (see `VhsList.jsx`).
  - Rentals: `GET /api/rentals` or `GET /api/users/{id}/rentals` -> rentals with fields like `returnDate` and nested `vhs` object (see `App.jsx` useMemo for rented vhs ids).
  - Mutations use standard REST verbs: `POST /api/users`, `POST /api/rentals`, `DELETE /api/vhs/{id}`, `DELETE /api/users/{id}`.

- Conventions and patterns to preserve

  - CSS modules: component styles are adjacent files named `Component.module.css` and imported as `styles` (e.g., `VhsList.module.css`).
  - UX: components use `window.confirm()` for destructive actions and inline alerts/messages for validation/back-end errors. Keep these behaviors unless explicitly refactoring UX.
  - Error handling: components expect server errors in `err.response.data.message` or `err.response.data.errors`. Preserve this inspection when surfacing errors to users.
  - Refresh pattern: prefer using `onActionComplete()` or `refreshTrigger` to refresh lists rather than forcing parent internals.

- Refactor hints (non-breaking)

  - If centralizing axios base URL, avoid changing inline API strings in a single PR — provide a small, well-tested migration (e.g., create `src/api.js` exporting an axios instance with baseURL and update imports across components).
  - When adding new endpoints, follow existing success/error UX: set local loading state, call `onActionComplete()` when appropriate, and display back-end messages when present.

- Files to reference when editing

  - `src/App.jsx` — central data fetching and refresh orchestration.
  - `src/components/VhsList.jsx`, `UserList.jsx`, `AddUserForm.jsx` — examples of axios usage, validation, and error display.
  - `package.json` — scripts and dependencies (Vite + React + axios + Tailwind plugins).

- Safety checklist for any PR
  - Run `npm run dev` to verify HMR and UI flows.
  - Run `npm run lint` and fix any ESLint issues introduced.
  - Manually test create/delete/rent flows against a local backend at `http://localhost:8080` or mock API responses when adding tests.

If anything here looks off or you'd like more detail about a specific file or flow, tell me which area to expand and I will update these instructions.
