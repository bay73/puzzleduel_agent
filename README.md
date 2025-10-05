# PuzzleDuel Agent
AI agent to analyze result of contests which is held at www.puzzleduel.club website.
PuzzleDuel website organizes online competitions where participants compete in puzzle solving speed and accuracy.
Each competition has a set of puzzles available during specified tme and participants can solve them with logged
solving progress and time. After contest finish participants gain points for each solved puzzle depending on there
performance and total amount of points gained by a participant determine the final standing.

Agent has a set of tools which can read and parse PuzzleDuel data. Agent help collecting different
statistics, analysing contest results, preparing announcement and publications about them.

Typical questions to agent:
- who is the winner/leaders/looser of a contest
- what is the hardest/easiest puzzle
- who made less/more mistakes
- prepare announcement about new contest
- prepare article with contest review and results

Execution time for answers depends on number of required tool calls. Each tool returns only specific information
and some questions may require multiple tool calls to collect different pieces of data. It also depends on used LLM model.
While using GPT4.1 model typical response time is between 2 and 10 seconds.
