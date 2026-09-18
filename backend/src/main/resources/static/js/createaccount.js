import { registerUser } from "./register.js";

const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
const joinCode = sessionStorage.getItem("joinCode") 
const joiningHousehold = joinCode !== null; // join mode

var userData = {
  
    "household_name" : '',
    "first_name" : '',
    "last_name" : '', 
    "email": '',
    "username": '', 
    "passwordHash": ''
  };

if (joiningHousehold) { // no need to provide household name
  document.getElementById("householdNamePane").hidden = true;
  document.getElementById("firstNamePane").hidden = false;
}

document.getElementById("next6").addEventListener('click', (event) => {
  event.preventDefault();
  const houseHoldName = document.getElementById("householdName").value.trim();
  
  if (houseHoldName === '') { // in join mode, household name can be empty
    console.log("Household name can not be empty");
    const errorDiv = document.getElementById("householdNameError");
    errorDiv.textContent = "Please provide the household name";
    errorDiv.style.display = "block";

  } else {
    // name provided
    userData.household_name = houseHoldName; 
    console.log("Saved to UserData: ", userData); 

    document.getElementById("householdNamePane").hidden = true;  
    document.getElementById("firstNamePane").hidden = false;    

  }
}); 

// first name handler
document.getElementById("next1").addEventListener('click', (event) => {
  event.preventDefault(); // dont submit just yet
  const userFirstName = document.getElementById("fname").value.trim();
  // validate user input
  if (userFirstName === '') { // handle empty name
    console.log("First name can not be empty");
    const errorDiv = document.getElementById("fnameError");
    errorDiv.textContent = "Don't be shy - what's your name?";
    errorDiv.style.display = "block";

  } else {
    userData.first_name = userFirstName; // update dictionary
    console.log("Saved to UserData: ", userData);    
    document.getElementById("firstNamePane").hidden = true;  
    document.getElementById("lastNamePane").hidden = false;
  }
}); 

// last name handler
document.getElementById("next2").addEventListener('click', (event) => {
  const userLastName = document.getElementById("lname").value.trim();
  event.preventDefault(); // dont submit just yet
  // validate user input

  if (userLastName === '') { // handle empty name
    console.log("Last Name can not be empty");
    const errorDiv = document.getElementById("lnameError");
    errorDiv.textContent = "Last Name can not be empty!";
    errorDiv.style.display = "block";

  } else {
    userData.last_name = userLastName; // update dictionary
    console.log("Saved to UserData: ", userData);  
    document.getElementById("lastNamePane").hidden = true;  
    document.getElementById("emailPane").hidden = false;    
  }
}); 

document.getElementById("next3").addEventListener('click', (event) => {
  event.preventDefault(); // dont submit just yet
  const userEmail = document.getElementById("email").value.trim();

  if (userEmail === '') { 
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
    userData.email = userEmail;
    console.log("Saved to UserData: ", userData);    
    document.getElementById("emailPane").hidden = true;  
    document.getElementById("usernamePane").hidden = false;
  }
}); 

document.getElementById("next4").addEventListener('click', (event) => {
  event.preventDefault(); 
  // username can be blank
  const userUserName = document.getElementById("username").value.trim();
  if (userUserName === "") {
    userData.username = "";
  }
  else {
  userData.username = userUserName;
  }
  console.log("Saved to UserData: ", userData);    
  
  document.getElementById("usernamePane").hidden = true;  
  document.getElementById("passhashPane").hidden = false;
  
}); 

document.getElementById("next5").addEventListener('click', async (event) => {
  event.preventDefault();
  const nextButton = document.getElementById("next5");

  if (nextButton.dataset.accountExists === "true") {
    window.location.href = "login.html";
    return;
  }

  const userPassword = document.getElementById("passhash").value.trim();
  const errorDiv = document.getElementById("passwordError");
  const errorMsg = "Password must be at least 8 characters, with at least one uppercase, one lowercase, and one special character."

if (userPassword.length < 8 || !/[A-Z]/.test(userPassword) || !/[a-z]/.test(userPassword) || !/[!@#$%^&*]/.test(userPassword)) {
    errorDiv.textContent = errorMsg;
    errorDiv.style.display = "block";
    return;

  } else {
    userData.passwordHash = userPassword;    
    console.log("Password saved to UserData");
    errorDiv.style.display = "none";
  
  }  

  try {
    if (joiningHousehold) {
      userData.joinCode
    }
    const response = await registerUser(userData); //register user
    if (!response.ok) {
      if (response.status === 409) {
        errorDiv.textContent = "Account already exists.";
        errorDiv.style.display = "block";
        nextButton.textContent = "Login to an existing account"; // should redirect to login page
        nextButton.dataset.accountExists = "true";
        return;
      }
      throw new Error(`Error - status: ${response.status}`);
    }
    console.log(await response.text()); //message from backend
    window.location.href = "userDashboard.html"

    } catch (error) {
      console.error(error.message);    
    }  

}); 
