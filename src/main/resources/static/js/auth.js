/**
 * JavaScript для страниц аутентификации (логин и регистрация)
 * Обеспечивает динамическое изменение размера поля ввода при регистрации
 */

document.addEventListener('DOMContentLoaded', function() {
    const registerForm = document.getElementById('registerForm');
    const registerPanel = document.querySelector('.register-panel');
    
    if (registerForm && registerPanel) {
        adjustRegisterPanelSize();

        const formInputs = registerForm.querySelectorAll('input');
        formInputs.forEach(input => {
            input.addEventListener('focus', function() {
                registerPanel.style.transition = 'width 0.3s ease';
            });
            
            input.addEventListener('blur', function() {
                adjustRegisterPanelSize();
            });
        });
    }
    
    /**
     * Настраивает размер панели регистрации в зависимости от размера экрана
     */
    function adjustRegisterPanelSize() {
        const windowWidth = window.innerWidth;
        
        if (windowWidth > 1200) {
            registerPanel.style.width = '50%';
        } else if (windowWidth > 900) {
            registerPanel.style.width = '55%';
        } else {
            // На маленьких экранах панель занимает 100%
            registerPanel.style.width = '100%';
        }
    }

    window.addEventListener('resize', function() {
        if (registerPanel) {
            adjustRegisterPanelSize();
        }
    });
    
    // Плавная анимация появления формы
    if (registerForm) {
        registerForm.style.opacity = '0';
        registerForm.style.transform = 'translateY(20px)';
        registerForm.style.transition = 'opacity 0.5s ease, transform 0.5s ease';
        
        setTimeout(function() {
            registerForm.style.opacity = '1';
            registerForm.style.transform = 'translateY(0)';
        }, 100);
    }
});

