package es.elb4t.googlecastpersonalizada;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.cast.Cast.MessageReceivedCallback;
import com.google.android.gms.cast.CastDevice;
import com.google.android.gms.cast.framework.CastButtonFactory;
import com.google.android.gms.cast.framework.CastContext;
import com.google.android.gms.cast.framework.CastSession;
import com.google.android.gms.cast.framework.Session;
import com.google.android.gms.cast.framework.SessionManager;
import com.google.android.gms.cast.framework.SessionManagerListener;
import com.google.android.gms.common.api.BatchResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import java.io.IOException;

public class ActividadPrincipal extends AppCompatActivity {
    private CastSession mCastSession;
    private SessionManager mSessionManager;
    private Button textoButton;
    private Button fondoButton;
    private EditText txt;
    private Spinner colores;
    CanalCast mCanalCast = new CanalCast();
    CastContext castContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        castContext = CastContext.getSharedInstance(this);
        mSessionManager = castContext.getSessionManager();
        textoButton = (Button) findViewById(R.id.btn_texto);
        textoButton.setOnClickListener(btnClickListener);
        fondoButton = (Button) findViewById(R.id.btn_fondo);
        fondoButton.setOnClickListener(btnClickListener);
        txt = (EditText)findViewById(R.id.editText);
        colores = (Spinner)findViewById(R.id.spinner);
        String []opciones={"AZUL","VERDE","AMARILLO","ROJO"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item, opciones);
        colores.setAdapter(adapter);
    }

    private final View.OnClickListener btnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_texto:
                    sendMessage("#T#" + txt.getText().toString());
                    Log.e("CAST", "-----BONON TEXTO");
                    break;
                case R.id.btn_fondo:
                    switch (colores.getSelectedItem().toString()){
                        case "AZUL":
                            sendMessage("#F#blue");
                            break;
                        case "VERDE":
                            sendMessage("#F#green");
                            break;
                        case "AMARILLO":
                            sendMessage("#F#yellow");
                            break;
                        case "ROJO":
                            sendMessage("#F#red");
                            break;
                    }

                    Log.e("CAST", "-----BONON FONDO");
                    break;
            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu, menu);
        CastButtonFactory.setUpMediaRouteButton(getApplicationContext(),
                menu,
                R.id.media_route_menu_item);
        return true;
    }


    private final SessionManagerListener mSessionManagerListener =
            new SessionManagerListenerImpl();

    private class SessionManagerListenerImpl implements SessionManagerListener {
        @Override
        public void onSessionStarted(Session session, String sessionId) {
            invalidateOptionsMenu();
            setSessionStarted(true);
            mCastSession = mSessionManager.getCurrentCastSession();
        }

        @Override
        public void onSessionResumed(Session session, boolean wasSuspended) {
            invalidateOptionsMenu();
            setSessionStarted(true);
        }

        @Override
        public void onSessionSuspended(Session session, int error) {
            setSessionStarted(false);
        }

        @Override
        public void onSessionStarting(Session session) {
        }

        @Override
        public void onSessionResuming(Session session, String sessionId) {
        }

        @Override
        public void onSessionStartFailed(Session session, int error) {
        }

        @Override
        public void onSessionResumeFailed(Session session, int error) {
        }

        @Override
        public void onSessionEnding(Session session) {
        }

        @Override
        public void onSessionEnded(Session session, int error) {
            setSessionStarted(false);
        }
    }

    private void setSessionStarted(boolean enabled) {
        textoButton.setEnabled(enabled);
        fondoButton.setEnabled(enabled);
        if (mCastSession != null && mCanalCast == null) {
            mCanalCast = new CanalCast();
            try {
                mCastSession.setMessageReceivedCallbacks(mCanalCast.getNamespace(), mCanalCast);
                Log.e("CAST", "SESION mesage recived callback");
            } catch (IOException e) {
                mCanalCast = null;
                Log.e("CAST", "ERROR SESION mesage recived callback" + e.getMessage().toString());
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSessionManager.addSessionManagerListener(mSessionManagerListener);
        if (mCastSession == null) {
            mCastSession = mSessionManager.getCurrentCastSession();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSessionManager.removeSessionManagerListener(mSessionManagerListener);
        mCastSession = null;
    }

    private void sendMessage(String message) {
        if (mCanalCast != null) {
            try {
                mCastSession.sendMessage(mCanalCast.getNamespace(), message)
                        .setResultCallback(
                                new ResultCallback<Status>() {
                                    @Override
                                    public void onResult(Status result) {
                                        if (!result.isSuccess()) {
                                            Toast.makeText(getApplicationContext(), "Error al enviar el mensaje.", Toast.LENGTH_LONG);
                                        }
                                    }
                                });
            } catch (Exception e) {
                Toast.makeText(getApplicationContext()
                        , "Error al enviar el mensaje: " + e, Toast.LENGTH_LONG);
            }
        }
    }


    public class CanalCast implements MessageReceivedCallback {
        public String getNamespace() {
            return "urn:x-cast:es.elb4t.googlecastpersonalizada";
        }

        @Override
        public void onMessageReceived(CastDevice castDevice, String namespace, String message) {
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG);
            Log.e("CAST", "MENSAJE RECIBIDO: "+ message);
            if (message.equals("apagar")){
                finish();
            }
        }
    }
}
