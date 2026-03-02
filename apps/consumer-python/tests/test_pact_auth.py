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

def test_login_contract(pact: Pact):
    (
        pact
        .upon_receiving("a login request")
        .given("user exists")
        .with_request("POST", "/login")
        .with_header("Content-Type", "application/json")
        .with_body({
            "username": match.str("jose"),
            "password": match.str("secret"),
        })
        .will_respond_with(200)
        .with_header("Content-Type", "application/json")
        .with_body({"token": match.str("token-abc123")})
    )

    with pact.serve() as srv:
        client = ApiClient(str(srv.url))
        data = client.login("jose", "secret")
        assert "token" in data
