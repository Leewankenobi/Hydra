const loginMessage = document.getElementById("loginMessage");
const createAccountButton = document.getElementById("createAccountButton");
const loginButton = document.getElementById("loginButton");

const userLoginData = {
  email: "",
  password: ""
};

const error = new URLSearchParams(window.location.search).get("error");

document.getElementById("nextEmail").addEventListener("click", (event) => {
  event.preventDefault();

  const email = document.getElementById("email").value.trim();
  const emailError = document.getElementById("emailError");

  if (email === "") {
    emailError.textContent = "Email must be provided";
    emailError.style.display = "block";
    return;
  }

  emailError.style.display = "none";
  userLoginData.email = email;
  document.getElementById("emailPane").hidden = true;
  document.getElementById("passwordPane").hidden = false;
});

document.getElementById("loginButton").addEventListener("click", async (event) => {
  event.preventDefault();

  const password = document.getElementById("password").value;
  const passwordError = document.getElementById("passwordError");

  if (password === "") {
    passwordError.textContent = "Password must be provided";
    passwordError.style.display = "block";
    return;
  }

  passwordError.style.display = "none";
  userLoginData.password = password;
  await submitLogin(userLoginData);
});

createAccountButton.addEventListener("click", () => {
  window.location.href = "createAccount.html";
});

async function submitLogin(userLoginData) {
  try {
    const response = await fetch("http://localhost:8080/api/login", {
      method: "POST",
      mode: "cors",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify(userLoginData)
    });

    if (response.ok) {
      window.location.href = "userDashboard.html";
      return;
    }

    if (response.status === 401) {
      passwordError.textContent = "Incorrect username or password";
      passwordError.style.display = "block";
      createAccountButton.hidden = true;
      return;
    }

    if (response.status === 404) {
      passwordError.textContent = "No account found for this email, Please create an account first.";
      passwordError.style.display = "block";
      createAccountButton.hidden = false;
      loginButton.hidden = true;

      return;
    }

    throw new Error(`Request failed with status ${response.status}`);
  } catch (error) {
    console.error("login failed:", error);
    passwordError.textContent = "Something went wrong. Please try again later.";
    passwordError.style.display = "block";
    loginButton.hidden = true;
  }
}

if (error === "account-exists") {
  passwordError.textContent = "An account with that email already exists. Please log in instead.";
  passwordError.style.display = "block";
}