window.onload = async function() 
{
    const userRes = await fetch('http://127.0.0.1:8080/api/user/info', 
    {
        credentials: 'include' 
    });
    if(!userRes.ok) 
    {
        window.location.href = 'login.html';
        return;
    }
    
    loadCurrencies();
};

async function loadCurrencies() 
{
    const currencySelect = document.getElementById('currency-select');
    try 
    {
        const response = await fetch('http://127.0.0.1:8080/api/currencies', 
        { 
            method: 'GET', 
            credentials: 'include' 
        });
        
        if(response.ok) 
        {
            const currencies = await response.json();
            currencySelect.innerHTML = '';
        
            currencies.forEach(curr => 
            {
                const option = document.createElement('option');
                option.value = curr.code;
                option.textContent = `${curr.code} - ${curr.name || ''}`;
                currencySelect.appendChild(option);
            });
        }
    } 
    catch(error) 
    {
        console.error("Error loading currencies:", error);
        document.getElementById('create-msg').innerText = "Failed to load currencies";
    }
}

async function handleCreateAccount() 
{
    const email = document.getElementById('confirm-email').value;
    const password = document.getElementById('confirm-password').value;
    const currency = document.getElementById('currency-select').value;
    const msgEl = document.getElementById('create-msg');

    const payload = 
    {
        useEmail: email,     
        userPassword: password,     
        accountCurrencyCode: currency
    };

    const res = await fetch('http://127.0.0.1:8080/api/accounts/create', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        credentials: 'include',
        body: JSON.stringify(payload)
    });

    if(res.ok) 
    {
        msgEl.innerText = "Account created Successfully";
        msgEl.style.color = "green";
        setTimeout(() => window.location.href = '../Front/Front_page.html', 1500);
    } 
    else 
    {
        const errorText = await res.text();
        msgEl.innerText = errorText;
        msgEl.style.color = "red";
    }
}