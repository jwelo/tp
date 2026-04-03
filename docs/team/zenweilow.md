# zenweilow - Project Portfolio Page

## Project: CG2StocksTracker

CG2StocksTracker is a desktop command-line portfolio tracker built for typing-oriented users who want to manage holdings quickly.
The user interacts with it through a CLI, and it is written in Java.

Given below are my contributions to the project.

### New Features: Implemented main app integration and command pipeline foundations

- What:
	- Integrated the core application flow across `Ui`, `Parser`, `CG2StocksTracker`, `Storage`, and `PortfolioBook`.
	- Added the main command execution loop and application-level error handling structure.
- Why:
	- The project needed a working end-to-end command pipeline before individual features could be integrated safely.
	- Centralized command coordination reduces duplicated logic and keeps persistence behavior consistent.
- How:
	- Implemented early versions of `CG2StocksTracker` as the main integration point.
	- Connected parsing, execution, UI response handling, and storage updates into a single command-driven flow.

### New Features: Added transaction fee support for `/add` and `/remove`

- What:
	- Implemented support for optional transaction fee fields:
		- `--brokerage`
		- `--fx`
		- `--platform`
	- Extended command parsing and execution so fees affect holding cost basis and realized P&L.
- Why:
	- A portfolio tracker should reflect actual returns rather than idealized buy/sell prices.
	- Real-world investing workflows commonly include multiple fee categories.
- How:
	- Extended `ParsedCommand` to store fee fields and compute `totalFees()`.
	- Updated parser validation for fee options.
	- Integrated fee-aware calculations into `Holding`, `Portfolio`, and command execution flow.

### Enhancement: Improved parser robustness and integration reliability

- What:
	- Added parser edge-case handling and supporting tests.
	- Improved documentation and assertions around command execution and holding state behavior.
- Why:
	- Parser-level validation prevents malformed input from leaking into execution logic.
	- Assertions and regression tests improve confidence during late-stage refactoring.
- How:
	- Added checks for invalid options, duplicate options, and no-argument command validation.
	- Added assertions in holding update/removal logic and updated tests accordingly.

### Code contributed

- **[RepoSense contribution link](https://nus-cs2113-ay2526-s2.github.io/tp-dashboard/?search=zenweilow&breakdown=true&sort=groupTitle%20dsc&sortWithin=title&since=2026-02-20T00%3A00%3A00&timeframe=commit&mergegroup=&groupSelect=groupByRepos&checkedFileTypes=docs~functional-code~test-code~other&filteredFileName=)**

### Documentation

- User Guide:
	- Updated UG content to reflect the final fee-enabled command format and examples.
	- Helped align examples and output wording with actual CLI behavior.
- Developer Guide:
	- Added and refined DG content for the application architecture and command pipeline.
	- Helped document the fee-handling implementation and overall execution flow.
	- Added/updated UML diagrams for `/help`, `/exit`, and `/insights`.

### Contributions to team-based tasks

- Helped prepare and stabilize the project during integration-heavy phases.
- Supported release readiness by fixing parser edge cases, matching outputs to expected behavior, and improving internal documentation.
- Contributed unit-test and text-UI-test updates to reduce regressions during feature merging.

### Review and mentoring contributions

- Reviewed teammates' work related to parser behavior, integration flow, and command consistency.
- Helped troubleshoot failing tests and integration issues when command behavior changed.

### Contributions beyond the project team

- Shared implementation and debugging approaches with peers where relevant.
