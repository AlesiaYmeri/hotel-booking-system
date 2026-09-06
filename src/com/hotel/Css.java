package com.hotel;

public final class Css {

    private Css() {
    }

    public static final String CONTENT =
        ":root{\n" +
        "  --bg:#f4f6fb;\n" +
        "  --card:#ffffff;\n" +
        "  --primary:#2b5eff;\n" +
        "  --primary-dark:#1e46cc;\n" +
        "  --text:#1c2233;\n" +
        "  --muted:#6b7280;\n" +
        "  --border:#e6e9f2;\n" +
        "  --success-bg:#e7f8ef;\n" +
        "  --success-text:#0f9d58;\n" +
        "  --error-bg:#fdecec;\n" +
        "  --error-text:#d93025;\n" +
        "  --occupied:#fff1e6;\n" +
        "  --occupied-text:#c2661a;\n" +
        "  --free:#e7f8ef;\n" +
        "  --free-text:#0f9d58;\n" +
        "  --radius:14px;\n" +
        "}\n" +
        "*{box-sizing:border-box;}\n" +
        "body{\n" +
        "  margin:0;\n" +
        "  font-family:'Segoe UI', Roboto, Helvetica, Arial, sans-serif;\n" +
        "  background:var(--bg);\n" +
        "  color:var(--text);\n" +
        "  line-height:1.5;\n" +
        "}\n" +
        "header.topbar{\n" +
        "  background:linear-gradient(120deg,#2b5eff,#5c8bff);\n" +
        "  color:#fff;\n" +
        "  padding:22px 32px;\n" +
        "  box-shadow:0 4px 18px rgba(43,94,255,0.25);\n" +
        "}\n" +
        "header.topbar h1{\n" +
        "  margin:0;\n" +
        "  font-size:26px;\n" +
        "  letter-spacing:0.3px;\n" +
        "}\n" +
        "header.topbar p{\n" +
        "  margin:4px 0 0;\n" +
        "  opacity:0.9;\n" +
        "  font-size:14px;\n" +
        "}\n" +
        "nav.tabs{\n" +
        "  display:flex;\n" +
        "  gap:8px;\n" +
        "  margin-top:16px;\n" +
        "}\n" +
        "nav.tabs a{\n" +
        "  color:#fff;\n" +
        "  text-decoration:none;\n" +
        "  background:rgba(255,255,255,0.16);\n" +
        "  padding:8px 16px;\n" +
        "  border-radius:999px;\n" +
        "  font-size:14px;\n" +
        "  font-weight:600;\n" +
        "  transition:background .15s ease;\n" +
        "}\n" +
        "nav.tabs a:hover{background:rgba(255,255,255,0.3);}\n" +
        "main{\n" +
        "  max-width:1100px;\n" +
        "  margin:0 auto;\n" +
        "  padding:28px 24px 60px;\n" +
        "}\n" +
        "section{margin-bottom:38px;}\n" +
        "section h2{\n" +
        "  font-size:19px;\n" +
        "  margin:0 0 14px;\n" +
        "  color:var(--text);\n" +
        "  display:flex;\n" +
        "  align-items:center;\n" +
        "  gap:8px;\n" +
        "}\n" +
        "section h2 .count{\n" +
        "  background:var(--border);\n" +
        "  color:var(--muted);\n" +
        "  font-size:12px;\n" +
        "  font-weight:700;\n" +
        "  padding:2px 9px;\n" +
        "  border-radius:999px;\n" +
        "}\n" +
        ".grid{\n" +
        "  display:grid;\n" +
        "  grid-template-columns:repeat(auto-fill,minmax(230px,1fr));\n" +
        "  gap:16px;\n" +
        "}\n" +
        ".card{\n" +
        "  background:var(--card);\n" +
        "  border:1px solid var(--border);\n" +
        "  border-radius:var(--radius);\n" +
        "  padding:18px;\n" +
        "  box-shadow:0 2px 10px rgba(20,30,60,0.04);\n" +
        "}\n" +
        ".room-card{position:relative;}\n" +
        ".room-card h3{margin:0 0 4px;font-size:18px;}\n" +
        ".room-type{\n" +
        "  display:inline-block;\n" +
        "  font-size:11px;\n" +
        "  font-weight:700;\n" +
        "  text-transform:uppercase;\n" +
        "  letter-spacing:0.5px;\n" +
        "  color:var(--primary);\n" +
        "  background:#eef2ff;\n" +
        "  padding:3px 9px;\n" +
        "  border-radius:999px;\n" +
        "  margin-bottom:10px;\n" +
        "}\n" +
        ".price{font-size:20px;font-weight:700;margin:6px 0;}\n" +
        ".price small{font-size:12px;color:var(--muted);font-weight:400;}\n" +
        ".status{\n" +
        "  display:inline-block;\n" +
        "  font-size:12px;\n" +
        "  font-weight:600;\n" +
        "  padding:3px 10px;\n" +
        "  border-radius:999px;\n" +
        "  margin-bottom:12px;\n" +
        "}\n" +
        ".status.free{background:var(--free);color:var(--free-text);}\n" +
        ".status.occupied{background:var(--occupied);color:var(--occupied-text);}\n" +
        ".btn{\n" +
        "  display:inline-block;\n" +
        "  border:none;\n" +
        "  cursor:pointer;\n" +
        "  background:var(--primary);\n" +
        "  color:#fff;\n" +
        "  font-weight:600;\n" +
        "  font-size:14px;\n" +
        "  padding:9px 16px;\n" +
        "  border-radius:9px;\n" +
        "  text-decoration:none;\n" +
        "  transition:background .15s ease;\n" +
        "}\n" +
        ".btn:hover{background:var(--primary-dark);}\n" +
        ".btn.danger{background:#e0453c;}\n" +
        ".btn.danger:hover{background:#c73a32;}\n" +
        ".btn.secondary{background:#eef1f8;color:var(--text);}\n" +
        ".btn.secondary:hover{background:#e2e6f2;}\n" +
        ".btn.block{display:block;width:100%;text-align:center;}\n" +
        "table{\n" +
        "  width:100%;\n" +
        "  border-collapse:collapse;\n" +
        "  background:var(--card);\n" +
        "  border-radius:var(--radius);\n" +
        "  overflow:hidden;\n" +
        "  box-shadow:0 2px 10px rgba(20,30,60,0.04);\n" +
        "}\n" +
        "th, td{\n" +
        "  text-align:left;\n" +
        "  padding:12px 14px;\n" +
        "  border-bottom:1px solid var(--border);\n" +
        "  font-size:14px;\n" +
        "}\n" +
        "th{\n" +
        "  background:#f7f9fd;\n" +
        "  font-size:12px;\n" +
        "  text-transform:uppercase;\n" +
        "  letter-spacing:0.4px;\n" +
        "  color:var(--muted);\n" +
        "}\n" +
        "tr:last-child td{border-bottom:none;}\n" +
        ".empty{\n" +
        "  color:var(--muted);\n" +
        "  font-style:italic;\n" +
        "  padding:18px;\n" +
        "  background:var(--card);\n" +
        "  border-radius:var(--radius);\n" +
        "  border:1px dashed var(--border);\n" +
        "  text-align:center;\n" +
        "}\n" +
        ".flash{\n" +
        "  padding:12px 16px;\n" +
        "  border-radius:10px;\n" +
        "  margin-bottom:20px;\n" +
        "  font-weight:600;\n" +
        "  font-size:14px;\n" +
        "}\n" +
        ".flash.success{background:var(--success-bg);color:var(--success-text);}\n" +
        ".flash.error{background:var(--error-bg);color:var(--error-text);}\n" +
        ".form-card{max-width:460px;}\n" +
        ".form-card h2{margin-top:0;}\n" +
        "label{\n" +
        "  display:block;\n" +
        "  font-size:13px;\n" +
        "  font-weight:600;\n" +
        "  margin:14px 0 6px;\n" +
        "  color:var(--muted);\n" +
        "}\n" +
        "input, select{\n" +
        "  width:100%;\n" +
        "  padding:10px 12px;\n" +
        "  border:1px solid var(--border);\n" +
        "  border-radius:8px;\n" +
        "  font-size:14px;\n" +
        "  background:#fbfcfe;\n" +
        "  color:var(--text);\n" +
        "}\n" +
        "input:focus, select:focus{\n" +
        "  outline:none;\n" +
        "  border-color:var(--primary);\n" +
        "  background:#fff;\n" +
        "}\n" +
        ".row-actions{display:flex;gap:8px;margin-top:22px;}\n" +
        ".back-link{\n" +
        "  display:inline-block;\n" +
        "  margin-bottom:16px;\n" +
        "  color:var(--primary);\n" +
        "  text-decoration:none;\n" +
        "  font-size:14px;\n" +
        "  font-weight:600;\n" +
        "}\n" +
        ".back-link:hover{text-decoration:underline;}\n" +
        ".summary-bar{\n" +
        "  display:flex;\n" +
        "  gap:16px;\n" +
        "  flex-wrap:wrap;\n" +
        "  margin-bottom:24px;\n" +
        "}\n" +
        ".summary-item{\n" +
        "  flex:1;\n" +
        "  min-width:150px;\n" +
        "  background:var(--card);\n" +
        "  border:1px solid var(--border);\n" +
        "  border-radius:var(--radius);\n" +
        "  padding:16px 18px;\n" +
        "}\n" +
        ".summary-item .label{font-size:12px;color:var(--muted);font-weight:600;text-transform:uppercase;}\n" +
        ".summary-item .value{font-size:22px;font-weight:700;margin-top:4px;}\n" +
        "footer{\n" +
        "  text-align:center;\n" +
        "  color:var(--muted);\n" +
        "  font-size:12px;\n" +
        "  padding:20px;\n" +
        "}\n" +
        "@media(max-width:600px){\n" +
        "  header.topbar{padding:18px 18px;}\n" +
        "  main{padding:20px 14px 40px;}\n" +
        "  table, thead, tbody, th, td, tr{display:block;}\n" +
        "  thead{display:none;}\n" +
        "  tr{margin-bottom:12px;border:1px solid var(--border);border-radius:10px;overflow:hidden;}\n" +
        "  td{border-bottom:1px solid var(--border);}\n" +
        "  td:before{content:attr(data-label);font-weight:700;display:block;font-size:11px;color:var(--muted);text-transform:uppercase;margin-bottom:2px;}\n" +
        "}\n";
}
