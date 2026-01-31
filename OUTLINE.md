# Cycles - Product Outline

## 1. Product Goals

**User Promise:** This app helps you track your cycle privately, simply, and without dark patterns.

## 2. Non-Negotiables (Trust/Ethics)

- [ ] Local-first by default (no account required)
- [ ] No third-party trackers or ads SDKs
- [ ] Clear consent toggles (no coercion)
- [ ] Data export + delete that actually works
- [ ] Privacy policy matches reality

## 3. Personas + JTBD

### Target Users
| Persona | Need |
|---------|------|
| Simple Tracker | Calendar/logbook basics |
| Irregular Cycles | Uncertainty/confidence in predictions |
| TTC / Avoid Pregnancy | Goal-based insights, fertility windows |
| Privacy-Sensitive | Local-only + stealth options |

### Jobs To Be Done
- "When I log symptoms, I want it to take **<10 seconds**…"
- "When my cycle is irregular, I want the app to **admit uncertainty**…"
- "When I'm anxious about pregnancy, I want **calm, clear windows**…"

## 4. MVP Scope

### Core Tracking
- Daily log: bleeding, pain, mood, meds, notes
- Calendar view (history)
- Predictions: period window + confidence band
- Notifications (opt-in, minimal)

### Data Architecture
- On-device encrypted store
- Export: CSV + PDF summary
- Delete: full local wipe, no account needed

### Safety + Privacy
- No analytics by default
- Minimal permissions
- Threat model documented

## 5. Screens + Flows

| Flow | Steps | Acceptance Criteria |
|------|-------|---------------------|
| Onboarding | Goal → Privacy mode → Notifications | <30 seconds |
| Log Today | Open → Tap symptoms → Save | <10 sec, ≤3 taps, works offline |
| Review History | Calendar → Day detail → Trends | — |
| Prediction | Window + confidence + "why" | Shows range, not single date |
| Export | Date range → Format → Share | CSV + PDF options |
| Delete | Confirm → Wipe → Verify | Complete local deletion |

## 6. Data Model + Storage

- **Storage:** Local encrypted SQLite / IndexedDB
- **Backup:** Optional encrypted cloud (post-MVP)
- **Schema:** Cycles, DailyLogs, Settings

## 7. Security + Privacy Requirements

- No network calls until user opts into sync
- Encryption at rest
- Stealth mode option (disguised icon/name)
- No PII in logs or crash reports

## 8. Monetization Model

- Core tracking: **always free**
- Options: donation, paid add-ons, paid sync
- No paywalls on essential features

## 9. Metrics + QA

Privacy-respecting metrics only (opt-in, aggregated):
- App opens (local count)
- Feature usage (local)

### Acceptance Tests
- [ ] Log flow <10 seconds
- [ ] No network calls without consent
- [ ] Predictions show uncertainty range
- [ ] Export produces valid CSV/PDF
- [ ] Delete removes all data

## 10. Risks + Mitigations

| Risk | Mitigation |
|------|------------|
| Data breach | Local-first, encryption |
| Legal/subpoena | Minimal data, no cloud default |
| User confusion | Clear onboarding, simple UI |

## 11. Timeline / Milestones

1. **Week 1-2:** Core data model + local storage
2. **Week 3-4:** Daily log + calendar UI
3. **Week 5-6:** Predictions engine
4. **Week 7-8:** Export/delete + polish
5. **Week 9:** Testing + security audit
6. **Week 10:** MVP release
