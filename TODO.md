# Cycles - Development TODO

## Phase 1: Foundation
- [ ] Set up project structure (React Native / Flutter / PWA)
- [ ] Implement encrypted local storage
- [ ] Design data schema (cycles, logs, settings)
- [ ] Create basic app shell + navigation

## Phase 2: Core Features
- [ ] **Daily Log Screen**
  - [ ] Bleeding intensity selector
  - [ ] Pain level input
  - [ ] Mood picker
  - [ ] Medications checkbox
  - [ ] Notes field
  - [ ] Save to local DB
- [ ] **Calendar View**
  - [ ] Month view with cycle markers
  - [ ] Day detail on tap
  - [ ] Scroll through history

## Phase 3: Predictions
- [ ] Implement cycle prediction algorithm
- [ ] Display prediction window (range, not single date)
- [ ] Show confidence band/percentage
- [ ] "Why this prediction" explainer

## Phase 4: Settings + Privacy
- [ ] Onboarding flow (goal, privacy mode, notifications)
- [ ] Notification preferences (opt-in)
- [ ] Stealth mode option
- [ ] Privacy policy screen

## Phase 5: Data Management
- [ ] Export to CSV
- [ ] Export to PDF summary
- [ ] Delete all data (with confirmation)
- [ ] Verify deletion completeness

## Phase 6: Polish + Launch
- [ ] Accessibility audit
- [ ] Security review
- [ ] Performance optimization
- [ ] App store assets
- [ ] Documentation

---

## Acceptance Criteria Checklist
- [ ] Log flow completes in <10 seconds
- [ ] Log flow requires ≤3 taps
- [ ] Works fully offline
- [ ] No network calls without explicit opt-in
- [ ] Predictions always show uncertainty range
- [ ] Export produces valid, readable files
- [ ] Delete removes ALL local data
