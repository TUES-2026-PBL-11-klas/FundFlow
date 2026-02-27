window.onload = async function() 
    {
        try 
        {
            const res = await fetch('http://127.0.0.1:8080/api/accounts', { credentials: 'include' });
            if(res.ok) 
            {
                const accounts = await res.json();
                const list = document.getElementById('my-accounts');
                list.innerHTML = '';
                accounts.forEach(acc => 
                {
                    list.innerHTML += `
                        <div class="account-item">
                            <strong>${acc.iban}</strong> | ${Number(acc.balance).toFixed(2)} ${acc.currencyCode}
                        </div>`;
                });
            }
        } 
        catch(e) 
        {
            document.getElementById('my-accounts').innerText = "Error loading accounts";
        }
        await loadCurrencies();
    };

    async function loadCurrencies() 
    {
        const currencySelect = document.getElementById('currencyCode');
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
                    option.textContent = curr.code; 
                    currencySelect.appendChild(option);
                });
            }
        } 
        catch(error) 
        {
            console.error("Error:", error);
        }
    }

    document.getElementById('transferForm').onsubmit = async (e) => 
    {
        e.preventDefault();
        const msg = document.getElementById('msg');
        
        const data = 
        {
            senderIban: document.getElementById('senderIban').value.trim(),
            receiverIban: document.getElementById('receiverIban').value.trim(),
            amount: Number(document.getElementById('amount').value),
            currencyCode: document.getElementById('currencyCode').value
        };

        try 
        {
            const res = await fetch('http://127.0.0.1:8080/api/transfer', 
            {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                credentials: 'include',
                body: JSON.stringify(data)
            });

            if(res.ok) 
            {
                msg.style.color = "green";
                msg.innerText = "Succesfull transaction";
                setTimeout(() => window.location.href = '../Front/Front_page.html', 1500);
            } 
            else 
            {
                const errorText = await res.text();
                msg.style.color = "red";
                msg.innerText = "Error: " + errorText;
            }
        } 
        catch(err) 
        {
            msg.style.color = "red";
            msg.innerText = "Server error";
        }
    };