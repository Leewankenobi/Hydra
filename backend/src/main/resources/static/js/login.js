const loginMessage = document.getElementById("loginMessage");
const error = new URLSearchParams(window.location.search).get("error");

var userLoginData = {
  
   "email" : " ",
   "password" : " " 

  };


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
  document.getElementById("emailPane").hidden = true;
  document.getElementById("passwordPane").hidden = false;
});

document.getElementById("loginButton").addEventListener("click", (event) => {
  event.preventDefault();

  const password = document.getElementById("password").value;
  const passwordError = document.getElementById("passwordError");

  if (password === "") {
    passwordError.textContent = "Password must be provided";
    passwordError.style.display = "block";
    return;
  }

  passwordError.style.display = "none";
});

if (error === "account-exists") {
  loginMessage.textContent = "An account with that email already exists. Please log in instead.";
  loginMessage.hidden = false;
}
