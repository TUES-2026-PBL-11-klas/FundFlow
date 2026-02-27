
window.onload = function()
{
    const registerForm = document.getElementById('registerForm');
    const errorMsg = document.getElementById('error-msg');
    if(registerForm)
    {
    
        registerForm.addEventListener("submit", async function(e)
        {
            e.preventDefault();
            let pass = document.getElementById("password").value;
            let confirm = document.getElementById("confirm").value;
            const email = document.getElementById('email').value;
            const username = document.getElementById('username').value;
            if(pass !== confirm)
            {
                errorMsg.innerText = "Passwords do not match!";
                errorMsg.style.color = "red";
                return
            }
            errorMsg.innerText = "Creating account...";
            errorMsg.style.color = "#6f42c1";
            try
            {
                const response = await fetch('http://127.0.0.1:8080/api/register', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify({ 
                        email: email, 
                        password: pass,
                        username: username
                    })
                });


                if(response.ok) 
                {
                    window.location.href = 'login.html';
                } 
                else 
                {
                    const errorMessage = await response.text();
                    errorMsg.innerText = (errorMessage || "Registration failed");
                    errorMsg.style.color = "red";
                }
            }
            catch(error)
            {
                errorMsg.innerText = "Server error";
                errorMsg.style.color = "red";
            }
        });
    }
}