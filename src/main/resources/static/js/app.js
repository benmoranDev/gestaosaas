document.addEventListener("DOMContentLoaded", function () {

    // Alertas globais
    const alerts = document.querySelectorAll(".alert");


    alerts.forEach(function (alert) {
        setTimeout(function () {
            alert.style.transition = "opacity 0.5s ease";
            alert.style.opacity = "0";
        }, 3000);
    });
});