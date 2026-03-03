from pathlib import Path
import pytest
from pact import Pact, match

from consumer_python.client import ApiClient

PACT_DIR = Path(__file__).resolve().parent.parent / "pacts"

@pytest.fixture
def pact():
    pact = Pact("consumer-python", "provider-java").with_specification("V4")
    PACT_DIR.mkdir(parents=True, exist_ok=True)
    yield pact
    pact.write_file(PACT_DIR)

def test_create_order_contract(pact: Pact):
    token = "token-abc123"

    (
        pact
        .upon_receiving("a checkout order creation request")
        .given("user is authenticated")
        .given("product 123 exists")
        .with_request("POST", "/orders")
        .with_header("Content-Type", "application/json")
        .with_header("Authorization", match.str(f"Bearer {token}"))
        .with_body({
            "productId": match.integer(123),
            "quantity": match.integer(2),
        })
        .will_respond_with(201)
        .with_header("Content-Type", "application/json")
        .with_body({
            "orderId": match.integer(1),
            "status": match.str("CREATED"),
        })
    )

    with pact.serve() as srv:
        client = ApiClient(str(srv.url))
        data = client.create_order(token, 123, 2)
        assert data["status"] == "CREATED"
