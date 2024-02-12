package com.wafflestudio.ai.icebreaker.application.common

import com.wafflestudio.ai.icebreaker.application.icebreaking.IceBreakingTools
import com.wafflestudio.ai.icebreaker.application.user.BasicInformation

fun ICE_BREAKING_TASK(
    personA: Set<BasicInformation>,
    personB: Set<BasicInformation>
) = """
    given several basic information about two people who just met,
    You are required to make a list of question that can be used as an ice-breaker for them.
    the order of the questions is IMPORTANT - questions that contain common things between them as much as possible should appear first.
    And you should NEVER ask a question about basic information.
    use tools that consumes basic information to question.

    Available basic information:
    Person A : ${personA.joinToString(", ")}
    Person B : ${personB.joinToString(", ")}

    Tools available for each information:
    ${IceBreakingTools.asPrompt()}

    What tools can be used as an ice-breaker for them?:
""".trimIndent()

fun SELECT_STEP_PROMPT(task: String) = """
    Select several reasoning modules that are crucial to utilize in order to solver the given task:
    
    All reasoning module descriptions are provided below:
    1 How could I devise an experiment to help solve that problem?
    2 Listing ideas: Make a list of ideas for solving this problem, and apply them one by one to the problem to see if any progress can be made.
    3 How could I measure progress on this problem?
    4 How can I simplify the problem so that it is easier to solve?
    5 What are the key assumptions underlying this problem?
    6 What are the potential risks and drawbacks of each solution?
    7 What are the alternative perspectives or viewpoints on this problem?
    8 What are the long-term implications of this problem and its solutions?
    9 Breaking Down: How can I break down this problem into smaller, more manageable parts?
    10 Critical Thinking: This style involves analyzing the problem from different perspectives, questioning assumptions, and evaluating
    the evidence or information available. It focuses on logical reasoning, evidence-based decision-making, and identifying
    potential biases or flaws in thinking.
    11 Try creative thinking, generate innovative and out-of-the-box ideas to solve the problem. Explore unconventional solutions,
    thinking beyond traditional boundaries, and encouraging imagination and originality.
    12 Seek input and collaboration from others to solve the problem. Emphasize teamwork, open communication, and leveraging the
    diverse perspectives and expertise of a group to come up with effective solutions.
    13 Use systems thinking: Consider the problem as part of a larger system and understanding the interconnectedness of various elements.
    Focuses on identifying the underlying causes, feedback loops, and interdependencies that influence the problem, and developing holistic
    solutions that address the system as a whole.
    14 Use Risk Analysis: Evaluate potential risks, uncertainties, and tradeoffs associated with different solutions or approaches to a
    problem. Emphasize assessing the potential consequences and likelihood of success or failure, and making informed decisions based
    on a balanced analysis of risks and benefits.
    15 Use Reflective Thinking: Step back from the problem, take the time for introspection and self-reflection. Examine personal biases,
    assumptions, and mental models that may influence problem-solving, and being open to learning from past experiences to improve
    future approaches.
    16 What is the core issue or problem that needs to be addressed?
    17 What are the underlying causes or factors contributing to the problem?
    18 Are there any potential solutions or strategies that have been tried before? If yes, what were the outcomes and lessons learned?
    19 What are the potential obstacles or challenges that might arise in solving this problem?
    20 Are there any relevant data or information that can provide insights into the problem? If yes, what data sources are available,
    and how can they be analyzed?
    21 Are there any stakeholders or individuals who are directly affected by the problem? What are their perspectives and needs?
    22 What resources (financial, human, technological, etc.) are needed to tackle the problem effectively?
    23 How can progress or success in solving the problem be measured or evaluated?
    24 What indicators or metrics can be used?
    25 Is the problem a technical or practical one that requires a specific expertise or skill set? Or is it more of a conceptual or
    theoretical problem?
    26 Does the problem involve a physical constraint, such as limited resources, infrastructure, or space?
    27 Is the problem related to human behavior, such as a social, cultural, or psychological issue?
    28 Does the problem involve decision-making or planning, where choices need to be made under uncertainty or with competing
    objectives?
    29 Is the problem an analytical one that requires data analysis, modeling, or optimization techniques?
    30 Is the problem a design challenge that requires creative solutions and innovation?
    31 Does the problem require addressing systemic or structural issues rather than just individual instances?
    32 Is the problem time-sensitive or urgent, requiring immediate attention and action?
    33 What kinds of solution typically are produced for this kind of problem specification?
    34 Given the problem specification and the current best solution, have a guess about other possible solutions.
    35 Let’s imagine the current best solution is totally wrong, what other ways are there to think about the problem specification?
    36 What is the best way to modify this current best solution, given what you know about these kinds of problem specification?
    37 Ignoring the current best solution, create an entirely new solution to the problem.
    38 Let’s think step by step.
    39 Let’s make a step by step plan and implement it with good notion and explanation.
    
    Task examples without answers:
    Example1: A restaurant offers three desserts, and exactly twice as many appetizers as main courses. A dinner consists of an appetizer, a main course, and a dessert. 
    What is the least number of main courses that the restaurant should offer so that a customer could have a different dinner each night in the year 2003?
    
    Example2: Two people are just met and they need an ice-breaker. 
    You have several tools and actions to help them. {ACTION1}, {ACTION2}, ...
    What action flows would be nice to provide them some good ice-breaker questions?
    
    
    Select several modules that are crucial for solving the task above: 
    ${task.trimIndent()}
""".trimIndent()

