.root {
    -fx-font-family: 'Segoe UI', sans-serif;
    -fx-background-color: #f4f4f9;
}

.label-header {
    -fx-font-size: 28px;
    -fx-font-weight: bold;
    -fx-text-fill: #2c3e50;
    -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 2);
}

.panel-container {
    -fx-background-color: white;
    -fx-background-radius: 10;
    -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 5);
    -fx-padding: 20;
}

.button-action {
    -fx-background-color: #3498db;
    -fx-text-fill: white;
    -fx-font-weight: bold;
    -fx-background-radius: 5;
    -fx-cursor: hand;
}
.button-action:hover { -fx-background-color: #2980b9; }

.button-danger {
    -fx-background-color: #e74c3c;
    -fx-text-fill: white;
    -fx-font-weight: bold;
    -fx-background-radius: 5;
    -fx-cursor: hand;
}
.button-danger:hover { -fx-background-color: #c0392b; }

.button-success {
    -fx-background-color: #2ecc71;
    -fx-text-fill: white;
    -fx-font-weight: bold;
    -fx-background-radius: 5;
    -fx-cursor: hand;
}

/* Estilos para los Slots (Cuadritos) */
.slot-button {
    -fx-background-radius: 8;
    -fx-border-radius: 8;
    -fx-border-color: #bdc3c7;
    -fx-border-width: 1;
    -fx-font-size: 14px;
    -fx-font-weight: bold;
}
.slot-free {
    -fx-background-color: #d5f5e3; /* Verde claro */
    -fx-text-fill: #1e8449;
}
.slot-occupied {
    -fx-background-color: #fadbd8; /* Rojo claro */
    -fx-text-fill: #922b21;
}