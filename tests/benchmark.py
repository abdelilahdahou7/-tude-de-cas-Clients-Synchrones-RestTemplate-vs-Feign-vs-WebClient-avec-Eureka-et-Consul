import requests
import time
import statistics

BASE_URL = "http://localhost:8082/api/client-test"
ENDPOINTS = {
    "RestTemplate": "/rest-template/clients",
    "Feign": "/feign/clients",
    "WebClient": "/web-client/clients"
}

ITERATIONS = 50

print(f"Starting Benchmark ({ITERATIONS} requests per client)...\n")
print(f"{'Client':<15} | {'Avg (ms)':<10} | {'Min (ms)':<10} | {'Max (ms)':<10} | {'Status':<10}")
print("-" * 65)

results = {}

for client_name, path in ENDPOINTS.items():
    latencies = []
    success_count = 0
    
    url = f"{BASE_URL}{path}"
    
    # Warmup
    try:
        requests.get(url, timeout=5)
    except:
        pass

    for _ in range(ITERATIONS):
        start = time.time()
        try:
            resp = requests.get(url, timeout=5)
            elapsed = (time.time() - start) * 1000
            if resp.status_code == 200:
                latencies.append(elapsed)
                success_count += 1
            else:
                print(f"Fail: {resp.status_code}")
        except Exception as e:
            print(f"Error: {e}")

    if latencies:
        avg_lat = statistics.mean(latencies)
        min_lat = min(latencies)
        max_lat = max(latencies)
        print(f"{client_name:<15} | {avg_lat:<10.2f} | {min_lat:<10.2f} | {max_lat:<10.2f} | {success_count}/{ITERATIONS}")
        results[client_name] = avg_lat
    else:
        print(f"{client_name:<15} | N/A        | N/A        | N/A        | 0/{ITERATIONS}")

print("\nBenchmark Complete.")
