# iOS Design Tokens - Paratik

iOS SwiftUI uygulamasından çıkarılan tasarım tokenlari. Android Jetpack Compose uygulaması bu tokenlara göre stillendirilecektir.

## Renk Paleti (Colors)

### Semantic Colors
```kotlin
// iOS System Colors
Blue = Color(0xFF007AFF)        // iOS systemBlue
Green = Color(0xFF34C759)       // iOS systemGreen, income
Red = Color(0xFFFF3B30)         // iOS systemRed, expense
Orange = Color(0xFFFF9500)      // iOS systemOrange, streak
Yellow = Color(0xFFFBBF24)      // iOS systemYellow, achievements
Purple = Color(0xFFAF52DE)      // iOS systemPurple
Pink = Color(0xFFFF2D55)        // iOS systemPink
Cyan = Color(0xFF32ADE6)        // iOS systemCyan
Mint = Color(0xFF00C7BE)        // iOS systemMint
```

### Gradients
```kotlin
// Dashboard Balance Card
balanceGradient = listOf(Blue.copy(alpha = 0.1f), Purple.copy(alpha = 0.1f))

// Quick Actions
incomeBackground = Green
expenseBackground = Red

// Income/Expense Summary Cards
incomeCardBackground = Green.copy(alpha = 0.1f)
expenseCardBackground = Red.copy(alpha = 0.1f)

// Streak Card
streakBackground = Orange.copy(alpha = 0.1f)

// Achievement Cards
achievementUnlockedBackground = Yellow.copy(alpha = 0.1f)
achievementLockedBackground = Color.Gray.copy(alpha = 0.1f)
```

### Light Mode
```kotlin
background = Color.White                     // systemBackground
surface = Color(0xFFF2F2F7)                 // secondarySystemBackground
onBackground = Color.Black
onSurface = Color(0xFF1C1B1F)
onSurfaceVariant = Color(0xFF8E8E93)        // secondaryLabel
outline = Color(0xFFC7C7CC)                 // separator
```

### Dark Mode
```kotlin
background = Color.Black                     // systemBackground (dark)
surface = Color(0xFF1C1C1E)                 // secondarySystemBackground (dark)
onBackground = Color.White
onSurface = Color(0xFFEBEBF5)
onSurfaceVariant = Color(0xFF8E8E93)        // secondaryLabel (dark)
outline = Color(0xFF38383A)                 // separator (dark)
```

## Tipografi (Typography)

### Font Family
- Default: San Francisco (iOS system font)
- Android karşılığı: System Default (Roboto benzeri davranış)

### Font Sizes
```kotlin
// iOS → Android mapping
largeTitle = 34.sp      // .largeTitle
title = 28.sp           // .title
title2 = 22.sp          // .title2
title3 = 20.sp          // .title3
headline = 17.sp        // .headline
body = 17.sp            // .body
callout = 16.sp         // .callout
subheadline = 15.sp     // .subheadline
footnote = 13.sp        // .footnote
caption = 12.sp         // .caption
caption2 = 11.sp        // .caption2

// Custom sizes (Dashboard balance)
displayLarge = 42.sp    // Balance amount
```

### Font Weights
```kotlin
bold = FontWeight.Bold           // .bold
semibold = FontWeight.SemiBold   // .semibold
medium = FontWeight.Medium       // .medium
regular = FontWeight.Normal      // .regular
```

### Line Heights
```kotlin
// iOS default line heights
largeTitle: lineHeight = 41.sp
title: lineHeight = 34.sp
title2: lineHeight = 28.sp
title3: lineHeight = 25.sp
headline: lineHeight = 22.sp
body: lineHeight = 22.sp
subheadline: lineHeight = 20.sp
caption: lineHeight = 16.sp
caption2: lineHeight = 13.sp
```

### Letter Spacing
```kotlin
// iOS uses tight tracking
normal = 0.sp
tight = (-0.5).sp
```

## Spacing Scale

```kotlin
// iOS SwiftUI spacing values (extracted from padding/spacing calls)
spacing2 = 2.dp
spacing4 = 4.dp
spacing6 = 6.dp
spacing8 = 8.dp
spacing12 = 12.dp
spacing16 = 16.dp
spacing20 = 20.dp
spacing24 = 24.dp
spacing30 = 30.dp
spacing32 = 32.dp
spacing40 = 40.dp
spacing50 = 50.dp
spacing60 = 60.dp
```

## Corner Radius

```kotlin
// iOS rounded corners (from RoundedRectangle calls)
radius4 = 4.dp          // Small progress bars
radius8 = 8.dp          // Small cards, badges
radius10 = 10.dp        // Category icons
radius12 = 12.dp        // Achievement cards, info boxes
radius15 = 15.dp        // Summary cards, buttons
radius16 = 16.dp        // Large cards, input fields
radius20 = 20.dp        // Balance card
radius25 = 25.dp        // Large buttons
radius30 = 30.dp        // Modal sheets

// Special
circle = CircleShape    // Circular elements
capsule = Capsule       // Pill-shaped buttons
```

## Elevation & Shadows

```kotlin
// iOS shadow specs (from .shadow() modifier)
shadowSmall = Shadow(
    color = Color.Black.copy(alpha = 0.05f),
    offset = Offset(0f, 4f),
    blurRadius = 8.dp
)

shadowMedium = Shadow(
    color = Color.Black.copy(alpha = 0.1f),
    offset = Offset(0f, 4f),
    blurRadius = 12.dp
)

shadowLarge = Shadow(
    color = Color.Black.copy(alpha = 0.1f),
    offset = Offset(0f, 8f),
    blurRadius = 30.dp
)

// Colored shadows (buttons, cards)
buttonShadow = Shadow(
    color = Blue.copy(alpha = 0.3f),
    offset = Offset(0f, 8f),
    blurRadius = 15.dp
)
```

