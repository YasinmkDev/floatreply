# FloatReply — Floating AI Comment Overlay for LinkedIn (Simulator)

You are tired of writing LinkedIn comments from scratch. FloatReply is the
prototype for fixing that: a floating overlay that sits on top of a LinkedIn
feed, reads the post underneath it, and generates calibrated comments in your
voice — one tap injects a comment into the post's comment box.

This app is a **simulator**: it runs a realistic LinkedIn feed inside a phone
frame and simulates the system overlay (`SYSTEM_ALERT_WINDOW` /
accessibility-node targeting) inside that frame, so the whole interaction can
be designed and tested without a real overlay service.

Originally exported from AI Studio
(https://ai.studio/apps/542f2c5f-5d91-45ec-8301-d0cff3d0a87b) and redesigned
to a clean white minimal look.

## Features (all integrated)

- **Floating overlay pill** — drag it anywhere on the feed. Dropping it on a
  post (or tapping it) locks that post as the target, using real drop
  coordinates: post cards report their on-screen bounds and the drop point is
  matched against them.
- **Expanded overlay card** — shows the targeted post, a one-line summary,
  full sentiment detail (theme, confidence, reach, controversy), and 4
  comment angles: Contrarian, Add Experience, Smart Question, Punchy 1-Liner.
- **Tap to inject** — the selected comment types itself into the post's
  comment box character by character. With auto-submit on, it posts itself.
- **Persona calibration** — role + presets, 4 tones (Direct, Witty, Academic,
  Casual), custom rules / anti-slop guardrails, auto-submit toggle, and a
  role sign-off toggle (appends your role under injected comments, into the
  editable draft — never silently posted).
- **Custom thought** — type a quick thought in the overlay card and the next
  generation weaves it in.
- **Custom posts** — paste any real LinkedIn post (author, headline, body)
  into the feed and test the overlay on it.
- **Live Gemini generation** — with a `GEMINI_API_KEY` in `.env`, comments
  come from Gemini (gemini-3.5-flash) with anti-slop directives (banned
  buzzwords, no re-summarizing, 1–3 sentences). Without a key, a
  tone-aware, per-post fallback generator runs instead, so the app always
  works.
- **Telemetry** — every overlay event (binding, node locks, Gemini calls,
  injections, submissions) is logged. On phones it has its own Telemetry tab;
  on wide screens it lives in the sidebar.
- **Feed interactions** — like, comment, submit, and AI-injected comments are
  badged in the feed.

## Design: clean white minimal

White surfaces, near-black ink, hairline (`#E5E7EB`) borders, one flat accent
(LinkedIn blue `#0A66C2`). No gradients, no glow, no glassmorphism, no
pulsing animations. The feed itself matches real LinkedIn: `#F4F2EE`
background, white cards.

## Floating overlay UX

- Dragging is 1:1 with the finger and clamped to the phone viewport, so the
  pill can never leave the screen.
- The pill springs open into the card (scale + fade); the card is scrollable
  if it ever outgrows the viewport.
- Dragging is scoped: the whole pill drags when collapsed, but only the
  header row drags when the card is open — tapping angle chips or typing in
  the card never moves it by accident.
- Dragging the open card never retargets; only a pill drop (or tap) changes
  the target.

## Run it

Prerequisites: [Android Studio](https://developer.android.com/studio).

1. Open Android Studio → **Open** → choose this directory, let it sync.
2. Optional: create `.env` in the project root with `GEMINI_API_KEY=...`
   (see `.env.example`). Without it, the fallback generator runs.
3. Run on an emulator or a physical device.

Note: the debug `signingConfig` assignment from the AI Studio export was
removed so local builds use Gradle's standard debug keystore. Restore it if
you re-import this project into AI Studio.

## Layout

- `app/src/main/java/com/example/ui/FloatReplyMainScreen.kt` — top bar,
  phone/persona/telemetry tabs (compact) and split layout (wide).
- `app/src/main/java/com/example/ui/components/FloatingOverlayBubble.kt` —
  the pill + card, drag handling, drop targeting input.
- `app/src/main/java/com/example/ui/components/PhoneSimulatorFrame.kt` —
  phone frame, viewport drag bounds, drop-coordinate matching.
- `app/src/main/java/com/example/ui/components/LinkedInFeedView.kt` — the
  simulated LinkedIn feed and post cards (which report their bounds).
- `app/src/main/java/com/example/ui/components/PersonaCalibrationSidebar.kt`
  — role, tone, rules, auto-submit, sign-off, telemetry.
- `app/src/main/java/com/example/viewmodel/FloatReplyViewModel.kt` — all
  state, targeting, injection typing animation, submission.
- `app/src/main/java/com/example/network/GeminiService.kt` — Gemini API +
  fallback generator.
- `app/src/main/java/com/example/ui/theme/` — the white minimal palette
  (`Color.kt`), light theme (`Theme.kt`), type scale (`Type.kt`).
