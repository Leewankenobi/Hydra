export async function registerUser(userData) {
  const response = await fetch("http://localhost:8080/api/createAccount", {
    method: "POST",
    mode: "cors",
    headers: {
      "Content-Type": "application/json"
    },
    body: JSON.stringify(userData)
  });
  return response;
}

