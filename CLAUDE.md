# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

**Cycles** is a privacy-first, local-first menstrual cycle + fertility + pregnancy tracker for Android. It aims to replicate the full Flo feature stack while fixing the trust, UX, and ethical problems users consistently complain about — then adds astrology functionality on top.

**Mission:** Help users track their cycle privately, simply, and without dark patterns.

**Platform:** Android-first (Kotlin, Jetpack Compose). iOS later.

**Why this exists:** Flo and similar apps have deep user resentment around: predatory subscriptions, coerced data consent, inaccurate predictions presented with false confidence, pushy/sexualized content, and privacy violations. Cycles is built to be the ethical alternative.

## Product Principles

### Non-Negotiables (ship blockers)
- **Local-first encrypted storage** — works fully offline, no account required
- **No third-party trackers / ad SDKs** — zero analytics by default
- **No coercive consent** — no "accept data processing or delete account" patterns
- **Core tracking is free forever** — no paywall for logging, calendar, basic predictions, or basic health info
- **Uncertainty-forward predictions** — show confidence bands/windows, never a single magic date
- **Content is opt-in** — no feed, no pushy articles, no "orgasm tips" unless user enables that module

### Target Users
| Persona | Need |
|---------|------|
| Simple Tracker | Calendar/logbook basics, <10 second logging |
| Irregular Cycles (PCOS, perimenopause, stress) | Uncertainty/confidence in predictions, no fake precision |
| TTC / Avoid Pregnancy | Goal-based insights, fertility windows with verification (BBT, OPK) |
| Privacy-Sensitive | Local-only + stealth mode options |
| Astrology-Interested | Cycle insights connected to lunar/zodiac data |

### What Users Actually Want (from competitive research)
- **Control, not a coach** — logbook first, insights only when asked
- **Peace of mind** — UX friction in cycle tracking becomes "I don't feel safe using this"
- **Honesty** — predictions that admit uncertainty, especially for irregular cycles
- **Calm tone** — safe, neutral, respectful; user controls what topics appear
- **Stability** — crashes and data loss are disqualifying (learned from Stardust)

## Architecture

### Data Model (core entities)
- `DailyLog` — bleeding, pain, mood, discharge, meds, sex, temp (BBT), OPK result, notes (all nullable/optional)
- `Cycle` — start/end dates, flow intensity, linked logs
- `Prediction` — period window, fertile window, confidence level, rationale text
- `Pregnancy` — LMP date, ultrasound dating events, due date source, gestational age display prefs
- `Settings` — privacy toggles, content modules, notification prefs, accessibility prefs, display prefs
- `AstrologyProfile` — birth chart data, lunar cycle correlations

### Key Design Patterns
- **Storage:** On-device encrypted SQLite. No network calls until user opts in.
- **Everything nullable:** Users are never forced to answer/log anything.
- **Modular features:** Cycle tracking, TTC mode, pregnancy mode, astrology — each is a toggleable module.
- **Export:** CSV (raw logs) + PDF (doctor summary) — works without account.
- **Predictions:** Rolling average + variance for periods. TTC mode requires OPK/BBT evidence before showing ovulation estimates. Calendar-only predictions always labeled "estimate only."

### Prediction Engine Rules
**Period prediction:**
- Rolling average cycle length + variance → output is a **window**, never a single date
- Confidence derived from variance + data completeness
- Irregular cycle mode: widens windows, drops confidence, stops pretending

**Fertile window / ovulation (two modes):**
1. **Casual mode (default):** Broad fertile window, low confidence, no precise ovulation day. Encourages switching to TTC mode for verification.
2. **TTC mode:** Supports BBT temp input, OPK results, cervical mucus. Uses OPK surge + BBT shift to infer ovulation **range**. Never shows single "Ovulation Day" without evidence. Calendar-only output always marked "estimate only."

### Pregnancy Module Rules
- Due date sources: LMP-based (default), ultrasound dating override, manual adjustment
- Gestational age always shows **both**: completed time (11w4d) AND current week (Week 12)
- One-tap switch between display styles
- No fear-mongering content; content modules are opt-in

### Privacy & Consent Model
- No account required for any core feature
- Separate toggles: crash reports, optional sync, optional research donation
- Disabling data processing never forces account deletion
- "Nuke my data" button with visible confirmation
- **Safety/stealth mode:** disguised app icon/name, local-only storage, quick-hide screen, minimal notifications

### Monetization (ethical constraints)
- **Free forever:** logging, calendar/history, basic predictions, basic health info
- **Paid options:** hosted E2EE sync (subscription), premium features (one-time purchase / lifetime), clinician report templates
- **Subscription rules:** renewal reminders, "confirm to renew" option, easy in-app cancellation
- No paywall on anything a user would describe as "basic health information"

