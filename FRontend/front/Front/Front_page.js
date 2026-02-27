    window.onload = async function() 
    {
        const userRes = await fetch('http://127.0.0.1:8080/api/user/info', 
        { 
            credentials: 'include' 
        });
        if(!userRes.ok) window.location.href = 'login.html';
        
        const userData = await userRes.json();
        document.getElementById('welcome-msg').innerText = `${userData.username}`;
        
        loadAccounts();
    };

    async function loadAccounts()
     {
        const res = await fetch('http://127.0.0.1:8080/api/accounts', 
        { 
            credentials: 'include' 
        });
        if(res.ok) 
            {
            const accounts = await res.json();
            const tableBody = document.getElementById('accounts-table-body');
            tableBody.innerHTML = '';
            
            accounts.forEach(acc => 
            {
                tableBody.innerHTML += `
                        <td style="font-weight:bold; color:#007bff;">${acc.iban}</td>
                        <td>${Number(acc.balance).toFixed(2)}</td>
                        <td>${acc.currencyCode}</td>
                `;
            });
        }
    }
    
    async function handleDepositAndWithDraw(type) 
    {
        const iban = document.getElementById('trans-iban').value;
        const amountInput = document.getElementById('trans-amount').value;
        const msgId = 'trans-msg';

        if(!iban || !amountInput) return;

        const endpoint = type === 'deposit' ? '/api/accounts/deposit' : '/api/withdraw';
        const amount = parseFloat(amountInput);
        const res = await fetch(`http://127.0.0.1:8080${endpoint}`, 
        {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            credentials: 'include',
            body: JSON.stringify({ iban: iban, amount: parseFloat(amount) })
        });

        showResult(res, msgId, type === 'deposit' ? "Succesfull deposit" : "Succesfull withdraw");
    }

    async function showResult(res, elementId, successText) 
    {
        const el = document.getElementById(elementId);
        if(res.ok) 
        {
            el.innerText =  successText;
            el.style.color = "green";
            loadAccounts();
        }
        else 
        {
            const txt = await res.text();
            el.innerText = txt;
            el.style.color = "red";
        }
    }

    async function logout() 
    {
        await fetch('http://127.0.0.1:8080/api/logout', { method: 'POST', credentials: 'include' });
        window.location.href = '../../login/log_in.html';
    }