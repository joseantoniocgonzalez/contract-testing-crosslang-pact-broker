from pathlib import Path

import pytest
from pact import Pact, match
import requests

PACT_DIR = Path(__file__).resolve().parent.parent / "pacts"

@pytest.fixture
def pact():
    pact = Pact("consumer-python", "provider-java").with_specification("V4")
    PACT_DIR.mkdir(parents=True, exist_ok=True)
    yield pact
    pact.write_file(PACT_DIR)

def test_get_product_contract(pact: Pact):
    (
        pact
        .upon_receiving("a request for a product by id")
        .given("product 123 exists")
        .with_request("GET", "/products/123")
        .will_respond_with(200)
        .with_header("Content-Type", "application/json")
        .with_body({
            "id": match.int(123),
            "name": match.str("Example Product"),
            "price": match.decimal(9.99),
        })
    )

    with pact.serve() as srv:
        r = requests.get(f"{srv.url}/products/123", timeout=5)
        assert r.status_code == 200
        data = r.json()
        assert "id" in data and "name" in data and "price" in data
