console.log('JS file loaded');
let userData = {firstName: '', lastName: '', email: '',username: '', password: ''};
const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

// first name handler
document.getElementById("next1").addEventListener('click', (event) => {
  event.preventDefault(); // dont submit just yet

  // validate user input
  const firstNameInput = document.getElementById("fname");
  const userFirstName = firstNameInput.value; 

  if (userFirstName === '') { // handle empty name
    console.log("First name can not be empty");
    const errorDiv = document.getElementById("fnameError");
    errorDiv.textContent = "Don't be shy - what's your name?";
    errorDiv.style.display = "block";

  } else {
    userData.firstName = userFirstName.trim(); // update dictionary
    console.log("Saved to UserData: ", userData);    
    
    document.getElementById("firstNamePane").hidden = true;  
    document.getElementById("lastNamePane").hidden = false;
  }
}); 

// last name handler
  document.getElementById("next2").addEventListener('click', (event) => {
  event.preventDefault(); // dont submit just yet

  // validate user input
  const lastNameInput = document.getElementById("lname");
  const userLastName = lastNameInput.value; 

  if (userLastName === '') { // handle empty name
    console.log("Last Name can not be empty");
    const errorDiv = document.getElementById("lnameError");
    errorDiv.textContent = "Last Name can not be empty!";
    errorDiv.style.display = "block";

  } else {
    userData.lastName = userLastName.trim(); // update dictionary
    console.log("Saved to UserData: ", userData);  
    
    
    document.getElementById("lastNamePane").hidden = true;  
    document.getElementById("emailPane").hidden = false;    
  }
}); 

  document.getElementById("next3").addEventListener('click', (event) => {
  event.preventDefault(); // dont submit just yet

  // validate user input
  const emailInput = document.getElementById("email");
  const userEmail = emailInput.value; 

  if (userEmail === '') { // handle empty name
    console.log("Email can not be empty");
    const errorDiv = document.getElementById("emailError");
    errorDiv.textContent = "Email must be provided";
    errorDiv.style.display = "block";

  } else if (!emailRegex.test(userEmail)) { //check correct email format

    const errorDiv = document.getElementById("emailError");
    errorDiv.textContent = "Please enter a valid email (example: user@domain.com)";
    errorDiv.style.display = "block";
    console.log("Please enter a valid email (example: user@domain.com)");

  } else {
    userData.email = userEmail.trim(); // update dictionary
    console.log("Saved to UserData: ", userData);    
    
    document.getElementById("emailPane").hidden = true;  
    document.getElementById("usernamePane").hidden = false;
  }
}); 

document.getElementById("next4").addEventListener('click', (event) => {
  event.preventDefault(); 
  // validate suser input
  const usernameInput = document.getElementById("username");
  const userUsername = usernameInput.value; 

  // username can be blank
  userData.username = userUsername.trim(); // update dictionary
  console.log("Saved to UserData: ", userData);    
  
  document.getElementById("usernamePane").hidden = true;  
  document.getElementById("passhashPane").hidden = false;
  
}); 

document.getElementById("next5").addEventListener('click', (event) => {
  event.preventDefault(); // dont submit just yet

  // validate suser input
  const passwordInput = document.getElementById("passhash");
  const userPassword = passwordInput.value.trim(); 
  const errorDiv = document.getElementById("passwordError");
  const errorMsg = "Password must be at least 8 characters, with at least one uppercase, one lowercase, and one special character."

if (userPassword.length < 8) {

    errorDiv.textContent = errorMsg;
    errorDiv.style.display = "block";
  } else if (!/[A-Z]/.test(userPassword)) {
    errorDiv.textContent = errorMsg;
    errorDiv.style.display = "block";
  } else if (!/[a-z]/.test(userPassword)) {
    errorDiv.textContent = errorMsg;
    errorDiv.style.display = "block";
  } else if (!/[!@#$%^&*]/.test(userPassword)) {
    errorDiv.textContent = errorMsg;
    errorDiv.style.display = "block";

  } else {
    userData.passHash = userPassword;
    console.log("Saved to UserData");
}  
}); 
// registration complete -> create household page (createHousehold.js)