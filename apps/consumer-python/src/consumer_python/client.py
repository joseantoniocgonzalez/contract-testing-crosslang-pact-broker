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

    def get_product(self, product_id: str) -> dict:
        r = requests.get(
            f"{self.base_url}/products/{product_id}",
            timeout=5,
        )
        r.raise_for_status()
        return r.json()

    def create_order(self, token: str, product_id: int, quantity: int) -> dict:
        r = requests.post(
            f"{self.base_url}/orders",
            headers={"Authorization": f"Bearer {token}"},
            json={"productId": product_id, "quantity": quantity},
            timeout=5,
        )
        r.raise_for_status()
        return r.json()