fun ADAPT_STEP_PROMPT(selected_modules: String, task: String) = """
    Rephrase and specify each reasoning module so that it better helps solving the task:
    
    SELECTED module descriptions:
    ${selected_modules.trimIndent()}
    
    Task examples without answers:
    Example1: A restaurant offers three desserts, and exactly twice as many appetizers as main courses. A dinner consists of an appetizer, a main course, and a dessert. 
    What is the least number of main courses that the restaurant should offer so that a customer could have a different dinner each night in the year 2003?
    
    Example2: Two people are just met and they need an ice-breaker. 
    You have several tools and actions to help them. {ACTION1}, {ACTION2}, ...
    What action flows would be nice to provide them some good ice-breaker questions?
 
    
    Adapt each reasoning module description to better solve the task:
    ${task.trimIndent()}
""".trimIndent()

fun IMPLEMENT_STEP_PROMPT(adapted_modules: String, task: String) = """
    Operationalize the reasoning modules into a step-by-step reasoning plan in JSON format:
    
    Paired implementation of reasoning modules and task examples:
    When Reasoning description Example is :
        Try creative thinking, generate innovative and out-of-the-box ideas to solve the problem. Explore unconventional solutions, thinking beyond traditional boundaries, and encouraging imagination and originality.
        - Brainstorm unconventional topics that may spark an interesting conversation between A and B. Think beyond typical ice-breaking topics and encourage creativity and originality in coming up with ideas.

    Reasoning plan example in JSON format: 
        { "Brainstorm unconventional topics:": "" }
    
    ADAPTED module descriptions:
    ${adapted_modules.trimIndent()}
    
    Task examples without answers:
    Example1: A restaurant offers three desserts, and exactly twice as many appetizers as main courses. A dinner consists of an appetizer, a main course, and a dessert. 
    What is the least number of main courses that the restaurant should offer so that a customer could have a different dinner each night in the year 2003?
    
    Example2: Two people are just met and they need an ice-breaker. 
    You have several tools and actions to help them. {ACTION1}, {ACTION2}, ...
    What action flows would be nice to provide them some good ice-breaker questions?
    
    Implement a reasoning structure for solvers to follow step-by-step and arrive at correct answers:
    ${task.trimIndent()}
""".trimIndent()

fun EXECUTE_STEP_PROMPT(reasoningStructure: String, task: String) = """
    Follow the step-by-step reasoning plan in JSON to correctly solve the task.
    Fill in the values following the keys by reasoning specifically about the task given. Do not simply rephrase the keys.
    
    Reasoning Structure :
    ${reasoningStructure.trimIndent()}
    
    task:
    ${task.trimIndent()}
""".trimIndent()
