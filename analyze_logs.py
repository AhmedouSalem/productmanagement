import json
from collections import defaultdict, Counter

stats = defaultdict(Counter)

with open("logs/app.jsonl") as f:
    for line in f:
        log = json.loads(line)
        uid = log.get("userId")
        event = log.get("event")
        if uid and event:
            stats[uid][event] += 1

for uid, cnt in stats.items():
    profile = cnt.most_common(1)[0][0]
    print(f"userId={uid} counts={dict(cnt)} profile={profile}")

