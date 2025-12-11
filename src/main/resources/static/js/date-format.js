// Date formatting utility for dd/mm/yyyy format
document.addEventListener('DOMContentLoaded', function() {
    // Convert date input to text input with dd/mm/yyyy format
    const dateInputs = document.querySelectorAll('input[type="date"][id*="Date"]');
    
    dateInputs.forEach(function(input) {
        // Get current value and format it
        if (input.value) {
            const date = new Date(input.value + 'T00:00:00');
            if (!isNaN(date.getTime())) {
                const day = String(date.getDate()).padStart(2, '0');
                const month = String(date.getMonth() + 1).padStart(2, '0');
                const year = date.getFullYear();
                input.value = day + '/' + month + '/' + year;
            }
        }
        
        // Change input type to text for custom formatting
        const originalType = input.type;
        const originalValue = input.value;
        
        // Create text input
        const textInput = document.createElement('input');
        textInput.type = 'text';
        textInput.className = input.className;
        textInput.id = input.id;
        textInput.name = input.name;
        textInput.placeholder = 'dd/mm/yyyy';
        textInput.pattern = '\\d{2}/\\d{2}/\\d{4}';
        textInput.maxLength = 10;
        if (input.required) textInput.required = true;
        
        // Set initial value
        if (originalValue) {
            const date = new Date(originalValue + 'T00:00:00');
            if (!isNaN(date.getTime())) {
                const day = String(date.getDate()).padStart(2, '0');
                const month = String(date.getMonth() + 1).padStart(2, '0');
                const year = date.getFullYear();
                textInput.value = day + '/' + month + '/' + year;
            }
        }
        
        // Create hidden date input for form submission
        const hiddenInput = document.createElement('input');
        hiddenInput.type = 'hidden';
        hiddenInput.name = input.name;
        hiddenInput.id = input.id + '_hidden';
        
        // Replace original input
        input.parentNode.replaceChild(textInput, input);
        textInput.parentNode.appendChild(hiddenInput);
        
        // Format on input (add slashes automatically)
        textInput.addEventListener('input', function(e) {
            let value = e.target.value.replace(/\D/g, '');
            if (value.length >= 2) {
                value = value.substring(0, 2) + '/' + value.substring(2);
            }
            if (value.length >= 5) {
                value = value.substring(0, 5) + '/' + value.substring(5, 9);
            }
            e.target.value = value;
        });
        
        // Convert to yyyy-mm-dd format on blur and update hidden input
        textInput.addEventListener('blur', function(e) {
            const value = e.target.value;
            const dateMatch = value.match(/^(\d{2})\/(\d{2})\/(\d{4})$/);
            
            if (dateMatch) {
                const day = parseInt(dateMatch[1], 10);
                const month = parseInt(dateMatch[2], 10);
                const year = parseInt(dateMatch[3], 10);
                
                // Validate date
                if (day >= 1 && day <= 31 && month >= 1 && month <= 12 && year >= 1900) {
                    const date = new Date(year, month - 1, day);
                    if (date.getDate() === day && date.getMonth() === month - 1 && date.getFullYear() === year) {
                        // Format as yyyy-mm-dd for hidden input
                        const formattedDate = year + '-' + 
                                             String(month).padStart(2, '0') + '-' + 
                                             String(day).padStart(2, '0');
                        hiddenInput.value = formattedDate;
                        e.target.setCustomValidity('');
                    } else {
                        e.target.setCustomValidity('Неверная дата');
                        e.target.reportValidity();
                    }
                } else {
                    e.target.setCustomValidity('Неверная дата');
                    e.target.reportValidity();
                }
            } else if (value && value.length > 0) {
                e.target.setCustomValidity('Неверный формат. Используйте dd/mm/yyyy');
                e.target.reportValidity();
            } else {
                e.target.setCustomValidity('');
                if (!textInput.required) {
                    hiddenInput.value = '';
                }
            }
        });
        
        // Clear validation on input
        textInput.addEventListener('input', function(e) {
            e.target.setCustomValidity('');
        });
    });
});

