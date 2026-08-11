console.log('JS file loaded');

let userData = {firstName: '', lastName: '', email: '',username: '', password: ''};

console.log('Submitted Data: ' , userData);

// first name handler
document.getElementById("next1").addEventListener('click', (event) => {
  event.preventDefault(); // dont submit just yet

  // validate user input
  const firstNameInput = document.getElementById("fname");
  const userFirstName = firstNameInput.value; 

  if (userFirstName === '') { // handle empty name
    console.log("First name can not be empty");

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
    console.log("Creating account using only the first name");

  } else {
    userData.lastName = userLastName.trim(); // update dictionary
    console.log("Saved to UserData: ", userData);        
  }

  document.getElementById("lastNamePane").hidden = true;  
  document.getElementById("emailPane").hidden = false;

}); 