## Explicit "Do Not Build" List
1. Single-date ovulation claims without OPK/BBT evidence
2. Confusing pregnancy week math — always show both representations + explanation
3. Pushy sexual content feed unless user opts into sexual wellness module
4. Trial experiences that feel broken — stability is premium
5. Any dark pattern around subscriptions, consent, or deletion
6. Coerced consent flows ("accept or delete your account")
7. Third-party trackers, ad SDKs, or vague "improve services" data clauses

## MVP Phases

### Phase 1: Foundation
- Project structure (Android, Kotlin, Jetpack Compose)
- Encrypted local storage (SQLite + encryption at rest)
- Data schema with versioned migrations
- Basic app shell + navigation

### Phase 2: Core Tracking
- Today screen (quick log: bleeding, pain, mood, meds, notes — <10 sec, ≤3 taps)
- Calendar view (month view + cycle markers + day detail)
- Period prediction windows + confidence + "Why?" explainer
- Data export (CSV + PDF)

### Phase 3: TTC + Fertility
- TTC mode toggle
- BBT temperature input + OPK result logging
- Rules-based ovulation inference with evidence requirements
- Fertile window display with confidence bands

### Phase 4: Pregnancy Module
- Due date sources (LMP, ultrasound, manual)
- Gestational age display settings (both formats)
- Weekly pregnancy view
- Opt-in content modules

### Phase 5: Settings, Privacy & Safety
- Onboarding flow (goal → privacy mode → notifications, <30 sec)
- Privacy toggles (analytics off by default)
- Stealth mode (disguised icon, quick-hide)
- Notification preferences (opt-in, minimal)

### Phase 6: Astrology
- Birth chart input / profile
- Lunar cycle correlation with menstrual data
- Zodiac-based insights module
- Astrology content (opt-in)

### Phase 7: Polish + Launch
- Accessibility audit (large text, screen reader, plain language tooltips)
- Security review
- Performance optimization
- Play Store assets + listing
- Documentation

## QA Acceptance Criteria
- Log flow completes in <10 seconds, ≤3 taps
- Works fully offline (airplane mode forever)
- No network calls without explicit user opt-in
- Predictions always show uncertainty range + confidence
- TTC mode without OPK/BBT warns "estimate only"
- TTC mode with OPK + BBT shift generates inferred range (not single day)
- Pregnancy dating changes propagate everywhere consistently
- Export produces valid, readable CSV/PDF without account
- Delete removes ALL local data with verification
- App upgrade never wipes data (migration tests)
- Accessibility: large text + screen reader passes on key flows
- Irregular cycles: predictions widen and confidence drops (no fake precision)

## Workflow Orchestration

### 1. Plan Mode Default
- Enter plan mode for ANY non-trivial task (3+ steps or architectural decisions)
- If something goes sideways, STOP and re-plan immediately — don't keep pushing
- Use plan mode for verification steps, not just building
- Write detailed specs upfront to reduce ambiguity

### 2. Subagent Strategy
- Use subagents liberally to keep main context window clean
- Offload research, exploration, and parallel analysis to subagents
- For complex problems, throw more compute at it via subagents
- One task per subagent for focused execution

### 3. Self-Improvement Loop
- After ANY correction from the user, update `tasks/lessons.md` with the pattern
- Write rules for yourself that prevent the same mistake
- Ruthlessly iterate on these lessons until mistake rate drops
- Review lessons at session start for relevant project

### 4. Verification Before Done
- Never mark a task complete without proving it works
- Diff your behavior between main and your changes when relevant
- Ask yourself: "Would a staff engineer approve this?"
- Run tests, check logs, demonstrate correctness

### 5. Demand Elegance (Balanced)
- For non-trivial changes: pause and ask "is there a more elegant way?"
- If a fix feels hacky: "Knowing everything I know now, implement the elegant solution"
- Skip this for simple, obvious fixes — don't over-engineer
- Challenge your own work before presenting it

### 6. Autonomous Bug Fixing
- When given a bug report: just fix it. Don't ask for hand-holding
- Point at logs, errors, failing tests — then resolve them
- Zero context switching required from the user
- Go fix failing CI tests without being told how

## Task Management

1. **Plan First**: Write plan to `tasks/todo.md` with checkable items
2. **Verify Plan**: Check in before starting implementation
3. **Track Progress**: Mark items complete as you go
4. **Explain Changes**: High-level summary at each step
5. **Document Results**: Add review section to `tasks/todo.md`
6. **Capture Lessons**: Update `tasks/lessons.md` after corrections

## Core Principles

- **Simplicity First**: Make every change as simple as possible. Impact minimal code.
- **No Laziness**: Find root causes. No temporary fixes. Senior developer standards.
- **Minimal Impact**: Changes should only touch what's necessary. Avoid introducing bugs.

## IMPORTANT

Before completing any task, run these checks:
- Scan for hardcoded secrets, API keys, passwords
- Check for SQL injection, shell injection, path traversal
- Verify all user inputs are validated
- Run the test suite
- Check for type errors