## Component Specs

### Balance Card
```
- Padding: vertical 16.dp
- Border Radius: 20.dp
- Background: Linear gradient (Blue 0.1 alpha → Purple 0.1 alpha)
- Title: body medium, secondary color
- Amount: 42.sp, bold, green/red based on balance
```

### Quick Action Buttons
```
- Height: 56.dp
- Border Radius: 15.dp
- Spacing between: 16.dp
- Icon size: default
- Text: semibold, white
- Background: solid green (income) / red (expense)
```

### Income/Expense Summary Cards
```
- Border Radius: 15.dp
- Padding: 16.dp
- Icon size: 20.dp
- Title: body medium
- Amount: title large, semibold
- Background: green/red with 0.1 alpha
```

### Streak Card
```
- Border Radius: 15.dp
- Padding: 16.dp
- Icon size: 32.dp
- Title: title medium, bold
- Numbers: headline medium, bold
- Labels: body small, secondary
- Divider: 1.dp width, 40.dp height
- Background: orange with 0.1 alpha
```

### Achievement Card
```
- Size: 100.dp width × 120.dp height
- Border Radius: 12.dp
- Padding: 8.dp inside
- Icon size: 32.dp
- Icon color: yellow (unlocked) / gray (locked)
- Title: caption, semibold, 2 lines max, center aligned
- Subtitle: caption2, secondary color
- Background: yellow/gray with 0.1 alpha based on unlock status
```

### Transaction Row
```
- Padding vertical: 8.dp
- Icon container: 40.dp circle, radius 10.dp
- Icon size: 24.dp
- Title: body medium, medium weight
- Date: body small, secondary
- Amount: body medium, semibold, green/red
- Spacing: 12.dp horizontal
```

## Navigation & Transitions

### Screen Transitions
```kotlin
// Push (forward)
transition = slideIn(
    initialOffset = { IntOffset(it.width, 0) },
    animationSpec = tween(300, easing = FastOutSlowInEasing)
) + fadeIn(tween(150))

// Pop (back)
transition = slideOut(
    targetOffset = { IntOffset(it.width, 0) },
    animationSpec = tween(300, easing = FastOutSlowInEasing)
) + fadeOut(tween(150))
```

### Modal Sheet Presentation
```kotlin
// Bottom sheet
cornerRadius = 30.dp (top corners only)
dragIndicator = true
scrimOpacity = 0.4f
detents = [.medium, .large]
```

### Tab Bar
```
- Height: system default (49.dp on iOS)
- Background: system background with opacity
- Selected tint: blue
- Unselected tint: gray
- Icon size: default (28.dp equivalent)
- Label: caption
```

## Dividers & Separators

```kotlin
// List dividers (iOS)
thickness = 0.5.dp
color = outline (gray in light, darker gray in dark)
startInset = 16.dp  // iOS list separators have left inset

// Section dividers
thickness = 1.dp
color = outline
```

## Animation Specs

### Standard Animations
```kotlin
// iOS default (easeInOut)
standardAnimation = tween(
    durationMillis = 300,
    easing = FastOutSlowInEasing
)

// iOS spring
springAnimation = spring(
    dampingRatio = Spring.DampingRatioMediumBouncy,
    stiffness = Spring.StiffnessMedium
)

// iOS sheet presentation
sheetAnimation = tween(
    durationMillis = 500,
    easing = FastOutSlowInEasing
)
```

### Specific Durations
```
- Fade: 150ms - 500ms
- Slide: 300ms
- Spring: default
- Sheet: 500ms
- Tab switch: 300ms
```

## Safe Areas & Insets

```kotlin
// iOS safe areas
topSafeArea = WindowInsets.systemBars.top
bottomSafeArea = WindowInsets.systemBars.bottom
leadingSafeArea = WindowInsets.systemBars.left
trailingSafeArea = WindowInsets.systemBars.right

// Common paddings after safe area
topPadding = 8.dp   // After navigation bar
bottomPadding = 16.dp // Before tab bar or bottom
```

## Haptic Feedback Intent

```kotlin
// iOS haptic feedback types (Android equivalent)
selection = HapticFeedbackType.TextHandleMove
success = HapticFeedbackType.LongPress
warning = HapticFeedbackType.LongPress
error = HapticFeedbackType.LongPress
light = HapticFeedbackType.TextHandleMove
medium = HapticFeedbackType.TextHandleMove
heavy = HapticFeedbackType.LongPress
```

## List Styling

### LazyColumn (iOS List equivalent)
```kotlin
contentPadding = PaddingValues(vertical = 12.dp)
itemSpacing = 12.dp  // between sections
insetGrouped = {
    horizontalPadding = 16.dp
    cornerRadius = 16.dp
    backgroundColor = surface
}

// iOS list sections
sectionHeaderPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
sectionHeaderStyle = caption, uppercase, gray
```

## Parity Target

- **Visual Deviation**: ≤2px on key elements
- **Typography**: Visually identical (size, weight, line height, spacing)
- **Layout**: Same padding, margins, spacing
- **Colors**: Exact hex match
- **Corner Radius**: Exact dp match
- **Shadows**: Same opacity, offset, blur
- **Transitions**: Same duration, easing, direction
- **Dark Mode**: Full parity with iOS dark mode

## Notes

- iOS uses SF Symbols; Android uses Material Icons or custom vectors (already mapped in core/ui)
- iOS text fields have subtle borders; replicate with OutlinedTextField or custom styling
- iOS toggles are distinct; use Switch with custom styling
- iOS pickers use bottom sheets; replicate with ModalBottomSheet
- iOS alerts use specific corner radius (14.dp) and button styling

