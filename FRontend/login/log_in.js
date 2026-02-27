window.onload = function() 
{
    const loginForm = document.getElementById('loginForm');
    const errorMsg = document.getElementById('error-msg');

    if(loginForm) 
        {
        loginForm.addEventListener('submit', async function(e) 
        {
            e.preventDefault();

            const emailOrUsername = document.getElementById('email').value;
            const password = document.getElementById('password').value;

            try 
            {
                const response = await fetch('http://127.0.0.1:8080/api/login', 
                {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ emailOrUsername: emailOrUsername, password: password }),
                    credentials: 'include'
                });

                if(response.ok) 
                {
                    window.location.href = '../front/Front/Front_page.html';
                } 
                else 
                {
                    const errorMessage = await response.text();
                    if(errorMsg) errorMsg.innerText = (errorMessage || "Invalid login");
                }
            } 
            catch(error) 
            {
                if(errorMsg) errorMsg.innerText = "Server error";
            }
        });
    }
};