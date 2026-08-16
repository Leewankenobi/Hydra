document.getElementById("next").addEventListener('click', (event) => {
  event.preventDefault();
  const houseHoldName = document.getElementById("householdName").value.trim();
  
  if (houseHoldName === '') { // handle empty name
    console.log("Household name can not be empty");
    const errorDiv = document.getElementById("householdNameError");
    errorDiv.textContent = "Please provide the household name";
    errorDiv.style.display = "block";

  } else {
    // name provided
    console.log("Household name:", houseHoldName);
    window.location.href='createAccount.html';
    return houseHoldName;

  }
}); 