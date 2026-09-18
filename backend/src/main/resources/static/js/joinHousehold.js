const joinCodeInput = document.getElementById("joinCode");
const joinCodeError = document.getElementById("joinCodeError");

document.getElementById("submitCode").addEventListener("click", async (event) => {
  event.preventDefault();

  const joinCode = joinCodeInput.value.trim();

  if (joinCode === "") {
    joinCodeError.textContent = "Please enter a join code";
    joinCodeError.style.display = "block";
    return;
  }

  if (!/^\d{6}$/.test(joinCode)) {
  joinCodeError.textContent = "Join code must be 6 digits";
  joinCodeError.style.display = "block";
  return;
}
  // check if code exists in the backend
  await validateCode(joinCode); 
 
});


async function validateCode(joinCode) {
  try {
    const response = await fetch("http://localhost:8080/api/households/validateCode", {
      method: "POST",
      mode: "cors",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify(Number(joinCode)) // String to int!
    });

    if (response.ok) {
      // store code then redirect
      sessionStorage.setItem("joinCode", joinCode);
      window.location.href = "createAccount.html";
      return;
    }

    if (response.status === 400) {
      joinCodeError.textContent = "Please enter a join code";
      joinCodeError.style.display = "block";
      return;
    }

    if (response.status === 404) {
      joinCodeError.textContent = "Join code not found";
      joinCodeError.style.display = "block";
      return;
    }
    throw new Error(`Request failed with status ${response.status}`);

  } catch (error) {
    console.error("login failed:", error);
    joinCodeError.textContent = "Something went wrong. Please try again later.";
    joinCodeError.style.display = "block";
  }
}


