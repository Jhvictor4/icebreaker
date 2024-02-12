import openai
import os
import time

from openai import OpenAI

# Edit this part for your setup
client = OpenAI(api_key="yourkey")


def query_llm(messages, max_tokens=2048, temperature=0.1):
    # Retry forever
    while True:
        try:
            response = client.chat.completions.create(
                model="gpt-3.5-turbo",
                messages=messages,
                temperature=temperature,
                max_tokens=max_tokens,
                n=1,
            )

            content = response.choices[0].message.content.strip()

            return content
        except Exception as e:
            print(e)
            print("Failure querying the AI. Retrying...")
            time.sleep(1)

def query_openai(prompt):
    messages = [
        { "role": "user", "content": prompt }
    ]
    return query_llm(messages)

# Example usage
if __name__ == "__main__":
    # 연습장