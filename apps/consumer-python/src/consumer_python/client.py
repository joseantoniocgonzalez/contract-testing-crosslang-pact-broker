import requests

class ApiClient:
    def __init__(self, base_url: str):
        self.base_url = base_url.rstrip("/")

    def login(self, username: str, password: str) -> dict:
        r = requests.post(
            f"{self.base_url}/login",
            json={"username": username, "password": password},
            timeout=5,
        )
        r.raise_for_status()
        return r.json()
