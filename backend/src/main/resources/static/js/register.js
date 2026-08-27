async function registerUser(userData) {
    
  const response = await fetch("http://localhost:8080/api/createAccount", {
    method: "POST",
    headers: {
      "Content-Type": "application/json"
    },
    body: JSON.stringify(userData)
  });

  const response = await response.json();
  return response;
}

