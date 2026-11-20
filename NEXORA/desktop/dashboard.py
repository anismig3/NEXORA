import sys, threading, json
from datetime import datetime, timezone
from http.server import BaseHTTPRequestHandler, ThreadingHTTPServer
from PySide6.QtCore import Signal, QObject
from PySide6.QtWidgets import QApplication, QMainWindow, QWidget, QVBoxLayout, QHBoxLayout, QLabel, QPushButton, QTableWidget, QTableWidgetItem, QHeaderView, QFrame

events = []
server = None
class Bus(QObject):
    updated = Signal()
bus = Bus()

class Handler(BaseHTTPRequestHandler):
    def do_GET(self):
        if self.path == "/health":
            self.send_response(200); self.end_headers(); self.wfile.write(b'{"status":"ok"}')
        else: self.send_error(404)
    def do_POST(self):
        if self.path != "/event": self.send_error(404); return
        try:
            data = json.loads(self.rfile.read(int(self.headers.get("Content-Length","0"))))
            if data.get("package") != "com.example.labtarget": self.send_error(403); return
            name = str(data.get("event",""))[:50]
            if name not in {"WINDOW_STATE_CHANGED","VIEW_FOCUSED","VIEW_TEXT_CHANGED"}: self.send_error(400); return
            events.insert(0,(datetime.now(timezone.utc).strftime("%H:%M:%S"),"Lab Target Demo",name))
            del events[500:]; bus.updated.emit()
            self.send_response(200); self.end_headers(); self.wfile.write(b'{"ok":true}')
        except Exception: self.send_error(400)
    def log_message(self,*args): pass

class Window(QMainWindow):
    def __init__(self):
        super().__init__()
        self.setWindowTitle("NEXORA Dashboard"); self.resize(900,590)
        self.setStyleSheet("""
        QMainWindow,QWidget{background:#0d1523;color:#eaf0f8;font-family:'Segoe UI'}
        QLabel#brand{color:#e5ad50;font-size:13px;font-weight:700}
        QLabel#title{font-size:25px;font-weight:700}
        QFrame#panel{background:#19283b;border:1px solid #2b405a;border-radius:14px}
        QPushButton{background:#b8792c;color:#101827;border:0;border-radius:8px;padding:10px 18px;font-weight:700}
        QTableWidget{background:#121e30;gridline-color:#2b405a;border:1px solid #2b405a}
        QHeaderView::section{background:#22344b;color:#e5ad50;padding:9px;border:0}
        """)
        central=QWidget(); self.setCentralWidget(central); layout=QVBoxLayout(central)
        layout.setContentsMargins(28,24,28,24)
        brand=QLabel("N E X O R A"); brand.setObjectName("brand")
        title=QLabel("لوحة تحكم مختبر Android"); title.setObjectName("title")
        layout.addWidget(brand); layout.addWidget(title)
        panel=QFrame(); panel.setObjectName("panel"); row=QHBoxLayout(panel)
        self.status=QLabel("الخادم متوقف")
        self.start=QPushButton("Start Lab Server"); self.stop=QPushButton("Stop Session"); self.stop.setEnabled(False)
        row.addWidget(QLabel("Target: Lab Target Demo")); row.addStretch(); row.addWidget(self.status); row.addWidget(self.start); row.addWidget(self.stop)
        layout.addWidget(panel)
        self.table=QTableWidget(0,3); self.table.setHorizontalHeaderLabels(["الوقت UTC","التطبيق","نوع الحدث"])
        self.table.horizontalHeader().setSectionResizeMode(QHeaderView.Stretch); self.table.setEditTriggers(QTableWidget.NoEditTriggers)
        layout.addWidget(self.table,1)
        note=QLabel("أحداث Demo فقط — لا يتم جمع نصوص الحقول أو المحادثات أو كلمات المرور."); note.setStyleSheet("color:#e5ad50")
        layout.addWidget(note)
        self.start.clicked.connect(self.start_server); self.stop.clicked.connect(self.stop_server); bus.updated.connect(self.refresh)
    def start_server(self):
        global server
        if server is None:
            try:
                server=ThreadingHTTPServer(("0.0.0.0",8765),Handler)
                threading.Thread(target=server.serve_forever,daemon=True).start()
                self.status.setText("Running · Port 8765"); self.start.setEnabled(False); self.stop.setEnabled(True)
            except OSError as e: self.status.setText(str(e))
    def stop_server(self):
        global server
        if server:
            server.shutdown(); server.server_close(); server=None
        self.status.setText("Session stopped"); self.start.setEnabled(True); self.stop.setEnabled(False)
    def refresh(self):
        self.table.setRowCount(len(events))
        for r,row in enumerate(events):
            for c,value in enumerate(row): self.table.setItem(r,c,QTableWidgetItem(value))
    def closeEvent(self,event):
        self.stop_server(); event.accept()

if __name__=="__main__":
    app=QApplication(sys.argv); win=Window(); win.show(); sys.exit(app.exec())
